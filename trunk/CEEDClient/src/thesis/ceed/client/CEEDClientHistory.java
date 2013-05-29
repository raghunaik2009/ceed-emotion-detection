package thesis.ceed.client;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
public class CEEDClientHistory extends ListActivity {
	public ClientDbHelper mDataSource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activiry_history);
		
		mDataSource = new ClientDbHelper(this);
		mDataSource.open();
		
		List<ClientAttempt> allAttempt = new ArrayList<ClientAttempt>();
		Cursor mCursor = mDataSource.getAllValue();
		mCursor.moveToFirst();
		while(!mCursor.isAfterLast()){
			ClientAttempt tempAttempt = new ClientAttempt(mCursor.getString(1), mCursor.getString(2), mCursor.getString(3));
			allAttempt.add(tempAttempt);
			mCursor.moveToNext();
		}
		mCursor.close();
		
		ArrayAdapter<ClientAttempt> adapter = new ArrayAdapter<ClientAttempt>(this, R.layout.history_item, allAttempt);
		setListAdapter(adapter);		
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
