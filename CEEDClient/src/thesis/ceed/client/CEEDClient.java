package thesis.ceed.client;

import java.io.File;
import java.io.IOException;
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
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
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
	private ImageButton mImgBtnRecord;
	private ImageButton mImgBtnStop;
	private ImageButton mImgBtnPlay;
	private ImageButton mImgBtnPause;
	private ImageButton mImgBtnSDcard;
	private TextView mTxtViewStatus;
	private TextView mTxtViewFileName;
	private Button mBtnSend;
	private RadioButton mRdBtnGerman;
	private RadioButton mRdBtnVietnamese;
	private ImageView mImgViewEmotion;
	private TextView mTxtViewEmotion;	
	private Button mBtnSave;
	private Button mBtnViewHistory;	
	static String emoResult;
	static RecordingWav wavRecorder;
	static TelephonyManager telephony;
	static MediaPlayer mMediaPlayer;
	static int currentPos;
	static Context context;
	
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.activity_ceedclient);
		telephony = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		ClientNet.connect();
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
				try {					
					//
					if(mRdBtnGerman.isChecked() == true){
						ClientNet.send(new File(wavRecorder.getFileNameSaved()), "GER");
					}else
						ClientNet.send(new File(wavRecorder.getFileNameSaved()), "VIE");					
					new Thread() {
						@Override
						public void run() {
							String emotion = ClientNet.receiveResult();
							emoResult = emotion;
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
		
		mBtnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ClientDbHelper mDataSource = new ClientDbHelper(context);
				mDataSource.open();
				String attemptLang = "";
				if(mRdBtnGerman.isChecked()) attemptLang = "GER";
				else attemptLang = "VIE";
				mDataSource.insertValue(wavRecorder.getFileNameSaved(), attemptLang, emoResult);
				mDataSource.close();
				Toast.makeText(context, "History saved succesfully", Toast.LENGTH_LONG).show();				
			}
		});
		
		mBtnViewHistory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mIntentToHistory = new Intent (CEEDClient.this,
		    			CEEDClientHistory.class);
				startActivityForResult(mIntentToHistory, 101);
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
	protected void onStop() {
		// TODO Auto-generated method stub
		mMediaPlayer.release();
		//mMediaPlayer = null;
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
