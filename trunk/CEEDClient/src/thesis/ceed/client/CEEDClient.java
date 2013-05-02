package thesis.ceed.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CEEDClient extends Activity {
	
	private static final String RECORDING_STATUS = "Recording";
	private static final String RECORD_STOPPED_STATUS = "Recording Stopped";
	private static final String PLAYING_STATUS = "Playing Recorded Sound";
	private ImageView mImgViewRecord;
	private ImageView mImgViewStop;
	private ImageView mImgViewPlay;
	private ImageView mImgViewSDCard;
	private TextView mTxtViewStatus;
	private TextView mTxtViewFileName;
	private Button mBtnSend;
	
	private ImageView mImgViewEmotion;
	private TextView mTxtViewEmotion;
	
	private Button mBtnSave;
	private Button mBtnViewHistory;
	
	private RecordingWav wavRecorder;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ceedclient);
		
		mImgViewRecord = (ImageView)findViewById(R.id.imgviewRecord);
		mImgViewStop = (ImageView)findViewById(R.id.imgviewStop);
		mImgViewPlay = (ImageView)findViewById(R.id.imgviewPlay);
		mImgViewSDCard = (ImageView)findViewById(R.id.imgviewSDcard);	
		mTxtViewStatus = (TextView)findViewById(R.id.txtviewStatus);
		mTxtViewFileName = (TextView)findViewById(R.id.txtviewFileName);
		mBtnSend = (Button)findViewById(R.id.btnSend);
		
		mImgViewEmotion = (ImageView)findViewById(R.id.imgviewEmotion);
		mTxtViewEmotion = (TextView)findViewById(R.id.txtviewEmotion);
		
		mBtnSave = (Button)findViewById(R.id.btnSave);
		mBtnViewHistory = (Button)findViewById(R.id.btnViewHistory);
		
		mImgViewRecord.setEnabled(true);
		mImgViewStop.setEnabled(false);
		mImgViewPlay.setEnabled(false);
		
		mImgViewRecord.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				wavRecorder = new RecordingWav();
				wavRecorder.isRecording = true;
				mImgViewStop.setEnabled(true);
				mImgViewRecord.setEnabled(false);				
				mTxtViewStatus.setText(RECORDING_STATUS);
				wavRecorder.startRecording();
				mTxtViewFileName.setText(wavRecorder.getFileNameSaved());
			}
		});
		
		mImgViewStop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				wavRecorder.isRecording = false;				
				mImgViewStop.setEnabled(false);
				mImgViewRecord.setEnabled(true);
				mTxtViewStatus.setText(RECORD_STOPPED_STATUS);
				mTxtViewFileName.setText(wavRecorder.getFileNameSaved());
				wavRecorder.stopRecording();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ceedclient, menu);
		return true;
	}

}
