package egovframework.example.sample.web;

import java.util.ArrayList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import egovframework.rte.psl.dataaccess.util.EgovMap;

public class InBox
{
    public String UID;
    public int Midx;
    public String Title;    
    public long Expire;

    public ArrayList<Item> ItemList = new ArrayList<Item>();     

    public InBox(String uid, int idx, String title)
    {
        this.UID = uid;
        this.Midx = idx;
        this.Title = title;
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

    public static InBox MakeInBox(String uid, int idx, String title)
    {        
        String inboxuid = uid;
        try {
            inboxuid = BytesToHex(Sha256(uid, idx));
        }  catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();        
        }      

        InBox inbox = new InBox(inboxuid, idx, title);

        return inbox;
    }
}