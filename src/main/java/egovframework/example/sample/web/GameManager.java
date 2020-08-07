package egovframework.example.sample.web;

import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.springframework.web.socket.TextMessage;

public class GameManager {		
	public ArrayList<User> userlist = new ArrayList<User>();
	public CardManager cardManager = new CardManager();
	//public User reuser = new User();

	String GameMode="대기";
	//Calendar startTime;
	long timerStartTime = 0;//객체로 만들면 좋다
	int whosturn = 0;
	int turncnt = 0;
	int whosturnUseridx;
	int totalmoney = 0;
	int prebetmoney =0 ;//이전 사람의 베팅머니
	int preTotalBetmoney=0;//이전 사람의 현재 구의 총 배팅머니/ 콜금액 계산용.
	int startTime;
	int timer = -1;
	int dealerSeatNum = -1;
	int endtime;
	int[][] cardarr;
	int[] cardcopyarr;
	int[] arr1;// 각 유저벌 카드 코드를 담기 위한 변수 1~9 최대유저 9명
	int[] arr2;
	int[] arr3;
	int[] arr4;
	int[] arr5;
	int[] arr6;
	int[] arr7;
	int[] arr8;
	int[] arr9;

	Card card1;
	Card card2;
	Card card3;
	Card card4;
	Card card5;

	Room room;
	public GameManager(Room room){
		this.room = room;
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
		whosturn = 0;
		turncnt = 0;
		totalmoney = 0;
		arr1 = new int[] {53,53,53,53,53,53,53};
		arr2 = new int[] {53,53,53,53,53,53,53};
		arr3 = new int[] {53,53,53,53,53,53,53};
		arr4 = new int[] {53,53,53,53,53,53,53};
		arr5 = new int[] {53,53,53,53,53,53,53};
		arr6 = new int[] {53,53,53,53,53,53,53};
		arr7 = new int[] {53,53,53,53,53,53,53};
		arr8 = new int[] {53,53,53,53,53,53,53};
		arr9 = new int[] {53,53,53,53,53,53,53};

		cardarr = new int[userlist.size()][7];
		cardcopyarr = new int [cardarr.length*cardarr[0].length];
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
				sbBet();
			}
		}
		//===================
		if(GameMode.compareTo("bbBet")==0){			
			if(SocketHandler.second-startTime>3){
				//changeGameMode("nmBet");
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
		if(GameMode.compareTo("showBetPan")==0 || GameMode.compareTo("THEFLOP")==0 || GameMode.compareTo("THETURN")==0 || GameMode.compareTo("THERIVER")==0){
			if(timer!=-1 && SocketHandler.second-timer>8){ // 자기턴 타임아웃 시간 8초로.	
				for(User u : userlist){
					if(u.uidx==whosturnUseridx){
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
			obj.put("useridx", userlist.get(k).uidx);
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
		whosturnUseridx = userlist.get(whosturn%userlist.size()).uidx;
		obj.put("cmd","sbBet");
		obj.put("whosturn", ""+getWhoTurn() );
//		obj.put("whosturn",userlist.get(whosturn).uidx);
		sendRoom(obj);
	}

	public void bbBet(){
		JSONObject obj = new JSONObject();
		obj.put("cmd","bbBet");
		obj.put("whosturn", ""+getWhoTurn() );
		sendRoom(obj);
		timer = SocketHandler.second;
	}

	public void showBetPan(){

		JSONObject obj = new JSONObject();
		obj.put("cmd","bet");
		obj.put("whosturn", getWhoTurn() );
		timer = SocketHandler.second;
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
			if( money != -1 && uu.betmoney != money)
				return false;
			money = uu.betmoney;
		}
		return true;
	}
	public void bet(int roomidx, User u, int betkind){			
		//System.out.println("========= whosturnUseridx:"+whosturnUseridx +" uidx:"+u.uidx+" u seat:"+u.seat);
		if( getWhoTurn() != u.seat ){
			System.out.println(getWhoTurn()+" 잘못된 유저의 SBBET 차례 "+u.seat);
			return;
		}

		int tmo = thisTurnMoneyCompute(betkind , u.betmoney);
//		System.out.println("tmo:"+tmo +" u.betmoney:"+u.betmoney+" prebetmoney:"+prebetmoney+" preTotalBetmoney:"+preTotalBetmoney);
		u.betmoney += tmo ;//나의 배팅금액 현재돈+배팅금액
		prebetmoney = tmo;
		preTotalBetmoney = u.betmoney;
		//System.out.println("dbg6 u.betmoney:"+u.betmoney);

		totalmoney +=tmo;
		
		//배팅한 사람 돈 차감 시키기!!!
		u.balance -= tmo;
		
		System.out.println("BET whosturn: "+whosturn+"("+getWhoTurn()+") Game:"+GameMode+" betkind:"+betkind+" totalmoney:"+totalmoney+" 잔액:"+u.balance );

		JSONObject obj = new JSONObject();
		if(GameMode.compareTo("sbBet")==0)	{
			obj.put("cmd", "sbBetsuc");
			//System.out.println("sb가 자동베팅했습니다.");
			setRoomStartTime( SocketHandler.second );
			changeGameMode("bbBet");
		}
		else if(GameMode.compareTo("bbBet")==0)	{
			obj.put("cmd", "bbBetsuc");
			//System.out.println("bb가 자동베팅했습니다.");
			changeGameMode("nmBet");
		}
		else{
			obj.put("cmd", "betsuc");
			//System.out.println("<< 베팅 성공 >>");
		}
		whosturn++;//다음사람배팅
		boolean isBetEnd = isGuBetEnd();

		obj.put("totalmoney", totalmoney);
		obj.put("callmoney", "" + (preTotalBetmoney - u.betmoney) );
		obj.put("myBetMoney", tmo );
		obj.put("balance", u.balance);
		obj.put("seat", u.seat);//금방배팅한 사람
		obj.put("nextwho", getWhoTurn() );//이제 배팅할 사람의 번호
		if( isBetEnd )
			obj.put("betEnd", "1");//마지막 베팅인지 체크
		else
			obj.put("betEnd", "0");//마지막 베팅인지 체크

		timer = SocketHandler.second;

		//베팅 성공 정보를 전송
		for(User uu : userlist){
			try {
				uu.session.sendMessage(new TextMessage(obj.toJSONString()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		if( isBetEnd ){
			System.out.println("DBG 3 : 전원 베팅 끝");

			if(GameMode.compareTo("showBetPan")==0)	{
				showThreeCard();
			}
			else if(GameMode.compareTo("THEFLOP")==0){			
				TheTurn();
			}
			else if(GameMode.compareTo("THETURN")==0){
				TheRiver();
			}
			else if(GameMode.compareTo("THERIVER")==0){
				TheEnd();
			}
			return;
		}


	}

	public void showThreeCard(){	
		timer = SocketHandler.second+2;
		whosturn=getDealerSeat()+1;
		System.out.println("showThreeCard whosturn:"+whosturn);
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
		whosturnUseridx = userlist.get(whosturn%userlist.size()).uidx;

		JSONObject obj = new JSONObject();
		GameMode = "THETURN";

		card4=cardManager.popCard();

		obj.put("cmd","THETURN");
		obj.put("card4", card4.cardcode);
		obj.put("whosturn",userlist.get(whosturn).uidx);

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
		whosturnUseridx = userlist.get(whosturn%userlist.size()).uidx;

		JSONObject obj = new JSONObject();
		GameMode = "THERIVER";

		card5=cardManager.popCard();		
		obj.put("cmd","THERIVER");
		obj.put("card5", card5.cardcode);
		obj.put("whosturn",userlist.get(whosturn).uidx);

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
	public void showResult(){
		whosturn=0;

		//결과 계산하기
		// 유저들의 카드 목록 2차원 배열 출력
		for(int k = 0; k<cardarr.length; k++){
			System.out.println(k + "번째 유저의 카드 목록 2차원 배열");
			for(int i = 0; i<cardarr[k].length; i++){
				System.out.print("\t" + cardarr[k][i]);
			}
			System.out.println();
		}
/*
		// 카드 목록을 1차원 배열로 유저별로 변환
		for(int i=0; i<cardarr.length; i++) {
			for(int j=0; j<cardarr[i].length; j++) {
				//2차원 배열의 원소를 유저별 1차원 배열의 원소로 이동.
				if( ( i * cardarr[i].length ) + j < 7){
					arr1[ j ] = cardarr[i][j];
				}
				else if( ( i * cardarr[i].length ) + j < 14){
					arr2[ j ] = cardarr[i][j];
				}
				else if( ( i * cardarr[i].length ) + j < 21){
					arr3[ j ] = cardarr[i][j];
				}
				else if( ( i * cardarr[i].length ) + j < 28){
					arr4[ j ] = cardarr[i][j];
				}
				else if( ( i * cardarr[i].length ) + j < 35){
					arr5[ j ] = cardarr[i][j];
				}
				else if( ( i * cardarr[i].length ) + j < 42){
					arr6[ j ] = cardarr[i][j];
				}
				else if( ( i * cardarr[i].length ) + j < 49){
					arr7[ j ] = cardarr[i][j];
				}
				else if( ( i * cardarr[i].length ) + j < 56){
					arr8[ j ] = cardarr[i][j];
				}
				else if( ( i * cardarr[i].length ) + j < 63){
					arr9[ j ] = cardarr[i][j];
				}

			}
		}
		// 1차원 배열 출력
		System.out.println("==========================");
		for(int i=0; i<5; i++) {
			System.out.print("\t"+arr1[i]);  
		}
		System.out.println();
		for(int i=0; i<5; i++) {
			System.out.print("\t"+arr2[i]);  
		}
		System.out.println();
		for(int i=0; i<5; i++) {
			System.out.print("\t"+arr3[i]);  
		}
		System.out.println();
		for(int i=0; i<5; i++) {
			System.out.print("\t"+arr4[i]);  
		}
		System.out.println();
		for(int i=0; i<5; i++) {
			System.out.print("\t"+arr5[i]);  
		}
		System.out.println();
		for(int i=0; i<5; i++) {
			System.out.print("\t"+arr6[i]);  
		}
		System.out.println();
		for(int i=0; i<5; i++) {
			System.out.print("\t"+arr7[i]);  
		}
		System.out.println();
		for(int i=0; i<5; i++) {
			System.out.print("\t"+arr8[i]);  
		}
		System.out.println();
		for(int i=0; i<5; i++) {
			System.out.print("\t"+arr9[i]);  
		}
		System.out.println();
		System.out.println("==========================");




*/
		JSONObject obj = new JSONObject();
		obj.put("protocol","showResult");
		changeGameMode("showResult");
	}
}
