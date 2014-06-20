import java.io.Serializable;

public class Settings implements Serializable {

	private byte[] salt;
	private byte[] encryptedPass;
	private byte[] intialVector;

	public Settings(byte[] salt, byte[] encryptedPass, byte[] intialVector) {
		this.salt = salt;
		this.encryptedPass = encryptedPass;
		this.intialVector = intialVector;
	}

	public byte[] getIntialVector() {
		return intialVector;
	}

	public byte[] getEncryptedPass() {
		return encryptedPass;
	}

	public byte[] getSalt() {
		return salt;
	}

}
