package thesis.ceed.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ServerDbHelper {
	public static final String DB_NAME = "ceed";
	public static final String BASE_DB_NAME = "mysql";
	public static final String DB_URL = "jdbc:mysql://localhost:3306/" + DB_NAME;
	public static final String BASE_DB_URL = "jdbc:mysql://localhost:3306/" + BASE_DB_NAME;
	private Connection con;
	private Statement stmt;

	public ServerDbHelper() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void createDatabase() {
		try {
			con = DriverManager.getConnection(BASE_DB_URL, "root", "");
			stmt = con.createStatement();
			stmt.executeUpdate("drop database if exists " + DB_NAME);
			stmt.executeUpdate("create database " + DB_NAME);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void createTables() {
		try {
			con = DriverManager.getConnection(DB_URL, "root", "");
			stmt = con.createStatement();
			stmt.executeUpdate("drop table if exists user");
			stmt.executeUpdate("create table user (imei char(16) not null)");
			stmt.executeUpdate("drop table if exists attempt");
			stmt.executeUpdate("create table attempt (imei char(16) not null, path char(255), emotion char(10), uptime bigint)");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * return 1 if user existed, 0 if not, -1 if error occurs
	 */
	public int checkUser(String imei) {
		int userCount = 0;
		ResultSet rs = null;
		
		try {
			con = DriverManager.getConnection(DB_URL, "root", "");
			stmt = con.createStatement();
			rs = stmt.executeQuery("select count(*) as userCount from user where imei = \'" + imei + "\'");
			rs.next();
			userCount = rs.getInt(1);
		} catch (SQLException e) {
			// TODO: is -1 necessary?
			e.printStackTrace();
			userCount = -1;
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return userCount;
	}
	
	/*
	 * return true on successful adding, false otherwise
	 */
	public boolean addUser(String imei) {
		int userCount = 0;
		
		try {
			con = DriverManager.getConnection(DB_URL, "root", "");
			stmt = con.createStatement();
			userCount = stmt.executeUpdate("insert into user values(\'" + imei + "\')");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return (userCount > 0? true : false);
	}
	
	/*
	 * return an array of IMEI of all users
	 */
	public Object[] getAllUser() {
		Object[] users = null;
		ResultSet rs = null;
		int numUsers = 0, i = 0;
		
		try {
			con = DriverManager.getConnection(DB_URL, "root", "");
			stmt = con.createStatement();
			
			rs = stmt.executeQuery("select count(*) from user");
			rs.next();
			numUsers = rs.getInt(1);
			users = new Object[numUsers];
			
			rs = stmt.executeQuery("select * from user");
			while (rs.next()) {
				users[i] = rs.getString(1);
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return users;
	}
	
	/*
	 * return true on successful adding, false otherwise
	 */
	public boolean addAttempt(Attempt attempt) {
		int attemptCount = 0;
		
		try {
			con = DriverManager.getConnection(DB_URL, "root", "");
			stmt = con.createStatement();
			attemptCount = stmt.executeUpdate("insert into attempt values(\'" +
												attempt.getImei() + "\', \'" +
												attempt.getPath() + "\', \'" +
												attempt.getEmotion() + "\', " +
												Long.parseLong(attempt.getUpTime()) + ")");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return (attemptCount > 0? true : false);
	}
	
	/*
	 * tokens contains 4 Strings taken from createFilter() of Server
	 */
	public ArrayList<Attempt> queryDb(String[] tokens) {
		String imei = null, emotion = null;
		Long timeFrom = 0L, timeTo = 0L;
		ResultSet rs = null;
		ArrayList<Attempt> selectedAttempts = new ArrayList<Attempt>();
		
		// TODO: imei contains all IMEIs and "All" in user table; emotion contains 7 emotions and "All"
		// TODO: timeFrom and timeTo need to be checked in UI code before calling this method
		if (tokens[0].equals("All") == false)
			imei = (String)tokens[0];
		if (tokens[1].equals("All") == false)
			emotion = (String)tokens[1];
		// TODO: Form of the third and the fourth string is "EEE MMMM dd HH:mm:ss zzz yyyy"
		/*try {
			timeFrom = new SimpleDateFormat("EEE MMMM dd HH:mm:ss zzz yyyy", Locale.getDefault()).parse(tokens[2]).getTime();
			timeTo = new SimpleDateFormat("EEE MMMM dd HH:mm:ss zzz yyyy", Locale.getDefault()).parse(tokens[3]).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}*/
		timeFrom = Long.parseLong(tokens[2]);
		timeTo = Long.parseLong(tokens[3]);
		
		String queryStatement = "select * from attempt where uptime >= " + timeFrom + " and uptime <= " + timeTo;
		if (imei != null)
			queryStatement += " and imei = \'" + imei + "\'";
		if (emotion != null)
			queryStatement += " and emotion = \'" + emotion + "\'";
		queryStatement += " order by uptime";
		
		try {
			con = DriverManager.getConnection(DB_URL, "root", "");
			stmt = con.createStatement();
			rs = stmt.executeQuery(queryStatement);
			while (rs.next()) {
				selectedAttempts.add(new Attempt(rs.getString(1),
												rs.getString(2),
												rs.getString(3),
												rs.getString(4)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return selectedAttempts;
	}
}
