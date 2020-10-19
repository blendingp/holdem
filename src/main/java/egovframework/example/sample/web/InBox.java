package egovframework.example.sample.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import egovframework.rte.psl.dataaccess.util.EgovMap;

public class InBox
{
    public String UID;
    public int Midx;
    public int Type;
    public String Title;    
    public long Expire;

    public ArrayList<Item> ItemList = new ArrayList<Item>();     

    public static InBox MakeInBox(String uid, int idx, int type, String title)
    {        
        String inboxuid = uid;
        try {
            inboxuid = BytesToHex(Sha256(uid, idx));
        }  catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();        
        }      

        InBox inbox = new InBox(inboxuid, idx, type, title);

        return inbox;
    }

    public static ArrayList<InBox> GetUserInbox(int idx, int type)
    {        
        ArrayList<InBox> inboxlist = new ArrayList<InBox>();

        EgovMap delin = new EgovMap();
        delin.put("midx", idx);
        delin.put("now", System.currentTimeMillis());
        System.out.println(System.currentTimeMillis());
        SocketHandler.sk.sampleDAO.delete("DeleteExpire", delin);

        EgovMap in = new EgovMap();
        in.put("midx", idx);
        in.put("type", type);

        ArrayList<EgovMap> ed = (ArrayList<EgovMap>)SocketHandler.sk.sampleDAO.list("GetInboxList", in);
        
        if( ed != null )
        {            
            for( int nCount =0; nCount < ed.size(); ++nCount )
            {
                InBox inbox = new InBox(ed.get(nCount).get("uid").toString(), idx, type, ed.get(nCount).get("title").toString());
                inbox.Expire = (long)ed.get(nCount).get("expire");

                ObjectMapper mapper = new ObjectMapper();
                try
                {
                    inbox.ItemList = mapper.readValue(ed.get(nCount).get("body").toString(), new TypeReference<ArrayList<Item>>() {});                
                }
                catch(JsonProcessingException e)
                {
                    System.out.println(e.getMessage());    
                }
                                
                inboxlist.add(inbox);                
            }
        }

        return inboxlist;
    }

    public static InBox GetInbox(int idx, String uid)
    {                
        EgovMap in = new EgovMap();
        in.put("uid", uid);

        EgovMap ed = (EgovMap)SocketHandler.sk.sampleDAO.select("GetInbox", in);

        InBox inbox = null;
        
        if( ed != null )
        {            
            inbox = new InBox(uid, idx, 0, "");
            inbox.Expire = (long)ed.get("expire");
            if( inbox.Expire < System.currentTimeMillis() )
            {
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            try
            {
                inbox.ItemList = mapper.readValue(ed.get("body").toString(), new TypeReference<ArrayList<Item>>() {});                
            }
            catch(JsonProcessingException e)
            {
                System.out.println(e.getMessage());    
            }                                
        }

        return inbox;
    }

    public InBox(String uid, int idx, int type, String title)
    {
        this.UID = uid;
        this.Midx = idx;
        this.Type = type;
        this.Title = title;
    }

    public boolean DeleteInBox()
    {
        EgovMap deletein = new EgovMap();
        deletein.put("uid", this.UID);

        int rt = SocketHandler.sk.sampleDAO.delete("DeleteInBox", deletein);
        return true;
    }

    private static byte[] Sha256(String seed, int idx) throws NoSuchAlgorithmException {
        MessageDigest messagediegest = MessageDigest.getInstance("SHA-256");
        messagediegest.update(seed.getBytes());
        messagediegest.update((""+idx).getBytes());        
        messagediegest.update((""+System.currentTimeMillis()).getBytes());
        
        return messagediegest.digest();
    }

    private static String BytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();

        for (byte b: bytes) {
          builder.append(String.format("%02x", b));
        }
        
        return builder.toString();
    }    
}