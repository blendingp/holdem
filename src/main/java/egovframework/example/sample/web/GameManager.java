package egovframework.example.sample.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.socket.TextMessage;

public class GameManager {		
	public ArrayList<User> userlist = new ArrayList<User>();	
	public int[] seats = {-1, -1, -1, -1, -1, -1, -1, -1, -1};		
	public CardManager cardManager = new CardManager();	
	//public User reuser = new User();
	int gameId;//게임로그에 저장되는 게임번호
	int gu;//1구 2구 3구 4구
	String GameMode="대기";	
	//Calendar startTime;
	long timerStartTime = 0;//객체로 만들면 좋다
	int whosturn = 0;//누구차례인지 
	int turncnt = 0;
	int money = 0;
	int totalmoney = 0;
	int prebetmoney =0 ;//이전 사람의 베팅머니
	int preTotalBetmoney=0;//이전 사람의 현재 구의 총 배팅머니/ 콜금액 계산용.	
	
//	int startTime;
//	int endtime;
	int workTime=0;
	
	int timer = -1;
	int dealerSeatNum = 0;//누가딜러인지 유저인덱스 저장
	int lastCmdSecond=1000000;//마지막 명령을 받은 시간, 1분  이상 누구도 명령을 내리지 않은 상태인데 방이 대기 상태가 아니라면 에러난 상태이므로 방을 강제 초기화 시켜야 함. 
	
	int[][] cardarr;// 사람별 카드 소유
	
	Card card1;
	Card card2;
	Card card3;
	Card card4;
	Card card5;

	Room room;
	
	User currentUser = null;
	
	int bbBetCount = 0;
	int bbSeat = 0;
	
	ArrayList<Pot> gamePot = new ArrayList<>();
	
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
		seats = new int[] {-1, -1, -1, -1, -1, -1, -1, -1, -1};
		bbBetCount = 0;
		dealerSeatNum = 0;//누가딜러인지 유저인덱스 저장
		gamePot.clear();
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
	
	public int GetEmptySeat()
	{
		for( int nCount = 0; nCount < seats.length; ++nCount)
		{
			if( seats[nCount] < 0 )
			{
				return nCount;			
			}
		}
		
		return -1;
	}
	
	public void SetSeat(int seat)
	{		
		seats[seat] = 0;
	}
	
	public void EmptySeat(int seat)
	{		
		seats[seat] = -1;
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
		if(cnt>=2) {			
			return true;
		}
		return false;
	}


	void DealerSeatSetting(){
		dealerSeatNum = getDealerSeatOffset(0);
	}
	void startSetting(){
		gu = 1;
		setGameId(SocketHandler.GameIdxAdder());
		gamePot.clear();
		gamePot.add(new Pot());
		
		SocketHandler.insertLog(getGameId(), "gamestart", -1, -1, -1, "게임시작", -1, -1);
		whosturn = 0;
		turncnt = 0;
		totalmoney = 0;
		prebetmoney =0 ;//이전 사람의 베팅머니
		preTotalBetmoney=0;//이전 사람의 현재 구의 총 배팅머니/ 콜금액 계산용.		

		cardarr = new int[userlist.size()][7];

		for(User u : userlist){
			SocketHandler.insertLog(getGameId(), "join", u.uidx , u.balance , u.seat , "참가머니", room.defaultmoney , -1);
			u.init();
			u.PlayStatus = 1;
			money += room.defaultmoney;
			u.betmoney+=room.defaultmoney;
			u.balance-=room.defaultmoney;
			totalmoney+=room.defaultmoney;
		}				
	}
	
	void setDealerSeat()
	{
		int seat = dealerSeatNum + 1;
		for( int nCount = 0; nCount < this.seats.length; ++nCount )
		{						
			if( this.seats[(seat + nCount)%this.seats.length] >= 0 )
			{
				if( SearchUserBySeat((seat + nCount)%this.seats.length).balance <= 0 )
				{
					continue;					
				}
				
				dealerSeatNum = (seat + nCount)%this.seats.length;				
				return ;
			}			
		}						
	}
	
	int getDealerSeat(){
		
		for( int nCount = 0; nCount < this.seats.length; ++nCount )
		{						
			if( this.seats[(dealerSeatNum + nCount)%this.seats.length] >= 0 )
			{
				dealerSeatNum = (dealerSeatNum + nCount)%this.seats.length;				
				return dealerSeatNum;
			}			
		}	
		
		return -1;
	}
	
	int getDealerSeatOffset(int offset)
	{
		int seat = dealerSeatNum;
		
		for( int nCount = 0; nCount < this.seats.length; ++nCount )
		{				
			if( this.seats[(seat + nCount)%this.seats.length] >= 0 )
			{				
				if( offset > 0 )
				{
					offset--;
					continue;
				}
				
				seat = (seat + nCount)%this.seats.length;				
				return seat;
			}			
		}	
		
		return -1;
		
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
			if(  checkCmdTime(2)   ){
				setWorkTime();
				drawCard();
				resetGuBetmoney();
				changeGameMode("sbBet");
				whosturn = getDealerSeatOffset(1); // 첫 베팅 하는 사람은 딜러 다음 사람
			}

		}

		if(GameMode.compareTo("sbBet")==0)
		{
			// 2를 나중에 시스템 수치로 변경
			if( checkCmdTime(userlist.size()) ){
				changeGameMode("sbBeted");
				sbBet();
			}
		}
		//===================
		if(GameMode.compareTo("bbBet")==0){			
			if( checkCmdTime(0) ){
				changeGameMode("bbBeted");
				bbBet();
			}
		}

		if(GameMode.compareTo("nmBet")==0){	
			if(  checkCmdTime(2)  ){
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
			if(timer!=-1 && SocketHandler.second - timer > 12){ // 자기턴 타임아웃 시간 8초로.				
				for(User u : userlist){
					if(u.seat==whosturn ){
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
		obj.put("gameid", gameId);
		obj.put("smoney",  ""+totalmoney);
		obj.put("maxmoney",  ""+room.maxmoney);
		obj.put("roompeople",""+ userlist.size() );
		obj.put("dealer",""+ getDealerSeat() );
		obj.put("smallblind",""+ getDealerSeatOffset(1) );
		obj.put("bigblind",""+ getDealerSeatOffset(2));
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
										
		for( int nCount = 0; nCount < this.seats.length * 2; ++nCount )
		{							
			if( this.seats[(whosturn + nCount)%this.seats.length] >= 0 )
			{				
				if( checkDieturn((whosturn + nCount)%this.seats.length) == true )
				{
					System.out.println("--die--");
					System.out.println((whosturn + nCount)%this.seats.length);
					SearchUserBySeat((whosturn + nCount)%this.seats.length).PlayStatus = -1;
					continue;					
				}
				/*
				if( SearchUserBySeat((whosturn + nCount)%this.seats.length).balance <= 0 )
				{
					System.out.println("--balance--");
					System.out.println((whosturn + nCount)%this.seats.length);
					SearchUserBySeat((whosturn + nCount)%this.seats.length).PlayStatus = 0;
					continue;
				}
				
				if( SearchUserBySeat((whosturn + nCount)%this.seats.length).betmoney >= room.maxmoney )
				{
					System.out.println("--betmoney--");
					System.out.println((whosturn + nCount)%this.seats.length);
					SearchUserBySeat((whosturn + nCount)%this.seats.length).PlayStatus = 0;
					continue;
				}*/
					
				whosturn = (whosturn + nCount)%this.seats.length;
				return whosturn;
			}			
		}		

		return -1;
	}
	public void sbBet(){
		JSONObject obj = new JSONObject();
		whosturn = getDealerSeatOffset(1);
		obj.put("cmd","sbBet");
		obj.put("whosturn", ""+whosturn );
		obj.put("prebetmoney", preTotalBetmoney );
		obj.put("myBetMoney", SearchUserBySeat(whosturn).betmoney );
//		obj.put("whosturn",userlist.get(whosturn).uidx);
		sendRoom(obj);
		System.out.println("sbBet하시오 :"+whosturn );
	}

	public void bbBet(){		
		bbSeat = whosturn;
		bbBetCount++;
		JSONObject obj = new JSONObject();
		obj.put("cmd","bbBet");
		obj.put("whosturn", ""+whosturn );
		obj.put("prebetmoney", preTotalBetmoney );
		obj.put("myBetMoney", SearchUserBySeat(whosturn).betmoney );
		sendRoom(obj);
		timer = SocketHandler.second;		
		System.out.println("bbBet하시오 :"+whosturn );
	}

	public void showBetPan(){

		JSONObject obj = new JSONObject();		
		obj.put("cmd","bet");
		if( turncnt == 0 )
		{
			if( preTotalBetmoney >= room.maxmoney)
			{
				obj.put("betenable", "0,1,0,1,0,0,0,0");//체크/폴드/삥/콜/따당/하프/풀/맥스
			}
			else				
			{
				obj.put("betenable", "1,1,1,0,0,1,1,1");//체크/폴드/삥/콜/따당/하프/풀/맥스	
			}			
		}
		else
		{
			if( preTotalBetmoney >= room.maxmoney)
			{
				obj.put("betenable", "0,1,0,1,0,0,0,0");//체크/폴드/삥/콜/따당/하프/풀/맥스
			}
			else
			{
				obj.put("betenable", "0,1,0,1,1,1,1,1");//체크/폴드/삥/콜/따당/하프/풀/맥스	
			}			
		}
		obj.put("whosturn", whosturn );
		obj.put("prebetmoney", preTotalBetmoney );
		obj.put("myBetMoney", SearchUserBySeat(whosturn).betmoney );
		timer = SocketHandler.second;
		System.out.println("다음 사람 베팅 하시오:"+whosturn );		
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
			return (totalmoney + tc)/2;
		}
		else if(kind==5){// 풀
			int tc = preTotalBetmoney - mybetmoney;
			return totalmoney + tc;
		}
		else if(kind==6)// 맥스
			return room.maxmoney;
		else if(kind==7){// 쿼터
			int tc = preTotalBetmoney - mybetmoney;
			return (totalmoney+tc)/4;
		}
		else if(kind==8)// 패스 ( 올인이나 맥스벳 상태에서는 자기 차례올시에 자동 패스 커맨드 함) 
			return 0;
		else if(kind==9)// 체크 
			return 0;
		else
			return 1;

	}

	boolean isGuBetEnd(){					
		int diecnt=0;
		int extrap = 0;
		User lastp = null;
		for(User uu : userlist){
			if(uu.die == true){
				diecnt++;
			}
		}
						
		for(User uu : userlist){
			
			if(uu.die == true)
			{				
				continue;
			}
			
			if(uu.PlayStatus == 1)
			{				
				return false;
			}										
			
			if( uu.betmoney < money)
			{										
				System.out.println("uu.betmoney < money");
				System.out.println("seat");
				System.out.println(uu.seat);
				System.out.println(uu.betmoney);
				System.out.println(money);
				System.out.println(uu.PlayStatus);
				System.out.println(uu.die);
				return false;//배팅금액이 다르다
			}								
				
			extrap++;
			lastp = uu;				
					
		}
		
		for(User uu : userlist){
			if( uu.die == true || uu.balance <= 0 || uu.betmoney >= room.maxmoney )
			{
				continue;
			}				
			
			if(uu.betmoney != preTotalBetmoney)
			{			
				System.out.println("uu.betmoney != preTotalBetmoney");
				return false;
			}
		}	
				
				
		return true;//배팅금액이 똑같은경우 배팅끝
	}
	public void  nextTurn(){
		whosturn++;//다음사람배팅
		//int k=0;		
		if( getWhoTurn() < 0 )
		{			
			SocketHandler.insertLog(getGameId(), "error", -1, -1, -1, "DieTurnCheckError", -1, -1);
		}
		/*
		while( checkDieturn(getWhoTurn()) ){
			k++;
			if( k>10){//9명이상이니 여기에 들어오면 에러 무한루프 체크
				SocketHandler.insertLog(getGameId(), "error", -1, -1, -1, "DieTurnCheckError", -1, -1);
				break;
			}
			whosturn++;			
		}*/
	}

	public void bet(int roomidx, User u, int betkind){			
		//System.out.println("========= whosturnUseridx:"+whosturnUseridx +" uidx:"+u.uidx+" u seat:"+u.seat);
		if( whosturn != u.seat ){
			System.out.println(whosturn+" 잘못된 유저의 BET 차례 "+u.seat);
			return;
		}

		if(betkind==0)
		{ 
			u.die = true;
			u.PlayStatus = -1;
			
			System.out.println("die seat");
			System.out.println(u.seat);
		}
		
		Boolean isAllIn = false;
		
		int tmo = thisTurnMoneyCompute(betkind , u.betmoney);			
		
		if( u.balance <= tmo ){//올인인지 체크.
			tmo = u.balance;//올인 머니 셋팅
			isAllIn = true;
		}
		if( u.betmoney+tmo >= room.maxmoney ){//맥스 베팅인지 체크
			tmo = room.maxmoney - u.betmoney;
		}
		
		if( betkind != 0 )
		{
			gamePot.get(gamePot.size()-1).JoinPot(whosturn, tmo);	
		}		
		
		if( isAllIn == true )
		{									
			gamePot.add(gamePot.get(gamePot.size()-1).PotSlit(u.betmoney));
		}
				
		u.betmoney += tmo ;//나의 배팅금액 현재돈+배팅금액
		money = prebetmoney;
		
		prebetmoney = tmo;
		
		if( preTotalBetmoney < u.betmoney)
		{
			preTotalBetmoney = u.betmoney;	
		}
		
		//System.out.println("{ tmo:"+tmo +" u.betmoney:"+u.betmoney+" prebetmoney:"+prebetmoney+" preTotalBetmoney:"+preTotalBetmoney);
		//System.out.println("dbg6 u.betmoney:"+u.betmoney);

		totalmoney +=tmo;
		
		//배팅한 사람 돈 차감 시키기!!!
		u.balance -= tmo;			
		
		//System.out.println("BET whosturn: "+whosturn+"("+getWhoTurn()+") Game:"+GameMode+" betkind:"+betkind+" totalmoney:"+totalmoney+" 잔액:"+u.balance +"   :::" + "{ tmo:"+tmo +" u.betmoney:"+u.betmoney+" prebetmoney:"+prebetmoney+" preTotalBetmoney:"+preTotalBetmoney);
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
			u.PlayStatus = 0;
		}
				
		turncnt++;
		
		nextTurn();
				
		boolean isBetEnd = isGuBetEnd();
		obj.put("totalmoney", totalmoney);
		obj.put("prev", money);
		obj.put("callmoney", "" + (preTotalBetmoney - u.betmoney) );
		obj.put("prebetmoney", preTotalBetmoney );
		obj.put("myBetMoney", u.betmoney );
		obj.put("balance", u.balance);
		obj.put("betkind", betkind);
		obj.put("seat", u.seat);//금방배팅한 사람
		obj.put("nextwho", whosturn );//이제 배팅할 사람의 번호
		if( preTotalBetmoney >= room.maxmoney)
		{
			obj.put("betenable", "0,1,0,1,0,0,0,0");//체크/폴드/삥/콜/따당/하프/풀/맥스
		}
		else
		{
			obj.put("betenable", "0,1,0,1,1,1,1,1");//체크/폴드/삥/콜/따당/하프/풀/맥스	
		}		
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
			
			for( User user : userlist )
			{
				if( user.PlayStatus == 0 )
				{
					user.PlayStatus = 1;
				}
			}
			
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
		
		for( int nCount = 0; nCount < userlist.size(); ++nCount )
		{
			if(userlist.get(nCount).seat == who)
			{
				return userlist.get(nCount).die;
			}
		}
		
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
		obj.put("whosturn", whosturn );
		if( preTotalBetmoney >= room.maxmoney)
		{
			obj.put("betenable", "0,1,0,1,0,0,0,0");//체크/폴드/삥/콜/따당/하프/풀/맥스
		}
		else
		{
			obj.put("betenable", "1,1,1,0,0,1,1,1");//체크/폴드/삥/콜/따당/하프/풀/맥스	
		}		
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
		obj.put("whosturn",whosturn );
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
		obj.put("whosturn",whosturn );
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
	

	public boolean checkStraightFlush(int tarr[]){
		ArrayList<Integer> cards = new ArrayList<>();
		
		int [] arr;
		int ct = 0;
		int pre = 0;	
		int preShape = -1;
		
		ct=0;
		pre=0;
		preShape=-1;
		arr=cardsort(tarr,true);
		for(int i=0; i<7; i++){
			if(pre == arr[i]%13 && (preShape == arr[i]/13) )continue;//8 7 (7) 6 5 4 3 <==  같은수 걸러내기
			if(i!=0 && (pre-1 != arr[i]%13 || preShape != arr[i]/13) )
			{
				ct=0;
				cards.clear();
			}
			ct++;
			cards.add( arr[i] );
			if( ct == 1 ){//스트레이트 탑카드
				tempInfo1 = arr[i]%13;
			}
			if(ct>=5){					
				break;
			}
			pre= arr[i]%13;
			preShape = arr[i]/13;
		}
		
		if( ct< 5){//스트레이트를 못찾았다면
			ct=0;
			pre=0;
			preShape=-1;
			arr=cardsort(tarr);//위에는 에이스를 13으로 놓고 한것이고, 여기서 에이스를 0으로 놓고 다시한번 스트레이트 체크 시도
			for(int i=0; i<7; i++){
				if(pre == arr[i]%13 && (preShape == arr[i]/13) )continue;//8 7 (7) 6 5 4 3 <==  같은수 걸러내기
				if(i!=0 && (pre-1 != arr[i]%13 || preShape != arr[i]/13) )
				{
					ct=0;
					cards.clear();
					cards.add( arr[i] );
				}
				else{
					ct++;
					cards.add( arr[i] );
					if( ct == 1 ){//스트레이트 탑카드
						tempInfo1 = arr[i]%13;
					}
					if(ct>=5){					
						break;
					}
				}
				pre= arr[i]%13;
				preShape = arr[i]/13;
			}
		}
		
		if( ct >= 5 ){
			if( tempInfo1%13 == 0)tempInfo1 = 14;//마운틴 스트레이트
			if( tempInfo1%13 == 4)tempInfo1 = 13;//에이스 스트레이트
			JSONObject win = MakeWinCard(9, cards);
			currentUser.wincard.add(win);
			return true;
		}
		return false;
	}
	
	//4 풀하우스 트리플페어: 트리플+페어  777 22
	public boolean checkFullHouse(int tarr[]){
		int arr[]=cardsort(tarr,true);
		ArrayList<Integer> cards = new ArrayList<>();
		int []cNum = new int[13];
		int []tempNum = new int[13];
		int triple = 0;
		int pair = 0;
		tempInfo1 = -1;
		tempInfo2 = -1;
		for(int i=0; i<7; i++){
			cNum[ arr[i]%13 ]++;
			tempNum[i] = arr[i]%13;
		}

		for(int i=12;i>=0;i--){
			if(cNum[i] == 3 && tempInfo1 == -1){
				triple = 1;
				tempInfo1 = i;//트리플숫자 
			}
		}

		
		for(int i=12;i>=0;i--){
			if(cNum[i] >= 2 && i != tempInfo1 ){
				pair = 1;
				tempInfo2 = i;//투페어숫자
				break;
			}
		}
		

		
		if(triple==1 && pair==1){
			for(int i=0;i<7;i++){
				if( arr[i]%13 == tempInfo1 ||arr[i]%13 == tempInfo2 )
					cards.add(tempNum[i]);
			}
			//에이스일경우 높은값으로 적용.
			if( tempInfo1 == 0)tempInfo1=13;
			if( tempInfo2 == 0)tempInfo2=13;
			JSONObject win = MakeWinCard(7, cards);
			currentUser.wincard.add(win);
			
			return true;
		}
		else return false;
	}
	int cdval(int cd, boolean st){return st?(   cd%13==0?13:0  ): cd%13 ; }
	int[] cardsort(int tcl[]){return cardsort(tcl,false);}
	int[] cardsort(int tcl[],boolean ace){
		int cl[]=tcl.clone();
		for(int a=0;a<cl.length;a++){
			for(int b=a+1;b<cl.length;b++){
				if( cdval(cl[a],ace) < cdval(cl[b],ace) ){
					int tmp=cl[a];
					cl[a]=cl[b];
					cl[b]=tmp;
				}
			}
		}
		return cl;
	}
	
	public boolean checkPair(int tarr[]){
		ArrayList<Integer> cards = new ArrayList<>();
		int [] arr=cardsort(tarr,true);
		int level = -1;
		int []cNum = new int[13]; 
		for(int i=0; i<7; i++){
			cNum[ arr[i]%13 ]++;
			if(cNum[ arr[i]%13 ] >= 2){
				tempInfo1 = arr[i]%13;
				level = 2;
				break;
			}
		}
		
		if(level == 2 )
		{
			for(int i=0; i<7; i++){
				if( arr[i]%13 == tempInfo1 ){
					cards.add( arr[i] );
				}
			}
			JSONObject win = MakeWinCard(level, cards);
			currentUser.wincard.add(win);
			if(tempInfo1 % 13 == 0)tempInfo1 = 13;
			return true;
		}
		
		return false;
	}
	
	public boolean checkTwoPair(int tarr[]){
		ArrayList<Integer> cards = new ArrayList<>();
		int [] arr=cardsort(tarr,true);
		int level = -1;
		int []cNum = new int[13];
		tempInfo1=-1;
		tempInfo2=-1;
		for(int i=0; i<7; i++){
			cNum[ arr[i]%13 ]++;
			if(cNum[ arr[i]%13 ] >= 2){
				if(tempInfo1 == -1)
					tempInfo1 = arr[i]%13;
				else if(tempInfo2 == -1)
					tempInfo2 = arr[i]%13;
				if( tempInfo1!= -1 && tempInfo2 != -1)
					level = 3;
			}
		}
		
		if(level == 3 )
		{
			for(int i=0; i<7; i++){
				if( arr[i]%13 == tempInfo1 || arr[i]%13 == tempInfo2  ){
					cards.add( arr[i] );
				}
			}
			JSONObject win = MakeWinCard(level, cards);
			currentUser.wincard.add(win);
			if( tempInfo1%13==0)tempInfo1=13;
			return true;
		}
		
		return false;
	}

	public boolean checkThree(int tarr[]){
		ArrayList<Integer> cards = new ArrayList<>();
		int [] arr=cardsort(tarr,true);
		int level = -1;
		int []cNum = new int[13]; 
		for(int i=0; i<7; i++){
			cNum[ arr[i]%13 ]++;
			if(cNum[ arr[i]%13 ] >= 3){
				tempInfo1 = arr[i]%13;
				level = 4;
			}
		}
		
		if(level == 4 )
		{
			for(int i=0; i<7; i++){
				if( arr[i]%13 == tempInfo1 ){
					cards.add( arr[i] );
				}
			}
			JSONObject win = MakeWinCard(level, cards);
			currentUser.wincard.add(win);
			if(tempInfo1%13==0)tempInfo1=13;
			return true;
		}
		
		return false;
	}

	public boolean checkFourCard(int tarr[]){
		ArrayList<Integer> cards = new ArrayList<>();
		int [] arr=cardsort(tarr,true);
		int level = -1;
		int []cNum = new int[13]; 
		for(int i=0; i<7; i++){
			cNum[ arr[i]%13 ]++;
			if(cNum[ arr[i]%13 ] >= 4){
				tempInfo1 = arr[i]%13;//숫자네개
				level = 8;//포카드
			}
		}
		
		if(level == 8 )
		{
			for(int i=0; i<7; i++){
				if( arr[i]%13 == tempInfo1 ){
					cards.add( arr[i] );
				}
			}
			
			JSONObject win = MakeWinCard(level, cards);
			currentUser.wincard.add(win);
			if(tempInfo1 == 0)tempInfo1 = 13;
			return true;
		}
		
		return false;
	}
	
	//9페어:동일한숫자 한쌍   8투페어:동일한숫자 두쌍  7트리플: 동일한세장      3포카드 : 동일한숫자 4장 
	public int checkAllpair(int arr[]){
		
		ArrayList<Integer> cards = new ArrayList<>();
		
		int level = 100;
		int twopair = 0;
		int []cNum = new int[13]; 
		for(int i=0; i<7; i++){
			cNum[ arr[i]%13 ]++;
			if(cNum[ arr[i]%13 ] >= 4){
				tempInfo1 = arr[i]%13;//숫자네개
				cards.add(arr[i]%13);
				System.out.println("tempInfo1:"+tempInfo1);
				level = 3;//포카드
			}
			else if(cNum[ arr[i]%13 ] == 3 && level >7){
				cards.add(arr[i]%13);
				tempInfo1 = arr[i]%13;
				level = 7;//트리플 숫자세개
			}
			else if(cNum[ arr[i]%13 ] >= 2){//페어 , 투페어
				twopair++;				
				if(twopair==2  && level >8) {
					tempInfo2 = arr[i]%13;//숫자
					cards.add(arr[i]%13);
					level = 8; // 투페어				
				}
				else if(twopair==1){
					tempInfo1 = arr[i]%13;//숫자
					cards.add(arr[i]%13);
					level = 9; 
				}
			}
			
		}
		
		if(level < 10 )
		{
			JSONObject win = MakeWinCard(level, cards);
			currentUser.wincard.add(win);
		}
		
		return level;
	}
	
	//true 이면  6 스트레이트 : 9 10 j q k
	public boolean checkStraight(int tarr[]){
		ArrayList<Integer> cards = new ArrayList<>();

		int arr[];
		int ct = 0;
		int pre = 0;	
		
		
		ct=0;
		pre=0;
		arr=cardsort(tarr,true);//먼저 에이스를 13으로 놓고 스트레이트 체크
		for(int i=0; i<7; i++){
			if(pre == arr[i]%13 )continue;//8 7 (7) 6 5 4 3 <==  같은수 걸러내기
			if(i!=0 && pre-1 != arr[i]%13 )
			{
				ct=0;
				cards.clear();
			}
			ct++;
			cards.add( arr[i] );
			if( ct == 1 ){//스트레이트 탑카드
				tempInfo1 = arr[i]%13;
			}
			if(ct>=5){					
				break;
			}
			pre= arr[i]%13;
		}
		if(ct <5){//스트레이트가 안되었다면 에이스를 0으로 놓고 다시 스트레이트 체크 한번더
			ct=0;
			pre=0;
			arr=cardsort(tarr);//에이스를 0으로 놓고 스트레이트 체크
			for(int i=0; i<7; i++){
				if(pre == arr[i]%13 )continue;//8 7 (7) 6 5 4 3 <==  같은수 걸러내기
				if(i!=0 && pre-1 != arr[i]%13 )
				{
					ct=0;
					cards.clear();
					cards.add( arr[i] );
				}
				ct++;
				cards.add( arr[i] );
				if( ct == 1 ){//스트레이트 탑카드
					tempInfo1 = arr[i]%13;
				}
				if(ct>=5){					
					break;
				}
				pre= arr[i]%13;
			}
		}
		
		
		if( ct >= 5 ){
			if( tempInfo1%13 == 0 ) tempInfo1 = 14;//마운틴 스트레이트
			if( tempInfo1%13 == 4 ) tempInfo1 = 13;//에이스 스트레이트
			JSONObject win = MakeWinCard(5, cards);
			currentUser.wincard.add(win);
			return true;
		}
		return false;
	}
	//true 이면 5 플러시  : 하트5장
	public boolean checkFlush(int tarr[]){
		int []arr=cardsort(tarr,true);
		int []shape = new int[4]; 
		ArrayList<Integer> cards = new ArrayList<>();
		tempInfo1 = -1;
		int lv= -1;
		for(int k=0; k<7; k++){
			shape[ (int)(arr[k]/13) ]++;
			if( shape[ (int)(arr[k]/13) ] >= 5 ) {
				tempInfo2 = (int)(arr[k]/13);
				lv = 6;
			}
		}
		if( lv == 6 ){
			int tmp5=0;
			for(int k=0;k<7;k++){
				if(  (int)(arr[k]/13) == tempInfo2 && tmp5<5 ){
					if( tempInfo1 == -1)
						tempInfo1 = arr[k];
					cards.add( (arr[k]) );
					tmp5++;
				}
			}
			JSONObject win = MakeWinCard(5, cards);
			currentUser.wincard.add(win);
			if( tempInfo1%13==0)tempInfo1=13;
			return true;
		}
		return false;
	}
	
	//10 탑카드  : 제일 큰숫자 한장 리턴
	public int checkTopCard(int tarr[]){
		int[] arr=cardsort(tarr,true);
		int pre = -1;		
		ArrayList<Integer> cards = new ArrayList<>();

		tempInfo1 = arr[0];

		cards.add(tempInfo1);
		
		JSONObject win = MakeWinCard(1, cards);
		currentUser.wincard.add(win);
		if(tempInfo1%13 ==0)tempInfo1 = 13;
		
		return tempInfo1;
	}	
	
	
	int tempInfo1,tempInfo2;//임시 카드정보
	public void showResult(){
		ArrayList<User> sortRank;
		whosturn=0;
		bbBetCount = 0;		
		System.out.println("SHOW RESULT ");
		//결과 계산하기
		// 유저들의 카드 목록 2차원 배열 출력
		int winSeat=-1,wlv=10000;
		
		
		//기권승시 족보계산안함 ,이긴사람 돈줌 
		if( this.checkAbstention() ){
			sortRank=new ArrayList<User>();
			int cnt=0;
			for(User u : userlist){
				if(u.die == false){
					winSeat = cnt;
					sortRank.add(u);
				}
				cnt++;
			}
			wlv = 99;
		}else {
			for(int k = 0; k<cardarr.length; k++){						
				
				currentUser = userlist.get(k); 
				currentUser.wincard.clear();
				
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
				int lv =-1;// checkAllpair(card);
				//스트레이트 플러시 2
				if(checkStraightFlush(card)==true){//
					lv=9;
					currentUser.jokbocode=90000000+tempInfo1*100000;
				}else if(checkFourCard(card) == true ){// 포카드 *
					lv=8;
					currentUser.jokbocode=80000000+tempInfo1*100000;
				}else if(checkFullHouse(card)==true){//풀하우스 *
					lv=7;
					currentUser.jokbocode=70000000+tempInfo1*100000+tempInfo2*1000;
				}else if(checkFlush(card)==true){//플러시 모양 *
					lv=6;
					currentUser.jokbocode=60000000+tempInfo1*100000;
				}else if(checkStraight(card)==true){//스트레이트숫자 *?
					lv=5;
					currentUser.jokbocode=50000000+tempInfo1*100000;
				}else if(checkThree(card)==true){//트리플*
					lv=4;
					currentUser.jokbocode=40000000+tempInfo1*100000;
				}else if(checkTwoPair(card)==true){//투페어*
					lv=3;
					currentUser.jokbocode=30000000+tempInfo1*100000+tempInfo2*1000;
				}else if(checkPair(card)==true){//원페어 *
					lv=2;
					currentUser.jokbocode=20000000+tempInfo1*100000;
				}else {//탑카드 
					lv=1;
					checkTopCard(card);
					currentUser.jokbocode=10000000+tempInfo1*100000;
				}
				
			}
			
			//유저  족보 순위 정렬
			sortRank=(ArrayList<User>) userlist.clone();
			Collections.sort(sortRank, new Comparator<User>() {
	            @Override public int compare(User s1, User s2) {
	            	return s2.jokbocode - s1.jokbocode;
	            }
	        });
		}

		winSeat=sortRank.get(0).seat;// 이게 유저 자리 번호 맞나? 확인하기 2020 09 10
		
		
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
			u.PlayStatus = 1;
		}	
		
		setDealerSeat();
		
		System.out.println("승자 족보레벨:"+ (sortRank.get(0).jokbocode/10000000) +" 승자id:"+sortRank.get(0).nickname + " card:"+sortRank.get(0).wincard.toString() );
		
		JSONObject obj = new JSONObject();
		obj.put("cmd","showResult");
		obj.put("wlv",""+sortRank.get(0).jokbocode/10000000);//레벨순서 변경됨.
		obj.put("cardInfo1",sortRank.get(0).card1);//이제 안씀.
		obj.put("cardInfo2",sortRank.get(0).card2);//이제 안씀.
		obj.put("winnerbalance",""+sortRank.get(0).balance);
		obj.put("winmoney",""+this.totalmoney);
		obj.put("winSeat",""+winSeat);		
		obj.put("usersize",""+userlist.size());		
		obj.put("wincard", sortRank.get(0).wincard);//
		
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
	
	private JSONObject MakeWinCard(int lv, ArrayList<Integer> cards) {
		
		JSONObject win = new JSONObject();
		win.put("lv", lv);
		win.put("cards", cards);
		
		return win;
	}
	
	private User SearchUserBySeat(int seat)
	{
		for( User user : userlist )
		{
			if( user.seat == seat )
			{
				return user;				
			}
		}
		
		return null;		
	}
	
}

