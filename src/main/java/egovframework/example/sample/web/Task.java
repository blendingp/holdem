package egovframework.example.sample.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.ClassPathResource;

import aj.org.objectweb.asm.Type;
import egovframework.example.sample.web.model.TaskModel;
import egovframework.rte.psl.dataaccess.util.EgovMap;

public class Task {

    public static ArrayList<TaskModel> GetTask(int midx) {
        ArrayList<TaskModel> list = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        EgovMap in = new EgovMap();
        in.put("midx", midx);
        ArrayList<EgovMap> ed = (ArrayList<EgovMap>) SocketHandler.sk.sampleDAO.list("GetTaskList", in);
        String json = null;

        try {
            json = mapper.writeValueAsString(ed);            

            if( json == null )
            {
                return list;
            }

            list = mapper.readValue(json, new TypeReference<ArrayList<TaskModel>>() {});

        } catch (JsonProcessingException e) {            
            System.out.println(e.getMessage());
        }
        
        for( TaskModel task : list )
        {
            task.GetRewardItem();            
        }

        return list;
    }

    public static void PayReward(String uid, User user)
    {
        ObjectMapper mapper = new ObjectMapper();

        for( TaskModel task : user.tasklist )
        {
            if( task.uid.equals(uid) == true)
            {
                try {
                    task.payed = 1;
                    task.isChanged = true;
                    InBox inbox = InBox.MakeInBox(uid, user.uidx, 5, "admin");
                    inbox.Expire = System.currentTimeMillis() + 86400000;
                    inbox.ItemList.add(task.reward);
            
                    EgovMap inboxin = new EgovMap();
                    inboxin.put("uid", inbox.UID);
                    inboxin.put("midx", inbox.Midx);
                    inboxin.put("type", inbox.Type);
                    inboxin.put("title", inbox.Title);
                
                    inboxin.put("body", mapper.writeValueAsString(inbox.ItemList));
                    inboxin.put("expire", inbox.Expire);
        
                    SocketHandler.sk.sampleDAO.insert("AddInbox", inboxin);

                    String value = mapper.writeValueAsString(task);			
                    EgovMap jsonobject = mapper.readValue(value, EgovMap.class);			                
                    SocketHandler.sk.sampleDAO.update("UpdateTask", jsonobject);

                } catch (JsonProcessingException e) {
                    System.out.println(e.getMessage());
                }                                            
            }
        }                
    }

    public static void IncreaseTask(User user, int type, long offset)
    {
        for( TaskModel task : user.tasklist )
        {
            if( task.type == type )            
            {
                task.current += offset;
                task.isChanged = true;
            }
        }
    }

    public static void Expired(User user)
    {
        ObjectMapper mapper = new ObjectMapper();
        for( TaskModel task : user.tasklist )
        {
            if( task.expire <= System.currentTimeMillis() )
            {
                task.expire = System.currentTimeMillis() - (System.currentTimeMillis() % 86400000) + 86400000;
                task.payed = 0;
                task.current = 0;
                task.isChanged = true;
                                
                try {
                    String value = mapper.writeValueAsString(task);			
                    EgovMap jsonobject = mapper.readValue(value, EgovMap.class);			                
                    SocketHandler.sk.sampleDAO.update("UpdateTask", jsonobject);
                }catch (JsonProcessingException e) {
                    System.out.println(e.getMessage());
                }    
            }      
        }          
    }

    public static void UpdateDB(User user)
    {
        ObjectMapper mapper = new ObjectMapper();
        for( TaskModel task : user.tasklist )
        {
            if( task.isChanged == true )
            {
                task.isChanged = false;

                try {
                    String value = mapper.writeValueAsString(task);			
                    EgovMap jsonobject = mapper.readValue(value, EgovMap.class);			                
                    SocketHandler.sk.sampleDAO.update("UpdateTask", jsonobject);
                }catch (JsonProcessingException e) {
                    System.out.println(e.getMessage());
                }     
            }              
        }        
    }

    public static ArrayList<TaskModel> MakeTask(int midx)
    {
        ArrayList<TaskModel> list = null;
        ObjectMapper mapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource("json/task/task.json");
        try {
            Path path = Paths.get(resource.getURI());
            String content = Files.readString(path);	 
            
            list = mapper.readValue(content, new TypeReference<ArrayList<TaskModel>>() {});
            for( TaskModel task : list )
            {
                task.uid = BytesToHex(Sha256(String.format("%d-%d-%d", midx, task.type, task.max), midx, task.type, task.max));                
                task.expire = System.currentTimeMillis() - (System.currentTimeMillis() % 86400000) + 86400000;
                task.current = 0;
                task.payed = 0;
                task.midx = midx;
            }
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }  
        catch(NoSuchAlgorithmException e)
        {
            System.out.println(e.getMessage());
        }

        return list;
    }

    private static byte[] Sha256(String seed, int idx, int type, long max) throws NoSuchAlgorithmException {
        MessageDigest messagediegest = MessageDigest.getInstance("SHA-256");
        messagediegest.update(seed.getBytes());
        messagediegest.update((""+idx).getBytes());        
        messagediegest.update((""+type).getBytes());        
        messagediegest.update((""+max).getBytes());        
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