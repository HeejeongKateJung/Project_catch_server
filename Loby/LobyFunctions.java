import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LobyFunctions {

    String _requestCode;
    JSONObject _jo;
    DataCenter _dc = DataCenter.getInstance();

    public LobyFunctions(String requestCode, JSONObject jo) {
        _requestCode = requestCode;
        _jo = jo;
    }

    public void doSomething() {

        if (_requestCode.equals("report_user_info")) {
            save_user_info();
            send_user_list();
        }

        // if(_requestCode == "")
    }

    private void save_user_info() {
        System.out.println("Saving user info");
        String userId = (String) _jo.get("_userId");
        String nickname = (String) _jo.get("_nickname");
        int score = Integer.parseInt(_jo.get("_score").toString());

        GameUser user = new GameUser(userId, nickname, score, "0");
        _dc.addNewUser(userId, user);
    }

    private 
}