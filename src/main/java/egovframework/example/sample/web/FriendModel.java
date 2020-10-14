package egovframework.example.sample.web;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import egovframework.rte.psl.dataaccess.util.EgovMap;

public class FriendModel {

    public String UID;
    public int Status;
    public long Createtime;
    public long Nextsendtime;
    public long Nextsendtimefriend;
    public int Midx;
    public int Friendidx;
    public boolean IsConnected;    

    public FriendModel(String uid, int idx, int fidx, int status, String createtime, long nextsend, long nextsendfriend) {
        this.UID = uid;
        this.Midx = idx;
        this.Friendidx = fidx;
        this.Status = status;        
        try {
            createtime.replace(".0", "");
            SimpleDateFormat localdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");        
            Date  date = localdate.parse(createtime);
            this.Createtime = date.getTime();            
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
        
        this.Nextsendtime = nextsend;
        this.Nextsendtimefriend = nextsendfriend;
        this.IsConnected = false;
    }

    public String GetUserID(int idx)
    {
        EgovMap searchin = new EgovMap();
        searchin.put("midx", idx);

        EgovMap ed = (EgovMap) SocketHandler.sk.sampleDAO.select("GetUserID", searchin);

        return ed.get("muserid").toString();
    }

    public int GetUserBalance(int idx)
    {
        EgovMap searchin = new EgovMap();
        searchin.put("midx", idx);

        EgovMap ed = (EgovMap) SocketHandler.sk.sampleDAO.select("GetUserBalance", searchin);

        return (int)ed.get("amount");
    }

    public boolean SendGold(int idx, String uid) throws JsonProcessingException
    {
        if( this.UID == uid)
        {
            System.out.println(this.UID);    
            System.out.println(uid);    
            return false;            
        }

        if( this.Status <= 0 )
        {
            System.out.println("status : " + this.Status);    
            return false;
        }

        ObjectMapper mapper = new ObjectMapper();

        String name = this.GetUserID(idx);
        InBox inbox = InBox.MakeInBox(this.UID, idx, name);
        Item item = new Item("balance", 100);
        inbox.ItemList.add(item);
        inbox.Expire = System.currentTimeMillis() + 259200000;

        if( this.Friendidx == idx )
        {
            if( this.Nextsendtimefriend > System.currentTimeMillis() )
            {
                return false;
            }

            inbox.Midx = this.Midx;
            
            this.Nextsendtimefriend = System.currentTimeMillis() + 86400000;            
        }

        if( this.Midx == idx )
        {
            if( this.Nextsendtime> System.currentTimeMillis() )
            {
                return false;
            }
            
            inbox.Midx = this.Friendidx;

            this.Nextsendtime = System.currentTimeMillis() + 86400000;
        }

        EgovMap searchin = new EgovMap();
        searchin.put("uid", uid);
        searchin.put("nextsendtime", this.Nextsendtime);
        searchin.put("nextsendtimefriend", this.Nextsendtimefriend);

        SocketHandler.sk.sampleDAO.update("UpdateGiveGoldTime", searchin);

        EgovMap inboxin = new EgovMap();
        inboxin.put("uid", inbox.UID);
        inboxin.put("midx", inbox.Midx);
        inboxin.put("title", inbox.Title);
        inboxin.put("body", mapper.writeValueAsString(inbox.ItemList));
        inboxin.put("expire", inbox.Expire);

        SocketHandler.sk.sampleDAO.insert("AddInbox", inboxin);

        System.out.println(mapper.writeValueAsString(inbox));
            
        return true;

    }
    
}
