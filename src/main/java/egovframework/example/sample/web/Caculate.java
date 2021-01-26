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
	long getmoneyai,getmoneyuser;//정산 표시에서 ai 부분 계산용.
	
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
		getmoneyai=0;
		getmoneyuser=0;
		long winnermoney = 0;//winner가 가져가는 돈
		long pm =0;
		for(int n=nowrank; n<sortRank.size(); n++)//n값이 커질수록 꼴찌에 가까움.
		{
			if( sortRank.get(n).betmoney < maxbetmoney) {	
				winnermoney += sortRank.get(n).betmoney;
				sortRank.get(n).betmoney = 0;
			}else {
				winnermoney += maxbetmoney;
				sortRank.get(n).betmoney -= maxbetmoney;
			}

			if( sortRank.get(n).isAI )
				getmoneyai+=(winnermoney - pm);
			else
				getmoneyuser+=(winnermoney - pm);
			pm=winnermoney;
		}	
		return winnermoney;
	}
	
	public void giveWinMoney(int Rank, long winnermoney, int allincount,long betmoney2)
	{
		int tempwin=0;
		long aifee= 0;
		for(int winnercnt=0; winnercnt<NRanks.size(); winnercnt++)
		{
			long amount = (long)(winnermoney * NRanks.get(winnercnt).betmoney2 / NRanksTotalmoney);
			if(NRanks.size() - winnercnt == 1)
				amount = winnermoney - tempwin;
			else
				tempwin += amount;
			long winnerpoint = (long)(amount * ( 1 - NRanks.get(winnercnt).memberInfo.commission));
			
			//내 순위 RanK이하  에서 최대 betmoney를 비교해서 나보다 많은 betmoney를 가진 사람이 있으면 수수료 계산시 그 차액 betmoney - betmoney2을 제외한 돈에서 수수료를 계산해서 그만큼 을 amount에서 제외하고 winnerpoint에 넣는다.
			//노콜머니랑 같은 개념, 사실 이게 되면 위에 노콜머니는 굳이 안해도 되는데 괜히 했네...
			if( NRanks.get(winnercnt).betmoney2 > betmoney2 ) 
			{
				long tma = NRanks.get(winnercnt).betmoney2 - betmoney2;
				long tma2 = amount - tma;
				winnerpoint =  (long)( tma2 * ( 1 - NRanks.get(winnercnt).memberInfo.commission)) + tma ;
				SocketHandler.insertLog(room.gameManager.getGameId(), room.gameManager.getGameIdentifier(), "Payback2", -1, tma , -1, "a노콜머니 환불 "+NRanks.get(winnercnt).uidx  , -1, -1);
			}
			
			//if(total ==  NRanksTotalmoney) winnerpoint = amount;
			
			
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
				EgovMap in = new EgovMap();
				in.put("uidx", ""+NRanks.get(winnercnt).uidx);
				in.put("gameid", room.gameManager.getGameIdentifier() );
				in.put("winmoney", ""+amount);
				in.put("fee", ""+(amount-winnerpoint) );
				in.put("goldback", ""+gamount );
				SocketHandler.sk.sampleDAO.insert("insertCommission", in);
				aifee = (amount-winnerpoint);
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
			
			long wmoney =0;//+이면 AI 가 유저한테 딴돈,   -이면 USER가 AI한테 딴돈 표시, 정산에서 그만큼 표시하기 위함, 실제 정산하곤 상관없음.
			if(getmoneyai >0   && NRanks.get(winnercnt).isAI == false && winnermoney >0 ) {
				wmoney =(long)(getmoneyai * amount/(float)winnermoney) * -1;
			}
			if(getmoneyuser > 0   && NRanks.get(winnercnt).isAI == true && winnermoney >0 ) {
				wmoney =(long)(getmoneyuser * amount/(float)winnermoney);
			}
			if(NRanks.get(winnercnt).isAI == false) {
				aifee = 0;
			}
			SocketHandler.insertLog(room.gameManager.getGameId(), room.gameManager.getGameIdentifier(), "result", NRanks.get(winnercnt).uidx , NRanks.get(winnercnt).balance, NRanks.get(winnercnt).point
					, "승리금:"+winnerpoint , aifee , wmoney );
			
			NRanks.get(winnercnt).PlayStatus = 1;
			JackpotManager.SendJackpotMessage(NRanks.get(winnercnt));
			NRanks.get(winnercnt).ApplyBalanace(room.UsedItem);
			ProfileManager.UpdateProfile(NRanks.get(winnercnt).totalprofile);
			ProfileManager.UpdateTodayProfileNoExpire(NRanks.get(winnercnt).todayprofile);
			
		}		
	}
}
