package egovframework.example.sample.web.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MemberInfo {
    public int midx;
    public long exp = 0;
    public String nickname = "";
    public String avata = "";
    public String ban = "";
    public long limit = 5000000000000l;
    public int ai=0;

    public void RecordBan(int type, long expire) {
        BanModel ban = new BanModel();
        ban.type = type;
        ban.expire = expire;
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.ban = mapper.writeValueAsString(ban);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }
        
    }
}
