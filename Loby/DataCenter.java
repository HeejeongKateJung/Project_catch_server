import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
// import java.util;

//singleton 패턴 적용, 데이터 센터는 하나만 생성하도록 함.
public class DataCenter {

    private static DataCenter dataCenter = new DataCenter();

    // <userId(email), GameUser 객체 쌍>
    public static HashMap<String, GameUser> _users;
    // <roomId, GameRoom 객체 쌍>
    public static HashMap<String, GameRoom> _rooms;

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

    // 유저 리스트를 반환한다.
    public JSONArray getUserList() {
        JSONArray json_users = new JSONArray();
        // Hashmap users 에 있는 모든 GameUser Object 에 대하여 jsonobject로 변환 -> jsonArray 에 넣기.
        Iterator<String> keys = _users.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            GameUser user = _users.get(key);

            /**
             * 여기서 object 를 곧바로 jsonString 으로 바꿔버리면 JsonArray 에 string 형태로 추가되게 되므로 따로
             * object 와 매치되는 json object 를 만들어 주어야 한다.
             */
            JSONObject jo = new JSONObject();

            jo.put("_userId", user._userId);
            jo.put("_nickname", user._nickname);
            jo.put("_score", user._score);

            // JsonArray에 추가
            json_users.add(jo);

        }

        return json_users;
    }
}