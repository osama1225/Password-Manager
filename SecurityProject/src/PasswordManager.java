import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.omg.CORBA.portable.InputStream;

public class PasswordManager {
	static {
		System.out.println("here in static");
		// add at runtime the Bouncy Castle Provider
		// the provider is available only for this application
		Security.addProvider(new BouncyCastleProvider());

		// BC is the ID for the Bouncy Castle provider;
		if (Security.getProvider("BC") == null) {
			System.out.println("Bouncy Castle provider is NOT available");
		} else {
			System.out.println("Bouncy Castle provider is available");
		}
	}

	private Mac mac;
	private byte[] salt;
	private final int iterationCount = 1024;
	private final int keyLength = 256;
	private final String PBKDFAlgo = "PBKDF2WithHmacSHA1";
	private final String MACAlgo = "HmacSHA256";
	private SecretKey key;
	private byte[] encryptedMaster;// will be saved locally
	private HashMap<String, byte[]> map;
	private byte[] secretMasterKeyData;
	private SecretKey secretMasterKey;
	private GCMParameterSpec s;

	public PasswordManager() {
		try {
			// first check if A master is stored before.
			encryptedMaster = getSavedEncryptedMaster();
			// ini
			mac = Mac.getInstance(MACAlgo);
			key = new SecretKeySpec("123456789".getBytes()/* for now */, MACAlgo);
			mac.init(key);
			map = new HashMap<String, byte[]>();
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
			secretMasterKeyData = newMasterPass.getBytes();
			System.out.println(newMasterPass);
			secretMasterKey = new SecretKeySpec(secretMasterKeyData, "AES");
			s = new GCMParameterSpec(128, secretMasterKeyData);
			return true;
		} else {
			// check if Entered Master is the same as used before.
			boolean match = authenticate(newMasterPass);
			if (match) {
				secretMasterKeyData = newMasterPass.getBytes();
				System.out.println(newMasterPass);
				secretMasterKey = new SecretKeySpec(secretMasterKeyData, "AES");
				s = new GCMParameterSpec(128, secretMasterKeyData);
				try {
					loadEncryptedData();
				} catch (Exception e) {
					System.err.println("Error in loading Encrypted Data");
				}
			}
			return match;
		}
	}

	private byte[] encryptWithAesGcm(byte[] plaintext) throws IOException,
			InvalidKeyException, InvalidAlgorithmParameterException,
			NoSuchAlgorithmException, NoSuchProviderException,
			NoSuchPaddingException {
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");

		cipher.init(Cipher.ENCRYPT_MODE, secretMasterKey, s);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		CipherOutputStream cipherOutputStream = new CipherOutputStream(
				byteArrayOutputStream, cipher);

		cipherOutputStream.write(plaintext);
		cipherOutputStream.close();

		return byteArrayOutputStream.toByteArray();
	}

	public void encrypt(String DomainName, String password)
			throws InvalidKeyException, InvalidAlgorithmParameterException,
			NoSuchAlgorithmException, NoSuchProviderException,
			NoSuchPaddingException, IOException {
		// here domain will be saved using its tag
		byte[] tag = mac.doFinal(DomainName.getBytes());

		// encrypt process go here.

		// password will be saved using its encryption by GCM
		byte[] pass = encryptWithAesGcm(password.getBytes());

		map.put(Arrays.toString(tag), pass);
		// save to file
		try {
			saveEncryptedData();
		} catch (Exception e) {
			System.err.println("Error in saving data");
		}
	}

	public String decrypt(byte[] encryptedPass) {
		String result = "";
		try {
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
			// randomIV = createRandomArray(16);
			GCMParameterSpec s = new GCMParameterSpec(128, secretMasterKeyData);
			cipher.init(Cipher.DECRYPT_MODE, secretMasterKey, s);
			byte[] decryptedPlaintext = cipher.doFinal(encryptedPass);
			result = new String(decryptedPlaintext);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public String getPassword(String domainName) {
		// get the tag of the input domain name
		byte[] tag = mac.doFinal(domainName.getBytes());
		byte[] ePass = map.get(Arrays.toString(tag));
		if (ePass != null)
			return decrypt(ePass);

		return "";
	}

	public void removeDomain(String domain) {
		byte[] tag = mac.doFinal(domain.getBytes());
		map.remove(Arrays.toString(tag));
		try {
			saveEncryptedData();
		} catch (Exception e) {

		}
	}

	private void generateRandom() throws NoSuchAlgorithmException {
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		// Generate a 8 byte (64 bit) salt
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

	private void saveEncryptedData() throws Exception {
		FileOutputStream os = new FileOutputStream(
				System.getProperty("user.dir") + "//data.dat");
		ObjectOutputStream out = new ObjectOutputStream(os);
		out.writeObject(new SavedEntry(map));
		out.close();
		os.close();
	}

	private void loadEncryptedData() throws Exception {
		FileInputStream is = new FileInputStream(System.getProperty("user.dir")
				+ "//data.dat");
		ObjectInputStream in = new ObjectInputStream(is);

		map = ((SavedEntry) in.readObject()).getMap();
		in.close();
		is.close();
	}

	public HashMap<String, byte[]> getMap() {
		return map;
	}

}
