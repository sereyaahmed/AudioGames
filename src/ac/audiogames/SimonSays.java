package ac.audiogames;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import java.io.IOException;

//import com.immersion.uhl.Launcher;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import org.pielot.openal.Buffer;
import org.pielot.openal.SoundEnv;
import org.pielot.openal.Source;

public class SimonSays extends Activity implements TextToSpeech.OnInitListener {

    private View mContentView;
    ArrayList<MediaPlayer> directions;
    ArrayList<MediaPlayer> piano;
    ArrayList<Integer> gameSteps;
    int playerPos;
    int score;
    public boolean isPlaying;
    private TextToSpeech tts;
    String info;
    Thread start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        start=new Thread(){@Override public void run(){play();}};
        directions=new ArrayList<>();
        piano=new ArrayList<>();
        gameSteps= new ArrayList<>();
        playerPos=0;
        isPlaying=false;
        tts = new TextToSpeech(this, this);
        tts.setPitch(1);
        setContentView(R.layout.activity_simon_says);
        score=0;
        info="Listen to the given directions then follow them to play the tune. Tap and hold to start. Tap twice to return to the menu. Tap once to repeat this message.";
        directions.add(MediaPlayer.create(getApplicationContext(), R.raw.simon_up));
        directions.add(MediaPlayer.create(getApplicationContext(),R.raw.simon_right));
        directions.add(MediaPlayer.create(getApplicationContext(),R.raw.simon_down));
        directions.add(MediaPlayer.create(getApplicationContext(),R.raw.simon_left));
        piano.add(MediaPlayer.create(getApplicationContext(),R.raw.piano_up));
        piano.add(MediaPlayer.create(getApplicationContext(),R.raw.piano_right));
        piano.add(MediaPlayer.create(getApplicationContext(),R.raw.piano_down));
        piano.add(MediaPlayer.create(getApplicationContext(),R.raw.piano_left));

        mContentView = findViewById(R.id.fullscreen_content);

        mContentView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeLeft() {
            	if(!start.isAlive()){
                piano.get(3).seekTo(0);
                piano.get(3).start();
                if (gameSteps.size() != 0 && !isPlaying)
                    if (!verifyStep(3)) {

                        tts.speak("Game over. your score is " + score, TextToSpeech.QUEUE_FLUSH, null);
                    }}
            }

            @Override
            public void onSwipeRight() {
            	if(!start.isAlive()){
                piano.get(1).seekTo(0);
                piano.get(1).start();
                if (gameSteps.size() != 0 && !isPlaying)
                    if (!verifyStep(1)) {

                        tts.speak("Game over. your score is " + score, TextToSpeech.QUEUE_FLUSH, null);
                    }}

            }

            @Override
            public void onSwipeTop() {
            	if(!start.isAlive()){
                piano.get(0).seekTo(0);
                piano.get(0).start();
                if (gameSteps.size() != 0 && !isPlaying)
                    if (!verifyStep(0)) {

                        tts.speak("Game over. your score is " + score, TextToSpeech.QUEUE_FLUSH, null);
                    }}
            }

            @Override
            public void onSwipeBottom() {
            	if(!start.isAlive()){
                piano.get(2).seekTo(0);
                piano.get(2).start();
                if (gameSteps.size() != 0 && !isPlaying)
                    if (!verifyStep(2)) {
                        tts.speak("Game over. your score is " + score, TextToSpeech.QUEUE_FLUSH, null);
                    }}

            }

            public void onSingleTap() {
                if (!tts.isSpeaking()) {
                    tts.speak(info, TextToSpeech.QUEUE_FLUSH, null);
                } else
                    tts.stop();
            }

            public void onDoubleTap2() {
                gameSteps=null;
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                startActivity(intent);
                onPause();
                tts.stop();
            }

            public void longPress() {
            	if (!start.isAlive()){
                    tts.stop();
                    gameSteps = new ArrayList<>();
                    start = new Thread(){@Override public void run(){play();}};
                    start.start();
            	}

            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedSpeak(100);

    }

    public boolean verifyStep(int x) {
//        if(playerPos<gameSteps.size()){
        boolean ret;
        if(gameSteps.get(playerPos)==x){  playerPos++; ret= true;}
        else{playerPos=0; score=gameSteps.size()-1; gameSteps=new ArrayList<>(); ret= false;}
        if(playerPos==gameSteps.size() && ret){ start=new Thread(){@Override public void run(){play();}}; start.start(); playerPos=0;}
        return ret;
    }


    @Override
    public void onInit(int status) {
        tts.setLanguage(Locale.UK);
    }
    @Override
    public void onDestroy() {
        // Don't forget to shutdown!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            start.interrupt();
        }
        super.onDestroy();
    }
    @Override
    public void onPause(){
    	start.interrupt();
    	tts.stop();
    	super.onPause();
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
    public void play(){
    	 isPlaying=true;
         Random gen=new Random();
         gameSteps.add(gen.nextInt(4));
         for (int i=0;i < gameSteps.size(); i++) {
             try {
                 Thread.sleep(550);
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
             directions.get(gameSteps.get(i)).seekTo(0);
             directions.get(gameSteps.get(i)).start();
             piano.get(gameSteps.get(i)).seekTo(0);
             piano.get(gameSteps.get(i)).start();

         }
         isPlaying=false;
    }
}
