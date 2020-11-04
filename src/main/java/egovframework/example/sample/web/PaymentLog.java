package egovframework.example.sample.web;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.JSONObject;

import egovframework.example.sample.web.model.PaymentLogModel;
import egovframework.rte.psl.dataaccess.util.EgovMap;

public class PaymentLog {    

    public static void Insert(int midx, int isCash, long price, String product, String receipt) {
        
        LocalDateTime now = LocalDateTime.now();        
        String uid = "";
        try {
            uid = BytesToHex(Sha256(product, midx));            
            EgovMap payment = new EgovMap();
            payment.put("uid", uid);
            payment.put("midx", midx);
            payment.put("product", product);
            payment.put("receipt", receipt != null ? receipt : "");
            payment.put("iscash", isCash);
            payment.put("price", price);
            payment.put("year", now.getYear());
            payment.put("month", now.getMonthValue());
            payment.put("day", now.getDayOfMonth());

            SocketHandler.sk.sampleDAO.insert("InsertPaymentLog", payment);
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        }
    }

    public static long GetTotalPayment(int midx) {
        long total = 0;

        EgovMap in = new EgovMap();
        in.put("midx", midx);
        in.put("year", LocalDateTime.now().getYear());
        in.put("month", LocalDateTime.now().getMonthValue());

        ArrayList<EgovMap> ed = (ArrayList<EgovMap>) SocketHandler.sk.sampleDAO.list("GetPaymentLog", in);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonstring = mapper.writeValueAsString(ed);
            ArrayList<PaymentLogModel> loglist = mapper.readValue(jsonstring, new TypeReference<ArrayList<PaymentLogModel>>() {});
            for (PaymentLogModel model : loglist) {
                if(model.iscash == 1)
                {
                    total += model.price;
                }
            }
        } catch (JsonProcessingException e) {
            System.out.print(e.getMessage());
        }            

        return total;
    }

    private static byte[] Sha256(String seed, int idx) throws NoSuchAlgorithmException {
        MessageDigest messagediegest = MessageDigest.getInstance("SHA-256");
        messagediegest.update(seed.getBytes());
        messagediegest.update((""+idx).getBytes());        
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
