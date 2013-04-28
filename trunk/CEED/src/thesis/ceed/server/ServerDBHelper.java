package thesis.ceed.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ServerDBHelper {
	public static final String DB_NAME = "ceed";
	public static final String DB_URL = "jdbc:mysql://localhost:3306/" + DB_NAME;
	private Connection con;
	private Statement stmt;

	public ServerDBHelper() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * return true on success, false otherwise
	 */
	@SuppressWarnings("finally")
	public boolean createDatabase() {
		boolean result = false;
		try {
			con = DriverManager.getConnection(DB_URL, "root", "");
			stmt = con.createStatement();
			stmt.executeUpdate("drop database if exists ceed");
			stmt.executeUpdate("create database ceed");
			stmt.executeUpdate("drop table if exists user");
			stmt.executeUpdate("create table user (imei string)");
			stmt.executeUpdate("drop table if exists attempt");
			stmt.executeUpdate("create table attempt (imei string not null, path string, emotion string, uptime bigint)");
			result = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				return result;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return result;
			}
		}
	}
	
	/*
	 * return true if user existed, false otherwise
	 */
	@SuppressWarnings("finally")
	public boolean checkUser(String imei) {
		int userCount = 0;
		try {
			con = DriverManager.getConnection(DB_URL, "root", "");
			stmt = con.createStatement();
			stmt.executeQuery("select count(*) as userCount from user where imei = \'" + imei + "\'");
			userCount = stmt.getResultSet().getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				return (userCount > 0? true : false);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return (userCount > 0? true : false);
			}
		}
	}
	
	/*
	 * return true on successful adding, false otherwise
	 */
	@SuppressWarnings("finally")
	public boolean addUser(String imei) {
		int userCount = 0;
		try {
			con = DriverManager.getConnection(DB_URL, "root", "");
			stmt = con.createStatement();
			userCount = stmt.executeUpdate("insert into user values(\'" + imei + "\')");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				return (userCount > 0? true : false);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return (userCount > 0? true : false);
			}
		}
	}
	
	/*
	 * return true on successful adding, false otherwise
	 */
	@SuppressWarnings("finally")
	public boolean addAttempt(Attempt attempt) {
		int attemptCount = 0;
		try {
			con = DriverManager.getConnection(DB_URL, "root", "");
			stmt = con.createStatement();
			attemptCount = stmt.executeUpdate("insert into user values(\'" +
												attempt.getImei() + "\', \'" +
												attempt.getPath() + "\', \'" +
												attempt.getEmotion() + "\', " +
												attempt.getUpTime().getTime().getTime() + ")");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				return (attemptCount > 0? true : false);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return (attemptCount > 0? true : false);
			}
		}
	}
	
	/*
	 * tokens contains 4 Strings taken from createFilter() of Server
	 */
	public ResultSet queryDb(String[] tokens) {
		String imei = null, emotion = null;
		Long timeFrom = 0L, timeTo = 0L;
		
		// TODO: imei contains "All" and all IMEIs in user table; emotion contains "All" and 7 emotions
		// TODO: timeFrom and timeTo need to be checked in UI code before calling this method
		// TODO: Form of the third and the fourth string is "EEE MMMM dd HH:mm:ss zzz yyyy"
		if (tokens[0].equals("All") == false)
			imei = (String)tokens[0];
		if (tokens[1].equals("All") == false)
			emotion = (String)tokens[1];
		try {
			timeFrom = new SimpleDateFormat("EEE MMMM dd HH:mm:ss zzz yyyy", Locale.getDefault()).parse(tokens[2]).getTime();
			timeTo = new SimpleDateFormat("EEE MMMM dd HH:mm:ss zzz yyyy", Locale.getDefault()).parse(tokens[3]).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String queryStatement = "select * from attempt where uptime >= " + timeFrom + " and uptime <= " + timeTo;
		if (imei != null)
			queryStatement += " and imei = \'" + imei + "\'";
		if (emotion != null)
			queryStatement += " and emotion = \'" + emotion + "\'";
		queryStatement += " order by uptime";
		
		try {
			con = DriverManager.getConnection(DB_URL, "root", "");
			stmt = con.createStatement();
			stmt.executeQuery(queryStatement);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				return stmt.getResultSet();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
}
