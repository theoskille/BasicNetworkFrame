import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server{
    private ServerSocket ss;
    private int numUsers;
    private int serverMax;
    private ArrayList<ServerSideConnection> users;
    private Serializable[] auxData;

    //Initialize Fields
    //sets up server socket with port
    public Server(){
        System.out.println("----Server----");
        numUsers=0;
        serverMax=2;
        users=new ArrayList<ServerSideConnection>();
        auxData=new Serializable[serverMax];
        try {
            ss=new ServerSocket(51734);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //allows users to connect to server until the server is full
    //creates server side connection objects for each user and starts their threads
    public void acceptConnections(){
        try {
            System.out.println("waiting for connections");
            while (numUsers < serverMax) {
                Socket s = ss.accept();
                numUsers++;
                System.out.println("User # " + numUsers + " has connected");
                users.add(new ServerSideConnection(s,numUsers));
                Thread t=new Thread(users.get(numUsers-1));
                t.start();
            }
            System.out.println("server full");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ServerSideConnection implements Runnable{
        private Socket socket;
        private ObjectInputStream dataIn;
        private ObjectOutputStream dataOut;
        private int userId;

        public ServerSideConnection(Socket s,int id){
            socket=s;
            userId=id;

            try {
                dataOut=new ObjectOutputStream((socket.getOutputStream()));
                dataIn=new ObjectInputStream(socket.getInputStream());
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
                dataOut.writeInt(userId);
                dataOut.flush();

                while(true){
                    auxData[userId-1]=(Serializable)dataIn.readObject();
                    for(int i=0;i<users.size();i++)
                        users.get(i).sendData(auxData[userId-1]);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
    public static void main(String[] args){
        Server gs=new Server();
        gs.acceptConnections();
    }
}