package egovframework.example.sample.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.ClassPathResource;

import egovframework.rte.psl.dataaccess.util.EgovMap;

public class Attendance {
    
    public int Midx;
    public int Count;
    public long NextTick;

    public static Attendance MakeAttendance(int midx)
    {
        Attendance attendance = new Attendance(midx, 0, 0);

        EgovMap in = new EgovMap();
        in.put("midx", midx);

        EgovMap ed = (EgovMap) SocketHandler.sk.sampleDAO.select("GetAttendance", in);
        if( ed != null )
        {
            attendance.Count = (int)ed.get("count");
            attendance.NextTick = (long)ed.get("nexttick");
        }
        else
        {
            EgovMap insertin = new EgovMap();            
            insertin.put("midx", midx);            

            SocketHandler.sk.sampleDAO.insert("InsertAttendance", insertin);
        }

        return attendance;
    }

    public Attendance(int idx, int count, long tick)
    {
        this.Midx = idx;
        this.Count = count;
        this.NextTick = tick;
    }

    public int CheckAttendance()
    {
        if( this.NextTick > System.currentTimeMillis() )
        {
            return this.Count;
        }

        int count = this.Count;

        this.NextTick = System.currentTimeMillis() - (System.currentTimeMillis() % 86400000) + 86400000;          
        
        ClassPathResource resource = new ClassPathResource("json/attendance.json");
        ObjectMapper mapper = new ObjectMapper();

        try {
            Path path = Paths.get(resource.getURI());
            String content = Files.readString(path);	 
            
            ArrayList<AttendanceItem> itemlist = mapper.readValue(content, new TypeReference<ArrayList<AttendanceItem>>() {});

            InBox inbox = InBox.MakeInBox("" + this.Count, this.Midx, 1, "admin");
            
            for( AttendanceItem item : itemlist )
            {
                if( item.day == this.Count )
                {
                    for( int nCount = 0; nCount < item.reward.size(); ++nCount )
                    {
                        AttendanceReward reward = item.reward.get(nCount);
                        Item rewarditem = new Item(reward.type, reward.amount);
                        inbox.ItemList.add(rewarditem);
                    }                    
                }
            }
                        
            inbox.Expire = System.currentTimeMillis() + 172800000;

            EgovMap inboxin = new EgovMap();
            inboxin.put("uid", inbox.UID);
            inboxin.put("midx", inbox.Midx);
            inboxin.put("type", inbox.Type);
            inboxin.put("title", inbox.Title);
            inboxin.put("body", mapper.writeValueAsString(inbox.ItemList));
            inboxin.put("expire", inbox.Expire);

            SocketHandler.sk.sampleDAO.insert("AddInbox", inboxin);

            EgovMap searchin = new EgovMap();
            searchin.put("count", (this.Count + 1) % itemlist.size());
            searchin.put("nexttick", this.NextTick);
            searchin.put("midx", this.Midx);

            SocketHandler.sk.sampleDAO.update("UpdateAttendance", searchin);

            count = (this.Count + 1) % itemlist.size();

        }
        catch (IOException e) {
			e.printStackTrace();
		}        
        
        return count;
    }
    
}
