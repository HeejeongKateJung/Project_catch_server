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

public class LobyInterface {


    public static void main(String args[]) {

        try {

            //selector open
            final Selector selector = Selector.open();
            final ServerSocketChannel server = ServerSocketChannel.open();
            final InetSocketAddress addr = new InetSocketAddress(9000);

            //register server on selector
            server.bind(addr);
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_ACCEPT);

            while(true) {

                //대기중인 소캣 채널들 알아내기.
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();
                
                //selectedkey 가 2개면 1번은 serversocket, 2번은 clientsocket.
                while(it.hasNext()) {
                    SelectionKey key = it.next();

                    if(key.isAcceptable()) {
                        ServerSocketChannel s = (ServerSocketChannel)key.channel();
                        SocketChannel client = s.accept();

                        if(client == null) continue;
                        
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                        
                        System.out.println("new client accepted..");
                    }

                    if(key.isReadable()) {

                        

                    }
                    
                }

            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}