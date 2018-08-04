package com.klock.torchlight;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;

public class LightActivity extends AppCompatActivity {

    private GridLayout glRoot;
    private int        childHeight;
    private int        childWidth;
    private int        currentIndex;
    private boolean    isPressMode;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_light);
        //        setWindowBrightness(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
        init();
    }

    private void init () {
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.black));
        glRoot = findViewById(R.id.gl_root);
        for (int i = 0; i < glRoot.getColumnCount() * glRoot.getRowCount(); i++) {
            View v = new View(this);
            v.setBackgroundColor(getResources().getColor(R.color.white));
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.rowSpec = GridLayout.spec(i / glRoot.getColumnCount(), 1f);
            layoutParams.columnSpec = GridLayout.spec(i % glRoot.getColumnCount(), 1f);
            layoutParams.height = 0;
            layoutParams.width = 0;
            glRoot.addView(v, layoutParams);
        }
        glRoot.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout () {
                        if (childHeight * childWidth == 0) {
                            childHeight = glRoot.getChildAt(0).getHeight();
                            childWidth = glRoot.getChildAt(0).getWidth();
                        }
                    }
                });
        currentIndex = -1;
        isPressMode = false;
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        //        setWindowBrightness(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE);
    }

    private void setWindowBrightness (float brightness) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness;
        window.setAttributes(lp);
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        if (glRoot.getVisibility() == View.VISIBLE) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    switchLight(event);
                    return true;
            }
        }
        return super.onTouchEvent(event);
    }

    private void switchLight (MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int xIndex = x / childWidth;
        int yIndex = y / childHeight;
        int childIndex = glRoot.getColumnCount() * yIndex + xIndex;
        if (currentIndex != childIndex) {
            currentIndex = childIndex;
            View childView = glRoot.getChildAt(currentIndex);
            if (childView != null) {
                childView.setVisibility(childView.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                isPressMode = true;
                glRoot.setVisibility(View.VISIBLE);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp (int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                isPressMode = !isPressMode;
                glRoot.setVisibility(isPressMode ? View.GONE : View.VISIBLE);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (isPressMode) {
                    glRoot.setVisibility(View.GONE);
                }
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}