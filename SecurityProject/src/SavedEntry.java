import java.io.Serializable;
import java.util.HashMap;

public class SavedEntry implements Serializable {

	private HashMap<String, byte[]> map;

	public SavedEntry(HashMap<String, byte[]> map) {
		this.map = map;
	}

	public HashMap<String, byte[]> getMap() {
		return map;
	}
}
