import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.print.attribute.standard.PDLOverrideSupported;
import javax.swing.text.MaskFormatter;

public class PasswordManager {

	private Cipher cipher;
	private byte[] salt;
	private final int iterationCount = 1024;
	private final int keyLength = 256;
	private final String PBKDFAlgo = "PBKDF2WithHmacSHA1";
	private SecretKey key;
	private String masterPass;
	private byte[] encryptedMaster;// will be saved locally

	public PasswordManager() {
		try {
			// first check if A master is stored before.
			encryptedMaster = getSavedEncryptedMaster();
		} catch (Exception e) {
			System.err.println("Error in passManager");
		}

	}

	public boolean loginDone(String newMasterPass)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		if (encryptedMaster == null) {
			// this is the first time to set master pass
			generateRandom();
			encryptedMaster = encryptMasterPass(newMasterPass);
			// save encrypted pass.
			savedEncryptedMasterPass();
			return true;
		} else {
			// check if Entered Master is the same as used before.
			return authenticate(newMasterPass);
		}
	}

	public void encrypt(String data) {

	}

	public void decrypt(String encryptedData) {

	}

	private void generateRandom() throws NoSuchAlgorithmException {
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		// Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
		salt = new byte[8];
		random.nextBytes(salt);

	}

	private byte[] encryptMasterPass(String masterPass)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDFAlgo);
		KeySpec spec = new PBEKeySpec(masterPass.toCharArray(), salt,
				iterationCount, keyLength);
		return factory.generateSecret(spec).getEncoded();
	}

	private boolean authenticate(String newMaster)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] newEncrypted = encryptMasterPass(newMaster);
		return Arrays.equals(newEncrypted, encryptedMaster);
	}

	private void savedEncryptedMasterPass() {
		FileOutputStream os;
		try {
			os = new FileOutputStream(System.getProperty("user.dir")
					+ "//settings.dat");
			ObjectOutputStream out = new ObjectOutputStream(os);
			Settings set = new Settings(salt, encryptedMaster);
			out.writeObject(set);
			out.close();
			os.close();
		} catch (Exception e) {
			System.err.println("Error in saving encrypted Pass");
			e.printStackTrace();
		}
	}

	private byte[] getSavedEncryptedMaster() {
		File f = new File(System.getProperty("user.dir") + "//settings.dat");
		if (f.exists()) {
			try {
				FileInputStream is = new FileInputStream(f);
				ObjectInputStream in = new ObjectInputStream(is);
				Settings set = (Settings) in.readObject();
				salt = set.getSalt();
				in.close();
				is.close();
				return set.getEncryptedPass();
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}
}
