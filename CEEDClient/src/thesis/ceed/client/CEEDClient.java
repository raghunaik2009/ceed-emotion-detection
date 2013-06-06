package thesis.ceed.client;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class CEEDClient extends Activity {
	
	private static final String RECORDING_STATUS = "Recording";
	private static final String RECORD_STOPPED_STATUS = "Recording Stopped";
	private static final String PLAYING_STATUS = "Playing Recorded Sound";
	private static final String PAUSE_STATUS = "Paused Playing Recored Sound";
	private static final String STOPPED_STATUS = "Stopped Playing Recorded Sound";
	private static final String PROCESSING_STATUS = "Server's processing recorded sound";
	private static final String RECEIVED_STATUS = "Emotion received from server";
	private ImageButton mImgBtnRecord;
	private ImageButton mImgBtnStop;
	private ImageButton mImgBtnPlay;
	private ImageButton mImgBtnPause;
	private ImageButton mImgBtnSDcard;
	private TextView mTxtViewStatus;
	private TextView mTxtViewFileName;
	private static Button mBtnSend;
	private RadioButton mRdBtnGerman;
	private RadioButton mRdBtnVietnamese;
	private ImageView mImgViewEmotion;
	private TextView mTxtViewEmotion;	
	private Button mBtnSave;
	private Button mBtnViewHistory;	
	static String emoResult;
	static RecordingWav wavRecorder;
	static TelephonyManager telephony;
	static MediaPlayer mMediaPlayer = new MediaPlayer();
	static int currentPos;
	static Context context;
	private SendToServerAsync mSendToServerAsync;
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.activity_ceedclient);
		telephony = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);		
		mImgBtnRecord = (ImageButton)findViewById(R.id.imgbtnRecord);
		mImgBtnStop = (ImageButton)findViewById(R.id.imgbtnStop);
		mImgBtnPlay = (ImageButton)findViewById(R.id.imgbtnPlay);
		mImgBtnPause = (ImageButton)findViewById(R.id.imgbtnPause);
		mImgBtnSDcard = (ImageButton)findViewById(R.id.imgbtnSDcard);	
		mTxtViewStatus = (TextView)findViewById(R.id.txtviewStatus);
		mTxtViewFileName = (TextView)findViewById(R.id.txtviewFileName);
		mBtnSend = (Button)findViewById(R.id.btnSend);
		mRdBtnGerman = (RadioButton)findViewById(R.id.rdbtnGerman);
		mRdBtnVietnamese = (RadioButton)findViewById(R.id.rdbtnVietnamese);
		mImgViewEmotion = (ImageView)findViewById(R.id.imgviewEmotion);
		mTxtViewEmotion = (TextView)findViewById(R.id.txtviewEmotion);		
		mBtnSave = (Button)findViewById(R.id.btnSave);
		mBtnViewHistory = (Button)findViewById(R.id.btnViewHistory);		
		mImgBtnRecord.setEnabled(true);
		mImgBtnStop.setEnabled(false);
		mImgBtnPlay.setEnabled(false);
		mImgBtnPause.setEnabled(false);
		mRdBtnGerman.setChecked(true);		
		//mBtnSend.setEnabled(false);
		
		mRdBtnGerman.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mRdBtnGerman.setChecked(true);
				mRdBtnVietnamese.setChecked(false);				
			}
		});
		
		mRdBtnVietnamese.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mRdBtnGerman.setChecked(false);
				mRdBtnVietnamese.setChecked(true);
			}
		});
		
		mImgBtnRecord.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mImgViewEmotion.setImageResource(R.drawable.question);
				wavRecorder = new RecordingWav();
				wavRecorder.isRecording = true;
				mImgBtnStop.setEnabled(true);
				mImgBtnRecord.setEnabled(false);				
				mTxtViewStatus.setText(RECORDING_STATUS);
				wavRecorder.startRecording();
				mTxtViewFileName.setText(wavRecorder.getFileNameSaved());
				mMediaPlayer = new MediaPlayer();				
			}
		});
		
		mImgBtnStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				wavRecorder.isRecording = false;				
				mImgBtnStop.setEnabled(false);
				mImgBtnRecord.setEnabled(true);
				mImgBtnPlay.setEnabled(true);
				mTxtViewStatus.setText(RECORD_STOPPED_STATUS);
				mTxtViewFileName.setText(wavRecorder.getFileNameSaved());
				wavRecorder.stopRecording();
				try {
					mMediaPlayer.setDataSource(wavRecorder.getFileNameSaved());
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});		
		
		mImgBtnPlay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {					
					if(mMediaPlayer.getCurrentPosition() == 0){
						mMediaPlayer.prepare();
					}
					else{
						mMediaPlayer.seekTo(currentPos);
					}
					mMediaPlayer.start();
					mTxtViewStatus.setText(PLAYING_STATUS);
					mImgBtnPause.setEnabled(true);
					mImgBtnPlay.setEnabled(false);
					mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
						
						@Override
						public void onCompletion(MediaPlayer mp) {
							// TODO Auto-generated method stub
							currentPos = 0;					
							mTxtViewStatus.setText(STOPPED_STATUS);
							mImgBtnPlay.setEnabled(true);
							mImgBtnPause.setEnabled(false);
						}
					});
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		mImgBtnPause.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mMediaPlayer.pause();
				mTxtViewStatus.setText(PAUSE_STATUS);
				currentPos = mMediaPlayer.getCurrentPosition();
				mImgBtnPlay.setEnabled(true);
				mImgBtnPause.setEnabled(false);
			}
		});
		
		mBtnSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			mTxtViewStatus.setText(PROCESSING_STATUS);
			mSendToServerAsync = new SendToServerAsync();
			if(mRdBtnGerman.isChecked() == true){
				mSendToServerAsync.execute(wavRecorder.getFileNameSaved(), "GER");
			}
			else
			mSendToServerAsync.execute(wavRecorder.getFileNameSaved(), "VIE");	
			}				
		});	
		
		mBtnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(wavRecorder != null && emoResult != null){
					File tempFile = new File(wavRecorder.getFileNameSaved());
					String fileName = tempFile.getName();
					String time = fileName.substring(0, fileName.lastIndexOf(".wav"));
					
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(Long.parseLong(time));
					Date tempDate = cal.getTime();
					String tempTimeFormatted = tempDate.toLocaleString();
					
					ClientDbHelper mDataSource = new ClientDbHelper(context);
					mDataSource.open();
					String attemptLang = "";
					if(mRdBtnGerman.isChecked()) attemptLang = "GER";
					else attemptLang = "VIE";
					mDataSource.insertValue(tempTimeFormatted, attemptLang, emoResult);				
					mDataSource.close();				
					Toast.makeText(context, "History saved succesfully", Toast.LENGTH_LONG).show();
					emoResult = null;
				}
				else
					Toast.makeText(context, "Please use the program first", Toast.LENGTH_SHORT).show();						
			}
		});
				
		mBtnViewHistory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mIntentToHistory = new Intent (CEEDClient.this,
		    			CEEDClientHistory.class);
				startActivity(mIntentToHistory);
			}
		});
	}//end of onCreate
	
	
	public class SendToServerAsync extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				ClientNet.send(new File(params[0]), params[1]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getApplication(), "Error sending recorded file, please try again", Toast.LENGTH_SHORT).show();
			}
			return ClientNet.receiveResult();
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mTxtViewStatus.setText(RECEIVED_STATUS);
			emoResult = result;
			if (result != null) {
				final int emotionCode = Integer.parseInt(result);
				displayResult(emotionCode);
			}
		}				
	}//end SendToServerAsync
	
	private void displayResult(int emotionCode) {
		switch (emotionCode) {
		case 0:
			emoResult = "Anger";
			mImgViewEmotion.setImageResource(R.drawable.angry);
			mTxtViewEmotion.setText("Anger");
			break;
		case 1:
			emoResult = "Boredom";
			mImgViewEmotion.setImageResource(R.drawable.boredom);
			mTxtViewEmotion.setText("Boredom");
			break;
		case 2:
			emoResult = "Disgust";
			mImgViewEmotion.setImageResource(R.drawable.disgust);
			mTxtViewEmotion.setText("Disgust");
			break;
		case 3:
			emoResult = "Fear";
			mImgViewEmotion.setImageResource(R.drawable.fear);
			mTxtViewEmotion.setText("Fear");
			break;
		case 4:
			emoResult = "Happy";
			mImgViewEmotion.setImageResource(R.drawable.happy);
			mTxtViewEmotion.setText("Happy");
			break;
		case 5:
			emoResult = "Sad";
			mImgViewEmotion.setImageResource(R.drawable.sad);
			mTxtViewEmotion.setText("Sad");
			break;
		case 6:
			emoResult = "Neutral";
			mImgViewEmotion.setImageResource(R.drawable.neutral);   
			mTxtViewEmotion.setText("Neutral");
			break;
		default:
			break;
		}
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			mMediaPlayer.release();
			ClientNet.disconnect();
			Toast.makeText(context, "CEEDClient disconnected from server", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(context, "CEEDClient disconnected from server", Toast.LENGTH_SHORT).show();			
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub		
		/*try {
			mMediaPlayer.release();
			ClientNet.disconnect();
			Toast.makeText(context, "CEEDClient disconnected from server", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(context, "CEEDClient disconnected from server", Toast.LENGTH_SHORT).show();			
		}*/
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ceedclient, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()){
		case R.id.action_settings:
			{
				DialogFragment newDialog = new SetServerIPDialog();
				newDialog.show(getFragmentManager(), "Server IP Setting");
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	public static class SetServerIPDialog extends DialogFragment{
				
		final View view = View.inflate(context, R.layout.serverip_setting, null);
		final EditText mEdtTextServerIP = (EditText)view.findViewById(R.id.edttextServerIP);
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setView(view);
			mEdtTextServerIP.setText(ClientNet.SERVER_IP);
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					String validateString = mEdtTextServerIP.getText().toString();
					Matcher ipMatcher = Patterns.IP_ADDRESS.matcher(validateString);
					if(!ipMatcher.matches()){				
						Toast.makeText(getActivity(), "Wrong IP Address pattern, Server IP Address was not updated.", Toast.LENGTH_SHORT).show();				
					}else
					{						
						ClientNet.SERVER_IP = mEdtTextServerIP.getText().toString();
						Toast.makeText(getActivity(), "Server IP Address succesfully updated.", Toast.LENGTH_SHORT).show();						
						ClientNet.connect();
						/*if(ClientNet.connect())
							mBtnSend.setEnabled(true);
						else{
							Toast.makeText(getActivity(), "Please reconfigure server IP address", Toast.LENGTH_SHORT).show();
							mBtnSend.setEnabled(false);
						}*/						
					}
					 
				}
			});
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					SetServerIPDialog.this.getDialog().cancel();
				}
			});
			return builder.create();
		}

	}
	

}
