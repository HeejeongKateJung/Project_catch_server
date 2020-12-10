import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LobyInterface {

    // private int byteToint(byte[] arr) {
    // return (arr[0] & 0xff) << 24 | (arr[1] & 0xff) << 16 | (arr[2] & 0xff) << 8 |
    // (arr[3] & 0xff);
    // }

    // private static int byteToInt(byte[] bytes, ByteOrder order) {
    // ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE / 8);
    // buff.order(order);

    // // buff size = 4
    // // bytes를 put 하면 position, limit 은 같은 위치가 된다.
    // buff.put(bytes);
    // // flip() : position 을 0에 위치시킨다.
    // buff.flip();

    // return buff.getInt();
    // }

    private static JSONObject string_2Json(String jsonString) {
        // JSONString -> Json object
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(jsonString);
            JSONObject jsonObj = (JSONObject) obj;
            return jsonObj;
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    private static String get_requestCode(String jsonString) {
        try {
            JSONObject jsonObj = string_2Json(jsonString);
            // JSON 데이터의 코드를 확인한다.
            String requestCode = (String) jsonObj.get("_requestCode");
            return requestCode;
        } catch (Exception e) {
            System.out.println(e);
            return "ERROR";
        }

    }

    private static void read_client(SocketChannel client) {
        try {
            ByteBuffer buf = ByteBuffer.allocateDirect(100);
            int bytesRead = client.read(buf);

            // 읽혀진 byte 가 없으면 빈 값이므로 리턴.
            if (bytesRead == -1) {
                return;
            }

            String result = "";
            while (true) {
                // 더이상 읽을 것이 없을 때까지 읽는다.
                if (bytesRead == 0)
                    break;

                buf.flip();
                Charset charset = Charset.forName("UTF-8");

                // 읽은 string 합치기
                result += charset.decode(buf).toString();
                buf.clear();
                bytesRead = client.read(buf);
            }

            // request code 확인
            if (result == "") {
                return;
            }

            System.out.println(result);
            String _requestCode = get_requestCode(result);
            if (_requestCode == "ERROR" || _requestCode == null)
                return;

            // LobyFunction 객체는 requestCode 에 따라 다른 동작을 하게 된다.
            System.out.println("request code: " + _requestCode);
            LobyFunctions lf = new LobyFunctions(_requestCode, string_2Json(result), client);
            lf.doSomething();

        } catch (Exception e) {
            System.out.println("READ FAILED");
            System.out.println(e);
        }
    }

    public static void main(String args[]) {

        try {

            // selector open
            final Selector selector = Selector.open();
            final ServerSocketChannel server = ServerSocketChannel.open();
            final InetSocketAddress addr = new InetSocketAddress(9000);

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

                        // client 도 nonblocking 설정
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                        System.out.println("new client accepted..");
                    }

                    if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        read_client(client);

                    }

                }

            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}