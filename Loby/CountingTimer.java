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
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.Iterator;
import java.util.HashMap;

public class CountingTimer {
    private long time_stamp;
    private SocketChannel client;

    public CountingTimer(long generated_time, SocketChannel channel) {
        this.time_stamp = generated_time;
        this.client = channel;

        Timer hb_timer = new Timer();
        TimerTask hb_task = new TimerTask() {

            @Override
            public void run() {

                // 5초마다 한번씩 핑을 받는다.
                // 4번 이상 받지 못하게 된다면 시간 초과로 client 연결을 끊는다.
                if (System.currentTimeMillis() - time_stamp > 20000) {

                    System.out.println("heartbeat 시간 초과");

                    try {
                        client.close();
                        System.out.println("client 닫힘");
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    throw new IllegalArgumentException("heartbeat 시간 초과");
                }
            }
        };

        hb_timer.schedule(hb_task, 5000, 5000);

    }

    // 핑을 받을때마다 실행됨. 마지막으로 받은 시각을 기록함.
    public void update_time(long time) {
        this.time_stamp = time;
    }

}