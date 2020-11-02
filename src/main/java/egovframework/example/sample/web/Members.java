package egovframework.example.sample.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.ClassPathResource;

import egovframework.example.sample.web.model.MembersInfo;
import egovframework.example.sample.web.model.MembersShopInfo;
import egovframework.rte.psl.dataaccess.util.EgovMap;

public class Members {
    
    public static MembersInfo BuyMembers(User user, int type)
    {
        ObjectMapper mapper = new ObjectMapper();
        MembersInfo memberinfo = new MembersInfo();
        MembersShopInfo shopinfo = GetShopInfo(type);

        EgovMap delin = new EgovMap();
        delin.put("midx", user.uidx);
        delin.put("now", System.currentTimeMillis());        
        SocketHandler.sk.sampleDAO.delete("DeleteMemberExpire", delin);

        int result = IsAbleBuy(user, type);
        
        if( result < 0 )
        {
            return user.memberInfo;
        }

        user.cash -= shopinfo.amount;

        // 기간 연장
        if( result == 0 )
        {
            user.memberInfo.expire += shopinfo.effect.expire;
        }
        else if( result == 1 ) // 업그레이드
        {
            int remainday = (int)((user.memberInfo.expire - System.currentTimeMillis())/86400000);

            if( remainday > 0 )
            {
                MembersShopInfo info = GetShopInfo(user.memberInfo.grade);
                long unitprice = info.amount / 30;

                InBox refundinbox = InBox.MakeInBox("refund_"+user.memberInfo.grade, user.uidx, 2, "admin");
                Item refunditem = new Item();
                refunditem.Type = "cash";
                refunditem.Amount = remainday * unitprice;                                
                refundinbox.ItemList.add(refunditem);
                refundinbox.Expire = System.currentTimeMillis() + 604800000;
                
                try {
                    EgovMap refundin = new EgovMap();
                    refundin.put("uid", refundinbox.UID);
                    refundin.put("midx", refundinbox.Midx);
                    refundin.put("type", refundinbox.Type);
                    refundin.put("title", refundinbox.Title);
                    refundin.put("body", mapper.writeValueAsString(refundinbox.ItemList));
                    refundin.put("expire", refundinbox.Expire);

                    SocketHandler.sk.sampleDAO.insert("AddInbox", refundin);
                }
                catch(IOException e){

                }
            }

            user.memberInfo = shopinfo.effect;
            user.memberInfo.expire = System.currentTimeMillis() + shopinfo.effect.expire;            
        }

        InBox payment = InBox.MakeInBox("payment_"+user.memberInfo.grade, user.uidx, 3, "admin");
        for( Item item : shopinfo.payitem )
        {
            payment.ItemList.add(item);    
        }        

        payment.Expire = System.currentTimeMillis() + 604800000;

        try {
            EgovMap paymentin = new EgovMap();
            paymentin.put("uid", payment.UID);
            paymentin.put("midx", payment.Midx);
            paymentin.put("type", payment.Type);
            paymentin.put("title", payment.Title);
            paymentin.put("body", mapper.writeValueAsString(payment.ItemList));
            paymentin.put("expire", payment.Expire);

            SocketHandler.sk.sampleDAO.insert("AddInbox", paymentin);
        }
        catch(IOException e){

        }
        
        EgovMap memberin = new EgovMap();
        memberin.put("midx", user.uidx);					
        memberin.put("type", user.memberInfo.grade);					
        memberin.put("expire", user.memberInfo.expire);			
        
        SocketHandler.sk.sampleDAO.insert("InsertMembers", memberin);

        return memberinfo;
    }

    public static MembersInfo GetUserMembersInfo(User user)
    {
        MembersInfo info = new MembersInfo();

        EgovMap delin = new EgovMap();
        delin.put("midx", user.uidx);
        delin.put("now", System.currentTimeMillis());        
        SocketHandler.sk.sampleDAO.delete("DeleteMemberExpire", delin);

        EgovMap in = new EgovMap();
        in.put("midx", user.uidx);

        EgovMap ed = (EgovMap) SocketHandler.sk.sampleDAO.select("GetMembers", in);
        if( ed != null )
        {                        
            MembersShopInfo shopinfo = GetShopInfo((int)ed.get("type"));

            info = shopinfo.effect;
            info.expire = (long)ed.get("expire");
        }
        else
        {
            //System.out.println("ExpireMembers");
            user.ExpireMembers();
        }

        return info;
    }

    private static int IsAbleBuy(User user, int type)
    {
        MembersShopInfo shopinfo = GetShopInfo(type);

        if( user.memberInfo.grade > 0 && user.memberInfo.expire < System.currentTimeMillis() )
        {
            user.ExpireMembers();            
            return -1;
        }

        // 구매에 필요한 재화
        if(user.cash < shopinfo.amount)
        {
            return -1;
        }

        // 하위 티어 인지 체크
        if(user.memberInfo.grade > type)
        {
            return -1;
        }

        if(user.memberInfo.grade == type)
        {
            // 기간 연장시 99일을 넘으면 구매 못하게
            if( ((user.memberInfo.expire - System.currentTimeMillis())/86400000) + 30 > 99 )
            {
                return -1;
            }
            
            return 0;
        }

        return 1;

    }

    private static MembersShopInfo GetShopInfo(int type)
    {
        ObjectMapper mapper = new ObjectMapper();
        MembersShopInfo shopinfo = null;
        String jsonpath = "";

        if( type == 1)
        {
            jsonpath = "json/shop/members_silver.json";
            
        }
        else if( type == 2)
        {
            jsonpath = "json/shop/members_gold.json";
        }
        else if( type == 3)
        {
            jsonpath = "json/shop/members_dia.json";
        }

        try{

            ClassPathResource resource = new ClassPathResource(jsonpath);                
            Path path = Paths.get(resource.getURI());
            String content = Files.readString(path);	             

            shopinfo = mapper.readValue(content, MembersShopInfo.class);
        }
        catch (IOException e) {
            
            e.printStackTrace();
        }  

        return shopinfo;
    }

}
