package ac.audiogames;


import ac.audiogames.Joystick.OnJoystickMoveListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class JoystickDemo extends Activity {
    private TextView angleTextView;
    private TextView powerTextView;
    private TextView directionTextView;
    private View mContentView;
    private ViewGroup container_test;
	private Joystick joystick;
	private Joystick joysticktest;
	// Importing also other views

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joystickdemo);
        container_test = (ViewGroup) findViewById(R.id.container_test);
        int sheight = getApplicationContext().getResources().getDisplayMetrics().heightPixels;
		int swidth = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
		joystick=new Joystick(getApplicationContext());
        container_test.setOnTouchListener(new OnTouchListener() {
	
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Point p = new Point();
				android.widget.RelativeLayout.LayoutParams params;
				if(event.getAction() == android.view.MotionEvent.ACTION_DOWN){
					
					p.x = (int)event.getX();
					p.y = (int)event.getY();
//					Log.d("XY", p.x + " " + p.y);
					
					params = new RelativeLayout.LayoutParams(360,360);
					params.leftMargin = p.x - 180;
					params.topMargin = p.y - 180;
						
					boolean joystick_check = container_test.findViewById(R.layout.joystickhidden) != null;
					View convertView;
					if(!joystick_check)
						container_test.removeAllViews();
					
					convertView = getLayoutInflater().inflate(R.layout.joystickhidden, container_test , false);
					container_test.addView(convertView, params);
					joystick = (Joystick) container_test.findViewById(R.id.joystick);
					joystick.xPosition = params.leftMargin;
					joystick.yPosition = params.topMargin;
					joystick.centerX = params.leftMargin;
					joystick.centerY = params.topMargin;
					
					
					 joystick.setOnJoystickMoveListener(new OnJoystickMoveListener() {

				            @Override
				            public void onValueChanged(int angle, int power, int direction) {
				             
				                angleTextView.setText(" " );
				                powerTextView.setText(" " );
				                Log.e("angle", String.valueOf(angle) + "°");
				                double angle2 = Math.toRadians(angle);
				                double yy = (double) ((Math.sin(angle2)));
				                double xx = (double) ((Math.cos(angle2)));
				                Log.e("x", String.valueOf(xx));
				                Log.e("y", String.valueOf(yy));
//				                Log.e("power", String.valueOf(power) + "%");
//				                switch (direction) {
//				                case Joystick.FRONT:
//				                    directionTextView.setText("front_lab");//R.string.front_lab
//				                    break;
//				                case Joystick.FRONT_RIGHT:
//				                    directionTextView.setText("front_right_lab");
//				                    break;
//				                case Joystick.RIGHT:
//				                    directionTextView.setText("right_lab");
//				                    break;
//				                case Joystick.RIGHT_BOTTOM:
//				                    directionTextView.setText("right_bottom_lab");
//				                    break;
//				                case Joystick.BOTTOM:
//				                    directionTextView.setText("bottom_lab");
//				                    break;
//				                case Joystick.BOTTOM_LEFT:
//				                    directionTextView.setText("bottom_left_lab");
//				                    break;
//				                case Joystick.LEFT:
//				                    directionTextView.setText("left_lab");
//				                    break;
//				                case Joystick.LEFT_FRONT:
//				                    directionTextView.setText("left_front_lab");
//				                    break;
//				                default:
//				                    directionTextView.setText("center_lab");
//				                }
				            }
				        }, Joystick.DEFAULT_LOOP_INTERVAL); 
				}
			
				
				
			if(event.getAction() == android.view.MotionEvent.ACTION_UP)
				joystick=null;
				return false;
				
				
			}
	
		});
        angleTextView = (TextView) findViewById(R.id.view_x);
        powerTextView = (TextView) findViewById(R.id.view_y);
        directionTextView = (TextView) findViewById(R.id.view_z);
        
        
        //Referencing also other views
        //joystick = (Joystick) findViewById(R.id.invisible);
        
     
//        joystick = (Joystick) findViewById(R.id.joystick);

        //Event listener that always returns the variation of the angle in degrees, motion power in percentage and direction of movement
       
    }
    
}