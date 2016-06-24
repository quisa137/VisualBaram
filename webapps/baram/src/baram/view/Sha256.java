package baram.view;

import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sha256 {
	
	public static String encrypt(String s, int limit, Logger logger) {
		
		try {
			StringBuilder sb = new StringBuilder();
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(s.getBytes());
			byte bytes[] = md.digest();
			for(int i=0;i<bytes.length;i++) {
				String hexString = Integer.toHexString(bytes[i] & 0xFF);
				if(hexString.length()<2) {
					sb.append("0");
					sb.append(hexString);
				} else {
					sb.append(hexString);
				}
			}
			String enc = sb.toString();
			if(limit<1) {
				limit = enc.length();
			}
			limit = Math.min((limit-1)/2, enc.length());
			
			return enc.substring(0,limit) + enc.substring(enc.length()-limit, enc.length());
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "Error in Sha256.encrypt.", e);
		}
		
		return null;
	}

	public static void main(String args[]) {
		System.out.println("'"+encrypt("demo", 15, Logger.getLogger(Sha256.class.getName()))+"'");
		System.out.println("'"+encrypt("admin", 15, Logger.getLogger(Sha256.class.getName()))+"'");
	}
}