package egovframework.example.sample.web.model;

import java.util.Date;

public class ProfileModel {
    public int midx = 0;
    public long totalgame = 0;
    public long win = 0;
    public long lose = 0;
    public long highgaingold = 0;
    public long gaingold = 0;
    public long gainbalance = 0;
    public long putallin = 0;
    public long goldrefillcount = 1;
    public long chiprefillcount = 3;
//    public long expire = System.currentTimeMillis() - (System.currentTimeMillis() % 86400000) + 86400000;
    public long expire = (new Date()).getTime();

    public boolean CheckExpire()
    {
    	Date a=new Date(),b=new Date();
    	a.setTime(expire);
    	b.setHours(0);
    	b.setMinutes(0);
    	
        if( a.getTime() < b.getTime()  )
        {
            return true;
        }

        return false;
    }
}
