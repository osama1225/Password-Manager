import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class PasswordManager {

	private Cipher cipher;
	byte[] salt = new String("123456789").getBytes();
	int iterationCount = 1024;
	int keyLength = 256;
	SecretKey key;

	public PasswordManager() {

	}

	public void encrypt(String data) {

	}

	public void decrypt(String encryptedData) {

	}
}
