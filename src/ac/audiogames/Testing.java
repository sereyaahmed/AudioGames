package ac.audiogames;

import java.io.IOException;
import java.text.DecimalFormat;

import org.pielot.openal.Buffer;
import org.pielot.openal.SoundEnv;
import org.pielot.openal.Source;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Testing extends Activity {
	private TextView tvx;
	private TextView tvy;
	private TextView tvz;
	private TextView tvg;
	private TextView tvp;
	private TextView tvr;
  
	private TableRow mContentView;
	private SoundEnv env;
	double x = 0;
	double y = 0;
	double z = 0;
	double r = 0;
	double p = 0;
	double g = 0;
	private Source bird1;
	// private Source bird2;
	// private Source park1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.test);
		mContentView = (TableRow) findViewById(R.id.table_row1);
		// TODO merge with joystick if kept
		// TODO sound selection
		// TODO auto rotating test
		tvx = (TextView) findViewById(R.id.view_x);
		tvy = (TextView) findViewById(R.id.view_y);
		tvz = (TextView) findViewById(R.id.view_z);
		tvg = (TextView) findViewById(R.id.view_gain);
		tvr = (TextView) findViewById(R.id.view_roll);
		tvp = (TextView) findViewById(R.id.view_pitch);
		tvx.setText("X:\n" + x);
		tvy.setText("Y:\n" + y);
		tvz.setText("Z:\n" + z);
		tvg.setText("G:\n" + g);
		tvr.setText("R:\n" + r);
		tvp.setText("P:\n" + p);
		try {
			/* First we obtain the instance of the sound environment. */
			this.env = SoundEnv.getInstance(this);

			/*
			 * Now we load the sounds into the memory that we want to play
			 * later. Each sound has to be buffered once only. To add new sound
			 * copy them into the assets folder of the Android project.
			 * Currently only mono .wav files are supported.
			 */
			Buffer bird = env.addBuffer("bird_test");
			// Buffer park = env.addBuffer("thunder");

			/*
			 * To actually play a sound and place it somewhere in the sound
			 * environment, we have to create sources. Each source has its own
			 * parameters, such as 3D position or pitch. Several sources can
			 * share a single buffer.
			 */
			this.bird1 = env.addSource(bird);
			// this.bird2 = env.addSource(bird);
			// this.park1 = env.addSource(park);

			// Now we spread the sounds throughout the sound room.
			this.bird1.setPosition(0, 0, 0);

			// this.bird2.setPosition(-6, 0, 4);
			// this.park1.setPosition(0, 0, 15); //0,0,0, left/right, up/down?,
			// front/rear

			// and change the pitch of the second bird.
			// this.bird2.setPitch(1.1f);
		} catch (IOException e) {
			System.out.println("error loading: rain sound.");
			e.printStackTrace();
		}
		mContentView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
			@Override
			public void longPress() {
				x = 0;
				y = 0;
				z = 0;
				g = 0;
				p = 0;
				r = 0;
				mHandler.post(mUpdateUITimerTask);
			}

			@Override
			public void onDoubleTap2() { // pause and go to menu
				Intent intent = new Intent(getApplicationContext(), Menu.class);
				startActivity(intent);
				onPause();
			}
		});
		tvx.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
			@Override
			public void onSwipeTop() {
				x+=0.10;
				mHandler.post(mUpdateUITimerTask);
			}

			@Override
			public void onSwipeBottom() {
				x-=0.10;
				mHandler.post(mUpdateUITimerTask);
			}

			@Override
			public void onDoubleTap2() { // pause and go to menu
				Intent intent = new Intent(getApplicationContext(), Menu.class);
				startActivity(intent);
				onPause();
			}
		});
		tvy.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
			@Override
			public void onSwipeTop() {
				y+=0.10;
				mHandler.post(mUpdateUITimerTask);
			}

			@Override
			public void onSwipeBottom() {
				y-=0.10;
				mHandler.post(mUpdateUITimerTask);
			}

			@Override
			public void onDoubleTap2() { // pause and go to menu
				Intent intent = new Intent(getApplicationContext(), Menu.class);
				startActivity(intent);
				onPause();
			}
		});
		tvz.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
			@Override
			public void onSwipeTop() {
				z+=0.10;
				mHandler.post(mUpdateUITimerTask);
			}

			@Override
			public void onSwipeBottom() {
				z-=0.10;
				mHandler.post(mUpdateUITimerTask);
			}

			@Override
			public void onDoubleTap2() { // pause and go to menu
				Intent intent = new Intent(getApplicationContext(), Menu.class);
				startActivity(intent);
				onPause();
			}
		});
		tvg.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
			@Override
			public void onSwipeTop() {
				g+=0.10;
				bird1.setGain((float)g);
				mHandler.post(mUpdateUITimerTask);
			}

			@Override
			public void onSwipeBottom() {
				g-=0.10;
				bird1.setGain((float)g);
				mHandler.post(mUpdateUITimerTask);
			}

			@Override
			public void onDoubleTap2() { // pause and go to menu
				Intent intent = new Intent(getApplicationContext(), Menu.class);
				startActivity(intent);
				onPause();
			}
		});
		tvr.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
			@Override
			public void onSwipeTop() {
				r+=0.10;
				bird1.setRolloffFactor((float)r);
				mHandler.post(mUpdateUITimerTask);
			}

			@Override
			public void onSwipeBottom() {
				r-=0.10;
				bird1.setRolloffFactor((float)r);
				mHandler.post(mUpdateUITimerTask);
			}

			@Override
			public void onDoubleTap2() { // pause and go to menu
				Intent intent = new Intent(getApplicationContext(), Menu.class);
				startActivity(intent);
				onPause();
			}
		});
		tvp.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
			@Override
			public void onSwipeTop() {
				p+=0.10;
				bird1.setPitch((float)p);
				mHandler.post(mUpdateUITimerTask);
			}

			@Override
			public void onSwipeBottom() {
				p-=0.10;
				bird1.setPitch((float)p);
				mHandler.post(mUpdateUITimerTask);
			}

			@Override
			public void onDoubleTap2() { // pause and go to menu
				Intent intent = new Intent(getApplicationContext(), Menu.class);
				startActivity(intent);
				onPause();
			}
		});

		/*
		 * These sounds are perceived from the perspective of a virtual
		 * listener. Initially the position of this listener is 0,0,0. The
		 * position and the orientation of the virtual listener can be adjusted
		 * via the SoundEnv class.
		 */
		// this.env.setListenerOrientation(20);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

	}

	@Override
	public void onResume() {
		super.onResume();
		// Log.i(TAG, "onResume()");

		/*
		 * Start playing all sources. 'true' as parameter specifies that the
		 * sounds shall be played as a loop.
		 */
		this.bird1.play(true);
		// this.bird2.play(true);
		// this.park1.play(true);
	}

	@Override
	public void onPause() {
		super.onPause();
		// Log.i(TAG, "onPause()");

		// Stop all sounds
		this.bird1.stop();
		// this.bird2.stop();
		// this.park1.stop();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Log.i(TAG, "onDestroy()");

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
			bird1.setPosition((float)x, (float)y, (float)z);
			tvx.setText("X:\n" + new DecimalFormat("#.#").format(x));
			tvy.setText("Y:\n" + new DecimalFormat("#.#").format(y));
			tvz.setText("Z:\n" + new DecimalFormat("#.#").format(z));
			tvg.setText("G:\n" + new DecimalFormat("#.#").format(g));
			tvr.setText("R:\n" + new DecimalFormat("#.#").format(r));
			tvp.setText("P:\n" + new DecimalFormat("#.#").format(p));
		}
	};
	private final Handler mHandler = new Handler();
}
