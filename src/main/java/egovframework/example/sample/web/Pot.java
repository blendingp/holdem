package egovframework.example.sample.web;

import java.util.ArrayList;

import org.antlr.misc.IntSet;
import org.apache.commons.lang3.ArrayUtils;
import org.hsqldb.lib.ArrayUtil;
import org.json.simple.JSONObject;

public class Pot {
	
	private ArrayList<JSONObject> PotUserSeats = new ArrayList<JSONObject>();	
	
	public int JoinPot(int seat, int amount)
	{
		if( amount <= 0 )
		{
			return 1;
		}		
		
		JSONObject potinfo = null;
		for( int nCount = 0; nCount < PotUserSeats.size(); ++nCount)
		{
			if( PotUserSeats.get(nCount).containsKey(seat) == true )
			{
				potinfo = PotUserSeats.get(nCount);
			}
		}
		
		if( potinfo != null )
		{
			potinfo.replace(seat, (int)potinfo.get(seat) + amount);
		}
		else
		{
			potinfo = new JSONObject();
			potinfo.put(seat, amount);
		}
		
		return 0;
	}
	
	public int[] RequestPot(int[] list)
	{
		ArrayList<Integer> winner = new ArrayList<>();
		for( int seat : list )
		{
			for( int nCount = 0; nCount < PotUserSeats.size(); ++nCount)
			{
				if( PotUserSeats.get(nCount).containsKey(seat) == true )
				{
					winner.add(seat);
				}
			}		
		}
		
		return ArrayUtils.toPrimitive(winner.toArray(new Integer[0]));		
	}
	
	public int GetPrizeMoney(int size)
	{
		int totalPot = 0;
		
		for( JSONObject info : PotUserSeats )
		{
			for( int nCount = 0; nCount < 9; ++nCount )
			{
				if( info.containsKey(nCount) == true )
				{
					totalPot += (int)info.get(nCount);
				}
			}
		}
		
		return totalPot / size;
	}
	
	public int GetSplit(int seat)
	{
		int totalPot = 0;
		
		for( JSONObject info : PotUserSeats )
		{
			if( info.containsKey(seat) == true )
			{
				totalPot += (int)info.get(seat);
			}
		}
		
		return totalPot;
	}
	
	public Pot PotSlit(int amount)
	{
		Pot slitpot = new Pot();
		
		for( JSONObject info : PotUserSeats )
		{
			for( int nCount = 0; nCount < 9; ++nCount )
			{
				if( info.containsKey(nCount) == true )
				{
					if( (int)info.get(nCount) > amount )
					{
						info.replace(nCount, amount);
						slitpot.JoinPot(nCount, (int)info.get(nCount) - amount);	
					}
				}
			}
		}
		
		return slitpot;
	}

}
