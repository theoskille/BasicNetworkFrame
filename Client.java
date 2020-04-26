import java.io.*;
import java.net.Socket;

public class Client{
    private int userId;
    private ClientSideConnection csc;
    private Serializable auxData;

    public Client(){
    }
    //create a client side connection object and start its thread
    public void connectToServer(){
        csc=new ClientSideConnection();
        Thread t=new Thread(csc);
        t.start();
    }

    private class ClientSideConnection implements Runnable{
        private Socket socket;
        private ObjectInputStream dataIn;
        private ObjectOutputStream dataOut;

        public ClientSideConnection(){
            System.out.println("----client----");
            try {
                socket=new Socket("localhost", 51734);
                dataOut=new ObjectOutputStream(socket.getOutputStream());
                dataIn=new ObjectInputStream(socket.getInputStream());
                userId=dataIn.readInt();
                System.out.println("connected to server as User# "+userId);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void sendData(Serializable data){
            try {
                dataOut.writeObject(data);
                dataOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    auxData= (Serializable) dataIn.readObject();
                }
            } catch (IOException |ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}