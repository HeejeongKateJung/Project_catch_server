import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.invoke.*;

public class TestServer {

    //ServerSocket and ClientSocket
    private Socket client = null;
    private ServerSocket server_socket = null;

    //DataInpustStream and DataOutputStream
    private DataInputStream dis = null;
    private DataOutputStream dos = null;

    public TestServer()
    {

    }

    public static void main (String arg[])
    {
        TestServer testServer = new TestServer();
        testServer.connect();


        
    }

    public void connect()
    {

        //서버소켓 오픈
        try{
            server_socket = new ServerSocket(8000);
        }catch(IOException e){
            System.out.println("해당 포트가 열려있습니다.");
        }

        try{

            //클라이언트 연결
            System.out.println("서버 오픈!!");
            client = server_socket.accept();
            System.out.println("클라이언트 연결됨");

            
            System.out.println("debuging1");

            
            //data output stream

            /**에러난 부분*
             * 클라이언트와 서버간의 입출력 스트림은 반드시 1대1로 연결되어야 한다.
             * 따라서 만약에 클라이언트에서 계속 스트림을 재생성하고 있다면 문제가 될 수 있다.
             * 그리고 DataInputStream의 생성자는 서버의 DataOutputStream 생성자가 실행되기 전까지는
             * 블로킹 상태로 머물러 있으므로 반드시 체크하자.
             */

            //outputstream의 생성자는 스트림헤더를 클라이언트에 전송하여 clientInputStream과 연결
            dos = new DataOutputStream(client.getOutputStream());
            //data input stream
            dis = new DataInputStream(client.getInputStream());
            System.out.println("debuging2");
            
            //data output stream

            byte[] in = new byte[100];
            byte[] out = new byte[100];


                try{

                    //
                    String temp = "message from server";
                    out = temp.getBytes();
                    dos.write(out);
                    dos.flush();



                    dis.read(in, 0, 100);
                    String receiveMsg = new String(in,0,in.length);
                    receiveMsg.trim();

                    System.out.println("Client로 부터 온 메세지 : " + receiveMsg);
                    
                    out = receiveMsg.getBytes();
                    dos.write(out);
                    dos.flush();
                }catch (Exception e) {
                    //TODO: handle exception
                    System.out.println(e.getMessage());
                }
                

                

            dis.close();
            dos.close();

            client.close();
        }catch(IOException e){
            System.out.println("에러: " + e.getMessage());
        }
    }
}