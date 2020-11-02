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
        /**수신용 멀티 스레드 풀 생성
         * client 수신을 thread로 무한 생성할 경우 메모리 이슈가 발생할 수 있기 때문에
         * 그 숫자를 제한하는 쓰레드 풀을 사용한다.
         */
        ExecutorService clientService = Executors.newFixedThreadPool(50);
    
        service.submit(() ->{
            //9000포트로 서버 대기
            try(ServerSocket server = new ServerSocket()) {
                InetSocketAddress ipep = new InetSocketAddress(7000);
                server.bind(ipep);

                while(true){
                    System.out.println("서버 대기중...");
                    try {
                        Socket client = server.accept();
                        System.out.println("클라이언트 접속!");

                        //새로운 Task 를 clientService 에 할당한다.
                        Callable<void> task = new HeartbeatTask(client, new byte[]);
                        clientService.submit(task);

                    } catch (Exception e) {
                        e.printStackTrace();
                        //TODO: handle exception
                    }

                }


               
            }catch (Throwable e){
                e.printStackTrace();
            }

            
        });
    
    }

    private static class HeartbeatTask implements Callable<void> {
        private Socket client;
        private DataOutputStream dos;
        private Byte[] buffer;
        private Bool _lock = false;

        HeartbeatTask(Socket client, Byte[] buffer){
            this.client = client;
            this.buffer = buffer;
            this.dos = new DataOutputStream(client.getOutputStream());
        }

        public void run(){
            try{

            }catch(IOException ex){
                System.err.println(ex);
            }finally {
                client.close();
            }catch(IOException e){
            }
        }
    }
}