import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInput;
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
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.lang.invoke.*;


public class SocketConnection {
    public static void main(String args[])
    {

        //서버용 싱글 스레드 풀 생성
        ExecutorService service = Executors.newSingleThreadExecutor();
        //수신용 클라이언트 쓰레드풀 생성
        ExecutorService clientService = Executors.newFixedThreadPool(10);
    
        service.submit(() ->{
            //9000포트로 서버 대기
            try(ServerSocket server = new ServerSocket()) {
                InetSocketAddress ipep = new InetSocketAddress(9000);
                server.bind(ipep);
                System.out.println("서버 대기중...");

                Socket client = server.accept();
                System.out.println("클라이언트 접속!");
                clientService.submit(() -> {
                    System.out.println("뭐야 접속");
                });


               
            }catch (Throwable e){
                e.printStackTrace();
            }

            
        });
    
    }
}