package utiles;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class BehaviorCuston<V extends View> extends BottomSheetBehavior<V> {


    private boolean mLocked = false;
    public BehaviorCuston(){
        super();
    }
    public BehaviorCuston(Context context, AttributeSet attrs){
        super(context,attrs);
    }
    public void setLocked(boolean locked) {
        mLocked = locked;
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        boolean handled = false;

        if (!mLocked) {
            handled = super.onInterceptTouchEvent(parent, child, event);
        }

        return handled;
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        boolean handled = false;

        if (!mLocked) {
            if(child.getId()==0){

            }
            handled = super.onTouchEvent(parent, child, event);
        }

        return handled;
    }


}
