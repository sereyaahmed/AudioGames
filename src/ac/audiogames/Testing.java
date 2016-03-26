package ac.audiogames;

import java.io.IOException;

import org.pielot.openal.Buffer;
import org.pielot.openal.SoundEnv;
import org.pielot.openal.Source;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class Testing extends Activity {
	private TextView tvx ;
	private TextView tvy ;
	private TextView tvz ;
	private TextView tvg ;
	private TextView tvp ;
	private TextView tvr ;
    //private View mContentView;
	private SoundEnv			env;
	int x=0;
	int y=0;
	int z=0;
	int r=0;
	int p=0;
	int g=0;
	private Source				lake1;
//	private Source				lake2;
//	private Source				park1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.test);
		//mContentView = findViewById(R.id.fullscreen_content_test);
		
		  tvx= (TextView) findViewById(R.id.view_x);
		  tvy= (TextView) findViewById(R.id.view_y);
		  tvz= (TextView) findViewById(R.id.view_z);
		  tvg= (TextView) findViewById(R.id.view_gain);
		  tvr= (TextView) findViewById(R.id.view_roll);
		  tvp= (TextView) findViewById(R.id.view_pitch);
		  tvx.setText(" X: "+x);
		  tvy.setText(" Y: "+y);
		  tvz.setText(" Z: "+z);
		  tvg.setText(" G: "+g);
		  tvr.setText(" R: "+r);
		  tvp.setText(" P: "+p);
		try {
			/* First we obtain the instance of the sound environment. */
			this.env = SoundEnv.getInstance(this);

			/*
			 * Now we load the sounds into the memory that we want to play
			 * later. Each sound has to be buffered once only. To add new sound
			 * copy them into the assets folder of the Android project.
			 * Currently only mono .wav files are supported.
			 */
			Buffer lake = env.addBuffer("bird_test");
//			Buffer park = env.addBuffer("thunder");

			/*
			 * To actually play a sound and place it somewhere in the sound
			 * environment, we have to create sources. Each source has its own
			 * parameters, such as 3D position or pitch. Several sources can
			 * share a single buffer.
			 */
			this.lake1 = env.addSource(lake);
//			this.lake2 = env.addSource(lake);
//			this.park1 = env.addSource(park);

			// Now we spread the sounds throughout the sound room.
			this.lake1.setPosition(0, 0, 0);
//			this.lake2.setPosition(-6, 0, 4);
//			this.park1.setPosition(0, 0, 15); //0,0,0, left/right, up/down?, front/rear

			// and change the pitch of the second lake.
//			this.lake2.setPitch(1.1f);
				} catch (IOException e) {
			System.out.println("error loading: rain sound.");
			e.printStackTrace();
		}
		      tvx.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
		    	  @Override
		            public void onSwipeTop() {
		            	x++;       mHandler.post(mUpdateUITimerTask);
		            }
		    	  @Override
		            public void onSwipeBottom() {
		            	x--;        mHandler.post(mUpdateUITimerTask);
		            }
		    	  @Override
		            public void onDoubleTap2() { //pause and go to menu
		                Intent intent = new Intent(getApplicationContext(), Menu.class);
		                    startActivity(intent);
		                    onPause();}
		        });
		      tvy.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
		    	  @Override
		            public void onSwipeTop() {
		            	y++;        mHandler.post(mUpdateUITimerTask);
		            }
		    	  @Override
		            public void onSwipeBottom() {
		            	y--;       mHandler.post(mUpdateUITimerTask);
		            }
		    	  @Override
		            public void onDoubleTap2() { //pause and go to menu
		                Intent intent = new Intent(getApplicationContext(), Menu.class);
		                    startActivity(intent);
		                    onPause();}
		        });
		      tvz.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
		    	  @Override
		            public void onSwipeTop() {
		            	z++;        mHandler.post(mUpdateUITimerTask);
		            }
		    	  @Override
		            public void onSwipeBottom() {
		            	z--;        mHandler.post(mUpdateUITimerTask);
		            }
		    	  @Override
		            public void onDoubleTap2() { //pause and go to menu
		                Intent intent = new Intent(getApplicationContext(), Menu.class);
		                    startActivity(intent);
		                    onPause();}
		        });
		      tvg.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
		    	  @Override
		            public void onSwipeTop() {
		            	g++; lake1.setGain(g);       mHandler.post(mUpdateUITimerTask);
		            }
		    	  @Override
		            public void onSwipeBottom() {
		            	g--; lake1.setGain(g);        mHandler.post(mUpdateUITimerTask);
		            }
		    	  @Override
		            public void onDoubleTap2() { //pause and go to menu
		                Intent intent = new Intent(getApplicationContext(), Menu.class);
		                    startActivity(intent);
		                    onPause();}
		        });
		      tvr.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
		    	  @Override
		            public void onSwipeTop() {
		            	r++; lake1.setRolloffFactor(r);        mHandler.post(mUpdateUITimerTask);
		            }
		    	  @Override
		            public void onSwipeBottom() {
		            	r--; lake1.setRolloffFactor(r);        mHandler.post(mUpdateUITimerTask);
		            }
		    	  @Override
		            public void onDoubleTap2() { //pause and go to menu
		                Intent intent = new Intent(getApplicationContext(), Menu.class);
		                    startActivity(intent);
		                    onPause();}
		        });
		      tvp.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
		    	  @Override
		            public void onSwipeTop() {
		            	p++; lake1.setPitch(p);        mHandler.post(mUpdateUITimerTask);
		            }
		    	  @Override
		            public void onSwipeBottom() {
		            	p--; lake1.setPitch(p);        mHandler.post(mUpdateUITimerTask);
		            }
		    	  @Override
		            public void onDoubleTap2() { //pause and go to menu
		                Intent intent = new Intent(getApplicationContext(), Menu.class);
		                    startActivity(intent);
		                    onPause();}
		        });
		    
		
			/*
			 * These sounds are perceived from the perspective of a virtual
			 * listener. Initially the position of this listener is 0,0,0. The
			 * position and the orientation of the virtual listener can be
			 * adjusted via the SoundEnv class.
			 */
//			this.env.setListenerOrientation(20);
	}
	  @Override
	    protected void onPostCreate(Bundle savedInstanceState) {
	        super.onPostCreate(savedInstanceState);
	       
	        }
	@Override
	public void onResume() {
		super.onResume();
		//Log.i(TAG, "onResume()");

		/*
		 * Start playing all sources. 'true' as parameter specifies that the
		 * sounds shall be played as a loop.
		 */
		this.lake1.play(true);
//		this.lake2.play(true);
//		this.park1.play(true);
	}

	@Override
	public void onPause() {
		super.onPause();
		//Log.i(TAG, "onPause()");

		// Stop all sounds
		this.lake1.stop();
//		this.lake2.stop();
//		this.park1.stop();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//Log.i(TAG, "onDestroy()");

		// Be nice with the system and release all resources
		this.env.stopAllSources();
		this.env.release();
	}

	@Override
	public void onLowMemory() {
		this.env.onLowMemory();
	}
	private final Runnable mUpdateUITimerTask = new Runnable() {
	    public void run() {
	        // do whatever you want to change here, like:
	    	lake1.setPosition(x, y, z); 
	    	tvx.setText(" X: "+x);
	    	tvy.setText(" Y: "+y);
	    	tvz.setText(" Z: "+z);
	    	tvg.setText(" G: "+g);
	    	tvr.setText(" R: "+r);
	    	tvp.setText(" P: "+p);
	    }
	};
	private final Handler mHandler = new Handler();
}
