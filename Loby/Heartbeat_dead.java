import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.lang.invoke.*;
import java.util.Timer;
import java.util.TimerTask;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Heartbeat_io implements Runnable {
    private Socket client;
    private DataInputStream dis;
    private DataOutputStream dos;
    private long time_stamp;
    

    public Heartbeat(Socket client, DataInputStream dis, DataOutputStream dos) {
        this.client = client;
        this.dis = dis;
        this.dos = dos;
    }

    //네트워크가 느릴때를 대비하여 5번의 margincall 을 준 뒤
    //다음 패킷이 실행되지 않을 경우 네트워크 연결을 끊는다.
    //이때 클라이언트에게는 응답하지 않는다.
    @Override
    public void run() {
        try{
            //마지막 heartbeat으로부터 몇초가 지낫는지 계산하는 객체들
            Timer hb_timer = new Timer();
            TimerTask hb_task = new TimerTask(){
                
                
                @Override
                public void run() {
                    if(System.currentTimeMillis() - time_stamp > 20000){
                        System.out.println("heartbeat 시간 초과");
                        throw new IllegalArgumentException("heartbeat 시간 초과");
                    }
                }
            };

            /*timer 는 5초마다 마지막 패킷 경과시간을 계산하고, 
              네트워크 상태를 고려하여 20초 이상 패킷이 도착하지 않았다면
              클라이언트가 비정상 종료되었다고 가정하고 client socket을 종료한다.*/
            hb_timer.schedule(hb_task, 5000, 5000);

            while(true) {
                
                //클라는 heartbeat 을 5초마다 보낼것이다.
                byte[] buffer = new byte[100];


                //ping 을 받기 전 시각을 기록한다.
                //packet 도착 대기 시간을 계산하기 위해서이다.
                time_stamp = System.currentTimeMillis();
                dis.read(buffer, 0, 100);

                String signal = new String(buffer, Charset.forName("uTF-8"));
                System.out.println(signal);

                
            }


        }catch(IOException ex){
            System.out.println("this is catch");
            System.err.println(ex);
        }
        finally {
            try {
                client.close();
                System.out.println("client hb_socket closed");
            } catch (Exception e) {
                System.err.println("fail to close client socket");
            }
            
        }
    }

    public static void main(String args[]) {


        try(ServerSocket server = new ServerSocket();) {
            InetSocketAddress ipep = new InetSocketAddress(7000);
            server.bind(ipep);
            while(true) {
                System.out.println("서버 대기중");
                try {
                    
                    Socket client = server.accept();
                    DataInputStream dis = new DataInputStream(client.getInputStream());
                    DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                    System.out.println("클라이언트 접속!");

                    Heartbeat hb = new Heartbeat(client, dis, dos);
                    Thread listenhb = new Thread(hb);

                    listenhb.start();

                } catch (Exception e) {
                    //TODO: handle exception
                }
            }


        } catch (Exception e) {
            System.err.println(e);
            //TODO: handle exception
        }
    

    }
}