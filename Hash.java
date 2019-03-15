package cs455.scaling.hash;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {

	public String SHA1FromBytes(byte[] data) {
		MessageDigest digest;
		String result = "";
		try {
			digest = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			System.err.println("No SHA1 Algorithm:" + e);
			return null;
		}
		byte[] hash = digest.digest(data);
		BigInteger hashInt = new BigInteger(1, hash);
		result = String.format("%040x", hashInt);
		return result;
	}
	
	/*public static void main(String[] args) {
		Hash hash = new Hash();
		byte[] data = new byte[8000];
		Random r = new Random();
		for(int i = 0; i < data.length; i++) {
			data[i] = (byte) r.nextInt(10);
		}
		String result = hash.SHA1FromBytes(data);
		System.out.println(result.length());
	}*/
	
}
