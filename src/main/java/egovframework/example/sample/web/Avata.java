package egovframework.example.sample.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.ClassPathResource;

import egovframework.example.sample.web.model.AvataModel;
import egovframework.rte.psl.dataaccess.util.EgovMap;

public class Avata {

    public static boolean Buy(String item, int midx)
    {
        ClassPathResource resource = new ClassPathResource("json/shop/avata.json");
        ObjectMapper mapper = new ObjectMapper();

        try {            
            Path path = Paths.get(resource.getURI());
            String content = Files.readString(path);	 
            
            ArrayList<AvataModel> list = mapper.readValue(content, new TypeReference<ArrayList<AvataModel>>() {});

            for( AvataModel avataitem : list )
            {
                if( avataitem.id.equals(item) == true)
                {
                    for( int nCount = 0; nCount < avataitem.avata; ++nCount )
                    {
                        InBox avatainbox = InBox.MakeInBox(item, midx, 3, "admin");
                        Item avatapayment = new Item();
                        avatapayment.Type = "avata";
                        avatapayment.Amount = 1;
                        avatainbox.ItemList.add(avatapayment);
                        avatainbox.Expire = System.currentTimeMillis() + 604800000;

                        try {
                            EgovMap paymentin = new EgovMap();
                            paymentin.put("uid", avatainbox.UID);
                            paymentin.put("midx", avatainbox.Midx);
                            paymentin.put("type", avatainbox.Type);
                            paymentin.put("title", avatainbox.Title);
                            paymentin.put("body", mapper.writeValueAsString(avatainbox.ItemList));
                            paymentin.put("expire", avatainbox.Expire);
                
                            SocketHandler.sk.sampleDAO.insert("AddInbox", paymentin);
                        }
                        catch(IOException e){
                
                        }
                    }   
                    
                    InBox avatainbox = InBox.MakeInBox(item, midx, 3, "admin");
                    Item avatapayment = new Item();
                    avatapayment.Type = "point";
                    avatapayment.Amount = avataitem.point;
                    avatainbox.ItemList.add(avatapayment);
                    avatainbox.Expire = System.currentTimeMillis() + 604800000;

                    try {
                        EgovMap paymentin = new EgovMap();
                        paymentin.put("uid", avatainbox.UID);
                        paymentin.put("midx", avatainbox.Midx);
                        paymentin.put("type", avatainbox.Type);
                        paymentin.put("title", avatainbox.Title);
                        paymentin.put("body", mapper.writeValueAsString(avatainbox.ItemList));
                        paymentin.put("expire", avatainbox.Expire);
                
                        SocketHandler.sk.sampleDAO.insert("AddInbox", paymentin);
                    }
                    catch(IOException e){
                
                    }
                    
                }
            }

        }catch (IOException e) {
			e.printStackTrace();
		}   

        return false;
        
    }
    
}
