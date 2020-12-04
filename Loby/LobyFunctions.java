import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LobyFunctions {

    String _requestCode;
    JSONObject _jo;
    Map<String, GameUser> users;

    public LobyFunctions(String requestCode, JSONObject jo) {
        _requestCode = requestCode;
        _jo = jo;
    }

    public void doSomething() {

        if (_requestCode.equals("report_user_info")) {
            save_user_info();
        }

        // if(_requestCode == "")
    }

    private void save_user_info() {
        System.out.println("Saving user info");

    }
}