package thesis.ceed.client;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
public class CEEDClientHistory extends Activity {
	public ClientDbHelper mDataSource;
	Context mContext;
	private ListView mListview;
	SimpleCursorAdapter dataAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activiry_history);
		mContext = this;
		mListview = (ListView) findViewById(R.id.lviewHistory);
		mDataSource = new ClientDbHelper(this);
		mDataSource.open();		
		Cursor mCursor = mDataSource.getAllValue();
		mCursor.moveToFirst();		
		String[] columns = new String[] {
				ClientDbHelper.ID,
			    ClientDbHelper.TIME,
			    ClientDbHelper.LANG,
			    ClientDbHelper.EMOTION
			  };
			  int[] idList = new int[] { 
				R.id.txtviewid,
			    R.id.txtviewtime,
			    R.id.txtviewlang,
			    R.id.txtviewemotion
			  };
		dataAdapter = new SimpleCursorAdapter(mContext, R.layout.history_item, mCursor, columns, idList, 0);
		mListview.setAdapter(dataAdapter);
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mDataSource.close();		
		super.onPause();		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mDataSource.open();
		super.onResume();	
	}
	
	
}
