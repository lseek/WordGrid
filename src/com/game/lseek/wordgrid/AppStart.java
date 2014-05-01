package com.game.lseek.wordgrid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class AppStart extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void startGame(View btn) {
        WordGridApp app = (WordGridApp)getApplication();
        app.loadGame(null);

        Intent showGridIntent = new Intent(this, GuiGridActivity.class);
        startActivity(showGridIntent);
    }
}
