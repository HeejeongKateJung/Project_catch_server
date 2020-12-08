import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class LobyFunctions {

    String _requestCode;
    JSONObject _jo;
    DataCenter _dc = DataCenter.getInstance();
    SocketChannel _client; // socket channel for writing to network buffer

    public LobyFunctions(String requestCode, JSONObject jo, SocketChannel client) {
        _requestCode = requestCode;
        _jo = jo;
        _client = client;
    }

    // doSomething(): request code 에 따라 다른 동작을 하는 인터페이스 메소드
    public void doSomething() {

        if (_requestCode.equals("report_user_info")) {
            // 로비 유저 리스트를 먼저 보내고, 새로운 유저를 등록한다.
            send_user_list();
            save_user_info();
        }

        // if(_requestCode == "")
    }

    /// <summery>
    // 유저 정보를 저장
    /// </summery>
    private void save_user_info() {
        System.out.println("Saving user info");
        String userId = (String) _jo.get("_userId");
        String nickname = (String) _jo.get("_nickname");
        int score = Integer.parseInt(_jo.get("_score").toString());

        GameUser user = new GameUser(userId, nickname, score, "0");
        _dc.addNewUser(userId, user);
    }

    // stream으로 쓰는 부분은 따로 빼서 함수로 만든다. 나중에
    private void send_user_list() {
        System.out.println("Send user list");

        JSONArray ja = _dc.getUserList();

        // System.out.println(ja.toString());

        String data = ja.toString();

        // datasize->bytebuffer 변환
        final ByteBuffer datasize = ByteBuffer.allocate(Integer.BYTES);
        datasize.putInt(data.length());
        datasize.flip();

        // data->bytebuffer 변환
        Charset charset = Charset.forName("UTF-8");
        ByteBuffer bb = ByteBuffer.allocateDirect(data.length());
        bb = charset.encode(data);

        // System.out.println(data.length());
        // System.out.println(datasize.getInt());

        try {
            System.out.println(datasize.getInt());
            _client.write(datasize);
            // _client.write(bb);
        } catch (Exception e) {
            System.out.println("보내기 실패: ");
            System.out.println(e);
        }

    }

}