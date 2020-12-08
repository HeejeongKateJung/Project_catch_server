import java.io.OutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RoomManager {

    private Map<String, GameRoom> rooms; // roomid 로 GameRoom 객체를 찾을 것임
    private Map<String, GameUser> users; // userId 로 user 객체를 찾을 것

    // Data type code

    public static int CODE_CHATMSG = 111; // 채팅 메시지
    public static int CODE_NOTICE = 112; // 공지사항
    public static int CODE_USERUPDATE = 113; // 다른 유저들 정보

    public RoomManager() {
        rooms = new HashMap<String, GameRoom>();
        users = new HashMap<String, GameUser>();
    }

    public String EnterUser(JSONObject jo) {
        System.out.println("EnterUser function has been called");

        // json object 에서 값 받아오기
        String userId = (String) jo.get("_userId");
        String nickname = (String) jo.get("_nickname");

        /** 유저가 처음 로비에 입장하면 소속된 방이 없으므로 priority -1, roomId 0 */
        GameUser newUser = new GameUser(userId, nickname, -1, "0");
        users.put(userId, newUser);

        for (Map.Entry<String, GameUser> entry : users.entrySet()) {
            System.out.println("현재 로비에 있는 유저: " + entry.getKey());
        }

        // 클라이언트로 결과를 송신하는 작업은 RoomManagementServer에서 한다.
        return "0";
    }

    /** 방을 만드는 함수. */
    public String MakeRoom(JSONObject jo) {
        System.out.println("MakeRoom Funciton has been called");
        String userId = (String) jo.get("_userId");
        String title = (String) jo.get("_title");
        String password = (String) jo.get("_password");

        // GameRoom(GameUser owner, int roomid, int memNum, String title, String
        // password)
        /**
         * 새로운 게임 방 객체를 만들고 map에 추가한다. 추가하기 전 1. userId에 맞는 hash value 인 GameUser 를 찾아야
         * 한다. 2. roomId를 고유값으로 할당해야 한다. 3. memNum은 무조건 1이어야 한다. (파티로 방을 만드는 기능은 보류)
         */

        if (!users.containsKey(userId)) {
            String msg = "에러: 온라인이 아닌 유저가 방만들기 요청함";
            System.out.println(msg);
            return msg;
        }

        // userId로 roomOwner를 찾는다.
        GameUser roomOwner = users.get(userId);

        // 고유한 roomId 값을 할당한다.
        String roomId = UUID.randomUUID().toString();
        System.out.println("roomId: " + roomId);

        GameRoom newGameRoom = new GameRoom(roomOwner, roomId, 0, title, password);
        rooms.put(roomId, newGameRoom);

        // 클라이언트로 결과를 송신하는 작업은 RoomManagementServer에서 한다.

        return roomId;

    }

    /** 방 리스트가 담긴 HashMap을 클라이언트들에게 보내는 함수 */
    public String SendRoomList(DataOutputStream dos) {

        System.out.println("SendRoomList function called");

        JSONArray roomJsonArray = new JSONArray();

        // Hashmap rooms 에 있는 모든 GameRoom Object 에 대하여 jsonobject로 변환 -> jsonArray 에 넣기.
        Iterator<String> keys = rooms.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            GameRoom room = rooms.get(key);

            /**
             * 여기서 object 를 곧바로 jsonString 으로 바꿔버리면 JsonArray 에 string 형태로 추가되게 되므로 따로
             * object 와 매치되는 json object 를 만들어 주어야 한다.
             */
            JSONObject jo = new JSONObject();

            jo.put("_roomId", room._roomId);
            jo.put("_memNum", room._memNum);
            jo.put("_title", room._title);
            jo.put("_password", room._password);

            // JsonArray에 추가
            roomJsonArray.add(jo);

        }
        // System.out.println("roomJsonArray: " + roomJsonArray);

        String result = SendJsonArrayWithBytes(roomJsonArray, dos);

        return result;

    }

    public String SendJsonArrayWithBytes(JSONArray ja, DataOutputStream dos) {
        // 클라이언트에게 jsonArray를 byte 형태로 보낸다.
        /***
         * RoomManagementServer 에서는 매우 짧은 오류 코드와 성공 코드만 보냈기 때문에 따로 byte 크기를 명시할 필요가 없었으나
         * 지금의 경우는 긴 data 를 보내므로 임의로 정한 byte 크기가 아닌 data의 정확한 크기만큼 읽어들이도록 통신해야한다. 서버는
         * 클라이언트에게 byte size를 먼저 보낸 뒤 data 를 끝까지 송신한다.
         */
        try {
            String dataString = ja.toString();
            byte[] data = dataString.getBytes();
            int bufferSize = 100;
            int i = 0;

            // System.out.println("data: " + dataString);
            // System.out.println("data length: " + data.length);

            // 먼저 dataSize 를 4바이트만큼 보낸다.
            dos.write(BitConverter.getBytes(data.length), 0, 4);
            dos.flush();

            // bufferSize 만큼 끊어서 보낸다.
            while (i < data.length / bufferSize) {
                dos.write(data, bufferSize * i, bufferSize);
                dos.flush();
                i++;
            }

            // bufferSize 남은만큼 또 보내기
            dos.write(data, bufferSize * i, data.length % bufferSize);
            dos.flush();

            return "0";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String EnterRoom(JSONObject jo, DataOutputStreamStorage dosStorage) {

        System.out.println("EnterRoom Funciton has been called");
        String userId = (String) jo.get("_userId");
        String roomId = (String) jo.get("_roomId");
        String nickname = (String) jo.get("_nickname");

        DataOutputStream dos = dosStorage._dataOutputStreams.get(userId);

        // 1. 해당 방이 존재하는지 확인한다.
        // roomId 로 GameRoom 객체를 찾는다.
        GameRoom room = rooms.get(roomId);

        // 1-1. 방이 존재하지 않는다면 errorCode return
        if (room == null) {
            String msg = "방이 존재하지 않습니다";
            System.out.println(msg);
            return msg;
        }

        // 2. 방에 입장처리한다.
        // 2-1. userId 에 해당하는 user 를 찾는다.
        GameUser targetUser = users.get(userId);

        if (targetUser == null) {
            String msg = "입장처리 실패";
            System.out.println(msg);
            return msg;
        }

        room._members.put(userId, targetUser);
        // 2-2. memNum 을 증가시킨다.
        room._memNum = room._memNum + 1;
        // 2-3. targetUser 에게 소속 방 Id 를 할당한다.
        targetUser._roomId = roomId;
        // 2-4. targetUser 의 방장 priority 는 마지막이므로 항상 멤버수-1 이다.
        // (멤버가 targetUser 를 포함하여 3명일 경우 priority 2)
        targetUser._priority = room._memNum - 1;

        // 3. 방에 있는 모든 멤버들에게 유저 입장을 알림.
        String strNotice = "[" + nickname + "]" + "님이 입장하셨습니다";
        BroadcastData enterNotice = new BroadcastData(CODE_NOTICE, userId, nickname, strNotice, "time");
        Broadcast(room, enterNotice, dosStorage);

        // 4. 방에 멤버가 나 말고 또 있을경우 방안에 있는 유저들의 정보를 전달한다.
        if (room._memNum > 1) {
            SendMembersInfo(room, targetUser, dos);
        }

        return "0";
    }

    public void SendMembersInfo(GameRoom targetRoom, GameUser targetUser, DataOutputStream dos) {
        JSONArray memberJsonArray = new JSONArray();

        targetRoom._members.keySet().iterator();
        // Hashmap _members object 에 대하여 jsonobject로 변환 -> jsonArray 에 넣기.
        Iterator<String> keys = targetRoom._members.keySet().iterator();
        while (keys.hasNext()) {
            System.out.println("SendMemebersInfo; while문 도는중");
            String key = keys.next();
            GameUser user = targetRoom._members.get(key);

            /**
             * 여기서 object 를 곧바로 jsonString 으로 바꿔버리면 JsonArray 에 string 형태로 추가되게 되므로 따로
             * object 와 매치되는 json object 를 만들어 주어야 한다.
             */
            JSONObject jo = new JSONObject();

            jo.put("_dataTypeCode", CODE_USERUPDATE);
            jo.put("_userId", user._userId);
            jo.put("_nickname", user._nickname);
            jo.put("_priority", user._priority);
            jo.put("_roomId", user._roomId);

            // JsonArray에 추가
            memberJsonArray.add(jo);

        }

        SendJsonArrayWithBytes(memberJsonArray, dos);
    }

    /** Client 로부터 채팅 객체를 받아 방에 있는 모든 유저들에게 송신한다 */
    public void ReceiveChat(JSONObject jo, DataOutputStreamStorage dosStorage) {
        System.out.println("RoomManager: ReceiveChat() has been called");

        String userId = (String) jo.get("_userId");
        String nickname = (String) jo.get("_nickname");
        String time = (String) jo.get("_time");
        String chatMsg = (String) jo.get("_chatText");

        // BroadcastData 객체 만들기
        BroadcastData broadcastData = new BroadcastData(CODE_CHATMSG, userId, nickname, chatMsg, time);

        // userId 로 GameUser 객체 얻기
        GameUser sentUser = users.get(userId);

        // sentUser가 속한 roomId
        String roomId = sentUser._roomId;

        // roomId 로 GameRoom 객체 얻기
        // targetRoom 안에 속한 멤버들에게 메시지를 Broadcast 하기 위함.
        GameRoom targetRoom = rooms.get(roomId);

        // 속한 방으로 broadcast
        Broadcast(targetRoom, broadcastData, dosStorage);

    }

    // 타겟 방에 속한 모든 멤버들에게 채팅 내용을 보내는 함수
    public void Broadcast(GameRoom targetRoom, BroadcastData broadcastData, DataOutputStreamStorage dosStorage) {

        Map<String, GameUser> members = targetRoom._members;

        JSONArray ja = new JSONArray();
        Iterator<String> keys = members.keySet().iterator();
        while (keys.hasNext()) {
            // 여기서 key 는 타겟룸에 속한 멤버들의 userId 이다
            String key = keys.next();

            // 만약에 key 가 이 메시지를 보낸 userId 라면 생략한다.
            if (key.equals(broadcastData._sentUserId)) {
                System.out.println("이 메시지를 보낸 본인입니다: " + key);
                // continue;
            }

            System.out.println("RoomManager; Broadcast(): targetRoom's UserId: " + key);

            // dosStorage 로부터 userId를 통해 dos 를 찾는다.
            DataOutputStream dos = dosStorage._dataOutputStreams.get(key);

            JSONObject jo = new JSONObject();

            jo.put("_dataTypeCode", broadcastData._dataTypeCode);
            jo.put("_sentNickname", broadcastData._sentNickname);
            jo.put("_chatMsg", broadcastData._chatMsg);
            jo.put("_time", broadcastData._time);

            // JsonArray에 추가
            ja.add(jo);

            System.out.println("RoomManager; Broadcast; jsonArray string: " + ja.toString());

            // dos와 ja를 매개로 JsonArray를 Client 로 보낸다.
            SendJsonArrayWithBytes(ja, dos);

        }

    }

    public void QuitRoom(JSONObject jo) {
        System.out.println("QuitRoom Funciton has been called");
        String userId = (String) jo.get("_userId");

        // 1. 방에 퇴장처리한다.
        // 1-1. userId 에 해당하는 user 를 찾는다.
        GameUser targetUser = users.get(userId);

        // 1-2. targetUser 의 GameRoom을 찾는다
        String roomId = targetUser._roomId;
        GameRoom targetRoom = rooms.get(roomId);

        // targetRoom 의 memNum -1
        targetRoom._memNum--;

        // 1-3. targetRoom 의 MemNum이 0이었을 경우에 DestroyRoom 호출
        if (targetRoom._memNum == 0) {
            DestroyRoom(roomId);
            return;
        }

        // targetRoom 내의 member에서 본인을 제거한다
        targetRoom._members.remove(userId);

        // GameRoom 내 모든 유저의 방장 priority는 감소한다
        Iterator<String> keys = targetRoom._members.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            GameUser u = targetRoom._members.get(key);

            u._priority--;
        }

        targetUser._roomId = "0";

    }

    public void DestroyRoom(String roomId) {
        rooms.remove(roomId);
    }

}