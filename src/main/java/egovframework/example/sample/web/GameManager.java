package egovframework.example.sample.web;

import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.socket.TextMessage;

public class GameManager {		
	public ArrayList<User> userlist = new ArrayList<User>();
	public CardManager cardManager = new CardManager();
	//public User reuser = new User();
	int gameId;//게임로그에 저장되는 게임번호
	int gu;//1구 2구 3구 4구
	String GameMode="대기";	
	//Calendar startTime;
	long timerStartTime = 0;//객체로 만들면 좋다
	int whosturn = 0;//누구차례인지 
	int turncnt = 0;
	
	int totalmoney = 0;
	int prebetmoney =0 ;//이전 사람의 베팅머니
	int preTotalBetmoney=0;//이전 사람의 현재 구의 총 배팅머니/ 콜금액 계산용.
	int startTime;
	int timer = -1;
	int dealerSeatNum = -1;//누가딜러인지 유저인덱스 저장
	int endtime;
	
	int[][] cardarr;// 사람별 카드 소유
	
	Card card1;
	Card card2;
	Card card3;
	Card card4;
	Card card5;

	Room room;
	
	public void init(){
		cardManager.init();
		gu=0;//1구 2구 3구 4구
		GameMode="대기";	
		timerStartTime = 0;//객체로 만들면 좋다
		whosturn = 0;//누구차례인지 
		turncnt = 0;//안쓰기로함
		
		totalmoney = 0;
		prebetmoney =0 ;//이전 사람의 베팅머니
		preTotalBetmoney=0;//이전 사람의 현재 구의 총 배팅머니/ 콜금액 계산용.
		startTime=0;
		timer = -1;
		dealerSeatNum = -1;//누가딜러인지 유저인덱스 저장
		endtime=0;
		System.out.println("게임초기화");
	}
	
	public GameManager(Room room){
		this.room = room;
	}

	public int getGameId(){
		return gameId;
	}
	
	public void setGameId(int gameid){
		this.gameId = gameid;
	}
	
	void resetGuBetmoney(){
		for(User u : userlist)
			u.currentGuBetMoney = 0;
	}
	void sendRoom(JSONObject obj){
		for(User u : userlist)
			try {
				u.session.sendMessage(new TextMessage(obj.toJSONString()));
			} catch (IOException e) {
				e.printStackTrace();
			}						
	}

	void startCheck(User user, ArrayList<User> userlist){
		if (user != null)
			user.setGameStat("OK");
		int cnt = 0;
		for(User u : userlist){
			if(u.getGameStat()=="OK"){
				cnt++;
			}
		}
		if(cnt>=3) {			
			startTime = SocketHandler.second;
//			GameMode="checkstart";
			changeGameMode("대기");
		}

	}

	boolean isPlayable()
	{
		int cnt = 0;
		for(User u : userlist){
			if(u.getGameStat()=="OK"){
				cnt++;
			}
		}
		if(cnt>=3) {			
			return true;
		}
		return false;
	}


	void DealerSeatSetting(){
		dealerSeatNum++;
		if(dealerSeatNum == userlist.size()){
			dealerSeatNum = 0;
		}
	}
	void startSetting(){
		gu = 1;
		setGameId(SocketHandler.GameIdxAdder());
		
		SocketHandler.insertLog(getGameId(), "gamestart", -1, -1, -1, "게임시작", -1, -1);
		whosturn = 0;
		turncnt = 0;
		totalmoney = 0;

		cardarr = new int[userlist.size()][7];

		for(User u : userlist){
			SocketHandler.insertLog(getGameId(), "join", u.uidx , u.balance , u.seat , "참가머니", 1000, -1);
			u.init();
			
			u.betmoney+=1000;
			totalmoney+=1000;
		}
		
		
	}
	int getDealerSeat(){
		return  (dealerSeatNum % userlist.size() );
	}
	boolean isStartTime(){
		return SocketHandler.second-startTime>3 ;
	}
	boolean isEndGame(){
		return SocketHandler.second-endtime>10 ;
	}
	void changeGameMode(String mode){
		System.out.println("GameMode:"+mode);
		GameMode = mode;
	}
	void setRoomStartTime( int st){
		startTime = st; 
	}
	void setRoomEndTime( int et){
		endtime = et; 
		System.out.println("대기시간 추가");
	}

	void checkStartGame(){
		if(GameMode.compareTo("checkstart")==0){
			startSetting();
			DealerSeatSetting();
			notifyGameStart();
			cardManager.init();
			changeGameMode("twoCard");
		}
		
		if(GameMode.compareTo("twoCard")==0)
		{
			if( isStartTime() ){
				setRoomStartTime(SocketHandler.second);
				drawCard();
				resetGuBetmoney();
				changeGameMode("sbBet");
				whosturn = getDealerSeat()+1; // 첫 베팅 하는 사람은 딜러 다음 사람
			}

		}

		if(GameMode.compareTo("sbBet")==0)
		{
			if(SocketHandler.second-startTime>3){
				changeGameMode("sbBeted");
				sbBet();
			}
		}
		//===================
		if(GameMode.compareTo("bbBet")==0){			
			if(SocketHandler.second-startTime>3){
				changeGameMode("bbBeted");
				bbBet();
			}
		}

		if(GameMode.compareTo("nmBet")==0){	
			if(SocketHandler.second-startTime>6){
				changeGameMode("showBetPan");
				showBetPan();
			}
		}
		if(GameMode.compareTo("THEEND")==0){
			System.out.println("gameMode THEEND");
			setRoomEndTime(SocketHandler.second);
			showResult();
		}

		if(GameMode.compareTo("showResult")==0){
			if( isEndGame() ){
				changeGameMode("대기");
				setRoomEndTime(SocketHandler.second);
			}
		}
		if(GameMode.compareTo("대기")==0){
			if (isPlayable() && SocketHandler.second-endtime>5 )
			{
				startTime = SocketHandler.second;
				changeGameMode("checkstart");
			}
			
		}



	}

	void checkTimerGame(){
		//배팅시간 지났음
		if( GameMode.compareTo("nmBet")==0 || GameMode.compareTo("showBetPan")==0 || GameMode.compareTo("THEFLOP")==0 || GameMode.compareTo("THETURN")==0 || GameMode.compareTo("THERIVER")==0){
			if(timer!=-1 && SocketHandler.second-timer>8){ // 자기턴 타임아웃 시간 8초로.	
				for(User u : userlist){
					if(u.seat==getWhoTurn() ){
						JSONObject obj = new JSONObject();						
						//방접속자에게 보냄
						obj.put("cmd","timeOut");
						
						try {
							u.session.sendMessage(new TextMessage(obj.toJSONString()));
							//bet(room.ridx, u, 1);//시간초안에 배팅 안하면 자동 1번 배팅 처리
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						

					}
				}										
			}
		}
	}

	public void notifyGameStart() {

		System.out.println("start dealerNum:"+getDealerSeat() );
		for(User u : userlist){
			JSONObject obj = new JSONObject();						
			//방접속자에게 보냄
			obj.put("cmd","startGame");
			System.out.println("totalmoney:"+totalmoney);
			obj.put("smoney",  ""+totalmoney);
			obj.put("roompeople",""+ userlist.size() );
			obj.put("dealer",""+ getDealerSeat() );
			obj.put("smallblind",""+ getDealerSeat() + 1 );
			obj.put("bigblind",""+ getDealerSeat() + 2 );
			try {
				u.session.sendMessage(new TextMessage(obj.toJSONString()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}

	public void drawCard(){
		cardManager.shuffleCard();

		JSONObject obj = new JSONObject();
		//JSONArray j = new JSONArray();
		//카드 두장씩 세팅
		for(int k =0; k<userlist.size(); k++){
			userlist.get(k).setCard(cardManager.popCard(), cardManager.popCard());
			//JSONObject item = new JSONObject();
			obj.put("cmd", "giveTwoCard");
			obj.put("seat", userlist.get(k).seat);
			obj.put("card1", userlist.get(k).card1.cardcode);
			obj.put("card2", userlist.get(k).card2.cardcode);
			//j.add(item);
			//System.out.println("userlist.size() 의 값 : " + userlist.size());
			//System.out.println("userlist.get(k).uidx 의 값 : " + userlist.get(k).uidx);
			//System.out.println(k + "번 째 유저가 받은 2장의 카드");
			//System.out.println(userlist.get(k).card1.cardcode);
			//System.out.println(userlist.get(k).card2.cardcode);

			cardarr[k][0] = userlist.get(k).card1.cardcode;
			cardarr[k][1] =userlist.get(k).card2.cardcode;
			SocketHandler.insertLog(getGameId(), "twoCard", userlist.get(k).uidx, userlist.get(k).card1.cardcode, userlist.get(k).card2.cardcode, "", -1, -1);
			//System.out.println( k + "번 째 유저의 0 번 째 카드 : " + cardarr[k][0]);
			//System.out.println( k + "번 째 유저의 1 번 째 카드 : " + cardarr[k][1]);


			try {
				userlist.get(k).session.sendMessage(new TextMessage(obj.toJSONString()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		//obj.put("twocardlist", j);

	}

	public void giveTwoCard() {
		for(User u : userlist){
			JSONObject obj = new JSONObject();						
			//방참여자에게 보냄
			obj.put("useridx","참여자인덱스");
			obj.put("protocol","RoomJoinOk");
			try {
				u.session.sendMessage(new TextMessage(obj.toJSONString()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public int getWhoTurn(){
		return whosturn%userlist.size();
	}
	public void sbBet(){
		JSONObject obj = new JSONObject();
		whosturn = getDealerSeat()+1;
		obj.put("cmd","sbBet");
		obj.put("whosturn", ""+getWhoTurn() );
//		obj.put("whosturn",userlist.get(whosturn).uidx);
		sendRoom(obj);
		System.out.println("sbBet하시오 :"+getWhoTurn() );
	}

	public void bbBet(){
		JSONObject obj = new JSONObject();
		obj.put("cmd","bbBet");
		obj.put("whosturn", ""+getWhoTurn() );
		sendRoom(obj);
		timer = SocketHandler.second;
		System.out.println("bbBet하시오 :"+getWhoTurn() );
	}

	public void showBetPan(){

		JSONObject obj = new JSONObject();
		obj.put("cmd","bet");
		if( turncnt == 0 )
			obj.put("betenable", "1,1,1,0,0,1,1,1");//체크/폴드/삥/콜/따당/하프/풀/맥스
		else
			obj.put("betenable", "0,1,0,1,1,1,1,1");//체크/폴드/삥/콜/따당/하프/풀/맥스
		obj.put("whosturn", getWhoTurn() );
		timer = SocketHandler.second;
		System.out.println("다음 사람 베팅 하시오:"+getWhoTurn() );
		sendRoom( obj);
	}

	public int thisTurnMoneyCompute(int kind,int mybetmoney){

		if(kind==0) // 다이
			return 0;
		else if(kind==1) // 삥
			return room.defaultmoney;
		else if(kind==2)// 콜
			return preTotalBetmoney - mybetmoney;
		else if(kind==3)// 따당? 
			return prebetmoney*2;
		else if(kind==4)// 하프 
			return totalmoney/2;
		else if(kind==5)// 풀 
			return totalmoney;
		else if(kind==6)// 맥스
			return room.maxmoney;
		else
			return 1;

	}

	boolean isGuBetEnd(){		
		int money = -1;
		for(User uu : userlist){
			if(uu.die == true) continue;//밑에 실행하지않고 다음 for문 검사
			if( money != -1 && uu.betmoney != money)
				return false;//배팅금액이 다르다
			money = uu.betmoney;
		}
		return true;//배팅금액이 똑같은경우 배팅끝
	}
	public void bet(int roomidx, User u, int betkind){			
		//System.out.println("========= whosturnUseridx:"+whosturnUseridx +" uidx:"+u.uidx+" u seat:"+u.seat);
		if( getWhoTurn() != u.seat ){
			System.out.println(getWhoTurn()+" 잘못된 유저의 BET 차례 "+u.seat);
			return;
		}

		if(betkind==0) u.die = true;
		int tmo = thisTurnMoneyCompute(betkind , u.betmoney);
		System.out.println("{ tmo:"+tmo +" u.betmoney:"+u.betmoney+" prebetmoney:"+prebetmoney+" preTotalBetmoney:"+preTotalBetmoney);
		u.betmoney += tmo ;//나의 배팅금액 현재돈+배팅금액
		prebetmoney = tmo;
		preTotalBetmoney = u.betmoney;
		//System.out.println("dbg6 u.betmoney:"+u.betmoney);

		totalmoney +=tmo;
		
		//배팅한 사람 돈 차감 시키기!!!
		u.balance -= tmo;
		
		System.out.println("BET whosturn: "+whosturn+"("+getWhoTurn()+") Game:"+GameMode+" betkind:"+betkind+" totalmoney:"+totalmoney+" 잔액:"+u.balance +"   :::" + "{ tmo:"+tmo +" u.betmoney:"+u.betmoney+" prebetmoney:"+prebetmoney+" preTotalBetmoney:"+preTotalBetmoney);
		SocketHandler.insertLog(getGameId(), "bet", u.uidx , u.betmoney , u.balance , "배팅액:"+tmo+", total:"+totalmoney , betkind, whosturn );
		JSONObject obj = new JSONObject();
		if(GameMode.compareTo("sbBeted")==0)	{
			obj.put("cmd", "sbBetsuc");
			//System.out.println("sb가 자동베팅했습니다.");
			setRoomStartTime( SocketHandler.second );
			changeGameMode("bbBet");
		}
		else if(GameMode.compareTo("bbBeted")==0)	{
			obj.put("cmd", "bbBetsuc");
			//System.out.println("bb가 자동베팅했습니다.");
			changeGameMode("nmBet");
		}
		else{
			obj.put("cmd", "betsuc");
			//System.out.println("<< 베팅 성공 >>");
		}
		turncnt++;
		whosturn++;//다음사람배팅
		int k=0;
		while( checkDieturn(getWhoTurn()) ){
			k++;
			if( k>10){//9명이상이니 여기에 들어오면 에러 무한루프 체크
				SocketHandler.insertLog(getGameId(), "error", -1, -1, -1, "DieTurnCheckError", -1, -1);
				break;
			}
			whosturn++;			
		}
		
		boolean isBetEnd = isGuBetEnd();

		obj.put("totalmoney", totalmoney);
		obj.put("callmoney", "" + (preTotalBetmoney - u.betmoney) );
		obj.put("myBetMoney", u.betmoney );
		obj.put("balance", u.balance);
		obj.put("betkind", betkind);
		obj.put("seat", u.seat);//금방배팅한 사람
		obj.put("nextwho", getWhoTurn() );//이제 배팅할 사람의 번호
		obj.put("betenable", "0,1,0,1,1,1,1,1");//체크/폴드/삥/콜/따당/하프/풀/맥스
		obj.put("gu", gu );
		if( isBetEnd )
			obj.put("betEnd", "1");//마지막 베팅인지 체크
		else
			obj.put("betEnd", "0");//마지막 베팅인지 체크

		timer = SocketHandler.second;

		//베팅 성공 정보를 전송
		sendRoom(obj);
		
		//용우 : 아래 구조 이상함 배팅성공 패킷 보낸 후 아무 패킷을 받지않고 또 패킷을 보냄, 차라리 위에 첫번째 패킷 보낼때 다같이 보낼까? 
		if( isBetEnd ){
			System.out.println("DBG 3 : 전원 베팅 끝");

			if(GameMode.compareTo("showBetPan")==0)	{
				gu++;//2구
				showThreeCard();
			}
			else if(GameMode.compareTo("THEFLOP")==0){			
				gu++;//3구
				TheTurn();
			}
			else if(GameMode.compareTo("THETURN")==0){
				gu++;//4구
				TheRiver();
			}
			else if(GameMode.compareTo("THERIVER")==0){
				TheEnd();
			}
			return;
		}


	}
	
	public boolean checkDieturn(int who){
		if(userlist.get(who).die == true)
			return true;
		else
			return false;
	}
	
	public void showThreeCard(){	
		timer = SocketHandler.second+2;
		whosturn=getDealerSeat()+1;
		turncnt=0;
		System.out.println("showThreeCard SB:"+whosturn);
		JSONObject obj = new JSONObject();		
		changeGameMode("THEFLOP");
		card1=cardManager.popCard();
		card2=cardManager.popCard();
		card3=cardManager.popCard();

		obj.put("cmd","THEFLOP");
		obj.put("card1", card1.cardcode);
		obj.put("card2", card2.cardcode);		
		obj.put("card3", card3.cardcode);
		obj.put("whosturn", getWhoTurn() );
		obj.put("betenable", "1,1,1,0,0,1,1,1");//체크/폴드/삥/콜/따당/하프/풀/맥스
		SocketHandler.insertLog(getGameId(), "THEFLOP", -1, card1.cardcode, card2.cardcode, "", card3.cardcode, -1);
		for(int k =0; k<userlist.size(); k++){
			cardarr[k][2] = card1.cardcode;
			cardarr[k][3] = card2.cardcode;
			cardarr[k][4] = card3.cardcode;
			//System.out.println( k + "번 째 유저의 2 번 째 카드 : " + cardarr[k][2]);
			//System.out.println( k + "번 째 유저의 3 번 째 카드 : " + cardarr[k][3]);
			//System.out.println( k + "번 째 유저의 4 번 째 카드 : " + cardarr[k][4]);
		}
		sendRoom( obj);
	}

	public void TheTurn(){
		timer = SocketHandler.second;
		turncnt = 0;
		//timer=-1;
		whosturn=getDealerSeat()+1;

		JSONObject obj = new JSONObject();
		GameMode = "THETURN";

		card4=cardManager.popCard();

		obj.put("cmd","THETURN");
		obj.put("card4", card4.cardcode);
		obj.put("whosturn",getWhoTurn() );
		SocketHandler.insertLog(getGameId(), "THETURN", -1, card4.cardcode, -1, "", -1, -1);
		for(int k =0; k<userlist.size(); k++){
			cardarr[k][5] = card4.cardcode;
			//System.out.println( k + "번 째 유저의 4 번 째 카드 : " + cardarr[k][5]);
		}

		for(User u : userlist){		
			try {
				u.session.sendMessage(new TextMessage(obj.toJSONString()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void TheRiver(){
		timer = SocketHandler.second;
		turncnt = 0;
		//System.out.println("theriver================ 마지막 카드 공개");
		//timer=-1;
		whosturn=getDealerSeat()+1;

		JSONObject obj = new JSONObject();
		GameMode = "THERIVER";

		card5=cardManager.popCard();		
		obj.put("cmd","THERIVER");
		obj.put("card5", card5.cardcode);
		obj.put("whosturn",getWhoTurn() );
		SocketHandler.insertLog(getGameId(), "THERIVER", -1, card5.cardcode, -1, "", -1, -1);
		for(int k =0; k<userlist.size(); k++){
			cardarr[k][6] = card5.cardcode;
			//System.out.println( k + "번 째 유저의 6 번 째 카드 : " + cardarr[k][6]);
		}

		for(User u : userlist){		
			try {
				u.session.sendMessage(new TextMessage(obj.toJSONString()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void TheEnd(){
		System.out.println("TheEnd================ ");

		/*whosturn=getDealerSeat();
		whosturnUseridx = userlist.get(whosturn%userlist.size()).uidx;*/

		JSONObject obj = new JSONObject();
		changeGameMode("THEEND");				
		obj.put("cmd","THEEND");
		SocketHandler.insertLog(getGameId(), "gameEnd", -1, totalmoney, -1, "게임 끝" , -1, -1);
		for(User u : userlist){		
			try { 
				u.session.sendMessage(new TextMessage(obj.toJSONString()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//GameMode = "showResult";	

	}	
	

	//true 이면 2 스트레이트 플러시 : 9 10 j q k 가  모두 하트 
	public boolean checkStraightFlush(int arr[]){
		if( checkStraight(arr) == true && checkFlush(arr) ==true)
			return true;
		else
			return false;
	}
	
	//4 풀하우스 트리플페어: 트리플+페어  777 22
	public boolean checkFullHouse(int arr[]){
		int []cNum = new int[13]; 
		int triple = 0;
		int pair = 0;
		for(int i=0; i<7; i++){
			cNum[ arr[i]%13 ]++;
		}
		
		for(int i=0;i<13;i++){
			if(cNum[i] == 3) triple = 1;
			if(cNum[i] == 2) pair = 1; 				
		}
		
		if(triple==1 && pair==1) return true;
		else return false;
	}
	//9페어:동일한숫자 한쌍   8투페어:동일한숫자 두쌍  7트리플: 동일한세장      3포카드 : 동일한숫자 4장 
	public int checkAllpair(int arr[]){
		int level = -1;
		int twopair = 0;
		int []cNum = new int[13]; 
		for(int i=0; i<7; i++){
			cNum[ arr[i]%13 ]++;
			if(cNum[ arr[i]%13 ] >= 4) level = 3;//포카드
			else if(cNum[ arr[i]%13 ] >= 3) level = 7;//트리플
			else if(cNum[ arr[i]%13 ] >= 2){//페어 , 투페어
				twopair++;
				if(twopair==2) level = 8; // 투페어
				else level = 9; 
			}
			
		}
		return level;
	}
	
	//true 이면  6 스트레이트 : 9 10 j q k
	public boolean checkStraight(int arr[]){
		int ct = 0;
		int pre = 0;
		for(int i=0; i<7; i++){
			if(i!=0 && pre-1 != arr[i] )
				ct=0;
			else{
				ct++;
				if(ct>=5)	return true;				
			}
		}
		return false;
	}
	//true 이면 5 플러시  : 하트5장
	public boolean checkFlush(int arr[]){
		int []shape = new int[4]; 
		for(int k=0; k<7; k++){
			shape[ (int)(arr[k]/13) ]++;
			if( shape[ (int)(arr[k]/13) ] >= 5 ) return true;
		}
		return false;
	}
	
	//10 탑카드  : 제일 큰숫자 한장 리턴
	public int checkTopCard(int arr[]){		
		int pre = -1;		
		for(int i=0; i<7; i++){
			if( pre < (arr[i]%13) )
				pre = arr[i]%13;
		}			
		return pre;
	}	
	
	
	public void showResult(){
		whosturn=0;
		System.out.println("SHOW RESULT ");
		//결과 계산하기
		// 유저들의 카드 목록 2차원 배열 출력
		int winSeat=-1,wlv=10000;
		for(int k = 0; k<cardarr.length; k++){
			int []card = new int[7]; 
			System.out.println(k + "번째 유저의 카드 목록 2차원 배열");
			for(int i = 0; i<cardarr[k].length; i++){				
				card[i] = cardarr[k][i];				
			}
			
			//3포카드 7트리플 8투페어 9페어			
			int lv = checkAllpair(card);
			
			//스트레이트 플러시 2
			if(checkStraightFlush(card)==true){
				if(lv>2) lv=2;
			}else if(checkFullHouse(card)==true){
				if(lv>4) lv=4;
			}else if(checkFlush(card)==true){
				if(lv>5) lv=5;
			}else if(checkStraight(card)==true){
				if(lv>6) lv=6;
			}else if(checkStraight(card)==true){
				if(lv>10) lv=10;
				userlist.get(k).topcard = checkTopCard(card);
			}
			
			if(wlv > lv ){
				winSeat = k;
				wlv = lv;
			}
			userlist.get(k).level = lv;
		}
		

		userlist.get(winSeat).balance+=totalmoney;
		JSONObject obj = new JSONObject();
		obj.put("cmd","showResult");
		obj.put("winnerbalance",""+userlist.get(winSeat).balance);
		obj.put("winmoney",""+this.totalmoney);
		obj.put("winSeat",""+winSeat);
		
		JSONArray j = new JSONArray();
		for(int i=0; i<userlist.size(); i++){
			JSONObject item = new JSONObject();
			item.put("seat",""+ userlist.get(i).seat);
			item.put("card1",""+ userlist.get(i).card1);
			item.put("card2",""+ userlist.get(i).card2);
			item.put("die",""+ userlist.get(i).die);
			j.add(item);
		}
		obj.put("cardlist", j);
		System.out.println("SHOW RESULT  SEND ");
		sendRoom(obj);
		changeGameMode("showResult");
	}
}
