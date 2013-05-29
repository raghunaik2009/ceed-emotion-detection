package thesis.ceed.client;


public class ClientAttempt{
	private String time;
	private String lang;
	private String emotion;
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public String getEmotion() {
		return emotion;
	}
	public void setEmotion(String emotion) {
		this.emotion = emotion;
	}
	
	public ClientAttempt (String _time, String _lang, String _emotion){
		this.time = _time;
		this.lang = _lang;
		this.emotion = _emotion;
	}
}
/*public class Attempt {
	private String imei;
	private String path;
	private String emotion;
	private String upTime;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getEmotion() {
		return emotion;
	}

	public void setEmotion(String emotion) {
		this.emotion = emotion;
	}

	public String getImei() {
		return imei;
	}

	public String getUpTime() {
		return upTime;
	}

	public Attempt(String imei, String path, String emotion, String time) {
		this.imei = imei;
		this.path = path;
		this.emotion = emotion;
		this.upTime = time;
	}
}
*/