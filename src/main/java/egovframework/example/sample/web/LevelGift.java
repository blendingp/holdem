package egovframework.example.sample.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.ClassPathResource;

import egovframework.example.sample.web.model.GiftLogModel;
import egovframework.example.sample.web.model.LevelGiftModel;
import egovframework.example.sample.web.model.MemberInfo;
import egovframework.rte.psl.dataaccess.util.EgovMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class LevelGift {

    public static boolean CheckLevelGift(MemberInfo info, int idx)
    {       
        if(info.lastlevel >= GetLevel(info) )
        {
            return false;
        }

        info.lastlevel = GetLevel(info);                
        RewardGift(GetLevel(info), idx);

        return true;
    }

    private static int GetLevel(MemberInfo info)
	{
		int level = 1;
        long necessary = (5 * level) + (5 * (long)Math.pow(level, 2));
        long exp = info.exp;
        while (exp >= necessary)
        {
            exp -= necessary;
            level++;
            necessary = (5 * level) + (5 * (long)Math.pow(level, 2));
        }

        return level;
	}

    private static void RewardGift(int level, int idx)
    {
        ClassPathResource resource = new ClassPathResource("json/levelreward.json");
        ObjectMapper mapper = new ObjectMapper();

        try {
            Path path = Paths.get(resource.getURI());
            String content = Files.readString(path);	 
            
            ArrayList<LevelGiftModel> itemlist = mapper.readValue(content, new TypeReference<ArrayList<LevelGiftModel>>() {});

            InBox inbox = InBox.MakeInBox("levelup", idx, 6, "admin");
            
            for( LevelGiftModel item : itemlist)
            {
                if( item.level == level )
                {
                    for( int nCount = 0; nCount < item.reward.length; ++nCount )
                    {
                        Item reward = item.reward[nCount];
                        Item rewarditem = new Item();
                        rewarditem.Amount = reward.Amount;
                        rewarditem.Type = reward.Type;
                        
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

        }
        catch (IOException e) {
			e.printStackTrace();
		}        
    }    
}
