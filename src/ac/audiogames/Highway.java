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

public class Highway extends Activity implements TextToSpeech.OnInitListener { //Highway game

    final int[] position = {0}; // your vehicle position.
    final int[] rand= {3}; // car direction stored here
    int x=0; // car position for sound
    int i=0; // car distance for sound
    //private Launcher m_launcher;
    final Random gen = new Random();
    private TextToSpeech tts;
    private View mContentView;
    public String info;
    private Source car_sound;
    private Source scrape;
    private Source engine_Start;
    private Source engine_running;
    private Source rain;
    private Source thunder;
    Buffer buffer;
    private SoundEnv env;
    int counter=-1;
    boolean startEng=false;
    Thread start;
    boolean mood=false; // rain mode
    private int difficulty = 3000; // higher value -> more time between cars = easier.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.env = SoundEnv.getInstance(this);
        start=new Thread(){@Override public void run(){play();}};
        //this.env.setListenerOrientation(20);
        try { // load Mono sounds to 3D library
        	Buffer bufferengine = env.addBuffer("engine_start");
        	Buffer bufferengineRun = env.addBuffer("engine_running");
			Buffer bufferScrape= env.addBuffer("scrape");
			this.scrape = env.addSource(bufferScrape);
			this.engine_Start= env.addSource(bufferengine);
			this.engine_running= env.addSource(bufferengineRun);
			this.scrape.setPosition(0, 0, 0);
			this.engine_Start.setPosition(0, 0, 0);
			this.engine_running.setPosition(0, 0, 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        info="Swipe right and left to avoid incoming cars. Swipe up, or down to change the difficulty, or mode. Tap and hold to start. Tap twice to go to the menu. Tap once to repeat this message.";
      //  m_launcher =  new Launcher(this);
        tts = new TextToSpeech(this, this);
        tts.setPitch(1);
        setContentView(R.layout.activity_2);

        mContentView = findViewById(R.id.fullscreen_content_act2);
        // alternative to below ->
        mContentView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeLeft() {

                //MediaPlayer.create(getApplicationContext(), R.raw.left).start();
                if(position[0]>0){
                	scrape.setPosition(-1, 0, 0);
                	scrape.play(false);}
                 position[0] =2; // move to the left.
                 car_sound.setPosition(x+position[0], i, 0);
              
            }
            public void onSwipeRight() {
        
                if(position[0]<0){ // if already at right position
                 	scrape.setPosition(1, 0, 0);
                	scrape.play(false);}
                	 position[0] =-2; //  move right
                	 car_sound.setPosition(x+position[0], i, 0);
            }

            public void onSwipeTop(){
            	tts.stop();
            	if(difficulty>1000)
            	difficulty=difficulty-500;
            	else difficulty = 4000;
            	setSpeed();
            	if(!start.isAlive())
            	tts.speak("Time between cars is " + difficulty/1000.0+ "seconds.", TextToSpeech.QUEUE_FLUSH, null);
            }

            public void longPress() {
            	if (!start.isAlive()){
                tts.stop();
                position[0]=-2;
                rand[0]=3;
                startEng=true;
                setSpeed();
                start = new Thread(){@Override public void run(){play();}};
                start.start();
                }

            }

            public void onSwipeBottom(){
            	tts.stop();
            	if(!start.isAlive()){
            	mood=!mood;
            	if(mood){
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
        			
            	}
            	else tts.speak("Normal mode.", TextToSpeech.QUEUE_FLUSH, null);
            }}

            public void onSingleTap() {
                if (!tts.isSpeaking()) {
                    tts.speak(info, TextToSpeech.QUEUE_FLUSH, null);
                } else
                    tts.stop();
            }
            public void onDoubleTap2() { //pause and go to menu
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                    startActivity(intent);
                    onPause();}
        });

    }
    public void setSpeed(){
    	if(difficulty>2000 && difficulty<3500)
            try {
    			 buffer= env.addBuffer("car_med");
    			car_sound = env.addSource(buffer);
    			car_sound.setPosition(0, 2, 0);
    		} catch (IOException e) {
    			System.out.println("error loading: car_med sound.");
    			e.printStackTrace();
    		}
        	else if (difficulty<2000)
        	     try {
        	    	 if(difficulty<1000) difficulty=1000;
         			buffer= env.addBuffer("car_fast");
         			car_sound = env.addSource(buffer);
         			car_sound.setPosition(0, 2, 0);
         		} catch (IOException e) {
         			System.out.println("error loading: car_fast sound.");
         			e.printStackTrace();
         		}
        	else if (difficulty>3500)
        		   try {
           			 buffer= env.addBuffer("car_slow");
           			car_sound = env.addSource(buffer);
           			car_sound.setPosition(0, 2, 0);
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
    protected void onPause(){
        tts.stop();
        super.onPause();
        this.env.stopAllSources();
		this.env.release();
		start.interrupt();
    }
	@Override
	public void onResume() {
		super.onResume();
	//	this.car_sound.play(true);
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
    public void play(){ // game rules, run from a thread.

 	   if(startEng)
 	   try{
 		   startEng=false;
 	       engine_Start.play(false);
     	   Thread.sleep(3000);
     	   if(mood){
     	   rain.play(true);
     	
     		   }
     	   
     	   engine_running.play(true);
 	   } catch (InterruptedException e) {
            e.printStackTrace();
        }
 	  for(i=5;i>0;i--)
     try {
         Thread.sleep(difficulty/5);
         if(mood)
         if(gen.nextInt(15+(difficulty/200))==1){
   		   thunder.setPosition(gen.nextInt(3)-1, 1, gen.nextInt(3)-1);
   		   thunder.setPitch(gen.nextFloat());
   		   thunder.play(false);}
         car_sound.setPosition(x+position[0], 0, i);
     } catch (InterruptedException e) {
         e.printStackTrace();
     }

         if((rand[0]==0 && position[0]==2)||(rand[0]==1&&position[0]==-2)){ // if crash
        	 engine_running.stop();
             MediaPlayer.create(getApplicationContext(),R.raw.crash).start(); // boom
             if(mood)
             //rain.stop();
             env.stopAllSources();
             tts.speak("Game over. your score is " + counter, TextToSpeech.QUEUE_FLUSH, null);
             counter=-1; difficulty = 4000;
         }
         else{
             rand[0] =gen.nextInt(2);
             counter++;
             if(counter%5==0) {difficulty=difficulty-500; setSpeed();}
             if(rand[0]==0)
             	x=-2;
             else x=2;
             
             car_sound.setPosition(x+position[0], 0, 5);
             car_sound.setGain(20);
             car_sound.play(false);
             	 play();
         }       
}
    
}
