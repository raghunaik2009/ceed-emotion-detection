package thesis.ceed.server;

import java.util.Calendar;

public class Attempt {
	private String imei;
	private String path;
	private String emotion;
	private Calendar upTime;

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

	public Calendar getUpTime() {
		return upTime;
	}

	public Attempt(String imei, String path, String emotion, Calendar time) {
		this.imei = imei;
		this.path = path;
		this.emotion = emotion;
		this.upTime = time;
	}
}
