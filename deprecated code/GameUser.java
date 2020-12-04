public class GameUser{
    public String _userId;
    public String _nickname;
    public int _priority; //소속된 방이 없을 경우에 -1
    public String _roomId;//소속된 방이 없을경우에 0

    public GameUser(){

    }

    public GameUser(String userId, String nickname, int priority, String roomId)
    {
        _userId = userId;
        _nickname = nickname;
        _priority = priority;
        _roomId = roomId;
    }
    
}