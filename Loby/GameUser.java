import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameUser {
    public String _userId;
    public String _nickname;

    public int _score; // 소속된 방이 없을 경우에 -1
    public String _roomId;// 소속된 방이 없을경우에 0

    // 테스트용
    public GameUser() {
        _userId = "test";
        _nickname = "nickname";
        _score = 100;
        _roomId = "roomId";
    }

    public GameUser(String userId, String nickname, int score, String roomId) {
        _userId = userId;
        _nickname = nickname;
        _score = score;
        _roomId = roomId;
    }

}