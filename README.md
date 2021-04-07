Server codes of project_catch.

# PROJECT CATCH (SERVER)

-----
## :wrench:서버 구조
<center><img src="https://github.com/HeejeongKateJung/Project_catch_server/blob/master/image_readme/server_structure.PNG" width="600px"></center>
<center> 로비, 게임 로직, 인증, 핑체크 로직이 분리되어 있음 </center>

-----
## :loudspeaker:통신 프로세스
<center><img src="https://github.com/HeejeongKateJung/Project_catch_server/blob/master/image_readme/game_process.PNG" width="600px"></center>

-----
## :clipboard:Flow Chart (진행중)
#### 1. 구글 로그인 / 회원가입 기능 구현
- 구글 로그인 API 
- 인증서버에 사용자 정보 전송, 인증

#### 2. Client - Server 간 Ping 체크 모듈 구현 (Heartbeat)
- 5초에 한번씩 ping 보냄
- 4번 이상 ping 도착 지연 시 비정상 연결로 간주

#### 3. 로비 인터페이스 구현
- 다른 플랫폼 간 객체 정보를 유지하여 통신하기 위해서 Json string 직렬화, 역직렬화 구현
- 패킷 Request Code 에 따라 다른 동작을 하게 

#### 4. 로비 채팅 기능 구현 (예정)
- 로비에 접속한 사용자들끼리 채팅 가능
- 채팅 로그 저장

#### 5. 유저 상태, 방 상태 저장 구조 구현 (예정)

#### 6. 게임 플레이 구현 (예정)



