package egovframework.example.sample.web;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

import javax.annotation.Resource;
import javax.json.JsonObject;

import org.json.simple.JSONObject;
import org.springframework.web.socket.WebSocketSession;

import egovframework.example.sample.service.impl.SampleDAO;
import egovframework.rte.psl.dataaccess.util.EgovMap;

public class Friend {

    private static byte[] Sha256(String seed, int idx, int friendidx) throws NoSuchAlgorithmException {
        MessageDigest messagediegest = MessageDigest.getInstance("SHA-256");
        messagediegest.update(seed.getBytes());
        messagediegest.update((""+idx).getBytes());
        messagediegest.update((""+friendidx).getBytes());
        
        return messagediegest.digest();
    }

    private static String BytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();

        for (byte b: bytes) {
          builder.append(String.format("%02x", b));
        }
        
        return builder.toString();
    }

    public static JSONObject SearchUserByID(String id) {
        JSONObject result = new JSONObject();
        result.put("cmd", "searchuser");
        EgovMap searchin = new EgovMap();
        searchin.put("muserid", id);

        EgovMap ed = (EgovMap) SocketHandler.sk.sampleDAO.select("SearchUser", searchin);
        if (ed == null) {
            result.put("result", false);
            return result;
        }

        result.put("result", true);
        result.put("id", id);

        return result;

    }

    public static JSONObject RequestFriend(User user, String id) {
        JSONObject result = new JSONObject();
        result.put("cmd", "requestresult");

        EgovMap searchin = new EgovMap();
        searchin.put("muserid", id);
        EgovMap usered = (EgovMap) SocketHandler.sk.sampleDAO.select("SearchUser", searchin);

        if (usered == null) {
            result.put("result", false);
            return result;
        }

        if (user.uidx == Integer.parseInt("" + usered.get("midx"))) {
            result.put("result", false);
            return result;
        }

        EgovMap friendsin = new EgovMap();
        friendsin.put("midx", user.uidx);

        ArrayList<EgovMap> friended = (ArrayList<EgovMap>) SocketHandler.sk.sampleDAO.list("GetFriends", friendsin);        

        if (friended != null) {
            for (int nCount = 0; nCount < friended.size(); ++nCount) {
                if (friended.get(nCount).get("friendidx") != null) {
                    if ((int) friended.get(nCount).get("friendidx") == Integer.parseInt("" + usered.get("midx"))) {
                        result.put("result", false);
                        return result;
                    }
                }
            }   
        }

        EgovMap requestfriendsin = new EgovMap();
        requestfriendsin.put("friendidx", user.uidx);

        ArrayList<EgovMap> requestfriended = (ArrayList<EgovMap>) SocketHandler.sk.sampleDAO.list("GetRequestFriend", requestfriendsin);        

        if (requestfriended != null) {
            for (int nCount = 0; nCount < requestfriended.size(); ++nCount) {
                if (requestfriended.get(nCount).get("midx") != null) {
                    if ((int) requestfriended.get(nCount).get("midx") == Integer.parseInt("" + usered.get("midx"))) {
                        result.put("result", false);
                        return result;
                    }
                }
            }   
        }
        
        String uid = "";
        try {
            uid = BytesToHex(Sha256("" + System.currentTimeMillis(), user.uidx, Integer.parseInt("" + usered.get("midx"))));
        }  catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        EgovMap requestin = new EgovMap();
        requestin.put("uid", uid);
        requestin.put("midx", user.uidx);
        requestin.put("friendidx", Integer.parseInt("" + usered.get("midx")));
        requestin.put("status", 0);

        SocketHandler.sk.sampleDAO.insert("requestfriend", requestin);

        result.put("result", true);

        return result;
    }    

    public static ArrayList<FriendModel> GetRequestFriendList(User user, int status)
    {        
        EgovMap friendsin = new EgovMap();
        friendsin.put("friendidx", user.uidx);
        friendsin.put("status", status);

        ArrayList<EgovMap> friendlist = (ArrayList<EgovMap>) SocketHandler.sk.sampleDAO.list("GetRequestFriendList", friendsin);       
        ArrayList<FriendModel> friendmodellist = new ArrayList<>();

        if (friendlist != null) {
            for (int nCount = 0; nCount < friendlist.size(); ++nCount) {
                FriendModel model = new FriendModel(friendlist.get(nCount).get("uid").toString(), 
                (int)friendlist.get(nCount).get("midx"), user.uidx, (int)friendlist.get(nCount).get("status"),  
                friendlist.get(nCount).get("createdtime").toString(), (long)friendlist.get(nCount).get("nextsendtime")
                , (long)friendlist.get(nCount).get("nextsendtimefriend"));

                JSONObject cobj = new JSONObject();		
                cobj.put("model", model);            

                System.out.println(cobj.toJSONString());

                friendmodellist.add(model);
            }   
        }

        JSONObject cobj = new JSONObject();		
        cobj.put("list", friendmodellist);            

        System.out.println(cobj.toJSONString());

        return friendmodellist;

    }

    public static ArrayList<FriendModel> GetFriendList(User user)
    {        
        EgovMap friendsin = new EgovMap();
        friendsin.put("midx", user.uidx);

        ArrayList<EgovMap> friendlist = (ArrayList<EgovMap>) SocketHandler.sk.sampleDAO.list("GetFriendList", friendsin);       
        ArrayList<FriendModel> friendmodellist = new ArrayList<>();

        if (friendlist != null) {
            for (int nCount = 0; nCount < friendlist.size(); ++nCount) {
                FriendModel model = new FriendModel(friendlist.get(nCount).get("uid").toString(), 
                user.uidx, (int)friendlist.get(nCount).get("friendidx"), (int)friendlist.get(nCount).get("status"), 
                friendlist.get(nCount).get("createdtime").toString(), (long)friendlist.get(nCount).get("nextsendtime")
                , (long)friendlist.get(nCount).get("nextsendtimefriend"));                

                friendmodellist.add(model);
            }   
        }

        return friendmodellist;

    }

    public static JSONObject AcceptFriend(String uid)
    {
        JSONObject result = new JSONObject();
        result.put("cmd", "acppetfriend");
        result.put("uid", uid);

        EgovMap searchin = new EgovMap();
        searchin.put("uid", uid);

        int rt = (int) SocketHandler.sk.sampleDAO.update("UpdateFriendStatus", searchin);
        result.put("result", rt > 0);
        return result;

    }

}
