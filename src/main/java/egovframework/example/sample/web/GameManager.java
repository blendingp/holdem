package egovframework.example.sample.web;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.json.JsonObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.ui.Model;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import egovframework.example.sample.web.model.BanModel;

public class GameManager {		
	public ArrayList<User> userlist = new ArrayList<User>();	
	public ArrayList<User> leaveuserlist = new ArrayList<User>();	
	public ArrayList<User> watchinguserlist = new ArrayList<User>();	
	public int[] seats = {-1, -1, -1, -1, -1, -1, -1, -1, -1};		
	public CardManager cardManager = new CardManager();	
	//public User reuser = new User();
	int gameId;//게임로그에 저장되는 게임번호
	String gameIdentifier;//게임로그에 저장되는 게임번호
	int gu;//1구 2구 3구 4구
	String GameMode="대기";	
	//Calendar startTime;
	long timerStartTime = 0;//객체로 만들면 좋다
	int whosturn = 0;//누구차례인지 
	int turncnt = 0;//각 구에서 몇번의 베팅이 되었는 카운팅
	int totalcnt= 0;//현 게임에서 몇번의 베팅이 되었는지 카운팅( sb,bb가 베팅을 했는지 체크하기 위함)
	long money = 0;
	long totalmoney = 0;
	long prebetmoney =0 ;//이전 사람의 베팅머니
	long preTotalBetmoney=0;//이전 사람의 현재 구의 총 배팅머니/ 콜금액 계산용.	
	
	int workTime=0;
	
	int timer = -1;
	int dealerSeatNum = 0;//누가딜러인지 유저인덱스 저장
	int lastCmdSecond=1000000;//마지막 명령을 받은 시간, 1분  이상 누구도 명령을 내리지 않은 상태인데 방이 대기 상태가 아니라면 에러난 상태이므로 방을 강제 초기화 시켜야 함.
	User outSBUser= null,outBBUser=null;// 첫 베팅하기전에 sb, bb 유저가 나가면 해당 유저 객체를 저장해둔다. 대신 베팅 처리를 위해서.
	long lastcallbackmoney=0,lastbetmoney=0;//마지막 콜 못받은 금액 환불처리하기위해 지정변수
	int lastbetuser=0;
	
	Card card1;
	Card card2;
	Card card3;
	Card card4;
	Card card5;

	Room room;
	
	User currentUser = null;
	
	int bbBetCount = 0;
	int bbSeat = 0;

	private int allincount = 0;
	
//	ArrayList<Pot> gamePot = new ArrayList<>();
	
	Caculate cal=new Caculate();
	
	/*
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
		outSBUser = outBBUser = null;
		System.out.println("게임초기화");
	}*/
	
	public GameManager(Room room){
		this.room = room;
	}

	public int getGameId(){
		return gameId;
	}
	
	public void setGameId(int gameid){
		this.gameId = gameid;
	}
	
	public String getGameIdentifier(){
		return gameIdentifier;
	}
	
	
	public void setGameIdenfitifer(String gameidentifier){
		this.gameIdentifier = gameidentifier;
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
		if( seat >= 0 )
		{
			seats[seat] = -1;
		}		
	}
	
	void resetGuBetmoney(){
		for(User u : userlist)
			u.currentGuBetMoney = 0;
	}
	void sendRoom(JSONObject obj){
		sendList(obj, userlist);
		sendList(obj, watchinguserlist);
	}
	void sendList(JSONObject obj, ArrayList<User> lst){

		ObjectMapper mapper = new ObjectMapper();

		for(User u : lst){
			if( u == null )
			{
				continue ;
			}

			if( u.session == null )
			{
				continue ;
			}

			if( u.session.isOpen() == false )
			{
				continue ;
			}

			try {								
				u.session.sendMessage(new TextMessage(mapper.writeValueAsString(obj)));
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}			
	}

	void startCheck(User user, ArrayList<User> userlist){
						
		if(userlist.size() >= 2) {			
			setWorkTime( );
			changeGameMode("대기");
		}

	}

	boolean isPlayable(){				

		if(userlist.size() >= 2) {			
			return true;
		}
		return false;
	}


	void DealerSeatSetting(){
		dealerSeatNum = getDealerSeatOffset(0);
	}
	void startSetting() throws NoSuchAlgorithmException{		
		totalcnt = 0;
		lastbetmoney = 0;
		lastcallbackmoney=0;
		lastbetuser = -1;
		gu = 1;
		room.roominfo.StartGame();
		setGameId(SocketHandler.GameIdxAdder());
		setGameIdenfitifer(SocketHandler.getGameIdentifier());
//		gamePot.clear();
//		gamePot.add(new Pot());

		card1 = null;
		card2 = null;
		card3 = null;
		card4 = null;
		card5 = null;
		
		SocketHandler.insertLog(getGameId(), getGameIdentifier(),"gamestart", -1, -1, -1, "게임시작", -1, -1);
		whosturn = 0;
		turncnt = 0;
		totalmoney = 0;
		prebetmoney =0 ;//이전 사람의 베팅머니
		preTotalBetmoney=0;//이전 사람의 현재 구의 총 배팅머니/ 콜금액 계산용.		

		//cardarr = new int[userlist.size()][7];

		for(User u : userlist){
			
			if( u == null )
			{
				continue;
			}
			
			u.init();
			u.PlayStatus = 1;
			u.jokbocode = 0;
			money = 0;
			u.betmoney = 0;
			u.cardarr.clear();
			u.wlv = 99;
			long usermoney = 0;			
			usermoney = room.defaultmoney;
			if( room.UsedItem.equals("balance") == true){
				u.prevamount = u.balance;					
				
				if( u.balance < room.defaultmoney )
				{
					usermoney = u.balance;
					u.balance = 0;
				}
				else
				{
					u.balance -= room.defaultmoney;	
				}

				u.todayprofile.gainbalance -= usermoney;
			}			
			else if( room.UsedItem.equals("point") == true){
				u.prevamount = u.point;								

				if( u.point < room.defaultmoney )
				{
					usermoney = u.point;
					u.point = 0;
				}
				else
				{
					u.point -= room.defaultmoney;	
				}

				u.todayprofile.gaingold -= usermoney;
			}	

			u.betmoney = usermoney;
			u.wincard.clear();

			JackpotManager.SendJackpotMessage(u);

			Task.IncreaseTask(u, 0, 1);
			Task.UpdateDB(u);			
			
			u.CheckExpireTodayRecord();
			u.totalprofile.totalgame++;
			u.todayprofile.totalgame++;
			ProfileManager.UpdateProfile(u.totalprofile);
			ProfileManager.UpdateTodayProfile(u.todayprofile);
			totalmoney += usermoney;
			outSBUser = outBBUser = null;
			SocketHandler.insertLog(getGameId(), getGameIdentifier(), "join", u.uidx , u.balance , u.seat , "참가머니", usermoney , -1);
		}			
		
		allincount = 0;		
	}
	
	void setDealerSeat()
	{
		int seat = dealerSeatNum + this.seats.length - 1;
		for( int nCount = this.seats.length * 2; nCount >= 0; --nCount )
		{						
			if( this.seats[(seat + nCount)%this.seats.length] >= 0 )
			{
				if( IsJoinGame((seat + nCount)%this.seats.length) == false)				
				{
					continue;					
				}

				if( room.UsedItem.equals("balance") == true){
					if( SearchUserBySeat((seat + nCount)%this.seats.length).balance <= 0 )
					{
						continue;					
					}
				}
				else if( room.UsedItem.equals("point") == true){
					if( SearchUserBySeat((seat + nCount)%this.seats.length).point <= 0 )
					{
						continue;					
					}
				}					
				
				dealerSeatNum = (seat + nCount)%this.seats.length;				
				return ;
			}			
		}						
	}
	
	int getDealerSeat(){
		
		for( int nCount = this.seats.length * 2; nCount >= 0; --nCount )
		{	
			if( IsJoinGame((dealerSeatNum + nCount)%this.seats.length) == false)				
			{
				continue;					
			}
			
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
		
		for( int nCount = this.seats.length * 2; nCount >= 0; --nCount )
		{				
			if( this.seats[(seat + nCount)%this.seats.length] >= 0 )
			{				
				if( IsJoinGame((seat + nCount)%this.seats.length) == false)				
				{
					continue;					
				}

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
		GameMode = mode;
	}
	void setWorkTime(){
		workTime = SocketHandler.second; 
	}
	boolean checkCmdTime(int timecmp){
		if(SocketHandler.second > workTime+timecmp)
			return true;
		return false;
	}

	void checkStartGame() throws NoSuchAlgorithmException{
		if(GameMode.compareTo("checkstart")==0 ){
			 if(isPlayable() )
			 {
				startSetting();
				DealerSeatSetting();
				notifyGameStart();
				cardManager.init();
				changeGameMode("twoCard");
			 }else
			 {
				 changeGameMode("대기");
				 setWorkTime();
			 }
		}
		
		if(GameMode.compareTo("twoCard")==0)
		{
			if (userlist.size() <= 1) {
				setWorkTime();
				changeGameMode("showResult");
			} else {
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
			if( checkCmdTime(userlist.size()*2) ){
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
			if(  checkCmdTime(1)  ){
				changeGameMode("showBetPan");
				showBetPan();
			}
		}
		if(GameMode.compareTo("THEEND")==0){
			setWorkTime();
			try {
				showResult();
				room.BroadCasetUser();
			}catch(Exception e) {
				System.out.println("error log: showResult 계산시 에러 발생================"+e.toString() );
			}
			changeGameMode("showResult");
		}
		
		if(GameMode.compareTo("showResult")==0){
			if( checkCmdTime(5) ){
				changeGameMode("대기");
				checkOutUser();
				LeaveReserveUser();
				setWorkTime(); 
			}
		}
		
		if(GameMode.compareTo("대기")==0){
			LeaveReserveUser();
			if (isPlayable() && checkCmdTime(6) ){
				setWorkTime();
				changeGameMode("checkstart");
			}
		}
	}

	void LeaveReserveUser()
	{		
		while( leaveuserlist.size() > 0 )
		{
			User user = leaveuserlist.get(0);
			room.notifyLeaveUser(user.seat);
			room.leave(user);
		}

		leaveuserlist.clear();
	}
	
	void checkOutUser(){
		//돈이 없는 유저 내보내기
		ArrayList<User> rmlist=new ArrayList<User>();
		for(User u : userlist) 
		{
			if( u == null )
			{
				continue ;
			}

			if( u.session == null )
			{
				continue ;
			}

			if( u.session.isOpen() == false )
			{
				continue ;
			}

			if( room.UsedItem.equals("balance") == true){
				
				if( room.isPrivate() == false && u.balance < room.defaultmoney * 3){
					rmlist.add(u);
				}
				else if( room.isPrivate() == true && u.balance <= 0){
					rmlist.add(u);
				}


				if( u.balance <= 0 && u.todayprofile.goldrefillcount > 0)
				{
					u.todayprofile.goldrefillcount = 0;
					u.balance = 500;
					u.ApplyBalanace("balance");
					ProfileManager.UpdateTodayProfile(u.todayprofile);
					JSONObject goldrefill = new JSONObject();
					goldrefill.put("cmd","goldrefill");

					u.sendMe(goldrefill);
				}

			}			
			else if( room.UsedItem.equals("point") == true){
				
				if( u.point < room.defaultmoney * 3){
					rmlist.add(u);
				}

				if( Math.abs(u.todayprofile.gaingold) >= u._info.limit && u.todayprofile.gaingold < 0)
				{					
					BanModel ban = new BanModel();
					ban.type = 1;
					ban.expire = System.currentTimeMillis() + 86400000;
					JSONObject obj = new JSONObject();											
					obj.put("cmd","ban");
					obj.put("ban", ban);

					ObjectMapper mapper = new ObjectMapper();
						
					try {
						u._info.ban = mapper.writeValueAsString(ban);
						u.UpdateMemberInfo();
						if(User.CheckSendPacket(u) == true)
						{
							u.session.sendMessage(new TextMessage(mapper.writeValueAsString(obj)));
						}
				    	synchronized(SocketHandler.sk.disconnectlist) {
				    		if( SocketHandler.sk.disconnectlist.contains(u.session) != true) {
				    			SocketHandler.sk.disconnectlist.add(u.session);
				    		}
				    	}
						SocketHandler.sk.disconnect();
						u.session.close();						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}				
		}
		/*for(User u:rmlist)
			room.leave(u);*/
		//room.notifyRoomUsers();
		JSONObject obj2 = new JSONObject();
		obj2.put("cmd","roomout");
		sendList(obj2,rmlist);//나간 유저에게는 당신 돈 없어서 나갔다는 패킷 보내줌
	}

	void checkTimerGame(){
		//배팅시간 지났음
		if( GameMode.compareTo("sbBeted")==0 || GameMode.compareTo("bbBeted")==0 || GameMode.compareTo("nmBet")==0  
				|| GameMode.compareTo("showBetPan")==0 || GameMode.compareTo("THEFLOP")==0				
				|| GameMode.compareTo("THETURN")==0 || GameMode.compareTo("THERIVER")==0)
		{
			if(timer!=-1 && SocketHandler.second - timer > 20)// 자기턴 타임아웃 시간 8초로.
			{ 				
				for(User u : userlist){					
					if(u.seat == whosturn ){						
						timer = SocketHandler.second;							
						bet(u, 0);	
						break;									
					}
				}										
			}
						
			for( User user : leaveuserlist )
			{
				if( user.die == true )
				{
					continue;	
				}

				if(user.seat == whosturn )
				{
					if( totalcnt ==0 && this.outSBUser != null)
					{
						System.out.println("SB 유저가 강종되어 서버가 대신 SB 베팅을 시도 해줌. uidx :"+outSBUser.uidx);
						bet(outSBUser,1);
					}
					else if( totalcnt ==1 && this.outBBUser != null)
					{
						System.out.println("BB 유저가 강종되어 서버가 대신 BB 베팅을 시도 해줌. uidx :"+outBBUser.uidx);
						bet(outBBUser,3);
					}
					else {
						System.out.println("예약 유저 다이처리=================== whosturn: "+whosturn);
						bet(user, 0);
					}
					break;
				}
			}
		}
	}

	public void notifyGameStart() {//게임 시작을 알림
		JSONObject obj = new JSONObject();					
		obj.put("cmd","startGame");
		obj.put("gameid", gameId);
		obj.put("smoney",  totalmoney );
		obj.put("maxmoney", room.maxmoney);
		obj.put("roompeople", userlist.size() );
		obj.put("dealer", getDealerSeat() );
		obj.put("smallblind", getDealerSeatOffset(1) );
		obj.put("bigblind", getDealerSeatOffset(2));
		sendRoom(obj);
	}
	
	int preJockboCheck() {
		ArrayList<User> tuserlist=(ArrayList<User>) userlist.clone();
		ArrayList<User> tsortRank;
		tsortRank=(ArrayList<User>) tuserlist.clone();

		int ci = 0;
		int ui = -1;
		for( User user : tuserlist )
		{
			ui++;
			currentUser = user;
			int []card = new int[7]; 		

			
			card[0] =  cardManager.cardlist.get(ui*2+0).cardcode;
			card[1] =  cardManager.cardlist.get(ui*2+1).cardcode;
			for(int i = 0; i < 5; i++){				
				card[2+i] = cardManager.cardlist.get(tuserlist.size()*2 + i).cardcode ;
			}
			
			//3포카드 7트리플 8투페어 9페어			
			
			//스트레이트 플러시 2
			if(checkStraightFlush(card)==true){//
				currentUser.wlv = 9;
				currentUser.jokbocode=0x9000000+tempInfo1;
				currentUser.balance += JackpotManager.GetJackpotAmount();
				JackpotManager.WithdrawJackpot();
			}else if(checkFourCard(card) == true ){// 포카드 *
				currentUser.wlv = 8;
				currentUser.jokbocode=0x8000000+tempInfo1*0x10 + tempInfo3;
			}else if(checkFullHouse(card)==true){//풀하우스 *
				currentUser.wlv = 7;
				currentUser.jokbocode=0x7000000+tempInfo1*0x10+tempInfo2;
			}else if(checkFlush(card)==true){//플러시 모양 *
				currentUser.wlv = 6;
				currentUser.jokbocode=0x6000000+tempInfo3;
			}else if(checkStraight(card)==true){//스트레이트숫자 *?
				currentUser.wlv = 5;
				currentUser.jokbocode=0x5000000+tempInfo1;
			}else if(checkThree(card)==true){//트리플*
				currentUser.wlv = 4;
				currentUser.jokbocode=0x4000000+tempInfo1*0x100+tempInfo3;
			}else if(checkTwoPair(card)==true){//투페어*
				currentUser.wlv = 3;
				currentUser.jokbocode=0x3000000+tempInfo1*0x100+tempInfo2*0x10+tempInfo3; 
			}else if(checkPair(card)==true){//원페어 *
				currentUser.wlv = 2;
				currentUser.jokbocode= 0x2000000+tempInfo1*0x1000 + tempInfo3;
			}else {//탑카드 
				currentUser.wlv = 1;
				checkTopCard(card);
				currentUser.jokbocode= 0x1000000 + tempInfo3; 
			}
			
//			SocketHandler.insertLog(getGameId(), "card", currentUser.uidx , currentUser.jokbocode , -1, ""+cardForDebug , -1 , -1 );
		}

		//유저  족보 순위 정렬
		tsortRank=(ArrayList<User>) tuserlist.clone();
		Collections.sort(tsortRank, new Comparator<User>() {
            @Override public int compare(User s1, User s2) {
            	return s2.jokbocode - s1.jokbocode;
            }
        });
		cardManager.popcard=0;
		return tsortRank.get(0).seat;
	}

	public void drawCard(){
		cardManager.shuffleCard();

		int winseat = preJockboCheck();

		JSONObject obj = new JSONObject();

		//카드 두장씩 세팅
		for(int k =0; k<userlist.size(); k++){
			userlist.get(k).setCard(cardManager.popCard(), cardManager.popCard());
			//JSONObject item = new JSONObject();
			obj.put("cmd", "giveTwoCard");
			obj.put("seat", userlist.get(k).seat);
			obj.put("card1", userlist.get(k).card1.cardcode);
			obj.put("card2", userlist.get(k).card2.cardcode);
			if( userlist.get(k).isAI == true )
			{
				if( winseat == userlist.get(k).seat)
					obj.put("wr", "1");
				else
					obj.put("wr", "2");
			}else
			{
				obj.put("wr", "-1");
			}
			userlist.get(k).cardarr.add(userlist.get(k).card1.cardcode);
			userlist.get(k).cardarr.add(userlist.get(k).card2.cardcode);
			SocketHandler.insertLog(getGameId(), getGameIdentifier(), "twoCard", userlist.get(k).uidx, userlist.get(k).card1.cardcode, userlist.get(k).card2.cardcode, "", -1, -1);

			try {
				if(User.CheckSendPacket(userlist.get(k)) == true)
				{
					userlist.get(k).session.sendMessage(new TextMessage(obj.toJSONString()));	
				}				
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public int getWhoTurn(){
										
		for( int nCount = this.seats.length * 2; nCount >= 0 ; --nCount )
		{							
			if( this.seats[(whosturn + nCount)%this.seats.length] >= 0 )
			{				
				if( checkDieturn((whosturn + nCount)%this.seats.length) == true )
				{
					if( SearchUserBySeat((whosturn + nCount)%this.seats.length) != null )
					{
						SearchUserBySeat((whosturn + nCount)%this.seats.length).PlayStatus = -1;	
					}					
					continue;					
				}
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
		obj.put("whosturn", whosturn );
		obj.put("prebetmoney", preTotalBetmoney );
		obj.put("myBetMoney", SearchUserBySeat(whosturn).betmoney );
		sendRoom(obj);
	}

	public void bbBet(){
		bbSeat = whosturn;
		bbBetCount++;
		JSONObject obj = new JSONObject();
		obj.put("cmd","bbBet");
		obj.put("whosturn", whosturn );
		obj.put("prebetmoney", preTotalBetmoney );
		obj.put("myBetMoney", SearchUserBySeat(whosturn).betmoney );
		sendRoom(obj);
		timer = SocketHandler.second;		
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
		sendRoom( obj);
	}

	public long thisTurnMoneyCompute(int kind, long mybetmoney){

		if(kind==0) // 다이
			return 0;
		else if(kind==1) // 삥
			return room.defaultmoney;
		else if(kind==2)// 콜
			return preTotalBetmoney - mybetmoney;
		else if(kind==3)// 따당 
			return prebetmoney*2;
		else if(kind==4){// 하프
			long tc = preTotalBetmoney - mybetmoney;
			return tc + ((totalmoney  + tc)/2);
		}
		else if(kind==5){// 풀
			long tc = preTotalBetmoney - mybetmoney;
			return tc + (totalmoney+tc) ;
		}
		else if(kind==6)// 맥스
			return room.maxmoney;
		else if(kind==7){// 쿼터
			long tc = preTotalBetmoney - mybetmoney;
			return tc + ((totalmoney + tc)/4);
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

		int betablecount = userlist.size();
		for(User uu : userlist){
			if( uu.die == true)
			{
				betablecount--;	
			}				
			if( room.UsedItem.equals("balance") == true){
				if( uu.balance <= 0)
				{
					betablecount--;						
				}	
			}
			else if( room.UsedItem.equals("point") == true){
				if( uu.point <= 0)
				{
					betablecount--;						
				}	
			}
		}
						
		for(User uu : userlist){
			
			if( uu.die == true)
			{
				continue;		
			}	
			
			if( room.UsedItem.equals("balance") == true){
				if( uu.balance <= 0 || uu.betmoney >= room.maxmoney)
				{
					continue;			
				}	
			}
			else if( room.UsedItem.equals("point") == true){
				if( uu.point <= 0 || uu.betmoney >= room.maxmoney)
				{
					continue;				
				}	
			}

			if(uu.PlayStatus == 1 && betablecount > 1)
			{			
				return false;
			}										
			
			if( uu.betmoney < money)
			{										
				return false;//배팅금액이 다르다
			}												
			extrap++;
			lastp = uu;				
					
		}
		
		for(User uu : userlist){
			if( uu.die == true)
			{
				continue;
			}	

			if( room.UsedItem.equals("balance") == true){
				if( uu.balance <= 0 || uu.betmoney >= room.maxmoney)
				{
					continue;			
				}	
			}
			else if( room.UsedItem.equals("point") == true){
				if( uu.point <= 0 || uu.betmoney >= room.maxmoney)
				{
					continue;				
				}	
			}

			if(uu.betmoney != preTotalBetmoney)
			{			
				return false;
			}
		}	

		if( room.isPrivate() == true )
		{
			for( User user : userlist)
			{
				if(user.PlayStatus == 1)
				{
					return false;
				}
			}
		}
								
		return true;//배팅금액이 똑같은경우 배팅끝
	}
	
	public void  nextTurn(){
		whosturn += this.seats.length - 1;//다음사람배팅
		if( getWhoTurn() < 0 )
		{			
			SocketHandler.insertLog(getGameId(), getGameIdentifier(), "error", -1, -1, -1, "DieTurnCheckError", -1, -1);
		}
	}

	public void bet(User u, int betkind){			
		if( whosturn != u.seat ){
			System.out.println(whosturn+" 잘못된 유저의 BET 차례 "+u.seat);
			return;
		}

		if(betkind==0)
		{ 
			u.die = true;
			u.PlayStatus = -1;
		}
		
		Boolean isAllIn = false;
		
		long tmo = thisTurnMoneyCompute(betkind , u.betmoney);		
		
		if( room.UsedItem.equals("balance") == true){
			if( u.balance <= tmo ){//올인인지 체크.
				tmo = u.balance;//올인 머니 셋팅
				isAllIn = true;
				if( tmo > 0 )
				{
					allincount++;
				}				
			}
		}
		else if( room.UsedItem.equals("point") == true){
			if( u.point <= tmo ){//올인인지 체크.
				tmo = u.point;//올인 머니 셋팅
				isAllIn = true;
				if( tmo > 0 )
				{
					allincount++;
				}			
			}
		}
		
		
		if( u.betmoney + tmo >= room.maxmoney ){//맥스 베팅인지 체크
			tmo = room.maxmoney - u.betmoney;
		}
		
/*		if( betkind != 0 )
		{
			gamePot.get(gamePot.size()-1).JoinPot(whosturn, tmo);	
		}		
		
		if( isAllIn == true )
		{									
			gamePot.add(gamePot.get(gamePot.size()-1).PotSlit(u.betmoney));
		}*/
		
		room.roominfo.Bet(tmo);
		
		u.lastbetmoney = tmo;
		u.betmoney += tmo ;//나의 배팅금액 현재돈+배팅금액
		money = prebetmoney;
		
		if( betkind != 8 && betkind != 0)
		{
			prebetmoney = tmo;
		}		
		
		if( preTotalBetmoney < u.betmoney)
		{
			preTotalBetmoney = u.betmoney;	
		}
		
		totalmoney += tmo;
		
		if( u.seat != lastbetuser )
		{
			if( lastbetmoney <= u.betmoney )
			{
				lastcallbackmoney = u.betmoney - lastbetmoney;
				lastbetmoney = u.betmoney;
				lastbetuser  = u.seat;
			}else
			{
				if( lastcallbackmoney  >  lastbetmoney - u.betmoney )
				{
					lastcallbackmoney = lastbetmoney - u.betmoney ;
				}
			}
		}
		
		//배팅한 사람 돈 차감 시키기!!!
		if( room.UsedItem.equals("balance") == true){		
			u.todayprofile.gainbalance -= tmo;	
			u.balance -= tmo;				

			JackpotManager.AccumulateJackpot(tmo);
		}
		else if( room.UsedItem.equals("point") == true){
			u.todayprofile.gaingold -= tmo;
			u.point -= tmo;	
		}
				
		u.ApplyBalanace(room.UsedItem);
		
		if( room.UsedItem.equals("balance") == true){
			SocketHandler.insertLog(getGameId(), getGameIdentifier(),"bet", u.uidx , u.betmoney , u.balance , "배팅액:"+tmo+", total:"+(totalmoney) , betkind, whosturn );
		}
		else if( room.UsedItem.equals("point") == true){
			SocketHandler.insertLog(getGameId(), getGameIdentifier(), "bet", u.uidx , u.betmoney , u.point , "배팅액:"+tmo+", total:"+(totalmoney) , betkind, whosturn );
		}		

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
			if(GameMode.compareTo("nmBet")==0 ){
				return ;

			}
			obj.put("cmd", "betsuc");			
			//System.out.println("<< 베팅 성공 >>");
			u.PlayStatus = 0;
		}
				
		turncnt++;
		totalcnt++;
		
		nextTurn();					
		long prev = prebetmoney;

		if( room.isPrivate() == true )
		{
			for( User user : userlist )
			{
				if( user.PlayStatus == 0 )
				{
					prev = 1;
				}
			}
		}		
				
		boolean isBetEnd = isGuBetEnd();
		obj.put("totalmoney", totalmoney );
		obj.put("prev", prev);
		obj.put("callmoney", "" + (preTotalBetmoney - u.betmoney) );
		obj.put("prebetmoney", preTotalBetmoney );
		obj.put("myBetMoney", u.betmoney );
		long amount = 0;		
		
		if( room.UsedItem.equals("balance") == true){		
			amount = u.balance;			
		}
		else if( room.UsedItem.equals("point") == true){			
			amount = u.point;			
		}
		
		obj.put("balance", amount);		
		obj.put("betkind", betkind);
		obj.put("seat", u.seat);//금방배팅한 사람
		obj.put("nextwho", whosturn );//이제 배팅할 사람의 번호

		/*
		if( preTotalBetmoney >= room.maxmoney)
		{
			obj.put("betenable", "0,1,0,1,0,0,0,0");//체크/폴드/삥/콜/따당/하프/풀/맥스
		}
		else
		{
			obj.put("betenable", "0,1,0,1,1,1,1,1");//체크/폴드/삥/콜/따당/하프/풀/맥스	
		}*/
		
		
		obj.put("gu", gu );
		if( isBetEnd )
			obj.put("betEnd", "1");//마지막 베팅인지 체크
		else
			obj.put("betEnd", "0");//마지막 베팅인지 체크

		timer = SocketHandler.second;

		if( room.isPrivate() == false)
		{
			if( GetAbleBettingUserCount() <= 1 && isBetEnd == true)
			{
				JSONArray j = new JSONArray();
				for(int i=0; i < userlist.size(); i++){
					JSONObject item = new JSONObject();
					item.put("seat",userlist.get(i).seat);			
					item.put("card1",userlist.get(i).card1.cardcode);
					item.put("card2",userlist.get(i).card2.cardcode);
					if( room.UsedItem.equals("balance") == true){					
						item.put("balance", userlist.get(i).balance);
					}
					else if( room.UsedItem.equals("point") == true){					
						item.put("balance", userlist.get(i).point);
					}				
					item.put("die",userlist.get(i).die);
					j.add(item);
				}
				obj.put("cardlist", j);	
			}			
		}		
					
		sendRoom(obj);//베팅 성공 정보를 전송					
		
		if( checkAbstention() ){
			TheEnd();
		}
		else if( isBetEnd )
		{
			
			for( User user : userlist )
			{
				if( user.PlayStatus == 0 )
				{
					user.PlayStatus = 1;
				}
			}
			
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
		
		for( int nCount = 0; nCount < watchinguserlist.size(); ++nCount )
		{
			if(watchinguserlist.get(nCount).seat == who)
			{
				return true;
			}
		}

		for( int nCount = 0; nCount < userlist.size(); ++nCount )
		{
			if(userlist.get(nCount).seat == who)
			{
				return userlist.get(nCount).die;
			}
		}
		
		return false;
	}

	private boolean checkLeave(int who)
	{
		for( int nCount = 0; nCount < leaveuserlist.size(); ++nCount )
		{
			if(leaveuserlist.get(nCount).seat == who)
			{
				return true;
			}
		}

		return false;
	}
	
	public void showThreeCard(){	
		timer = SocketHandler.second+2;
		whosturn=getDealerSeat();
		nextTurn();//딜러 다음 할 차례
		turncnt=0;		
		JSONObject obj = new JSONObject();		
		changeGameMode("THEFLOP");
		
	      /*카드변경******/
	      /*card1=cardManager.popCard(36);
	      card2=cardManager.popCard(37);
	      card3=cardManager.popCard(12);*/
		card1=cardManager.popCard();
		card2=cardManager.popCard();
		card3=cardManager.popCard();

		boolean isBetEnd = isGuBetEnd();

		ArrayList<Integer> cardlist = new ArrayList();
		cardlist.add(card1.cardcode);
		cardlist.add(card2.cardcode);
		cardlist.add(card3.cardcode);		

		obj.put("cmd","THEFLOP");
		obj.put("cardlist", cardlist);
		obj.put("whosturn", whosturn );	
		obj.put("betend", isBetEnd);
		SocketHandler.insertLog(getGameId(), getGameIdentifier(), "THEFLOP", -1, card1.cardcode, card2.cardcode, "", card3.cardcode, -1);
		for(int k =0; k<userlist.size(); k++){
			userlist.get(k).cardarr.add(card1.cardcode);
			userlist.get(k).cardarr.add(card2.cardcode);
			userlist.get(k).cardarr.add(card3.cardcode);
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

		boolean isBetEnd = isGuBetEnd();

		ArrayList<Integer> cardlist = new ArrayList();
		cardlist.add(card4.cardcode);		

		obj.put("cmd","THETURN");
		obj.put("cardlist", cardlist);
		obj.put("whosturn",whosturn );
		obj.put("betend", isBetEnd);
		SocketHandler.insertLog(getGameId(), getGameIdentifier(), "THETURN", -1, card4.cardcode, -1, "", -1, -1);
		for(int k =0; k<userlist.size(); k++){
			userlist.get(k).cardarr.add(card4.cardcode);
		}
		sendRoom(obj);
	}

	public void TheRiver(){
		timer = SocketHandler.second;
		turncnt = 0;
		whosturn=getDealerSeat();
		nextTurn();

		JSONObject obj = new JSONObject();
		GameMode = "THERIVER";
		card5=cardManager.popCard();	
		
		boolean isBetEnd = isGuBetEnd();

		ArrayList<Integer> cardlist = new ArrayList();
		cardlist.add(card5.cardcode);		

		obj.put("cmd","THERIVER");
		obj.put("cardlist", cardlist);
		obj.put("whosturn",whosturn );
		obj.put("betend", isBetEnd);
		SocketHandler.insertLog(getGameId(), getGameIdentifier(), "THERIVER", -1, card5.cardcode, -1, "", -1, -1);
		for(int k =0; k<userlist.size(); k++){
			userlist.get(k).cardarr.add(card5.cardcode);
		}
		sendRoom(obj);
	}

	public void TheEnd(){
		
		timer = -1;//타임아웃되는거 방지.

		JSONObject obj = new JSONObject();
		changeGameMode("THEEND");				
		obj.put("cmd","THEEND");
		SocketHandler.insertLog(getGameId(), getGameIdentifier(), "gameEnd", -1, totalmoney, -1, "게임 끝" , -1, -1);
		sendRoom(obj);
	}	
	

	public boolean checkStraightFlush(int tarr[]){
		ArrayList<Integer> cards = new ArrayList<>();
		
		int [] arr;
		int ct = 0;
		int pre = -1;	
		int preShape = -1;
		
		ct=0;
		pre=-1;
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
			pre=-1;
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
			tempNum[i] = arr[i];
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
	int cdval(int cd, boolean st){return st?(   cd%13==0?13:cd%13  ): cd%13 ; }
	int[] cardsort(int tcl[]){return cardsort(tcl,false);}
	int[] cardsort(int tcl[],boolean ace){
		int cl[]=tcl.clone();
		for(int a=0;a<cl.length;a++){
			for(int b=a+1;b<cl.length;b++){
				int tma = cdval(cl[a],ace);
				int tmb = cdval(cl[b],ace);
				if( tma < tmb ){
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
		tempInfo3 = 0;
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
				if(arr[i]%13 != tempInfo1)
				{
					if( tempInfo3 < 0x100)
					{
						tempInfo3=(tempInfo3*0x10+(arr[i]%13==0?0xd:arr[i]%13) );
					}
				}
				
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
		tempInfo3=-1;
		for(int i=0; i<7; i++){
			cNum[ arr[i]%13 ]++;
			if(cNum[ arr[i]%13 ] >= 2){
				if(tempInfo1 == -1)
					tempInfo1 = arr[i]%13;
				else if(tempInfo2 == -1)
					tempInfo2 = arr[i]%13;
				if( tempInfo1!= -1 && tempInfo2 != -1)
				{
					level = 3;
					break;
				}
			}
		}
		
		if(level == 3 )
		{
			for(int i=0; i<7; i++){
				if( arr[i]%13 != tempInfo1 && arr[i]%13 != tempInfo2  && tempInfo3 == -1)
				{
					int cnum = arr[i]%13==0?0xd:arr[i]%13;
					tempInfo3 =  cnum;
				}
				
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
		tempInfo3 = -1;
		ArrayList<Integer> cards = new ArrayList<>();
		int [] arr=cardsort(tarr,true);
		int level = -1;
		int []cNum = new int[13]; 
		for(int i=0; i<7; i++){
			cNum[ arr[i]%13 ]++;
			if(cNum[ arr[i]%13 ] >= 3){
				tempInfo1 = arr[i]%13;
				level = 4;
				break;
			}
		}
	    //0 1 2 3 4 5 6 7 8 9 a  b c d e
		//0 1 2 3 4 5 6 7 8 9 10 j q k
		if(level == 4 )
		{
			for(int i=0; i<7; i++){
				/*if(arr[i]%13 != tempInfo1) 
					if( tempInfo3 < 0x10)
						tempInfo3=(tempInfo3*0x10+(arr[i]%13==0?0xd:arr[i]%13) );*/
				if(arr[i]%13 != tempInfo1 ) 
				{
					int cnum = arr[i]%13==0?0xd:arr[i]%13;
					if(tempInfo3 == -1)
					{
						tempInfo3 = cnum;
					}
					else 
					{
						if( tempInfo3 < 0x10 )
						{
							tempInfo3 = tempInfo3*0x10 + cnum;
						}
					}
				}
				
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
				if( arr[i] % 13 != tempInfo1) {					
					tempInfo3 = arr[i]%13;
					break;
				}				
			}
			
			for(int i=0; i<7; i++){
				if( arr[i]%13 == tempInfo1 ){
					cards.add( arr[i] );
				}
			}
			
			JSONObject win = MakeWinCard(level, cards);
			currentUser.wincard.add(win);
			if(tempInfo1 == 0)tempInfo1 = 13;
			if(tempInfo3 == 0)tempInfo3 = 13;
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
		int pre = -1;	
		
		ct=0;
		pre=-1;
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
			if(pre == 0)
				pre = 13;
		}
		if(ct <5){//스트레이트가 안되었다면 에이스를 0으로 놓고 다시 스트레이트 체크 한번더
			ct=0;
			pre=-1;
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
			if( tempInfo1%13 == 0 ) tempInfo1 = 14;//마운틴
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
		tempInfo3 = 0;
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
					if( tempInfo3 < 0x10000)
					{
						tempInfo3=(tempInfo3*0x10 + getnum(arr[k]) );
					}
					cards.add( (arr[k]) );
					tmp5++;
				}
			}
			JSONObject win = MakeWinCard(6, cards);
			currentUser.wincard.add(win);
			return true;
		}
		return false;
	}
	
	int getnum(int g)
	{
		return g%13 == 0? 13 : g%13;
	}
	//10 탑카드  : 제일 큰숫자 한장 리턴
	public int checkTopCard(int tarr[]){
		int[] arr=cardsort(tarr,true);
		ArrayList<Integer> cards = new ArrayList<>();

		tempInfo3 = getnum(arr[0])*0x10000 + getnum(arr[1])*0x1000 + getnum(arr[2])*0x100 + getnum(arr[3])*0x10 + getnum(arr[4]) ;

		cards.add(arr[0]);
		
		JSONObject win = MakeWinCard(1, cards);
		currentUser.wincard.add(win);
		
		return tempInfo1;
	}	
	
	
	int tempInfo1,tempInfo2,tempInfo3;//임시 카드정보

	public void showResult(){
		ArrayList<User> sortRank;
		whosturn=0;
		bbBetCount = 0;		
		//결과 계산하기
		// 유저들의 카드 목록 2차원 배열 출력
		int winSeat=-1,wlv=10000;
		ArrayList<Integer> winners = new ArrayList<>();
		
		//승자정산하기 전에 콜을 못받은 머니는 환불해줌.
		if( room.isPrivate() != true || userlist.size() != 2)
		{
			if( lastcallbackmoney > 0 )
			{
				if( room.UsedItem.equals("balance") == true)
				{
					SearchUserBySeat(lastbetuser).balance += lastcallbackmoney;
				}else
				{
					SearchUserBySeat(lastbetuser).point += lastcallbackmoney;
				}
				SearchUserBySeat(lastbetuser).betmoney -= lastcallbackmoney;			
				totalmoney -= lastcallbackmoney;
				SocketHandler.insertLog(getGameId(), getGameIdentifier(), "Payback", -1, lastcallbackmoney, -1, "노콜머니 환불" , -1, -1);
			}
		}
		//기권승시 족보계산안함 ,이긴사람 돈줌 
		if( this.checkAbstention() ){
			sortRank=new ArrayList<User>();
			int cnt=0;
			for(User u : userlist){
				if(u.die == false){
					winSeat = cnt;
					sortRank.add(u);
					winners.add(u.seat);
					//u.prevamount -= u.lastbetmoney; 이거 확실친 않음 빼는게 맞을거 같은데 테스트 해보기, 마지막 베팅이 콜을 못받으면 그만큼 반환해줘야 하는데 그게 잘 되면 이거 빼는게 맞음.
					u.jokbocode = 99;
				}
				cnt++;
			}
			//************ 추가 
			for(User u : userlist){
				if(u.die == true){
					sortRank.add(u);
					u.jokbocode = 0;
				}
			}

			wlv = 99;						 
		}else {

			for( User user : userlist )
			{
				currentUser = user;
				if( user.die == true){		
					user.jokbocode = 0;
					continue;
				}

				int []card = new int[7]; 		

				String cardForDebug="";
				for(int i = 0; i < user.cardarr.size(); i++){				
					card[i] = user.cardarr.get(i);
					if( i!= 0)
						cardForDebug+=",";
					cardForDebug+=card[i];
				}
				
				//3포카드 7트리플 8투페어 9페어			
				
				//스트레이트 플러시 2
				if(checkStraightFlush(card)==true){//
					currentUser.wlv = 9;
					currentUser.jokbocode=0x9000000+tempInfo1;
					currentUser.balance += JackpotManager.GetJackpotAmount();
					JackpotManager.WithdrawJackpot();
				}else if(checkFourCard(card) == true ){// 포카드 *
					currentUser.wlv = 8;
					currentUser.jokbocode=0x8000000+tempInfo1*0x10 + tempInfo3;
				}else if(checkFullHouse(card)==true){//풀하우스 *
					currentUser.wlv = 7;
					currentUser.jokbocode=0x7000000+tempInfo1*0x10+tempInfo2;
				}else if(checkFlush(card)==true){//플러시 모양 *
					currentUser.wlv = 6;
					currentUser.jokbocode=0x6000000+tempInfo3;
				}else if(checkStraight(card)==true){//스트레이트숫자 *?
					currentUser.wlv = 5;
					currentUser.jokbocode=0x5000000+tempInfo1;
				}else if(checkThree(card)==true){//트리플*
					currentUser.wlv = 4;
					currentUser.jokbocode=0x4000000+tempInfo1*0x100+tempInfo3;
				}else if(checkTwoPair(card)==true){//투페어*
					currentUser.wlv = 3;
					currentUser.jokbocode=0x3000000+tempInfo1*0x100+tempInfo2*0x10+tempInfo3; 
				}else if(checkPair(card)==true){//원페어 *
					currentUser.wlv = 2;
					currentUser.jokbocode= 0x2000000+tempInfo1*0x1000 + tempInfo3;
				}else {//탑카드 
					currentUser.wlv = 1;
					checkTopCard(card);
					currentUser.jokbocode= 0x1000000 + tempInfo3; 
				}
				
				SocketHandler.insertLog(getGameId(), getGameIdentifier(), "card", currentUser.uidx , currentUser.jokbocode , -1, ""+cardForDebug , -1 , -1 );
			}

			//유저  족보 순위 정렬
			sortRank=(ArrayList<User>) userlist.clone();
			Collections.sort(sortRank, new Comparator<User>() {
	            @Override public int compare(User s1, User s2) {
	            	return s2.jokbocode - s1.jokbocode;
	            }
	        });
			
		}
		

		//===게임 참여자 정보 로그{
		String slog="";
		for( User user : userlist )
		{
			if(slog.length() > 1)
				slog+=",";
			String cd= ""+(1+user.cardarr.get(0)%13)+"/"+(1+user.cardarr.get(1)%13);
			slog+=user.uidx+":"+cd;
		}
		slog+=",W"+sortRank.get(0).uidx;
		SocketHandler.insertLog(getGameId(), getGameIdentifier(), "roominfo", 0, 0 , 0, ""+slog , -1 , -1 );
		//===게임 참여자 정보 로그}

		
		
		winSeat=sortRank.get(0).seat;
		ArrayList<JSONObject> wincards = new ArrayList<>();
		
		for( User user : userlist )
		{
			if( user.jokbocode == SearchUserBySeat(winSeat).jokbocode )
			{		
				if( wlv != 99 )
				{
					winners.add(user.seat);
				}				

				for( JSONObject card : user.wincard )
				{
					wincards.add(card);
				}				
			}
		}		
		//=========================={ 승리금 정산
				
		cal.init(totalmoney,room);
		cal.isGoldMode = false;
		if( room.UsedItem.equals("balance") == true)
			cal.isGoldMode = true;
		for(int n=0; n<sortRank.size(); n++)//이긴사람 정산 , 올인팟 사이드머니들 처리, 공동우승 포함.
		{
			if(cal.tempTotal  <= 0) 
			{
				sortRank.get(n).totalprofile.lose++;
				sortRank.get(n).todayprofile.lose++;
				Task.IncreaseTask(sortRank.get(n), 2, 1);
				Task.UpdateDB(sortRank.get(n));			
				if( room.isPrivate() == false )
				{
					if(SearchUserBySeat(winSeat).betmoney < sortRank.get(n).betmoney){
						if( room.UsedItem.equals("balance") == true){
							sortRank.get(n).balance += sortRank.get(n).betmoney - SearchUserBySeat(winSeat).betmoney;
						}
						else if( room.UsedItem.equals("point") == true){
							sortRank.get(n).point += sortRank.get(n).betmoney - SearchUserBySeat(winSeat).betmoney;
						}					
					}
				}
				sortRank.get(n).PlayStatus = 1;
				JackpotManager.SendJackpotMessage(sortRank.get(n));
				sortRank.get(n).ApplyBalanace(room.UsedItem);
				ProfileManager.UpdateProfile(sortRank.get(n).totalprofile);
				ProfileManager.UpdateTodayProfile(sortRank.get(n).todayprofile);
			}else 
			{
				//동점자 계산 관련, n등중에 제일큰 배팅금액 찾기			
				long maxbetmoney = cal.findSameScore( sortRank.get(n).jokbocode, sortRank );
				
				//나머지 사람에게 징수한다 
				long winnermoney = cal.takeOutMoney(sortRank, n, maxbetmoney);//n번쨰와 같은 순위가 가져가는돈이다 
				if( room.isPrivate() == true && sortRank.size() == 2)
				{//비밀방이고 총인원이 2명이면 모든 올인상관없이 모든 금액을 승리자가 갖게 한다.
					winnermoney = totalmoney;
				}
				cal.giveWinMoney(n, winnermoney, allincount); //n번째와 같은 순위들에게 winnermoney분배하기
				n += (cal.NRanks.size() -1);
				cal.NRanks = new ArrayList<User>();
			}
		}		
		//==========================} 승리금 정산
		
		setDealerSeat();		
				
		JSONObject obj = new JSONObject();
		obj.put("cmd","showResult");
		obj.put("wlv", sortRank.get(0).wlv);//레벨순서 변경됨.		
		if( room.UsedItem.equals("balance") == true){
			obj.put("winnerbalance", sortRank.get(0).balance);
		}
		else if( room.UsedItem.equals("point") == true){
			obj.put("winnerbalance", sortRank.get(0).point);
		}	
		obj.put("winmoney", this.totalmoney );
		obj.put("winSeat", winners);				
		obj.put("usersize", userlist.size());		
		obj.put("wincard", wincards);//
		
		JSONArray j = new JSONArray();
		for(int i=0; i<userlist.size(); i++){
			JSONObject item = new JSONObject();
			item.put("seat", userlist.get(i).seat);			
			item.put("card1", userlist.get(i).card1.cardcode);
			item.put("card2", userlist.get(i).card2.cardcode);
			if( room.UsedItem.equals("balance") == true){
				item.put("balance", userlist.get(i).balance);
				item.put("amount", userlist.get(i).balance - userlist.get(i).prevamount);
			}
			else if( room.UsedItem.equals("point") == true){
				item.put("balance", userlist.get(i).point);
				item.put("amount", userlist.get(i).point - userlist.get(i).prevamount);
			}									
			item.put("bankamount", userlist.get(i).bankamount);
			item.put("die", userlist.get(i).die);
			item.put("win", winners.contains(userlist.get(i).seat));
			item.put("profile", userlist.get(i).todayprofile);
			j.add(item);
			userlist.get(i).die = false;
		}
		obj.put("cardlist", j);
		sendRoom(obj);

		for( User u : watchinguserlist )
		{
			userlist.add(u);
		}

		watchinguserlist.clear();
	}

	
	public void InsertWatchingUser(User user)
	{
		watchinguserlist.add(user);
		ArrayList<Integer> cardlist = new ArrayList<>();
		if( card1 != null )
		{
			cardlist.add(card1.cardcode);
		}
		if( card2 != null )
		{
			cardlist.add(card2.cardcode);
		}
		if( card3 != null )
		{
			cardlist.add(card3.cardcode);
		}		
		if( card4 != null )
		{
			cardlist.add(card4.cardcode);
		}
		if( card5 != null )
		{
			cardlist.add(card5.cardcode);
		}

		JSONObject obj = new JSONObject();
		obj.put("cmd","watching");
		obj.put("cardlist", cardlist);
		obj.put("totalbet", totalmoney );

		try {
			if(User.CheckSendPacket(user) == true)
			{
				user.session.sendMessage(new TextMessage(obj.toJSONString()));
			}			
		} catch (IOException e) {
			System.out.println("InsertWatchingUser error:"+e.getMessage());
		}
	}
	
	private JSONObject MakeWinCard(int lv, ArrayList<Integer> cards) {
		
		JSONObject win = new JSONObject();
		win.put("lv", lv);
		win.put("cards", cards);
		
		return win;
	}
	
	public User SearchUserBySeat(int seat)
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

	public boolean IsJoinGame(int seat)
	{
		for( User user : userlist )
		{	
			if( user.seat == seat )
			{
				return true;				
			}
		}

		return false;
	}
	
	private int GetAbleBettingUserCount()
	{
		int total = 0;
		
		for( User user : userlist )
		{
			if( user.die == true )
			{
				continue;
			}
			
			if( user.betmoney >= room.maxmoney )
			{
				continue;
			}
			
			if( room.UsedItem.equals("balance") == true){
				if( user.balance <= 0 ){
					continue;
				}
			}			
			else if( room.UsedItem.equals("point") == true){
				if( user.point <= 0 ){
					continue;
				}
			}			

			total++;
		}
		
		return total;
	}	

	private int GetBettingEndUserCount()
	{
		int total = 0;
		
		for( User user : userlist )
		{
			if( user.die == true )
			{
				continue;
			}
			
			if( user.betmoney >= room.maxmoney )
			{
				continue;
			}

			if( room.UsedItem.equals("balance") == true){
				if( user.balance <= 0 ){
					continue;
				}
			}			
			
			if( room.UsedItem.equals("point") == true){
				if( user.point <= 0 ){
					continue;
				}
			}			

			if( user.PlayStatus == 0 )
			{
				continue;
			}

			total++;
		}
		
		return total;
	}

	public void EmoticonBroadCast(User user, int type) {
				
		JSONObject obj = new JSONObject();
		obj.put("cmd", "emoticon");
		obj.put("emoticon", type);
		obj.put("seat", user.seat);
		
		sendRoom(obj);		
		
	}
	
}

