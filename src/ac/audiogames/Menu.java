package ac.audiogames;

import java.util.ArrayList;
import java.util.Locale;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Menu extends Activity implements OnInitListener {
	 // TODO GIGEL mode
	// TODO info on options
	// TODO haptic on select
	private View mContentView;
    private TextToSpeech tts;
    public String info;
    public String navigation;
	private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    ArrayList<String> selection;
    int select_index=0;
	private SpeechRecognizer mSpeechRecognizer;
	private Intent mSpeechRecognizerIntent;
	private boolean interrupted = false;
	SpeechRecognitionListener listener;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
		mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		listener = new SpeechRecognitionListener();
		mSpeechRecognizer.setRecognitionListener(listener);
		 selection = new ArrayList<>();
		 info="Welcome to Audio Games, Please use headphones, tap once for navigation help";
		 navigation="Either tap and hold then say a voice command when you hear the beep. or slide your finger on the screen to the right or left, to navigate the menu, To select an option slide upwards. To return to this menu from any game, double tap. Tap once to repeat this message";
		 tts = new TextToSpeech(this, this);
	     tts.setPitch(1);   
		 selection.add("Highway");
	     selection.add("Simon sez"); // for pronunciation
	     selection.add("Audio Test");
	     selection.add("Alice in wonderland");
	     selection.add("Joystick Test");
	     selection.add("Road Crossing");
//	 		final SpeechRecognitionListener listener = new SpeechRecognitionListener();
//	 		listener.Initialize(this);
	 		
	 		
	 		
	     setContentView(R.layout.activity_first);
	     SensorManager sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);

	        mContentView = findViewById(R.id.fullscreen_content);

	       mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	        mAccelerometer = mSensorManager
	                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	        mShakeDetector = new ShakeDetector();
	        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

	            @Override
	            public void onShake(int count) {
					/*
					 * The following method, "handleShakeEvent(count):" is a stub //
					 * method you would use to setup whatever you want done once the
					 * device has been shook.
					 */
	                handleShakeEvent(count);

	            }
	        });
	        mContentView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
	            @Override
	            public void onSwipeLeft() {
	                tts.stop();
	                MediaPlayer.create(getApplicationContext(), R.raw.left).start();
	                if(select_index<selection.size()-1)
	                    select_index++;
	                else select_index=0;
	                tts.speak(selection.get(select_index), TextToSpeech.QUEUE_FLUSH, null); 
	           interrupted=true;

	            }

	            @Override
	            public void onSwipeRight() {
	                tts.stop();
	                MediaPlayer.create(getApplicationContext(), R.raw.right).start();
	                if(select_index>0)
	                    select_index--;
	                else
	                    select_index=selection.size()-1;
	                tts.speak(selection.get(select_index), TextToSpeech.QUEUE_FLUSH, null); 
	                interrupted=true;
	            }

	            @Override
	            public void onSwipeTop() {
	                if (select_index==0){
	                  //  m_launcher.play(Launcher.BOUNCE_33);
	                    Intent intent = new Intent(getApplicationContext(), Highway.class);
	                    startActivity(intent); onDestroy();
	                }
	                else if(select_index==1) {
	                //    m_launcher.play(Launcher.BOUNCE_33);
	                    Intent intent = new Intent (getApplicationContext(), SimonSays.class);
	                    startActivity(intent); onDestroy();
	                }
	                else if(select_index==2) {
	                 //   m_launcher.play(Launcher.BOUNCE_33);
	                    Intent intent = new Intent (getApplicationContext(), Testing.class);
	                    startActivity(intent); onDestroy();
	                }
	                else if(select_index==3) {
		                 //   m_launcher.play(Launcher.BOUNCE_33);
		                    Intent intent = new Intent (getApplicationContext(), Alice.class);
		                    startActivity(intent); onDestroy();
		                }
	                else if(select_index==4) {
		                 //   m_launcher.play(Launcher.BOUNCE_33);
		                    Intent intent = new Intent (getApplicationContext(), JoystickDemo.class);
		                    startActivity(intent); onDestroy();
		                }
	                else if(select_index==5) {
		                 //   m_launcher.play(Launcher.BOUNCE_33);
		                    Intent intent = new Intent (getApplicationContext(), Crossing.class);
		                    startActivity(intent); onDestroy();
		                }
	                else tts.speak("Unavailable.", TextToSpeech.QUEUE_FLUSH, null); interrupted=true;
	            }
	            public void onSingleTap() {interrupted=true;
	                if (!tts.isSpeaking()) {
	                    tts.speak(navigation, TextToSpeech.QUEUE_FLUSH, null);
	                } else
	                    tts.stop();
	            }
	            @Override
	            public void onSwipeBottom() {
	                

	            }
	        
	    		public void longPress() {
	    			mSpeechRecognizer.destroy();
						tts.stop();
						interrupted=true;
//				
						mSpeechRecognizer.setRecognitionListener(listener);
							mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
					

				}
	        });
	}
	            private void handleShakeEvent(int count) {
//	              if(count<3)
//	                  m_launcher.play(Launcher.ENGINE3_33);
//	              else if(count >= 3 && count < 5)
//	                  m_launcher.play(Launcher.ENGINE3_66);
//	              else
//	                  m_launcher.play(Launcher.ENGINE4_100);
	              Toast.makeText(this, "shake detected w/ speed: " + count, Toast.LENGTH_SHORT).show();
	          }

	

	@Override
	public void onResume() {
		super.onResume();
		mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	public void onPause() {
		super.onPause();
	    mSensorManager.unregisterListener(mShakeDetector);
        tts.stop();

	}
	  @Override
	    protected void onPostCreate(Bundle savedInstanceState) {
	        super.onPostCreate(savedInstanceState);
	        delayedSpeak(100);
	        }

	@Override
	   public void onDestroy() {
	        // Don't forget to shutdown!
//		if (mSpeechRecognizer != null) 
//			mSpeechRecognizer.destroy();
		 super.onDestroy();
	            tts.stop();
	            tts.shutdown();
//		 if(env!=null){
//	    		this.env.stopAllSources();
//	    		this.env.release();}
	 
	       
	    }
    public void onInit(int status) {
        tts.setLanguage(Locale.UK);
    }
    private final Handler mHandler = new Handler();
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            tts.speak(info, TextToSpeech.QUEUE_FLUSH, null);
        }
    };
    private void delayedSpeak(int delayMillis) {
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, delayMillis);
    }
	
	private class SpeechRecognitionListener implements RecognitionListener {

		@Override
		public void onBeginningOfSpeech() {
			// Log.d(TAG, "onBeginingOfSpeech");
		}

		@Override
		public void onBufferReceived(byte[] buffer) {

		}

		@Override
		public void onEndOfSpeech() {
			// mIslistening = false;
			// Log.d(TAG, "onEndOfSpeech");
		}

		@Override
		public void onError(int error) {
			mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
			tts.speak("Please repeat.", TextToSpeech.QUEUE_FLUSH, null);
			// Log.d(TAG, "error = " + error);
		}

		@Override
		public void onEvent(int eventType, Bundle params) {

		}

		@Override
		public void onPartialResults(Bundle partialResults) {

		}

		@Override
		public void onReadyForSpeech(Bundle params) {
			Log.d("rdy", "onReadyForSpeech"); 
		}

		@Override
		public void onResults(Bundle results) {
			ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			mSpeechRecognizer.destroy();
			Log.d("textospeech", matches.get(0));
			if (matches.get(0).toLowerCase().contains("highway")) {
			     Intent intent = new Intent(getApplicationContext(), Highway.class);
                 startActivity(intent); onDestroy();
			} else if (matches.get(0).toLowerCase().contains("simon") || matches.get(0).toLowerCase().contains("says")) {
			     Intent intent = new Intent(getApplicationContext(), SimonSays.class);
                 startActivity(intent); onDestroy();
			} else if (matches.get(0).toLowerCase().contains("alice") || matches.get(0).toLowerCase().contains("wonderland")) {
			     Intent intent = new Intent(getApplicationContext(), Alice.class);
                 startActivity(intent); onDestroy();
			}
			 else if (matches.get(0).toLowerCase().contains("joystick")) {
			     Intent intent = new Intent(getApplicationContext(), JoystickDemo.class);
                 startActivity(intent); onDestroy();
			}
			 else if (matches.get(0).toLowerCase().contains("test") || matches.get(0).toLowerCase().contains("testing") || matches.get(0).toLowerCase().contains("environment")) {
			     Intent intent = new Intent(getApplicationContext(), Testing.class);
                 startActivity(intent); onDestroy();
			}
			 else if (matches.get(0).toLowerCase().contains("cross") || matches.get(0).toLowerCase().contains("crossing") || matches.get(0).toLowerCase().contains("road")|| matches.get(0).toLowerCase().contains("roads")) {
			     Intent intent = new Intent(getApplicationContext(), Testing.class);
                 startActivity(intent); onDestroy();
			}
			 else if (matches.get(0).toLowerCase().contains("answer to life") || matches.get(0).toLowerCase().contains("meaning of life")) {
				 tts.speak("Forty two", TextToSpeech.QUEUE_FLUSH, null); 
			}
			 else if (matches.get(0).toLowerCase().contains("games") || matches.get(0).toLowerCase().contains("list")){
			interrupted=false;new Thread() {
		        @Override
		        public void run() {play();}}.start();;
			 }
			else
				tts.speak("Please say a game name or list games. Long press to try again.", TextToSpeech.QUEUE_FLUSH, null);
			
		}

		@Override
		public void onRmsChanged(float rmsdB) {
		}
	}

    public void play(){ 
   	 tts.speak("Available options:", TextToSpeech.QUEUE_FLUSH, null);
	 
	 for(int i=0;i<selection.size();i++){
		 while(tts.isSpeaking())
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		 if(!interrupted)
		 tts.speak(selection.get(i), TextToSpeech.QUEUE_FLUSH, null);
		 else i=selection.size();
	 }
	 } 
}
