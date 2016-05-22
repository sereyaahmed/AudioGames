package ac.audiogames;

import android.content.Intent;
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

public class Crossing extends Activity implements TextToSpeech.OnInitListener { // Road Crossing
																				// game

	final int[] position = {-1}; // your position.
	int x[] = {0,0,0}; // cars' position for sound
	int z[] = {1,2,3}; // cars' lanes for sound
	int c[] = {0,0,0}; // counters
	// private Launcher m_launcher; TODO Haptic feedback, left/right -> more modes 
	int incrementer[]={0,0,0}; // speed and direction
	final Random gen = new Random();
	private TextToSpeech tts;
	private View mContentView;
	public String info;
	private Source car_sound[];
	private Source step;
	private Source rain;
	private Source thunder;
	Buffer buffer[]; // car sound buffers
//	Buffer buffer2;
//	Buffer buffer3; 
	private SoundEnv env;
	boolean resume=true;
	int island=0;
	Thread start;
	final int mode[] = {1}; // game modes -> 0 tutorial -> 1 normal -> 2 rain ->>>?
	private int difficulty = 4000; // higher value -> more time between cars =
	

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
		car_sound = new Source[3];
		buffer = new Buffer[3];
		// this.env.setListenerOrientation(20);
		try { // load Mono sounds to 3D library
			Buffer bufferstep = env.addBuffer("crossing_step");
			this.step = env.addSource(bufferstep);
			this.step.setPosition(0, -1, 0);
			buffer[0] = env.addBuffer("crossing_car");
//			buffer[1] = env.addBuffer("car_med");
//			buffer[2] = env.addBuffer("car_fast");
		} catch (IOException e) {

			e.printStackTrace();
		}
		info = "During the game tap on screen to advance to the next lane when you hear that there is no car coming towards you there. Swipe up, or down to change the difficulty, swipe left, or right to change the mode. Tap and hold to start. Tap twice to go to the menu. Tap once to repeat this message.";
		// m_launcher = new Launcher(this);
		tts = new TextToSpeech(this, this);
		tts.setPitch(1);
		setContentView(R.layout.activity_2);
//		setSpeed();
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
			//	setSpeed();
				if (!start.isAlive())
					tts.speak("Time between cars is " + difficulty / 1000.0 + "seconds.", TextToSpeech.QUEUE_FLUSH,
							null);
			}}

			public void longPress() {
				if(mode[0]==0 && !start.isAlive()){
					difficulty=4000;
					tts.stop();
					position[0]=0;
//					startEng=true;
					start=new Thread(){
						@Override
						public void run(){
							//tutorial();
						}
					};
					start.start();
				}
				else if (!start.isAlive()) {
					tts.stop();
					position[0] = -1; resume=true;
//					counter = -1;
//					rand ={3,3};
//					startEng = true;
					//setSpeed();
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
				//setSpeed();
				if (!start.isAlive())
					tts.speak("Time between cars is " + difficulty / 1000.0 + "seconds.", TextToSpeech.QUEUE_FLUSH,
							null);
			}}

			public void onSingleTap() {
				if (!start.isAlive())
					if (!tts.isSpeaking()) {
						tts.speak(info, TextToSpeech.QUEUE_FLUSH, null);
					} else
						tts.stop();
				else if(mode[0]!=0) {
				
					position[0]++;
				gameOver(0); gameOver(1); // ends and resets the game if there is a hit
					if(position[0]!=-1) {	step.play(false); z[getLastLane()]=position[0]+1;}
			}}

			public void onDoubleTap2() { // pause and go to menu
				Intent intent = new Intent(getApplicationContext(), Menu.class);
				startActivity(intent);
				onDestroy();
			}
		});

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
	public int getLastLane(){ // rear most lane to be repositioned to front most (2 infront 1 behind at a time)
		int position=0;
		if(island<this.position[0])
			if(gen.nextInt(3)==0) island=this.position[0]+3; // generates a new safe island
		for(int i=0;i<z.length;i++)
		if (z[position]>z[i]&& z[i]<this.position[0]) position=i;
		return position;
	}
public void lane(int pos){
	if(island!=z[pos]){
		if(x[pos]==6 || x[pos]==-6){ incrementer[pos]=0; }//z[pos]++;}
	if(incrementer[pos]==0){
	incrementer[pos]=3-gen.nextInt(7); // speed and direction, 0 no incoming for a while
	if (incrementer[pos]>0){ // 1_3 <- 
		x[pos]=-5; 	car_sound[pos] = env.addSource(buffer[0]); //[Math.abs(incrementer[pos])-1]
		car_sound[pos].setPosition((float)x[pos]/10, 0, 2*(z[pos]-position[0])); car_sound[pos].play(false); //car_sound[pos].setGain(3);
		car_sound[pos].setPitch(Math.abs(incrementer[pos]));
	}
	else if(incrementer[pos]<0) {// -1_-3 ->
	x[pos]=5;car_sound[pos] = env.addSource(buffer[0]);
	car_sound[pos].setPosition((float)x[pos]/10, 0, 2*(z[pos]-position[0])); car_sound[pos].play(false);//car_sound[pos].setGain(3);
	car_sound[pos].setPitch(Math.abs(incrementer[pos]));
	}
	}
	try {if(incrementer[pos]!=0 && position[0]!=-1){
		x[pos]+=Math.abs(incrementer[pos])/incrementer[pos]; // decrementing/incrementing to the other direction
		Thread.sleep(Math.abs(2000*incrementer[pos])/10); 
		car_sound[pos].setPosition((float)x[pos]/10, 0, 2*(z[pos]-position[0]));
		gameOver(pos);}
	else Thread.sleep(1000); // if no car coming
	} catch (InterruptedException e) {
		
		e.printStackTrace();
	}
	}
	else{ car_sound[pos].stop(); try { // if island
		Thread.sleep(500);
	} catch (InterruptedException e) {
	
		e.printStackTrace();
	}}
	if (position[0]!=-1 && resume)
		lane(pos);
}
	public int gameOver(int pos){ // checks for collision on a given lane and resets it's values if so
		if(position[0]==z[pos]&& x[pos]==0){ //TODO hit sound
			tts.speak("Game over, your score is"+position[0], TextToSpeech.QUEUE_FLUSH, null);
			car_sound[pos].stop(); incrementer[pos]=0; z[pos]=pos+1; x[pos]=0;
		position[0]=-1; resume=false;
		}
		return pos;
	}
	public void play() { // game rules, run from a thread.
		if (position[0]==-1)
			try {
				position[0]++;
				step.play(false);
				Thread.sleep(1000);
				new Thread(){@Override public void run(){lane(0);}}.start(); // running 3 threads for 3 lanes
				new Thread(){@Override public void run(){lane(1);}}.start();
				//new Thread(){@Override public void run(){lane(2);}}.start(); 
				if (mode[0]==2) {
					rain.play(true);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	
	
			try {
				Thread.sleep(200);
				if (mode[0]==2)
					if (gen.nextInt(75) == 1) {
						thunder.setPosition(gen.nextInt(3) - 1, 1, gen.nextInt(3) - 1);
						thunder.setPitch(gen.nextFloat());
						thunder.play(false);
					}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(resume)
			play();

		}
	/*
	public void tutorial() { //tutorial thread
		if (startEng)
			try {
				startEng = false;
				step.play(false);
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
			for (i = 5; i > 0; i--)
				try {
					Thread.sleep(difficulty / 5);
					if (counter % 2 == 0)
						car_sound.setPosition(x + position[0], 0, i); // ,0,i
					else
						car_sound2.setPosition(x + position[0], 0, i); // ,0,i

					// car_sound.setRolloffFactor(i);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			// setSpeed();
			if(counter<13)
			tutorial();
			else 	{tts.speak("The tutorial is now over", TextToSpeech.QUEUE_FLUSH, null); mode[0]=1;}
		}
	*/

}
