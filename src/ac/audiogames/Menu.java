package ac.audiogames;

import java.util.ArrayList;
import java.util.Locale;

import org.pielot.openal.Buffer;
import org.pielot.openal.SoundEnv;
import org.pielot.openal.Source;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.widget.Toast;

public class Menu extends Activity implements OnInitListener {

	private View mContentView;
    private TextToSpeech tts;
    public String info;
    public String navigation;
	private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    ArrayList<String> selection;
    int select_index=0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		 selection = new ArrayList<>();
		 info="Welcome to Audio Games, Please use headphones, tap once for navigation help";
		 navigation="To navigate the menu, slide your finger on the screen to the right or left, To select an option slide upwards. To return to this menu from any game, double tap.";
		 tts = new TextToSpeech(this, this);
	     tts.setPitch(1);   
		 selection.add("Highway");
	     selection.add("Simon sez"); // intentional 
	     selection.add("Audio Test");
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
	                if(select_index>0)
	                    select_index--;
	                else
	                    select_index=selection.size()-1;
	                tts.speak(selection.get(select_index), TextToSpeech.QUEUE_FLUSH, null); 

	            }

	            @Override
	            public void onSwipeRight() {
	                tts.stop();
	                MediaPlayer.create(getApplicationContext(), R.raw.right).start();
	                if(select_index<selection.size()-1)
	                    select_index++;
	                else select_index=0;
	                tts.speak(selection.get(select_index), TextToSpeech.QUEUE_FLUSH, null); 

	            }

	            @Override
	            public void onSwipeTop() {
	                if (select_index==0){
	                  //  m_launcher.play(Launcher.BOUNCE_33);
	                    Intent intent = new Intent(getApplicationContext(), Highway.class);
	                    startActivity(intent);
	                }
	                else if(select_index==1) {
	                //    m_launcher.play(Launcher.BOUNCE_33);
	                    Intent intent = new Intent (getApplicationContext(), SimonSays.class);
	                    startActivity(intent);
	                }
	                else if(select_index==2) {
	                 //   m_launcher.play(Launcher.BOUNCE_33);
	                    Intent intent = new Intent (getApplicationContext(), Testing.class);
	                    startActivity(intent);
	                }
	                else tts.speak("Unavailable.", TextToSpeech.QUEUE_FLUSH, null);
	            }
	            public void onSingleTap() {
	                if (!tts.isSpeaking()) {
	                    tts.speak(navigation, TextToSpeech.QUEUE_FLUSH, null);
	                } else
	                    tts.stop();
	            }
	            @Override
	            public void onSwipeBottom() {
	                

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
}
