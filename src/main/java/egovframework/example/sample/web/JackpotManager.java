package egovframework.example.sample.web;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.springframework.web.socket.TextMessage;

import egovframework.rte.psl.dataaccess.util.EgovMap;

public class JackpotManager {

    private static long _amount = 0;
    private static long _tick = 0;
    private static boolean isChanged = false;

    public static boolean Init() {
        EgovMap in = new EgovMap();
        EgovMap ed = (EgovMap) SocketHandler.sk.sampleDAO.select("GetJackpot", in);
        if (ed != null) {
            _amount = (long) ed.get("jackpot");
        } else {
            _amount = 0;
            return false;
        }

        return true;
    }

    public static long GetJackpotAmount() {
        return _amount;
    }

    public static boolean WithdrawJackpot() {
        _amount = 0;
        isChanged = true;

        return true;
    }

    public static boolean AccumulateJackpot(long amount) {
        long offset = (long) (amount * 0.005);
        _amount += offset;
        isChanged = true;

        return true;
    }

    public static boolean Update() {
        _tick += 500;

        if (_tick >= 60000 && isChanged == true) {
            EgovMap in = new EgovMap();
            in.put("jackpot", _amount);
            SocketHandler.sk.sampleDAO.update("UpdateJackpot", in);
            _tick = 0;
            isChanged = false;
        } else {
            return false;
        }

        return true;
    }

    public static boolean SendJackpotMessage(User user) {
        JSONObject jackpot = new JSONObject();
        jackpot.put("cmd", "jackpot");
        jackpot.put("amount", _amount);

        try {
            //System.out.println(jackpot.toJSONString());
            user.session.sendMessage(new TextMessage(jackpot.toJSONString()));
        } catch (IOException e) {
            //System.out.println(e.getMessage());
            return false;
        }

        return true;
    }
    
}
