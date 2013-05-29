package thesis.ceed.client;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ClientDbHelper {	
	public static final String ID = "_id";
	public static final String EMOTION = "emotion";
	public static final String LANG = "lang";
	public static final String TIME = "time";	
	private static String TABLE_NAME = "tbl_history";
	
	private static String DATABASE_NAME = "history.db";
	final Context mContext;
	DatabaseHelper dbHelper;
	SQLiteDatabase db;
	private static String CREATE_TABLE_CMD = "create table " + TABLE_NAME +
			 " (" + ID + " integer primary key autoincrement, " + TIME + " text not null, " + LANG + " text not null, " + 
			EMOTION + " text not null);";
	
	
	public ClientDbHelper(Context context) {
		this.mContext = context;
		dbHelper = new DatabaseHelper(mContext);
	}
	
	public static class DatabaseHelper extends SQLiteOpenHelper{

		public DatabaseHelper(Context context) {
			// TODO Auto-generated constructor stub
			super(context, DATABASE_NAME, null, 1);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			try {
				db.execSQL(CREATE_TABLE_CMD);
			} catch (Exception e) {
				// TODO: handle exception
			}		
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
			onCreate(db);		
		}		
	}
	
	public ClientDbHelper open(){
		db = dbHelper.getWritableDatabase();
		
		return this;
	}
	
	public void close(){
		dbHelper.close();
	}
	
	public long insertValue(String time, String lang, String emotion){
		ContentValues tempValue = new ContentValues();
		tempValue.put(TIME, time);
		tempValue.put(LANG, lang);
		tempValue.put(EMOTION, emotion);		
		return db.insert(TABLE_NAME, null, tempValue);
	}
	
	public void deleteValue(){
		
	}
	
	public Cursor getAllValue(){
		return db.query(TABLE_NAME, new String[]{ID, TIME, LANG, EMOTION}, null,null,null,null,null);
	}
	
	public Cursor getSingleValue(long id){
		Cursor mCursor = db.query(true, TABLE_NAME, new String[]{ID, TIME, LANG, EMOTION}, ID + "=" + id , null, null, null, null, null);
		if(mCursor!= null){
			mCursor.moveToFirst();
		}	
		return mCursor;
	}
}


