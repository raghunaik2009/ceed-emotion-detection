package thesis.ceed.server;

import java.util.Date;

public class Attempt {
	private String IMEI;
	private String path;
	private String emotion;
	private Date time;
	public Attempt(String iMEI, String path, String emotion, Date time) {
		super();
		IMEI = iMEI;
		this.path = path;
		this.emotion = emotion;
		this.time = time;
	}
	public String getIMEI() {
		return IMEI;
	}
	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}
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
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	
}
