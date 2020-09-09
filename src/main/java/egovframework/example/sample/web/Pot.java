package egovframework.example.sample.web;

import java.util.ArrayList;

import org.antlr.misc.IntSet;
import org.apache.commons.lang3.ArrayUtils;
import org.hsqldb.lib.ArrayUtil;

public class Pot {
	
	private ArrayList<Integer> PotUserSeats = new ArrayList<>();
	private int TotalPot = 0;
	
	public int JoinPot(int seat, int amount)
	{
		if( amount <= 0 )
		{
			return 1;
		}
		
		TotalPot += amount;
		
		if( PotUserSeats.contains(seat) == true)
		{					
			return 1;
		}
		else
		{			
			PotUserSeats.add(seat);
		}
		
		return 0;
	}
	
	public int[] RequestPot(int[] list)
	{
		ArrayList<Integer> winner = new ArrayList<>();
		for( int seat : list )
		{
			if( PotUserSeats.contains(seat) == true )
			{
				winner.add(seat);
			}
		}
		
		return ArrayUtils.toPrimitive(winner.toArray(new Integer[0]));		
	}
	
	public int GetPrizeMoney(int size)
	{
		return TotalPot / size;
	}

}
