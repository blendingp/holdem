package egovframework.example.sample.web.model;

import java.util.ArrayList;

public class RoomInfoModel {

    public int roomidx;
    public int maxuser;
    public long ante;
    public long lowerlimit;
    public long max;
    public long playcount;
    public long totalbetamount;
    public long totalwinamount;
    public ArrayList<RoomHistory> roomhistory = new ArrayList<>();

    public void StartGame()
    {
        playcount++;   
    }

    public void Bet(long amount)
    {
        totalbetamount += amount;
    }

    public void EndGame(String winner, long amount)
    {
        totalwinamount += amount;
        for( int nCount = 0; nCount < roomhistory.size(); nCount++ )
        {
            System.out.println("dist : " + (playcount - roomhistory.get(nCount).playcount));
            if( playcount - roomhistory.get(nCount).playcount > 4)
            {
                roomhistory.remove(nCount);
                nCount = 0;
            }
        }

        RoomHistory history = new RoomHistory();
        history.playcount = playcount;
        history.winamount = amount;
        history.winplayer = winner;

        roomhistory.add(history);
    }
    
}
