package com.example.shawnocked.cs450project3;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by shawnocked on 9/29/16.
 */

public class GameActivity extends AppCompatActivity {

    public static final String KEY_RESTORE = "key_restore";
    public static final String PREF_RESTORE = "pref_restore";
    private Handler mHandler;
    private GameFragment gameFragment;
    private ControlFragment controlFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);setContentView(R.layout.activity_game);

        mHandler = new Handler();
        gameFragment = (GameFragment) getFragmentManager().findFragmentById(R.id.fragment_game);
        controlFragment = (ControlFragment)getFragmentManager().findFragmentById(R.id.control_fragment);

        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
        if (restore) {
            String gameData = getPreferences(MODE_PRIVATE).getString(PREF_RESTORE, null);
            if (gameData != null) {
                gameFragment.putState(gameData);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        String gameData = gameFragment.getState();
        getPreferences(MODE_PRIVATE).edit().putString(PREF_RESTORE, gameData).apply();
    }

    public void checkMatchBar(){
        View view = findViewById(R.id.checkmatch);
        view.setVisibility(View.VISIBLE);
    }

    public void stopChecking(){
        View view = findViewById(R.id.checkmatch);
        view.setVisibility(View.GONE);
    }

    public void restartGame(){ gameFragment.restart(); }

    public void update(final int number){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                gameFragment.count.setText(Integer.toString(number));
            }
        });
    }

}

