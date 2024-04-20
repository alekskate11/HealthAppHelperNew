package com.example.healthapphelper;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionManager;
import android.util.Log;

import com.example.healthapphelper.MainActivity;
import com.example.healthapphelper.R;

public class SplashScreen extends AppCompatActivity {
    private MotionLayout motionLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        motionLayout = findViewById(R.id.splashScreen);

        // Установите начальное состояние
        motionLayout.transitionToStart();

        // Прослушиватель для перехода
        motionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {
                Log.d("MotionDebug", "Transition started: " + startId + " -> " + endId);
            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {}

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                Log.d("MotionDebug", "Transition completed: " + currentId);
                if (currentId == R.id.end) { // Здесь "end" - это конечное состояние
                    // Запуск перехода на MainScreen
                    TransitionManager.beginDelayedTransition(motionLayout);
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {}
        });
    }
}