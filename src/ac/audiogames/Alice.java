package ac.audiogames;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.pielot.openal.Buffer;
import org.pielot.openal.SoundEnv;
import org.pielot.openal.Source;

import ac.audiogames.Joystick.OnJoystickMoveListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.media.MediaPlayer;

public class Alice extends Activity implements TextToSpeech.OnInitListener {
	// protected static final int RESULT_SPEECH = 0;
	public static final String PREFS_NAME = "AudioGamesPrefs";
	public static final String PREFS_KEY = "alice_status";
	private Source steps_sound_grass1;
	private Source steps_sound_grass2;
	private Source bush_sound1;
	private Source first_ambient;
	private Source river_sound1;
	private Source cogaltz;
	private Source shrinking_effect;
	private Source crying_boy;
	private SoundEnv env;
	private TextToSpeech tts;
	private View mContentView;
	public String info;
	Thread start;
	boolean startEng = false;
	boolean eaten = false;
	boolean repeat = false;
	final Random gen = new Random();
	int randomNum;
	final int[] rand = { 3 };
	int copy_x = 0;
	int copy_y = 0;
	int x = 0;
	int y = 0;
	int z = 0;
	float list_x = 0;
	float list_y = 0;
	float last_loc_x = 0;
	float last_loc_y = 0;
	float tap_x = 0;
	float tap_y = 0;
	float slope = -1;
	// angle between the tap on the screen and current location
	//double angle = 0;
	// the distance a character moves at one tap
	double dist = 1.5;
	// roll-off, pitch, gain
	int r = 0;
	int p = 0;
	int g = 0;
	// game progress timers
	long startTime;
	long endTime;
	long elapsedTime;
	// game progress tracker
	List<Integer> progress = new ArrayList<Integer>();
	// position as player
	int status = 0;
	//
	boolean continue_game = false;
	String int_val_x;
	String int_val_y;
	DisplayMetrics metrics = new DisplayMetrics();
	int height;
	int width;
	/*
	 * speech recognizer vars
	 */
	private SpeechRecognizer mSpeechRecognizer;
	private Intent mSpeechRecognizerIntent;
	private boolean mIslistening = true;
	SpeechRecognitionListener listener;
	/*
	 * vars used for saving the user state
	 */
	SharedPreferences settings;
	SharedPreferences.Editor editor;

	/*
	 * 
	 */
	private Joystick joystick;
	private ViewGroup container_test;
	
	
	double angle;
	int power;
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.env = SoundEnv.getInstance(this);
		mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
		mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		//// intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "speak now...");
		// startActivityForResult(intent, RESULT_SPEECH);
		// voiceIntent =
		//// RecognizerIntent.getVoiceDetailsIntent(getApplicationContext());
		listener = new SpeechRecognitionListener();
		mSpeechRecognizer.setRecognitionListener(listener);
		// mSpeechRecognizer .setRecognitionListener(recog_listener);

		// recog_listener.
		start = new Thread() {
			@Override
			public void run() {
				play();
			}
		};
		try {
			// initialising all the sounds in the game
			Buffer buffer_first_ambient = env.addBuffer("opening_theme");
			Buffer buffer_grass_sound_1 = env.addBuffer("steps_b");
			Buffer buffer_bush_sound_1 = env.addBuffer("bush_effect");
			Buffer buffer_river_sound_1 = env.addBuffer("river_effect");
			Buffer buffer_water_drinking = env.addBuffer("cogaltz");
			Buffer buffer_shrinking_sound = env.addBuffer("shrinking_effect");
			Buffer buffer_crying_boy = env.addBuffer("crying_boy");
			this.first_ambient = env.addSource(buffer_first_ambient);
			this.first_ambient.setPosition(0, 0, 0);
			this.steps_sound_grass1 = env.addSource(buffer_grass_sound_1);
			this.steps_sound_grass1.setPosition(0, 0, 0);
			this.steps_sound_grass1.setPitch(1);
			this.steps_sound_grass1.setGain(2);
			this.bush_sound1 = env.addSource(buffer_bush_sound_1);
			this.bush_sound1.setPosition(0, 0, 0);
			this.river_sound1 = env.addSource(buffer_river_sound_1);
			this.cogaltz = env.addSource(buffer_water_drinking);
			this.shrinking_effect = env.addSource(buffer_shrinking_sound);
			this.crying_boy = env.addSource(buffer_crying_boy);
			height = getApplicationContext().getResources().getDisplayMetrics().heightPixels;
			width = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
		} catch (IOException e) {
			// System.out.println("error loading: ambient sound?.");
			e.printStackTrace();
		}
		// this.env.setListenerOrientation(20);
		this.env.setListenerPos(0, 0, -1);

		info = "Welcome to Wonderland. Please follow the story steps. You can move the character using swipes. Tap twice to go to the menu. Tap and hold to start the game. In order to do that, you will have to say whether you want to start a new game or continue a previous one. Say something like 'start a new game' or 'continue the game'. Tap once to repeat this message.";
		// m_launcher = new Launcher(this);
		tts = new TextToSpeech(this, this);
		tts.setPitch(1);
		tts.speak(info, TextToSpeech.QUEUE_FLUSH, null);
		setContentView(R.layout.activity_alice);
		mContentView = (ViewGroup) findViewById(R.id.fullscreen_content_alice);
//		container_test = (ViewGroup) findViewById(R.id.container_test);
//	    int sheight = getApplicationContext().getResources().getDisplayMetrics().heightPixels;
//		int swidth = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
		joystick=new Joystick(getApplicationContext());
		joystick.setOnJoystickMoveListener(new OnJoystickMoveListener() {

            @Override
            public void onValueChanged(int tap_angle, int tap_power, int direction) {

            	 Log.e("angle", String.valueOf(tap_angle) + "°");
                if(tap_angle != 90)
                {
                	angle = Math.toRadians(tap_angle);
	                power = tap_power;
	                list_y += (float) (Math.sin(angle) * dist);
	                list_x += (float) (Math.cos(angle) * dist);
	                Log.e("x", String.valueOf(list_x));
	                Log.e("y", String.valueOf(list_y));
	                movePlayer(list_x, list_y, 0);
					play();
                }

            }
        }, Joystick.DEFAULT_LOOP_INTERVAL); 
		mContentView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {


			public void longPress() {
				if (!start.isAlive()) {
					tts.stop();
					// startEng = true;

					if (mIslistening) {
						mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
					} else {
						mSpeechRecognizer.destroy();
						startEng = true;
						// repeat = false;
						slope = 0;
						start = new Thread() {
							@Override
							public void run() {
								startTime = SystemClock.elapsedRealtime();
								play();
							}
						};
						//
						start.start();

					}

				}
			}

			public void onSingleTap() {
				if (slope == -1) {
					if (!tts.isSpeaking()) {
						tts.speak(info, TextToSpeech.QUEUE_FLUSH, null);
					} else
						tts.stop();

				}
			}

			/*
			 * Function that finds the distance a character moves using the joystick
			 * 
			 * @see ac.audiogames.OnSwipeTouchListener#moveCharacter(float,
			 * float)
			 */
			@Override
			public void moveCharacter(float char_x, float char_y) {
				android.widget.RelativeLayout.LayoutParams params;
				if (slope != -1) {
					Point tap = new Point();
					tap.x = (int) char_x;
					tap.y = (int) char_y;
					// screen pixel conversion to carthesian
//					char_x = char_x - width / 2;
//					char_y = height / 2 - char_y;

					tap_x = char_x;
					tap_y = char_y;
//					 Log.d("tap x && y", "x=" + char_x + " y=" + char_y);

					// Log.d("Height/width: ", height + " " + width);
					// Log.d("middle screen", height / 2 + " " + width / 2);
					
					
//					slope = (char_y) / (char_x);
					// Log.d("slope", slope + "");
					last_loc_x = list_x;
					last_loc_y = list_y;
					
					
					params = new RelativeLayout.LayoutParams(360,360);
					params.leftMargin = tap.x - 180;
					params.topMargin = tap.y - 180;
						
					boolean joystick_check = mContentView.findViewById(R.layout.joystickhidden) != null;
					View convertView;
					if(!joystick_check)
						((ViewGroup) mContentView).removeAllViews();
//					
					convertView = getLayoutInflater().inflate(R.layout.joystickhidden, (ViewGroup) mContentView , false);
					((ViewGroup) mContentView).addView(convertView, params);
					joystick = (Joystick) mContentView.findViewById(R.id.joystick);
					joystick.setOnJoystickMoveListener(new OnJoystickMoveListener() {

			            @Override
			            public void onValueChanged(int tap_angle, int tap_power, int direction) {

			            	 Log.e("angle", String.valueOf(tap_angle) + "°");
			                if(tap_angle != 90)
			                {
			                	angle = Math.toRadians(tap_angle);
				                power = tap_power;
				                list_y += (float) (Math.sin(angle) * dist);
				                list_x += (float) (Math.cos(angle) * dist);
				                Log.e("x", String.valueOf(list_x));
				                Log.e("y", String.valueOf(list_y));
				                movePlayer(list_x, list_y, 0);
								play();
			                }

			            }
			        }, Joystick.DEFAULT_LOOP_INTERVAL);
					joystick.xPosition = params.leftMargin;
					joystick.yPosition = params.topMargin;
					joystick.centerX = params.leftMargin;
					joystick.centerY = params.topMargin;
					
					
					
				}
			}

//			@Override
//			public void onDoubleTap2() { // pause and go to menu
//				Intent intent = new Intent(getApplicationContext(), Menu.class);
//				startActivity(intent);
//				onPause();
//			}

		});
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		delayedSpeak(100);///////
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		// start.interrupt();
		super.onPause();
		tts.stop();
		this.env.stopAllSources();
		this.env.release();
	}

	@Override
	public void onInit(int status) {
		tts.setLanguage(Locale.UK);
	}

	@Override
	public void onDestroy() {
		tts.stop();
		tts.shutdown();
		// Be nice with the system and release all resources
		this.env.stopAllSources();
		this.env.release();
		if (mSpeechRecognizer != null) {
			mSpeechRecognizer.destroy();
		}
		super.onDestroy();

	}

	@Override
	public void onLowMemory() {
		this.env.onLowMemory();
	}

	private final Handler mHandler = new Handler();

	private void delayedSpeak(int delayMillis) {
		mHandler.removeCallbacks(mRunnable);
		mHandler.postDelayed(mRunnable, delayMillis);
	}

	private final Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			tts.speak(info, TextToSpeech.QUEUE_FLUSH, null);
		}
	};

	/*
	 * Function that moves the player by modifying the listener's position
	 * regarding the new location
	 */
	public void movePlayer(float list_x, float list_y, float list_z) {

		env.setListenerPos(list_x, list_y, list_z);
		if (status == 0)
			first_ambient.setPosition(list_x, list_y, 0);
		// env.setListenerOrientation(tap_x, tap_y, 0);

	}

	public void save(Context context, int status) {
		SharedPreferences settings;
		Editor editor;
		settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); // 1
		editor = settings.edit(); // 2

		editor.putInt(PREFS_KEY, status); // 3
		editor.commit(); // 4
	}

	public int getValue(Context context) {
		SharedPreferences settings;
		int value;
		settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); // 1
		value = settings.getInt(PREFS_KEY, 0); // 2
		return value;
	}

	public void play() {
		Log.e("starteng", startEng + "");
		if (continue_game == true) {
			status = getValue(getApplicationContext());
			progress.add(status);
//			startEng = false;
		} else {
			status = 0;
		}
//		if (startEng) {
			mSpeechRecognizer.destroy();
			// try {
			//// startEng = false;
			// eaten = false;
			//// repeat = false;
			//// Thread.sleep(2000);
			// // game start
			//// first_ambient.play(false);
			//// do {
			//// x = gen.nextInt(10) - 5;
			//// y = gen.nextInt(19) - 5;
			//// } while (Math.abs(x) < 4 || Math.abs(y) < 4);
			////
			//// river_sound1.setPosition(x, y, 0);
			//// // river_sound1.setRolloffFactor(3);
			//// river_sound1.play(true);
			// // Thread.sleep(28700);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
//		}
		if (eaten) { // if eaten
			first_ambient.stop();
			steps_sound_grass1.stop();
			river_sound1.stop();
			MediaPlayer.create(getApplicationContext(), R.raw.crash).start(); // boom

		} else {

			if ((Math.abs(Math.abs(list_x) - Math.abs(x)) < 1 && Math.abs(Math.abs(list_y) - Math.abs(y)) < 1) || startEng == true) {
				
				// MediaPlayer.create(getApplicationContext(),
				// R.raw.crash).start();
				if (status != 0) {
					status = (int) progress.get(progress.size() - 1);
				}
				if(!startEng)
					status += 1;
			
				progress.add(status);
				/*
				 * saves user status into user settings
				 */
				save(getApplicationContext(), status);

				// previous object's location
				copy_x = x;
				copy_y = y;
				// generate new object location
				do {
					x = gen.nextInt(10) - 5;
					y = gen.nextInt(10) - 5;
				} while (Math.abs(x) < 4 || Math.abs(y) < 4);
				// play stage
				Log.d("status", status + " ");
				switch (status) {
				case 0:
					if (startEng) {
						// mSpeechRecognizer.destroy();
						try {
//							startEng = false;
							// eaten = false;
							// repeat = false;
							Thread.sleep(2000);
							// game start
							first_ambient.play(false);
//							Log.d("message", first_ambient.)
							river_sound1.setPosition(x, y, 0);
							// river_sound1.setRolloffFactor(3);
							river_sound1.play(true);
							startEng=false;
							 //Thread.sleep(28700);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
					}
					break;
				case 1:
					cogaltz.setPosition(list_x, list_y, 0);
					cogaltz.play(false);
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					shrinking_effect.setPosition(copy_x, copy_y, 0);
					shrinking_effect.play(false);
					river_sound1.stop();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					tts.speak("What was that!", TextToSpeech.QUEUE_FLUSH, null);
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					tts.speak("Where did the river go?", TextToSpeech.QUEUE_FLUSH, null);
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					crying_boy.setPosition(x, y, 0);
					crying_boy.play(true);
					break;
				case 2:
					crying_boy.stop();
					// picture sound

					// river_sound1.wait(2000);
					break;
				}
			} else {
				// if the player hasn't reached the destination
				Log.d("VALUES", x + " " + y);
				Log.d("statusmove", status + " ");
//				first_ambient.play(false);
				do {
					if (last_loc_x > list_x)
						last_loc_x = (float) (last_loc_x - 0.1);
					else
						last_loc_x = (float) (last_loc_x + 0.1);
					if (last_loc_y > list_y)
						last_loc_y = (float) (last_loc_y - 0.1);
					else
						last_loc_y = (float) (last_loc_y + 0.1);
					movePlayer(last_loc_x, last_loc_y, 0);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} while (Math.floor(last_loc_x) != Math.floor(list_x) || Math.floor(last_loc_y) != Math.floor(list_y));
				Log.d("player vals", list_x + " " + list_y);
				//movePlayer(list_x, list_y, 0);
				if (status != 0)
					status = (int) progress.get(progress.size() - 1);
				switch (status) {
				case 0:
					first_ambient.setPosition(list_x, list_y, 0);
					steps_sound_grass1.setPosition(list_x, list_y, 0);
					steps_sound_grass1.play(false);
					break;
				case 1:
					steps_sound_grass1.setPosition(list_x, list_y, 0);
					steps_sound_grass1.play(false);
					// bush_sound1.play(false);
					break;
				case 2:
					steps_sound_grass1.setPosition(list_x, list_y, 0);
					steps_sound_grass1.play(false);
					// river_sound1.setPosition(x, y, 0);
					// river_sound1.play(false);
					break;
				}
			}
		}

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
			Log.d("rdy", "onReadyForSpeech"); //$NON-NLS-1$
		}

		@Override
		public void onResults(Bundle results) {
			// Log.d(TAG, "onResults"); //$NON-NLS-1$
			ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			// matches are the return values of speech recognition engine
			// Use these values for whatever you wish to do
			// ArrayList<String> text =
			// results.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			Log.d("textospeech", matches.get(0));
			if (matches.get(0).contains("continue")) {
				continue_game = true;
				mIslistening = false;
				tts.speak("You have chosen to continue the game. Long press to start.", TextToSpeech.QUEUE_FLUSH, null);
			} else if (matches.get(0).contains("new")) {
				continue_game = false;
				mIslistening = false;
				tts.speak("You have chosen to start a new game. Long press to start.", TextToSpeech.QUEUE_FLUSH, null);
			} else {
				tts.speak("Please repeat. Long press to try again.", TextToSpeech.QUEUE_FLUSH, null);
			}
		}

		@Override
		public void onRmsChanged(float rmsdB) {
		}
	}
}
