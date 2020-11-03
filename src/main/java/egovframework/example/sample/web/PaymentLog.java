package egovframework.example.sample.web;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

import org.json.simple.JSONObject;

public class PaymentLog {
    
    private static LocalDateTime now;

    public static void Insert(int midx, int isCash, long price, String product, String receipt)
    {        
        now = LocalDateTime.now();       
        String uid;
        try {
            uid = BytesToHex(Sha256(product, midx));
            JSONObject payment = new JSONObject();        
            payment.put("uid", uid);
            payment.put("midx", midx);
            payment.put("product", product);
            payment.put("receipt", receipt);
            payment.put("iscash", isCash);
            payment.put("year", now.getYear());
            payment.put("month", now.getMonthValue());
            payment.put("day", now.getDayOfMonth());

            SocketHandler.sk.sampleDAO.insert("InsertPaymentLog", payment);
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());            
        }                
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
