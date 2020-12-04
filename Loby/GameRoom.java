import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GameRoom{
    
    public Map<String, GameUser> _members;
    public String _roomId;
    public int _memNum;
    public String _title;
    public String _password;

    public GameRoom(){
        _members = new HashMap<String, GameUser>();
    }

    public GameRoom(GameUser owner, String roomId, int memNum, String title, String password){
        _members = new HashMap<String, GameUser>();
        _roomId = roomId;
        _memNum = memNum;
        _title = title;
        _password = password;
    }

    // public JSONObject toJSON() {
    //     JSONObject jo = new JSONObject();
    //     jo.put("_roomId", _roomId);
    //     jo.put("_memNum", _memNum);
    //     jo.put("_title", _title);
    //     jo.put("_password", _password);

    //     return jo;
    // }


}