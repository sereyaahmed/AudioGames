package ac.audiogames;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Detects left and right swipes across a view.
 */
public class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;

    public OnSwipeTouchListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void onSwipeLeft() {
    }

    public void onSwipeRight() {
    }
    public void onSwipeTop() {
    }
    public void onSwipeBottom() {
    }
    public void onSingleTap(){
    }
    public void onDoubleTap2(){
    }
    public void longPress(){
    }
    public boolean onTouch(View v, MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) return true;
        else { return false;}
//        return true;
    }
    public void onClick(){

    }
    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {

        //    onClick();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if(Math.abs(distanceX)+Math.abs(distanceY) < SWIPE_DISTANCE_THRESHOLD) onClick();
            else if (distanceX > 0 && Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD){
                onSwipeRight(); return true;}
            else if (distanceX < 0 && Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD){
                onSwipeLeft();return true;}
            else if (distanceY < 0 && Math.abs(distanceY) > Math.abs(distanceX) && Math.abs(distanceY) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD){
                onSwipeTop();return true;}
            else if(distanceY > 0 && Math.abs(distanceY) > Math.abs(distanceX) && Math.abs(distanceY) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD){
                onSwipeBottom();return true;}
            //  else {onClick();
            return false;
        }
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e1){
            onSingleTap();return true;
        }
        @Override
        public boolean onDoubleTap(MotionEvent e1){
            onDoubleTap2();return true;
        }
        @Override
        public void onLongPress(MotionEvent e1){
            longPress();
        }

    }
}