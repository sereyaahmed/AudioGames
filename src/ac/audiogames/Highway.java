package ac.audiogames;

import android.content.Intent;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.app.Activity;

import java.io.IOException;

//import com.immersion.uhl.Launcher;
import java.util.Locale;
import java.util.Random;
import org.pielot.openal.Buffer;
import org.pielot.openal.SoundEnv;
import org.pielot.openal.Source;

public class Highway extends Activity implements TextToSpeech.OnInitListener { // Highway
																				// game

	final int[] position = { -2 }; // your vehicle position.
	final int[] rand = { 3 }; // car direction stored here
	int x = 0; // car position for sound
	int i = 0; // car distance for sound
	// private Launcher m_launcher; TODO Haptic feedback
	//TODO maria mode?
	// TODO 1 sound duration / difficulty = pitch! 
				// TODO 100 ms difficulty changes 
	final Random gen = new Random();
	private TextToSpeech tts;
	private View mContentView;
	public String info;
	private Source car_sound;
	private Source car_sound2;
	private Source engine_Start;
	private Source engine_running;
	private Source rain;
	private Source thunder;
	Buffer buffer;
	Buffer buffer2;
	private SoundEnv env;
	int counter = -1;
	boolean startEng = false;
	Thread start;
	final int mode[] = {1}; // game modes -> 0 tutorial -> 1 normal -> 2 rain ->>>?
	private int difficulty = 4000; // higher value -> more time between cars =
									// easier.

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.env = SoundEnv.getInstance(this);
		start = new Thread() {
			@Override
			public void run() {
				play();
			}
		};
		// this.env.setListenerOrientation(20);
		try { // load Mono sounds to 3D library
			Buffer bufferengine = env.addBuffer("engine_start");
			Buffer bufferengineRun = env.addBuffer("engine_running");
			this.engine_Start = env.addSource(bufferengine);
			this.engine_running = env.addSource(bufferengineRun);
			this.engine_Start.setPosition(0, 0, 0);
			this.engine_running.setPosition(0, 0, 0);
		} catch (IOException e) {

			e.printStackTrace();
		}
		info = "During the game tap on screen to change direction when a car is coming directly towards you. Swipe up, or down to change the difficulty, swipe left, or right to change the mode. Tap and hold to start. Tap twice to go to the menu. Tap once to repeat this message.";
		// m_launcher = new Launcher(this);
		tts = new TextToSpeech(this, this);
		tts.setPitch(1);
		setContentView(R.layout.activity_2);
		setSpeed();
		mContentView = findViewById(R.id.fullscreen_content_act2);
		mContentView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
			public void onSwipeLeft() {
				tts.stop();
				if (!start.isAlive()) {
					if(mode[0]<2)
					mode[0]=mode[0]+1;
					else mode[0]=0;
					if (mode[0]==2) {
						Buffer bufferRain;
						Buffer bufferThunder;
						try {
							bufferRain = env.addBuffer("rain");
							bufferThunder = env.addBuffer("thunder");
							rain = env.addSource(bufferRain);
							thunder = env.addSource(bufferThunder);
							rain.setPosition(0, 0, 0);
							thunder.setPosition(0, 0, 0);
							tts.speak("Rain mode", TextToSpeech.QUEUE_FLUSH, null);
						} catch (IOException e) {
							System.out.println("error loading: rain sound.");
							e.printStackTrace();
						}

					} else if(mode[0]==1)
						tts.speak("Normal mode.", TextToSpeech.QUEUE_FLUSH, null);
					else if(mode[0]==0)
						tts.speak("Tutorial mode.", TextToSpeech.QUEUE_FLUSH, null);
				}
			}

			public void onSwipeRight() {
				tts.stop();
				if (!start.isAlive()) {
					if(mode[0]>0)
					mode[0]=mode[0]-1;
					else mode[0]=2;
					if (mode[0]==2) {
						Buffer bufferRain;
						Buffer bufferThunder;
						try {
							bufferRain = env.addBuffer("rain");
							bufferThunder = env.addBuffer("thunder");
							rain = env.addSource(bufferRain);
							thunder = env.addSource(bufferThunder);
							rain.setPosition(0, 0, 0);
							thunder.setPosition(0, 0, 0);
							tts.speak("Rain mode", TextToSpeech.QUEUE_FLUSH, null);
						} catch (IOException e) {
							System.out.println("error loading: rain sound.");
							e.printStackTrace();
						}

					} else if(mode[0]==1)
						tts.speak("Normal mode.", TextToSpeech.QUEUE_FLUSH, null);
					else if(mode[0]==0)
						tts.speak("Tutorial mode.", TextToSpeech.QUEUE_FLUSH, null);
				}
			}

			public void onSwipeTop() {
				if(mode[0]!=0){
				tts.stop();
				if (difficulty > 1000)
					difficulty = difficulty - 500;
				else
					difficulty = 4000;
				setSpeed();
				if (!start.isAlive())
					tts.speak("Time between cars is " + difficulty / 1000.0 + "seconds.", TextToSpeech.QUEUE_FLUSH,
							null);
			}}
			public void longPress() {
				if(mode[0]==0 && !start.isAlive()){
					difficulty=4000;
					tts.stop();
					counter=-1;
					startEng=true;
					start=new Thread(){
						@Override
						public void run(){
							tutorial();
						}
					};
					start.start();
				}
				else if (!start.isAlive()) {
					tts.stop();
					position[0] = -2;
					counter = -1;
					rand[0] = 3;
					startEng = true;
					setSpeed();
					start = new Thread() {
						@Override
						public void run() {
							play();
						}
					};
					start.start();
				}

			}

			public void onSwipeBottom() {
				if(mode[0]!=0){
				tts.stop();
				if (difficulty < 4000)
					difficulty = difficulty + 500;
				else
					difficulty = 1000;
				setSpeed();
				if (!start.isAlive())
					tts.speak("Time between cars is " + difficulty / 1000.0 + "seconds.", TextToSpeech.QUEUE_FLUSH,
							null);
			}}

			public void onSingleTap() {
				if(mode[0]!=0)
				if (!start.isAlive())
					if (!tts.isSpeaking()) {
						tts.speak(info, TextToSpeech.QUEUE_FLUSH, null);
					} else
						tts.stop();
				else {
					position[0] = -position[0];
					car_sound.setPosition((float)(x + position[0])/3, 0, (float)i/10);
					car_sound2.setPosition((float)(x + position[0])/3, 0, (float)i/10);
				}
			}

			public void onDoubleTap2() { // pause and go to menu
				Intent intent = new Intent(getApplicationContext(), Menu.class);
				startActivity(intent);
				onDestroy();
			}
		});

	}

	public void setSpeed() {
		if (difficulty > 2000 && difficulty < 3000)
			try {
				buffer = env.addBuffer("car_med");
				buffer2 = env.addBuffer("car_med");
				car_sound = env.addSource(buffer);
				car_sound2 = env.addSource(buffer2);
				// car_sound.setPosition(0, 0, i);// ,2,0
				// car_sound2.setPosition(0, 0, i);// ,2,0
			} catch (IOException e) {
				System.out.println("error loading: car_med sound.");
				e.printStackTrace();
			}
		else if (difficulty < 2000)
			try {
				if (difficulty < 1000)
					difficulty = 1000;
				buffer = env.addBuffer("car_fast");
				buffer2 = env.addBuffer("car_fast");
				car_sound = env.addSource(buffer);
				car_sound2 = env.addSource(buffer2);
				// car_sound.setPosition(0, 0, i);
				// car_sound2.setPosition(0, 0, i);
			} catch (IOException e) {
				System.out.println("error loading: car_fast sound.");
				e.printStackTrace();
			}
		else// (difficulty>3000)
			try {
				buffer = env.addBuffer("car_slow");
				buffer2 = env.addBuffer("car_slow");
				car_sound2 = env.addSource(buffer2);
				car_sound = env.addSource(buffer);
				// car_sound.setPosition(0, 0, i);
				// car_sound2.setPosition(0, 0, i);
			} catch (IOException e) {
				System.out.println("error loading: car_slow sound.");
				e.printStackTrace();
			}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		delayedSpeak(100);
	}

	@Override
	protected void onPause() {
		tts.stop();
		super.onPause();
		this.env.stopAllSources();
		this.env.release();
	}

	@Override
	public void onResume() {
		super.onResume();
		// this.car_sound.play(true);
	}

	@Override
	public void onInit(int status) {
		tts.setLanguage(Locale.UK);
	}

	@Override
	public void onDestroy() {
		// Don't forget to shutdown!

		tts.stop();
		tts.shutdown();
		this.env.stopAllSources();
		this.env.release();
		super.onDestroy();
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

	public void play() { // game rules, run from a thread.
		if (startEng)
			try {
				startEng = false;
				engine_Start.play(false);
				Thread.sleep(3000);
				if (mode[0]==2) {
					rain.play(true);

				}

				engine_running.play(true);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		for (i = 4; i > -1; i--)
			try {
				Thread.sleep(difficulty / 5);
				if (mode[0]==2)
					if (gen.nextInt(15 + (difficulty / 200)) == 1) {
						thunder.setPosition(gen.nextInt(3) - 1, 1, gen.nextInt(3) - 1);
						thunder.setPitch(gen.nextFloat());
						thunder.play(false);
					}
				if (counter % 2 == 0)
					car_sound.setPosition((float)(x + position[0])/3, 0, (float)i/10); // ,0,i
				else
					car_sound2.setPosition((float)(x + position[0])/3, 0, (float)i/10); // ,0,i

				// car_sound.setRolloffFactor(i);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		if ((rand[0] == 0 && position[0] == 2) || (rand[0] == 1 && position[0] == -2)) { // if
																							// crash
			engine_running.stop();
			MediaPlayer.create(getApplicationContext(), R.raw.crash).start(); // boom
			if (mode[0]==2)
				// rain.stop();
				env.stopAllSources();
			tts.speak("Game over. your score is " + counter, TextToSpeech.QUEUE_FLUSH, null);
			counter = -1;
			difficulty = 4000;
		} else {
			rand[0] = gen.nextInt(2);
			counter++;
			if (counter % 5 == 0) {
				difficulty = difficulty - 500;
				setSpeed();
			} // difficulty=difficulty-500; //
			if (rand[0] == 0)
				x = -2;
			else
				x = 2;
			if (counter % 2 == 0)
				car_sound.setPosition(x + position[0], 0, 5);
			else
				car_sound2.setPosition(x + position[0], 0, 5);
			car_sound.setRolloffFactor(2);
			car_sound2.setRolloffFactor(2);
			// car_sound.setGain(3);
			// car_sound2.setGain(3);
			if (counter % 2 == 0) { // allows for car sound to keep playing
									// after you avoided it by using 2
									// alternative sources.
				car_sound.play(false);
				car_sound.setGain(3);
				car_sound2.setGain(1);
			} else {
				car_sound2.play(false);
				car_sound2.setGain(3);
				car_sound.setGain(1);
			}
			// setSpeed();
			play();

		}
	}
	public void tutorial() { //tutorial thread
		if (startEng)
			try {
				startEng = false;
				engine_Start.play(false);
				Thread.sleep(3000);
					} catch (InterruptedException e) {
				e.printStackTrace();
			}
	
				if(counter==-1){
			tts.speak("You will hear 4 cars coming from your right side now", TextToSpeech.QUEUE_FLUSH, null);
			}
			counter++;
			if(counter<4){
			rand[0] = 1; position[0]=2;
			}
			if(counter==4){
				tts.speak("You will hear 4 cars coming from your left side now", TextToSpeech.QUEUE_FLUSH, null);
			}
			
			if (counter>4 && counter<9) {
				rand[0]=0; position[0]=-2;
			}
//				setSpeed();
			if(counter==9){
				tts.speak("You will hear 4 cars coming directly at you now", TextToSpeech.QUEUE_FLUSH, null);
				}
				if(counter>9 && counter<14){
					position[0]=2; rand[0]=0; 
				}
			// difficulty=difficulty-500; //
			if (rand[0] == 0)
				x = -2;
			else
				x = 2;
			if (counter % 2 == 0)
				car_sound.setPosition(x + position[0], 0, 5);
			else
				car_sound2.setPosition(x + position[0], 0, 5);
			car_sound.setRolloffFactor(2);
			car_sound2.setRolloffFactor(2);
			if (counter % 2 == 0 && counter!=4) {
				car_sound.play(false);
				car_sound.setGain(3);
				car_sound2.setGain(1);
			} else if (counter!=9 && counter!=4) {
				car_sound2.play(false);
				car_sound2.setGain(3);
				car_sound.setGain(1);
			}
			for (i = 4; i > -1; i--)
				try {
					Thread.sleep(difficulty / 5);
					if (counter % 2 == 0)
						car_sound.setPosition((float)(x + position[0])/3, 0, (float)i/10); // ,0,i
					else
						car_sound2.setPosition((float)(x + position[0])/3, 0, (float)i/10); // ,0,i

					// car_sound.setRolloffFactor(i);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			// setSpeed();
			if(counter<13)
			tutorial();
			else 	{tts.speak("The tutorial is now over", TextToSpeech.QUEUE_FLUSH, null); mode[0]=1;}
		}
	

}
