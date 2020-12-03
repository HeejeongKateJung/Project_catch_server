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
import java.util.Map;

public class Heartbeat {

    // register client on selector
    private static void register_client(Selector selector, ServerSocketChannel server) throws IOException {
        SocketChannel client = server.accept();

        // if(client == null){
        // return;
        // }
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        System.out.println("client connected");
    }

    private static void read_client(SocketChannel client) {
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(100);
            client.read(byteBuffer);
            byteBuffer.flip();
            Charset charset = Charset.forName("UTF-8");
            System.out.println("Received: " + charset.decode(byteBuffer).toString());
        } catch (Exception e) {
            System.out.println("READ FAILED");
            System.out.println(e);
        }
    }

    public static void main(String args[]) {

        final Map<SocketChannel, CountingTimer> identifier = new HashMap<SocketChannel, CountingTimer>();

        try {

            // selector open
            final Selector selector = Selector.open();
            final ServerSocketChannel server = ServerSocketChannel.open();
            final InetSocketAddress addr = new InetSocketAddress(7000);

            // register server on selector
            server.bind(addr);
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {

                // 대기중인 소캣 채널들 알아내기.
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();

                // selectedkey 가 2개면 1번은 serversocket, 2번은 clientsocket.
                while (it.hasNext()) {
                    SelectionKey key = it.next();

                    if (key.isAcceptable()) {
                        ServerSocketChannel s = (ServerSocketChannel) key.channel();
                        SocketChannel client = s.accept();

                        if (client == null)
                            continue;

                        // NonBlocking 설정
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);

                        System.out.println("new client accepted..");
                        // counting timer 가 만들어지면 5초마다 시간을 검사하는 timer가 실행됨
                        CountingTimer ct = new CountingTimer(System.currentTimeMillis(), client);
                        identifier.put(client, ct);
                    }

                    if (key.isReadable()) {
                        // System.out.println("it is readable");

                        SocketChannel client = (SocketChannel) key.channel();
                        read_client(client);

                        // 새로운 핑이 감지될때마다 countingtimer 에 마지막으로 받은 시각을 기록.
                        CountingTimer ct = identifier.get((SocketChannel) key.channel());
                        ct.update_time(System.currentTimeMillis());
                    }

                }

            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
