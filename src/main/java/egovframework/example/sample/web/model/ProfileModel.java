package egovframework.example.sample.web.model;

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
    public long expire = System.currentTimeMillis() - (System.currentTimeMillis() % 86400000) + 86400000;

    public boolean CheckExpire()
    {
        if( expire < System.currentTimeMillis() )
        {
            return true;
        }

        return false;
    }
}
