import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//singleton 패턴 적용, 데이터 센터는 하나만 생성하도록 함.
public class DataCenter {

    private static DataCenter dataCenter = new DataCenter();

    // <userId(email), GameUser 객체 쌍>
    private static HashMap<String, GameUser> _users;
    // <roomId, GameRoom 객체 쌍>
    private static HashMap<String, GameRoom> _rooms;

    private DataCenter() {
        System.out.println("데이터 센터 생성");
        _users = new HashMap<String, GameUser>();

        // testcode;
        _users.put("test1", new GameUser());
        _users.put("test2", new GameUser());

        _rooms = new HashMap<String, GameRoom>();
    }

    /**
     * 객체가 존재하지 않으면 생성해서 리턴을 하고 객체가 존재하면 존재하는 객체를 리턴한다.
     */

    public static DataCenter getInstance() {
        if (dataCenter == null) {
            dataCenter = new DataCenter();
            return dataCenter;
        }
        return dataCenter;
    }

    // 새로운 유저를 추가한다.
    public void addNewUser(String userId, GameUser user) {
        _users.put(userId, user);
    }
}