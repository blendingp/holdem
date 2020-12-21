package egovframework.example.sample.web;

import java.util.ArrayList;

import egovframework.rte.psl.dataaccess.util.EgovMap;

public class Caculate {
	
	ArrayList<User> NRanks;//n등 공동우승자 seat번호 모아두는 배열
	long tempTotal;
	long NRanksTotalmoney;//n등들의 totalmoney		
	public boolean isGoldMode=false;//true면 골드머니, false이면 칩머니
	public Room room=null;	
	long total=0;
	
	public void init(long totalmoney,Room r) {
		NRanks=new ArrayList<User>();
		tempTotal=totalmoney;
		total = totalmoney;
		NRanksTotalmoney=0;
		room = r;		
	}
		
	public long findSameScore(int score, ArrayList<User> sortRank)
	{
		NRanksTotalmoney = 0;
		long maxbetmoney = 0;//n등중에 가장 큰 배팅금액
		for(int sortcnt=0; sortcnt<sortRank.size(); sortcnt++)
		{
			if( sortRank.get(sortcnt).jokbocode == score)
			{
				if( sortRank.get(sortcnt).betmoney >= maxbetmoney )	
					maxbetmoney = sortRank.get(sortcnt).betmoney;
				NRanks.add( sortRank.get(sortcnt) );
				NRanksTotalmoney += sortRank.get(sortcnt).betmoney;
			}		
		}
		return maxbetmoney;
	}
	
	public long takeOutMoney(ArrayList<User> sortRank, int nowrank, long maxbetmoney)
	{
		long winnermoney = 0;//winner가 가져가는 돈		
		for(int n=nowrank; n<sortRank.size(); n++)
		{
			if( sortRank.get(n).betmoney < maxbetmoney)	
				winnermoney += sortRank.get(n).betmoney;
			else
				winnermoney += maxbetmoney;			
		}	
		return winnermoney;
	}
	
	public void giveWinMoney(int Rank, long winnermoney, int allincount)
	{
		int tempwin=0;
		for(int winnercnt=0; winnercnt<NRanks.size(); winnercnt++)
		{			
			long amount = (long)(winnermoney * NRanks.get(winnercnt).betmoney / NRanksTotalmoney);
			if(NRanks.size() - winnercnt == 1)
				amount = winnermoney - tempwin;
			else
				tempwin += amount;
			
			System.out.println("amount:"+amount);
			long winnerpoint = (long)(amount * ( 1 - NRanks.get(winnercnt).memberInfo.commission));
			if(total ==  NRanksTotalmoney) winnerpoint = amount;
			//<===== *** 여기에 수수료 로그 남겨야 함.
			NRanks.get(winnercnt).totalprofile.win++;
			NRanks.get(winnercnt).todayprofile.win++;
			NRanks.get(winnercnt).totalprofile.putallin += allincount;
			NRanks.get(winnercnt).todayprofile.putallin += allincount;
			//골드대전
			if( isGoldMode == true) 
			{
				NRanks.get(winnercnt).balance += winnerpoint; 
				NRanks.get(winnercnt).todayprofile.gainbalance += winnerpoint;
				
				if( NRanks.get(winnercnt).totalprofile.highgaingold < winnerpoint )
				{
					NRanks.get(winnercnt).totalprofile.highgaingold = winnerpoint;
				}	
				
				long gamount = (long)(amount * NRanks.get(winnercnt).memberInfo.gold_cashback);				
				NRanks.get(winnercnt).bank += gamount;				
				if( NRanks.get(winnercnt).bank + gamount > NRanks.get(winnercnt).memberInfo.bank_gold )
				{
					NRanks.get(winnercnt).bankamount = NRanks.get(winnercnt).memberInfo.bank_gold - NRanks.get(winnercnt).bank;
				}
				else
				{
					NRanks.get(winnercnt).bankamount = gamount;
				}

				if( NRanks.get(winnercnt).bank > NRanks.get(winnercnt).memberInfo.bank_gold )
				{
					NRanks.get(winnercnt).bank = NRanks.get(winnercnt).memberInfo.bank_gold;
				}
				else{
					// 저금통 갱신
					if( gamount > 0 ){
						NRanks.get(winnercnt).ApplyBalanace("bank");
					}
				}				
				
			}
			else // 칩
			{
				NRanks.get(winnercnt).point += winnerpoint;
				NRanks.get(winnercnt).todayprofile.gaingold += winnerpoint;
			}
			tempTotal -= amount;
			
			room.roominfo.EndGame(NRanks.get(winnercnt).nickname, winnerpoint);
			Task.IncreaseTask(NRanks.get(winnercnt), 1, 1);
			Task.UpdateDB(NRanks.get(winnercnt));
			
			SocketHandler.insertLog(room.gameManager.getGameId(), room.gameManager.getGameIdentifier(), "result", NRanks.get(winnercnt).uidx , NRanks.get(winnercnt).balance, NRanks.get(winnercnt).point
					, "승리금:"+winnerpoint , NRanks.get(winnercnt).jokbocode , -1 );
			
			EgovMap in = new EgovMap();
			in.put("uidx", ""+NRanks.get(winnercnt).uidx);
			in.put("gameid", room.gameManager.getGameId());
			in.put("winmoney", ""+amount);
			in.put("fee", ""+(amount-winnerpoint) );
			SocketHandler.sk.sampleDAO.insert("insertCommission", in);
			NRanks.get(winnercnt).PlayStatus = 1;
			JackpotManager.SendJackpotMessage(NRanks.get(winnercnt));
			NRanks.get(winnercnt).ApplyBalanace(room.UsedItem);
			ProfileManager.UpdateProfile(NRanks.get(winnercnt).totalprofile);
			ProfileManager.UpdateTodayProfile(NRanks.get(winnercnt).todayprofile);
			
		}		
	}
}
