package com.example.xm.widget;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.example.xm.bean.StaticVar;
import com.example.xm.fragment.MachineStatusFragment;

/**
 * Created by liuwei on 2016/11/19.
 */
public class LocationView extends ImageView {
    int touchMode;
    final static int TAP=0;
    final static int SWIPE=1;
    float start_X;
    float start_Y;
    float stop_X;
    float stop_Y;

    public LocationView(Context context) {
        super(context);
    }

    public LocationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(widthSize, widthSize * 3 / 5);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                start_X = event.getX();
                start_Y = event.getY();
                touchMode = TAP;

                return true;
            case MotionEvent.ACTION_MOVE:
                touchMode = SWIPE;
                break;
            case MotionEvent.ACTION_UP:
                stop_X = event.getX();
                stop_Y = event.getY();
                getTouchParams();
                break;
        }

        return super.onTouchEvent(event);
    }


    public void getTouchParams(){
        StringBuffer sb = new StringBuffer();
        if(touchMode==TAP){
            sb.append("input tap ");
            sb.append((start_X / getWidth()) * 800 + " ");
            sb.append((start_Y / getHeight()) * 480);
        }
        if(touchMode==SWIPE){
            sb.append("input swipe ");
            sb.append((start_X/getWidth())*800+" ");
            sb.append((start_Y / getHeight()) * 480+" ");
            sb.append((stop_X/getWidth())*800+" ");
            sb.append((stop_Y / getHeight()) * 480+" ");
        }
        Message.obtain(MachineStatusFragment.handler, StaticVar.SEND_MESSAGE,sb.toString()).sendToTarget();
        System.out.println(sb.toString());
    }
}
