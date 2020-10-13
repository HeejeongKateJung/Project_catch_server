import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.lang.invoke.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class RoomManagementServer {

        //요청 코드
    public static final int CODE_ENTERUSER = 11;
    public static final int CODE_MAKEROOM = 21;
    public static final int CODE_ENTERROOM = 22;
    public static final int CODE_QUITROOM = 23;
    public static final int CODE_REFRESHROOMLIST = 31;
    public static final int CODE_REFRESHUSERLIST = 51;
    public static final int CODE_SENDCHAT = 41;

    //Socket 으로부터 4byte 만큼의 데이터 사이즈 수신을 받으면 그 사이즈만큼 데이터를 받게 된다.
    private static byte[] getRecieve(DataInputStream dis) throws IOException{
        byte[] buffer = new byte[4];


        //4byte 크기의 data size 를 받는다.
        dis.read(buffer, 0, 4);

        //byte[]를 byteBuffer로 만든다ㅏ.
        ByteBuffer data = ByteBuffer.wrap(buffer);
        data.order(ByteOrder.LITTLE_ENDIAN);

        int size = data.getInt();
        buffer = new byte[size];

        //일단 사이즈를 받고나서 데이터를 받으므로 offset 은 0이다.
        dis.read(buffer, 0, size);

        return buffer;
    }

    private static void sendBytes(DataOutputStream dos, String msg) throws IOException{
        byte[] out = new byte[100];
        out = msg.getBytes();
        dos.write(out);
        dos.flush();
    }

    public static void main(String args[])
    {  

        //서버용 싱글 스레드 풀 생성
        ExecutorService service = Executors.newSingleThreadExecutor();
        //수신용 클라이언트 쓰레드풀 생성
        ExecutorService clientService = Executors.newFixedThreadPool(10);
    
        service.submit(() ->{
            //8000포트로 서버 대기
            try(ServerSocket server = new ServerSocket()) {
                InetSocketAddress ipep = new InetSocketAddress(8000);
                server.bind(ipep);
                System.out.println("서버 대기중...");

                //RoomManager 생성 -> 유저들과 방들을 관리하는 객체이므로 while 문 밖에 생성
                RoomManager roomManager = new RoomManager();

                //DataOutputStreamStorage 생성 -> broadcast 를 위한 dos 를 저장하기 위함
                DataOutputStreamStorage dosStorage = new DataOutputStreamStorage();
                
                while(true){
                    //클라이언트 접속
                    Socket client = server.accept();
                    System.out.println("클라이언트 접속!");
                    
                    clientService.submit(() -> {
                        try{
                            //DataInputStream, DataOutputStream을 받는다.
                            DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                            DataInputStream dis = new DataInputStream(client.getInputStream());
    
                            //getRecieve 함수로 데이터를 받는다

                            while(true){
                                String jsonString = new String(getRecieve(dis), Charset.forName("uTF-8"));
                                
                                //JSONString -> Json object
                                JSONParser parser = new JSONParser();
                                Object obj = parser.parse(jsonString);
                                JSONObject jsonObj = (JSONObject) obj;

                                //JSON 데이터의 코드를 확인한다.
                                String requestCode = (String)jsonObj.get("_requestCode");
                                System.out.println("request code: " + requestCode);
                                int _requestCode = Integer.parseInt(requestCode);
                                



                                //request code 에 따라 방관리 기능이 달라진다.
                                switch(_requestCode) {
                                    case CODE_ENTERUSER:
                                        //유저가 접속한 뒤 유저의 정보를 서버가 알기위함
                                        String enterUserResult = roomManager.EnterUser(jsonObj);

                                        //유저가 접속하자마자 후에 사용할 dos 를 저장하는 storage 에
                                        //userId 와 함께 저장한다.
                                        String userId = (String)jsonObj.get("_userId");
                                        dosStorage._dataOutputStreams.put((String)jsonObj.get("_userId"), dos);

                                        //유저 접속 처리 결과를 송신한다.
                                        sendBytes(dos, enterUserResult);

                                        break;
                                    case CODE_MAKEROOM:
                                        String roomId = roomManager.MakeRoom(jsonObj);
                                        //할당한 방 Id 를 리턴한다.
                                        sendBytes(dos, roomId);
                                        break;

                                    case CODE_ENTERROOM:
                                        String enterRoomResult = roomManager.EnterRoom(jsonObj, dosStorage);
                                        sendBytes(dos, enterRoomResult);
                                        break;
                                    case CODE_REFRESHROOMLIST:
                                        //방 리스트 송신 함수
                                        String refreshRoomListResult = roomManager.SendRoomList(dos);
                                        break;

                                    case CODE_REFRESHUSERLIST:
                                        break;

                                    
                                    case CODE_SENDCHAT:
                                        roomManager.ReceiveChat(jsonObj, dosStorage);
                                        break;
                                    
                                    case CODE_QUITROOM:
                                        roomManager.QuitRoom(jsonObj);
                                        break;

                                    
                                    
                                    
                                        

                                    // default:
                                    //     System.out.println("Request code 가 맞지 않습니다");
                                    //     System.out.println("requestcode: " + _requestCode);
                                    //     break;
                                }
                            }
    
                            
                            
    
                            
                        }catch(Throwable e){
                            e.printStackTrace();
                        }finally{
                            //수신용 소켓을 닫는다.
                            try{
                                client.close();
                            }catch(Throwable e){
                                e.printStackTrace();
                            }
                        }
                    });
                }

            }catch (Throwable e){
                e.printStackTrace();
            }

            
        });
    
    }
}