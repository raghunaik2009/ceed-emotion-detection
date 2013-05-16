package thesis.ceed.client;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ClientDbHelper extends SQLiteOpenHelper{	

	private static final String ID = "id";
	private static final String EMOTION = "emotion";
	private static final String TIME = "time";	
	private static String TABLE_NAME = "tbl_history";
	
	private static String DATABASE_NAME = "history.db";
	
	private static String CREATE_TABLE_CMD = "create table " + TABLE_NAME +
			 " (" + ID + " integer primary key autoincrement, " + EMOTION + " text not null, " + 
			TIME + " long);";
	
	public ClientDbHelper(Context context) {
		super(context, DATABASE_NAME, null, 1);
		// TODO Auto-generated constructor stub
	}
			 
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_TABLE_CMD);		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
		onCreate(db);		
	}
	
	
	
}
