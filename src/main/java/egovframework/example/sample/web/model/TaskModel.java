package egovframework.example.sample.web.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.ClassPathResource;

import egovframework.example.sample.web.Item;

public class TaskModel {

    public String uid;
    public int midx;
    public int type;
    public long current;
    public int payed;
    public Item reward;
    public long max;            
    public long expire;
    public boolean isChanged = true;
    
    public void GetRewardItem()
    {
        ObjectMapper mapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource("json/task/task.json");
        try {
            Path path = Paths.get(resource.getURI());
            String content = Files.readString(path);	 
            
            ArrayList<TaskModel> list = mapper.readValue(content, new TypeReference<ArrayList<TaskModel>>() {});
            for( TaskModel task : list )
            {
                if( task.type == this.type && task.max == this.max )
                {
                    reward = task.reward;
                }
            }
            isChanged = false;
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }        
    }
    
}
