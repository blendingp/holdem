package egovframework.example.sample.web;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

import javax.annotation.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import egovframework.example.sample.service.impl.SampleDAO;
import egovframework.example.sample.web.model.AuthSelf;
import egovframework.example.sample.web.model.BanModel;
import egovframework.example.sample.web.model.MemberInfo;
import egovframework.example.sample.web.model.MembersInfo;
import egovframework.example.sample.web.model.MembersShopInfo;
import egovframework.example.sample.web.model.ProfileModel;
import egovframework.example.sample.web.model.TaskModel;
import egovframework.rte.psl.dataaccess.util.EgovMap;

public class User {
	public int uidx;
	public boolean use = false;
	public String nickname = "";
	public int seat = -1;
	public int roomnum = -1;
	public long betmoney = 0;
	public long prevamount = 0;
	public long balance = 0;
	public long point = 0;
	public long safe_point = 0;
	public long safe_balance = 0;
	public long cash = 0;
	public long budget = 0;
	public long bank = 0;
	public long lastbetmoney = 0;
	public long bankamount = 0;
	public long totalpayment = 0;
	public boolean isAI = false;// true 일때 ai 유저.
	public boolean die = false;// true일떄 다이인 상태
	public int currentGuBetMoney = 0;// 현재 구 에 베팅한 머니 / 모든 유저가 이 머니가 같아야 다음 단계로 넘어감.
	public String img;
	public WebSocketSession session;
	public ArrayList<JSONObject> wincard = new ArrayList<JSONObject>();
	public int PlayStatus = 0;
	public int Blind = 0;
	public Attendance attendance;
	public ProfileModel totalprofile;
	public ProfileModel todayprofile;
	public MembersInfo memberInfo;
	public ArrayList<TaskModel> tasklist;
	public ArrayList<String> avatalist = new ArrayList<>();
	public ArrayList<Integer> cardarr = new ArrayList<>();
	public ArrayList<Item> consumableItem = new ArrayList<>();

	AuthSelf auth = null;
	MemberInfo _info;
	String gamestat = "";
	int level = 1000;
	int topcard = -1;
	int wlv = 99;
	int jokbocode = 0;// 천만자리는 첫번째 족보레벨, 십만~백만 자리는 족보레벨 탑카드번호,만~천자리는 족베레벨 두번째 탑카드번호 , 백자리는 두번째 족보레벨 ,
						// 일~십자리는 두번째족보레벨의 탑카드번호
	Card card1 = new Card(-1);// 아직카드없음
	Card card2 = new Card(-1);

	public void init() {	
		betmoney = 0;
		bankamount = 0;
		currentGuBetMoney = 0;// 현재 구 에 베팅한 머니 / 모든 유저가 이 머니가 같아야 다음 단계로 넘어감.
		die = false;
		wincard.clear();
		wlv = 99;
	}

	public User(int uidx, WebSocketSession session) {		
		this.uidx = uidx;		
		this.balance = 1100;
		this.session = session;		

		EgovMap in = new EgovMap();
		in.put("midx", uidx);
		ArrayList<EgovMap> ed = (ArrayList<EgovMap>) SocketHandler.sk.sampleDAO.list("SelectUserItem", in);

		if (ed == null) {
			System.out.println("실패");
		} else {
			for (int nCount = 0; nCount < ed.size(); ++nCount) {
				if (ed.get(nCount).get("type").toString().equals("balance") == true) {
					this.balance = (long) ed.get(nCount).get("amount");
				} else if (ed.get(nCount).get("type").toString().equals("cash") == true) {
					this.cash = (long) ed.get(nCount).get("amount");
				} else if (ed.get(nCount).get("type").toString().equals("budget") == true) {
					this.budget = (long) ed.get(nCount).get("amount");
				} else if (ed.get(nCount).get("type").toString().equals("point") == true) {
					this.point = (long) ed.get(nCount).get("amount");
				} else if (ed.get(nCount).get("type").toString().equals("safe_balance") == true) {
					this.safe_balance = (long) ed.get(nCount).get("amount");
				} else if (ed.get(nCount).get("type").toString().equals("safe_point") == true) {
					this.safe_point = (long) ed.get(nCount).get("amount");
				} else if (ed.get(nCount).get("type").toString().equals("bank") == true) {
					this.bank = (long) ed.get(nCount).get("amount");
				} else if (ed.get(nCount).get("type").toString().equals("avata1") == true) {
					if ((long) ed.get(nCount).get("amount") > 0) {
						avatalist.add("avata1");
					}
				} else if (ed.get(nCount).get("type").toString().equals("avata2") == true) {
					if ((long) ed.get(nCount).get("amount") > 0) {
						avatalist.add("avata2");
					}
				} else if (ed.get(nCount).get("type").toString().equals("avata3") == true) {
					if ((long) ed.get(nCount).get("amount") > 0) {
						avatalist.add("avata3");
					}
				} else if (ed.get(nCount).get("type").toString().equals("avata4") == true) {
					if ((long) ed.get(nCount).get("amount") > 0) {
						avatalist.add("avata4");
					}
				}
				else if (ed.get(nCount).get("type").toString().equals("chiprefill500") == true) {
					if ((long) ed.get(nCount).get("amount") > 0) {
						Item item = new Item();
						item.Type = ed.get(nCount).get("type").toString();
						item.Amount = (long) ed.get(nCount).get("amount");
						consumableItem.add(item);
					}
				}
				else if (ed.get(nCount).get("type").toString().equals("chiprefill1000") == true) {
					if ((long) ed.get(nCount).get("amount") > 0) {
						Item item = new Item();
						item.Type = ed.get(nCount).get("type").toString();
						item.Amount = (long) ed.get(nCount).get("amount");
						consumableItem.add(item);
					}
				}
				else if (ed.get(nCount).get("type").toString().equals("refill2000") == true) {
					if ((long) ed.get(nCount).get("amount") > 0) {
						Item item = new Item();
						item.Type = ed.get(nCount).get("type").toString();
						item.Amount = (long) ed.get(nCount).get("amount");
						consumableItem.add(item);
					}
				}
				else if (ed.get(nCount).get("type").toString().equals("aiuser") == true) {
					if ((long) ed.get(nCount).get("amount") > 0) {
						this.isAI = true; 
					}
				}
			}
		}

		//Random random = new Random();
		//this.img = "Character" + (random.nextInt(4) + 1);		

		attendance = Attendance.MakeAttendance(this.uidx);
		memberInfo = Members.GetUserMembersInfo(this);

		totalprofile = ProfileManager.GetTotalInfo(uidx);
		totalprofile.midx = uidx;
		todayprofile = ProfileManager.GetTodayInfo(uidx);
		todayprofile.midx = uidx;

		totalpayment = PaymentLog.GetTotalPayment(uidx);

		tasklist = Task.GetTask(uidx);
		if (tasklist.size() <= 0) {
			tasklist = Task.MakeTask(uidx);
			Task.UpdateDB(this);
		}

		GetMemberInfo();
		GetAuthInfo();

		this.nickname = _info.nickname;
	}

	private Object find(WebSocketSession session2) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setGameStat(String gamestat) {
		this.gamestat = gamestat;
	}

	public String getGameStat() {
		return gamestat;
	}

	public int buyItem(String product, String receipt) {
		/*
		 * 품목을 관리할수 있는게 없는 관계로 일단 하드 코딩으로 대체함...
		 * 
		 */

		/// System.out.println(product);
		// System.out.println(receipt);

		EgovMap in = new EgovMap();
		in.put("midx", uidx);		

		String item = "";
		int iscash = 0;
		long price = 0;	

		switch (product) {
			case "Gem0":
				price = 1100;			
				if( totalpayment + price > 500000 )
				{
					return 0;
				}
				this.cash += 110;
				in.put("amount", this.cash);
				in.put("type", "cash");
				iscash = 1;					
				break;
			case "Gem1":
				price = 5500;				
				if( totalpayment + price > 500000 )
				{
					return 0;
				}
				this.cash += 550;
				in.put("amount", this.cash);
				in.put("type", "cash");
				iscash = 1;				
				break;
			case "Gem2":
				price = 11000;			
				if( totalpayment + price > 500000 )
				{
					return 0;
				}
				this.cash += 1100;
				in.put("amount", this.cash);
				in.put("type", "cash");
				iscash = 1;					
				break;
			case "Gem3":
				price = 22000;			
				if( totalpayment + price > 500000 )
				{
					return 0;
				}
				this.cash += 2200;
				in.put("amount", this.cash);
				in.put("type", "cash");
				iscash = 1;				
				break;			
			case "silver": {
				Members.BuyMembers(this, 1);
				in.put("amount", this.cash);
				in.put("type", "cash");
			}
				break;
			case "gold": {
				Members.BuyMembers(this, 2);
				in.put("amount", this.cash);
				in.put("type", "cash");
			}
				break;
			case "dia": {
				Members.BuyMembers(this, 3);
				in.put("amount", this.cash);
				in.put("type", "cash");
			}
				break;
			case "refill500":
				if (this.cash >= 40) 
				{
					item = product;
				}
				break;
			case "refill1000":
				if (this.cash >= 40) 
				{
					item = product;
				}
				break;
			case "Avata0":
				price = 5500;
			case "Avata1":
				price = 11000;
			case "Avata2":
				price = 22000;
			case "Avata3": {
				price = 33000;
				iscash = 1;
				if( totalpayment + price > 500000 )
				{
					return 0;
				}
				Avata.Buy(product, uidx);
				in = null;
			}
				break;
			default:
				in = null;
				this.cash += 0;
				break;
		}		

		totalpayment += price;
		PaymentLog.Insert(uidx, iscash, price, product, receipt);

		int rt = 0;
		if (in != null) {
			rt = SocketHandler.sk.sampleDAO.update("updateItemAmont", in);
		} else {
			rt = 1;
		}

		if (item.isEmpty() == false) {			

			EgovMap balancein = new EgovMap();
			balancein.put("midx", uidx);
			balancein.put("amount", 10);
			balancein.put("type", item);

			SocketHandler.sk.sampleDAO.update("updateItemAmont", balancein);

		}		

		return rt;
	}

	public int ApplyBalanace(String useitem) {
		EgovMap in = new EgovMap();
		in.put("midx", uidx);

		if (useitem.equals("point") == true) {
			in.put("amount", this.point);
		}
		if (useitem.equals("safe_point") == true) {
			in.put("amount", this.safe_point);
		} else if (useitem.equals("balance") == true) {
			in.put("amount", this.balance);
		} else if (useitem.equals("safe_balance") == true) {
			in.put("amount", this.safe_balance);
		} else if (useitem.equals("bank") == true) {
			in.put("amount", this.bank);
		}
		in.put("type", useitem);

		int rt = SocketHandler.sk.sampleDAO.update("updateItemAmont", in);

		return rt;
	}

	public int Deal(int item, int action, long amount) {
		EgovMap[] in = new EgovMap[2];
		in[0] = new EgovMap();
		in[1] = new EgovMap();
		in[0].put("midx", uidx);
		in[1].put("midx", uidx);

		int[] rt = new int[2];

		if (item == 0) {
			if (action == 0) {
				if (this.point < amount) {
					return 0;
				}

				if (this.safe_point + amount > this.memberInfo.limit_safe_point) {
					return 0;
				}

				this.point -= amount;
				this.safe_point += amount;
			} else {
				if (this.safe_point < amount) {
					return 0;
				}

				if (this.point + amount > this.memberInfo.limit_point) {
					return 0;
				}

				this.safe_point -= amount;
				this.point += amount;
			}

			in[0].put("amount", this.point);
			in[0].put("type", "point");
			in[1].put("amount", this.safe_point);
			in[1].put("type", "safe_point");

			rt[0] = SocketHandler.sk.sampleDAO.update("updateItemAmont", in[0]);
			rt[1] = SocketHandler.sk.sampleDAO.update("updateItemAmont", in[1]);
		} else {
			if (action == 0) {
				if (this.balance < amount) {
					return 0;
				}

				if (this.safe_balance + amount > this.memberInfo.limit_safe_gold) {
					return 0;
				}

				this.balance -= amount;
				this.safe_balance += amount;
			} else {
				if (this.safe_balance < amount) {
					return 0;
				}

				if (this.balance + amount > this.memberInfo.limit_gold) {
					return 0;
				}

				this.safe_balance -= amount;
				this.balance += amount;
			}

			in[0].put("amount", this.balance);
			in[0].put("type", "balance");
			in[1].put("amount", this.safe_balance);
			in[1].put("type", "safe_balance");

			rt[0] = SocketHandler.sk.sampleDAO.update("updateItemAmont", in[0]);
			rt[1] = SocketHandler.sk.sampleDAO.update("updateItemAmont", in[1]);
		}

		return rt[0] & rt[1];
	}

	public int Beg() {
		this.balance = 500;
		return 1;
	}

	public void SetUserAvata(String avata) {

		if (avatalist.contains(avata) == false)        
        {
            return;
        }

        if (_info.avata.equals(avata) == true)
        {
            return;
        }

		_info.avata = avata;
		UpdateMemberInfo();
	}

	public int getGameStat(String stat) {
		if (getGameStat().compareTo(stat) == 0)
			return 1;
		else
			return 0;
	}

	public void setCard(Card card1, Card card2) {
		this.card1.cardcode = card1.cardcode;
		this.card2.cardcode = card2.cardcode;
	}

	public void clear() {
		seat = -1;
		roomnum = -1;
		betmoney = 0;
		gamestat = "";
		die = false;		
		currentGuBetMoney = 0;		
		wincard.clear();

		card1.clear();
		card2.clear();
	}

	public void CheckExpireTodayRecord() {
		if (todayprofile.CheckExpire() == true) {
			todayprofile = new ProfileModel();
			todayprofile.midx = uidx;
			ProfileManager.UpdateTodayProfile(todayprofile);
		}
	}

	public UserInfo MakeUserInfo() {
		Task.Expired(this);

		UserInfo info = new UserInfo();
		info.balance = balance;
		info.point = point;
		info.safe_point = safe_point;
		info.safe_balance = safe_balance;
		info.cash = cash;
		info.bank = bank;
		info.attendance = attendance;
		info.membersinfo = memberInfo;
		info.todayprofile = todayprofile;
		info.totalprofile = totalprofile;
		info.tasklist = tasklist;
		info.avatalist = avatalist;
		info.memberinfo = _info;
		info.totalpayment = totalpayment;

		return info;
	}

	public void ExpireMembers() {
		memberInfo = new MembersInfo();
		long goldamount = 0;
		long chipamount = 0;

		long overgold = balance - memberInfo.limit_gold;
		long oversafegold = safe_balance - memberInfo.limit_safe_gold;
		long overchip = point - memberInfo.limit_point;
		long oversafechip = safe_point - memberInfo.limit_safe_point;

		goldamount += (overgold > 0) ? overgold : 0;
		goldamount += (oversafegold > 0) ? oversafegold : 0;
		chipamount += (overchip > 0) ? overchip : 0;
		chipamount += (oversafechip > 0) ? oversafechip : 0;

		if (overgold > 0) {
			this.balance = memberInfo.limit_gold;
			ApplyBalanace("balance");
		}

		if (oversafegold > 0) {
			this.safe_balance = memberInfo.limit_safe_gold;
			ApplyBalanace("safe_balance");
		}

		if (overchip > 0) {
			this.point = memberInfo.limit_point;
			ApplyBalanace("point");
		}

		if (oversafechip > 0) {
			this.safe_point = memberInfo.limit_safe_point;
			ApplyBalanace("safe_point");
		}

		ObjectMapper mapper = new ObjectMapper();

		if (goldamount > 0) {
			InBox inbox = InBox.MakeInBox("goldover", uidx, 4, "admin");
			Item item = new Item();
			item.Type = "balance";
			item.Amount = goldamount;
			inbox.ItemList.add(item);
			inbox.Expire = System.currentTimeMillis() + 604800000;

			try {
				EgovMap refundin = new EgovMap();
				refundin.put("uid", inbox.UID);
				refundin.put("midx", inbox.Midx);
				refundin.put("type", inbox.Type);
				refundin.put("title", inbox.Title);
				refundin.put("body", mapper.writeValueAsString(inbox.ItemList));
				refundin.put("expire", inbox.Expire);

				SocketHandler.sk.sampleDAO.insert("AddInbox", refundin);
			} catch (IOException e) {

			}
		}

		if (chipamount > 0) {
			InBox inbox = InBox.MakeInBox("chipover", uidx, 4, "admin");
			Item item = new Item();
			item.Type = "point";
			item.Amount = chipamount;
			inbox.ItemList.add(item);
			inbox.Expire = System.currentTimeMillis() + 604800000;

			try {
				EgovMap refundin = new EgovMap();
				refundin.put("uid", inbox.UID);
				refundin.put("midx", inbox.Midx);
				refundin.put("type", inbox.Type);
				refundin.put("title", inbox.Title);
				refundin.put("body", mapper.writeValueAsString(inbox.ItemList));
				refundin.put("expire", inbox.Expire);

				SocketHandler.sk.sampleDAO.insert("AddInbox", refundin);
			} catch (IOException e) {

			}
		}
	}

	public boolean IsAuth()
	{
		if(isAI == true)
		{//ai유저이면 인증 통과
			return true;
		}
		if( auth == null )
		{
			return false;
		}

		if( auth.uid.isEmpty() == true )
		{
			return false;
		}

		if( auth.authtick + 31536000000L < System.currentTimeMillis())
		{
			return false;
		}
		return true;		
	}

	public boolean InsertItem(Item item) {
		if (item.Type.equals("point") == true) {
			if (this.point + item.Amount > memberInfo.limit_point) {
				return false;
			}

			this.point += item.Amount;

			EgovMap in = new EgovMap();
			in.put("midx", this.uidx);
			in.put("amount", this.point);
			in.put("type", "point");

			int rt = SocketHandler.sk.sampleDAO.update("updateItemAmont", in);
		} else if (item.Type.equals("balance") == true) {
			if (this.balance + item.Amount > memberInfo.limit_gold) {
				return false;
			}

			this.balance += item.Amount;

			EgovMap in = new EgovMap();
			in.put("midx", this.uidx);
			in.put("amount", this.balance);
			in.put("type", "balance");

			int rt = SocketHandler.sk.sampleDAO.update("updateItemAmont", in);
		} else if (item.Type.equals("cash") == true) {
			this.cash += item.Amount;

			EgovMap in = new EgovMap();
			in.put("midx", this.uidx);
			in.put("amount", this.cash);
			in.put("type", "cash");

			int rt = SocketHandler.sk.sampleDAO.update("updateItemAmont", in);
		} else if (item.Type.equals("avata") == true) {
			Random random = new Random();
			String avata = String.format("avata%d", random.nextInt(4) + 1);
			if (avatalist.contains(avata) == true) {
				return false;
			}

			avatalist.add(avata);

			EgovMap in = new EgovMap();
			in.put("midx", this.uidx);
			in.put("amount", 1);
			in.put("type", avata);

			int rt = SocketHandler.sk.sampleDAO.update("updateItemAmont", in);
		}

		return true;
	}

	private void GetMemberInfo() {
		EgovMap in = new EgovMap();
		in.put("midx", this.uidx);
		EgovMap ed = (EgovMap) SocketHandler.sk.sampleDAO.select("GetMemberInfo", in);

		ObjectMapper mapper = new ObjectMapper();
		if( ed != null )
		{
			try {
				String jsonstring = mapper.writeValueAsString(ed);				
				_info = mapper.readValue(jsonstring, MemberInfo.class);			
				if( _info.ban.isEmpty() == false )
				{
					BanModel ban = mapper.readValue(_info.ban, BanModel.class);
					if( ban.expire < System.currentTimeMillis() )
					{
						_info.ban = "";
						UpdateMemberInfo();
					}
				}
	
			} catch (JsonProcessingException e) {
				System.out.println(e.getMessage());
			}

			this.img = _info.avata;			
			try {
				if( (""+ed.get("ai")).compareTo("1") == 0 ) 
				{
					this.isAI = true;
					System.out.println("ai 유저 로긴:"+_info.nickname );
				}
			}catch(Exception e) 
			{
				System.out.println("ai info 읽기 실패");
			}
		}		
		else
		{
			_info = new MemberInfo();
			_info.midx = uidx;
			_info.exp = 0;
			_info.avata = "";
			_info.nickname = "";

			UpdateMemberInfo();
		}		
	}

	private void GetAuthInfo()
	{
		EgovMap in = new EgovMap();
		in.put("midx", this.uidx);
		EgovMap ed = (EgovMap) SocketHandler.sk.sampleDAO.select("GetAuthInfo", in);		
		if( ed != null )
		{
			ObjectMapper mapper = new ObjectMapper();

			try {
				String jsonstring = mapper.writeValueAsString(ed);		
				System.out.println(jsonstring);		
				auth = mapper.readValue(jsonstring, AuthSelf.class);			
			} catch (JsonProcessingException e) {			
				System.out.println(e.getMessage());
			}
		}		
	}

	public boolean GetAuth()
	{
		GetAuthInfo();		

		if( auth == null )
		{
			return false;
		}

		if( auth.authtick + 31536000000L < System.currentTimeMillis())
		{
			return false;
		}

		if( auth.uid.isEmpty() == true )
		{
			return false;
		}

		String birthyear = auth.birthdate.substring(0, 3);
		int year = LocalDateTime.now().getYear() - Integer.parseInt(birthyear);
		if( year < 18 )
		{
			return false;
		}
		return true;
	}

	public boolean SetNickName(String name)
	{
		_info.nickname = name;
		nickname = name;
		UpdateMemberInfo();

		return true;
	}

	public boolean GetNickNameEmpty()
	{
		return _info.nickname.isEmpty();
	}

	public void UpdateMemberInfo()
	{
		ObjectMapper mapper = new ObjectMapper();
		try {
			
			String jsonstring = mapper.writeValueAsString(_info);
			EgovMap in = mapper.readValue(jsonstring, EgovMap.class);	
			SocketHandler.sk.sampleDAO.update("UpdateMemberInfo", in);		

		} catch (JsonProcessingException e) {
			System.out.println(e.getMessage());
		}
		
		this.img = _info.avata;		
	}
	public void sendMe(JSONObject obj)
	{
		try {
			ObjectMapper mapper = new ObjectMapper();
			session.sendMessage(new TextMessage(mapper.writeValueAsString(obj)));
		} 
		catch (IOException e) {
			e.printStackTrace();
			System.out.println("sendMe Error:"+obj.toJSONString() );
		}
	}
}
