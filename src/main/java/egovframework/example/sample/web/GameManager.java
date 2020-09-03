package egovframework.example.sample.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
	
//	int startTime;
//	int endtime;
	int workTime=0;
	
	int timer = -1;
	int dealerSeatNum = -1;//누가딜러인지 유저인덱스 저장
	int lastCmdSecond=1000000;//마지막 명령을 받은 시간, 1분  이상 누구도 명령을 내리지 않은 상태인데 방이 대기 상태가 아니라면 에러난 상태이므로 방을 강제 초기화 시켜야 함. 
	
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
		workTime = SocketHandler.second;
		timer = -1;
		dealerSeatNum = -1;//누가딜러인지 유저인덱스 저장
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
		sendList(obj,userlist);
	}
	void sendList(JSONObject obj, ArrayList<User> lst){
		for(User u : lst)
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
			setWorkTime( );
			changeGameMode("대기");
		}

	}

	boolean isPlayable(){
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
			SocketHandler.insertLog(getGameId(), "join", u.uidx , u.balance , u.seat , "참가머니", room.defaultmoney , -1);
			u.init();
			
			u.betmoney+=room.defaultmoney;
			u.balance-=room.defaultmoney;
			totalmoney+=room.defaultmoney;
		}
		
		
	}
	int getDealerSeat(){
		return  (dealerSeatNum % userlist.size() );
	}
/*	boolean isStartTime(){
		return SocketHandler.second-startTime>2 ;
	}
	boolean isEndGame(){
		return SocketHandler.second-endtime>5 ;
	}
	*/
	
	
	void changeGameMode(String mode){
		System.out.println("GameMode:"+mode);
		GameMode = mode;
	}
/*	void setRoomStartTime( int st){
		startTime = st; 
	}
	void setRoomEndTime( int et){
		endtime = et; 
		System.out.println("대기시간 추가");
	}
	*/
	void setWorkTime(){
		workTime = SocketHandler.second; 
	}
	boolean checkCmdTime(int timecmp){
		if(SocketHandler.second > workTime+timecmp)
			return true;
		return false;
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
			if(  checkCmdTime(3)   ){
				setWorkTime();
				drawCard();
				resetGuBetmoney();
				changeGameMode("sbBet");
				whosturn = getDealerSeat()+1; // 첫 베팅 하는 사람은 딜러 다음 사람
			}

		}

		if(GameMode.compareTo("sbBet")==0)
		{
			if( checkCmdTime(1) ){
				changeGameMode("sbBeted");
				sbBet();
			}
		}
		//===================
		if(GameMode.compareTo("bbBet")==0){			
			if( checkCmdTime(1) ){
				changeGameMode("bbBeted");
				bbBet();
			}
		}

		if(GameMode.compareTo("nmBet")==0){	
			if(  checkCmdTime(4)  ){
				changeGameMode("showBetPan");
				showBetPan();
			}
		}
		if(GameMode.compareTo("THEEND")==0){
			setWorkTime();
			showResult();
			changeGameMode("showResult");
		}
		
		if(GameMode.compareTo("showResult")==0){
			if( checkCmdTime(5) ){
				changeGameMode("대기");
				checkOutUser();
				setWorkTime(); 
			}
		}
		
		if(GameMode.compareTo("대기")==0){
			//System.out.println("isPlayable():"+isPlayable()+" checkCmdTime(3):"+checkCmdTime(3));
			if (isPlayable() && checkCmdTime(6) ){
				setWorkTime();
				changeGameMode("checkstart");
			}
		}
	}
	
	void checkOutUser(){
		//돈이 없는 유저 내보내기
		ArrayList<User> rmlist=new ArrayList<User>();
		for(User u : userlist) 
			if( u.balance == 0)				
				rmlist.add(u);
		for(User u:rmlist)
			room.leave(u);
		room.notifyRoomUsers();
		JSONObject obj2 = new JSONObject();
		obj2.put("cmd","roomout");
		sendList(obj2,rmlist);//나간 유저에게는 당신 돈 없어서 나갔다는 패킷 보내줌
	}

	void checkTimerGame(){
		//배팅시간 지났음
		if( GameMode.compareTo("nmBet")==0 || GameMode.compareTo("showBetPan")==0 || GameMode.compareTo("THEFLOP")==0 || GameMode.compareTo("THETURN")==0 || GameMode.compareTo("THERIVER")==0){
			if(timer!=-1 && SocketHandler.second - timer > 8){ // 자기턴 타임아웃 시간 8초로.				
				for(User u : userlist){
					if(u.seat==getWhoTurn() ){
						JSONObject obj = new JSONObject();						
						//방접속자에게 보냄
						obj.put("cmd","timeOut");
						
						try {
							u.session.sendMessage(new TextMessage(obj.toJSONString()));
						} catch (IOException e) {
							e.printStackTrace();
						}						

					}
				}										
			}
		}
	}

	public void notifyGameStart() {//게임 시작을 알림
		JSONObject obj = new JSONObject();						
		obj.put("cmd","startGame");
		obj.put("smoney",  ""+totalmoney);
		obj.put("maxmoney",  ""+room.maxmoney);
		obj.put("roompeople",""+ userlist.size() );
		obj.put("dealer",""+ getDealerSeat() );
		obj.put("smallblind",""+ getDealerSeat() + 1 );
		obj.put("bigblind",""+ getDealerSeat() + 2 );
		sendRoom(obj);
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
		else if(kind==3)// 따당 
			return prebetmoney*2;
		else if(kind==4){// 하프
			int tc = preTotalBetmoney - mybetmoney;
			return tc + (totalmoney + tc)/2;
		}
		else if(kind==5){// 풀
			int tc = preTotalBetmoney - mybetmoney;
			return tc + totalmoney + tc;
		}
		else if(kind==6)// 맥스
			return room.maxmoney;
		else if(kind==7){// 쿼터
			int tc = preTotalBetmoney - mybetmoney;
			return tc+(totalmoney+tc)/4;
		}
		else if(kind==8)// 패스 ( 올인이나 맥스벳 상태에서는 자기 차례올시에 자동 패스 커맨드 함) 
			return 0;
		else if(kind==9)// 체크 
			return 0;
		else
			return 1;

	}

	boolean isGuBetEnd(){		
		int money = -1;
		int diecnt=0;
		int extrap = 0;
		User lastp = null;
		for(User uu : userlist)
			if(uu.die == true)
				diecnt++;
		if(userlist.size() >= turncnt -diecnt ){
			return false;
		}
		for(User uu : userlist){
			if(		uu.die == true	//죽었거나 
				|| 	uu.balance == 0 //올인이거나
				|| 	uu.betmoney == room.maxmoney //맥스 머니이거나
					) continue;//밑에 실행하지않고 다음 for문 검사
			extrap++;
			lastp = uu;
			if( money != -1 && uu.betmoney != money)
				return false;//배팅금액이 다르다
			money = uu.betmoney;
		}
		
		if( extrap == 1){//다 올인이거나 맥스뱃이거나 인데 혼자만 아닌경우, 다른 사람보다 같거나 더 많이 걸어야 함.
			for(User uu : userlist){
				if(lastp.betmoney <= uu.betmoney)
					return false;
			}
			
		}
		return true;//배팅금액이 똑같은경우 배팅끝
	}
	public void  nextTurn(){
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
	}

	public void bet(int roomidx, User u, int betkind){			
		//System.out.println("========= whosturnUseridx:"+whosturnUseridx +" uidx:"+u.uidx+" u seat:"+u.seat);
		if( getWhoTurn() != u.seat ){
			System.out.println(getWhoTurn()+" 잘못된 유저의 BET 차례 "+u.seat);
			return;
		}

		if(betkind==0) u.die = true;
		int tmo = thisTurnMoneyCompute(betkind , u.betmoney);
		if( u.balance <= tmo ){//올인인지 체크.
			tmo = u.balance;//올인 머니 셋팅
		}
		if( u.betmoney+tmo >= room.maxmoney ){//맥스 베팅인지 체크
			tmo = room.maxmoney - u.betmoney;
		}
		
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
			setWorkTime( );
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
		
		nextTurn();
		
		
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
		
		sendRoom(obj);//베팅 성공 정보를 전송
		
		
		
		
		if( checkAbstention() ){
			TheEnd();
		}else if( isBetEnd ){
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
	
	
	public boolean checkAbstention(){
		int cnt=0;
		for(User u : userlist){
			if(u.die == true)
				cnt++;
		}
		if(cnt == userlist.size()-1)
			return true;
		return false;
	}
	public boolean checkDieturn(int who){
		if(userlist.get(who).die == true)
			return true;
		else
			return false;
	}
	
	public void showThreeCard(){	
		timer = SocketHandler.second+2;
		whosturn=getDealerSeat();
		nextTurn();//딜러 다음 할 차례
		turncnt=0;
		System.out.println("showThreeCard SB:"+whosturn);
		JSONObject obj = new JSONObject();		
		changeGameMode("THEFLOP");
		
	      /*카드변경******/
	      /*card1=cardManager.popCard(36);
	      card2=cardManager.popCard(37);
	      card3=cardManager.popCard(12);*/
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
		whosturn=getDealerSeat();
		nextTurn();

		JSONObject obj = new JSONObject();
		GameMode = "THETURN";
	      /*카드변경******/
	      //card4=cardManager.popCard(30);
		card4=cardManager.popCard();

		obj.put("cmd","THETURN");
		obj.put("card4", card4.cardcode);
		obj.put("whosturn",getWhoTurn() );
		SocketHandler.insertLog(getGameId(), "THETURN", -1, card4.cardcode, -1, "", -1, -1);
		for(int k =0; k<userlist.size(); k++){
			cardarr[k][5] = card4.cardcode;
			//System.out.println( k + "번 째 유저의 4 번 째 카드 : " + cardarr[k][5]);
		}
		sendRoom(obj);
	}

	public void TheRiver(){
		timer = SocketHandler.second;
		turncnt = 0;
		//System.out.println("theriver================ 마지막 카드 공개");
		//timer=-1;
		whosturn=getDealerSeat();
		nextTurn();

		JSONObject obj = new JSONObject();
		GameMode = "THERIVER";
	    /*카드변경******/
	    //card5=cardManager.popCard(34);   
		card5=cardManager.popCard();		
		obj.put("cmd","THERIVER");
		obj.put("card5", card5.cardcode);
		obj.put("whosturn",getWhoTurn() );
		SocketHandler.insertLog(getGameId(), "THERIVER", -1, card5.cardcode, -1, "", -1, -1);
		for(int k =0; k<userlist.size(); k++){
			cardarr[k][6] = card5.cardcode;
			//System.out.println( k + "번 째 유저의 6 번 째 카드 : " + cardarr[k][6]);
		}
		sendRoom(obj);
	}

	public void TheEnd(){
		
		timer = -1;//타임아웃되는거 방지.
		System.out.println("TheEnd================ ");

		JSONObject obj = new JSONObject();
		changeGameMode("THEEND");				
		obj.put("cmd","THEEND");
		SocketHandler.insertLog(getGameId(), "gameEnd", -1, totalmoney, -1, "게임 끝" , -1, -1);
		sendRoom(obj);
	}	
	

	//true 이면 2 스트레이트 플러시 : 9 10 j q k 가  모두 하트 
	public boolean checkStraightFlush(int arr[]){
		if( checkStraight(arr) == true && checkFlush(arr) ==true){
			return true;
		}			
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
			if(cNum[i] == 3){
				triple = 1;
				tempInfo1 = i;//트리플숫자 
			}
			if(cNum[i] == 2){
				pair = 1;
				tempInfo2 = i;//투페어숫자
			}
		}
		
		if(triple==1 && pair==1){
			
			return true;
		}
		else return false;
	}
	//9페어:동일한숫자 한쌍   8투페어:동일한숫자 두쌍  7트리플: 동일한세장      3포카드 : 동일한숫자 4장 
	public int checkAllpair(int arr[]){
		int level = 100;
		int twopair = 0;
		int []cNum = new int[13]; 
		for(int i=0; i<7; i++){
			cNum[ arr[i]%13 ]++;
			if(cNum[ arr[i]%13 ] >= 4){
				tempInfo1 = arr[i]%13;//숫자네개
				System.out.println("tempInfo1:"+tempInfo1);
				level = 3;//포카드
			}
			else if(cNum[ arr[i]%13 ] == 3 && level >7){
				tempInfo1 = arr[i]%13;
				level = 7;//트리플 숫자세개
			}
			else if(cNum[ arr[i]%13 ] >= 2){//페어 , 투페어
				twopair++;				
				if(twopair==2  && level >8) {
					tempInfo2 = arr[i]%13;//숫자
					level = 8; // 투페어				
				}
				else if(twopair==1){
					tempInfo1 = arr[i]%13;//숫자
					level = 9; 
				}
			}
			
		}
		return level;
	}
	
	//true 이면  6 스트레이트 : 9 10 j q k
	public boolean checkStraight(int tarr[]){
		int [] arr=tarr.clone();
		Arrays.sort(arr);	//큰수가 앞으로 오게
		int ct = 0;
		int pre = 0;	
		int ck = -1;
		for(int i=0; i<7; i++){
			if(i!=0 && pre+1 != arr[i]%13 )
				ct=0;
			else{
				ct++;
				if(ct>=4){					
					ck = arr[i]%13;;
				}
			}
			pre= arr[i]%13;
		}
		if( ck != -1 ){
			tempInfo1 = ck;
			return true;
		}
		return false;
	}
	//true 이면 5 플러시  : 하트5장
	public boolean checkFlush(int arr[]){
		int []shape = new int[4]; 
		for(int k=0; k<7; k++){
			shape[ (int)(arr[k]/13) ]++;
			if( shape[ (int)(arr[k]/13) ] >= 5 ) {
				tempInfo2 = (int)(arr[k]/13); 
				return true;
			}
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
	
	
	int cardInfo1,cardInfo2;//이긴사람 카드정보
	int tempInfo1,tempInfo2;//임시 카드정보
	public void showResult(){
		whosturn=0;
		System.out.println("SHOW RESULT ");
		//결과 계산하기
		// 유저들의 카드 목록 2차원 배열 출력
		int winSeat=-1,wlv=10000;
		
		
		//기권승시 족보계산안함 ,이긴사람 돈줌 
		if( this.checkAbstention() ){
			int cnt=0;
			for(User u : userlist){
				if(u.die == false){
					winSeat = cnt;
				}
				cnt++;
			}
			wlv = 99;
		}else {
			for(int k = 0; k<cardarr.length; k++){
				
				//죽은 사람은 결과에서 제외
				boolean userDie = userlist.get(k).die;
				if( userDie == true){				
					continue;
				}
				int []card = new int[7]; 
				System.out.println(k + "번째 유저의 카드 목록 2차원 배열");
				for(int i = 0; i<cardarr[k].length; i++){				
					card[i] = cardarr[k][i];				
				}
				
				//3포카드 7트리플 8투페어 9페어			
				int lv = checkAllpair(card);
				//스트레이트 플러시 2
				if(checkStraightFlush(card)==true){//info1 스트레이트 숫자 , info2 플러시 모양
					if(lv>2) lv=2;
				}else if(checkFullHouse(card)==true){//info1트리플 숫자  info2 투페어숫자
					if(lv>4) lv=4;
				}else if(checkFlush(card)==true){//info2 플러시 모양
					if(lv>5) lv=5;
				}else if(checkStraight(card)==true){//info1 스트레이트숫자
					if(lv>6) lv=6;
				}else {//탑카드 
					if(lv>10) lv=10;
					userlist.get(k).topcard = checkTopCard(card);
					cardInfo1 = userlist.get(k).topcard; 
				}
				
				if(wlv > lv ){
					winSeat = k;
					wlv = lv;
					cardInfo1 = tempInfo1;
					cardInfo2 = tempInfo2;
					System.out.println("*********lv:"+lv+" cardInfo1:"+cardInfo1+" cardInfo2:"+cardInfo2);				
				}
				userlist.get(k).level = lv;
			}
		}
		

		//userlist.get(winSeat).balance+=totalmoney;
		int betMoney=0;
		int cnt=0;
		for(User u : userlist){
			if(userlist.get(winSeat).betmoney > u.betmoney){
				betMoney+=u.betmoney;
			}
			if(userlist.get(winSeat).betmoney <= u.betmoney)
				cnt++;
		}
		for(User u : userlist){
			if( u.seat == winSeat){
				userlist.get(winSeat).balance+=betMoney + (cnt*userlist.get(winSeat).betmoney);
			}else{
				if(userlist.get(winSeat).betmoney < u.betmoney){
					u.balance = u.betmoney - userlist.get(winSeat).betmoney;
				}
			}
		}
		
		JSONObject obj = new JSONObject();
		obj.put("cmd","showResult");
		obj.put("wlv",""+wlv);
		obj.put("cardInfo1",cardInfo1);
		obj.put("cardInfo2",cardInfo2);
		obj.put("winnerbalance",""+userlist.get(winSeat).balance);
		obj.put("winmoney",""+this.totalmoney);
		obj.put("winSeat",""+winSeat);		
		obj.put("usersize",""+userlist.size());		
		
		JSONArray j = new JSONArray();
		for(int i=0; i<userlist.size(); i++){
			JSONObject item = new JSONObject();
			item.put("seat",""+ userlist.get(i).seat);			
			item.put("card1",""+ userlist.get(i).card1.cardcode);
			item.put("card2",""+ userlist.get(i).card2.cardcode);
			item.put("die",""+ userlist.get(i).die);
			j.add(item);
		}
		obj.put("cardlist", j);
		sendRoom(obj);
	}
}
