import java.io.Serializable;

public class Settings implements Serializable {

	private byte[] salt;
	private byte[] encryptedPass;

	public Settings(byte[] salt, byte[] encryptedPass) {
		this.salt = salt;
		this.encryptedPass = encryptedPass;
	}

	public byte[] getEncryptedPass() {
		return encryptedPass;
	}

	public byte[] getSalt() {
		return salt;
	}
}
