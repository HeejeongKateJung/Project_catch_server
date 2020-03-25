public class BroadcastData{

    int _dataTypeCode;
    String _sentUserId;
    String _sentNickname;
    String _chatMsg;
    String _time;
    


    public BroadcastData(){

    }

    public BroadcastData(int dataTypeCode, String sentUserId, String sentNickname, String chatMsg, String time){
        _dataTypeCode = dataTypeCode;
        _sentUserId = sentUserId;
        _sentNickname = sentNickname;
        _chatMsg = chatMsg;
        _time = time;
    }
}