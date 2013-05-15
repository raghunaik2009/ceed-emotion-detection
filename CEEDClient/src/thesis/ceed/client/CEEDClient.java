package thesis.ceed.client;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
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
	
	static RecordingWav wavRecorder;
	static TelephonyManager telephony;
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ceedclient);
		telephony = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		ClientNet.connect();
		
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
		
		mBtnSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {					
					//ClientNet.send(new File(wavRecorder.getFileNameSaved()));
					ClientNet.send(new File(Environment.getExternalStorageDirectory() + "//CEED//1367916681975.wav"), "GER");
					new Thread() {
						@Override
						public void run() {
							String emotion = ClientNet.receiveResult();
							if (emotion != null) {
								final int emotionCode = Integer.parseInt(emotion);
								runOnUiThread(new Runnable() {						
									@Override
									public void run() {
										displayResult(emotionCode);
									}
								});					
								super.run();
							}
						}
					}.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});	
		
	}
	
	private void displayResult(int emotionCode) {
		switch (emotionCode) {
		case 0:
			mImgViewEmotion.setImageResource(R.drawable.angry);
			mTxtViewEmotion.setText("Anger");
			break;
		case 1:
			mImgViewEmotion.setImageResource(R.drawable.boredom);
			mTxtViewEmotion.setText("Boredom");
			break;
		case 2:
			mImgViewEmotion.setImageResource(R.drawable.disgust);
			mTxtViewEmotion.setText("Disgust");
			break;
		case 3:
			mImgViewEmotion.setImageResource(R.drawable.fear);
			mTxtViewEmotion.setText("Fear");
			break;
		case 4:
			mImgViewEmotion.setImageResource(R.drawable.happy);
			mTxtViewEmotion.setText("Happy");
			break;
		case 5:
			mImgViewEmotion.setImageResource(R.drawable.sad);
			mTxtViewEmotion.setText("Sad");
			break;
		case 6:
			mImgViewEmotion.setImageResource(R.drawable.neutral);
			mTxtViewEmotion.setText("Neutral");
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ceedclient, menu);
		return true;
	}

}
