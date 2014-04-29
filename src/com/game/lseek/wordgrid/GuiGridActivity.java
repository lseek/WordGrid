package com.game.lseek.wordgrid;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;


public class GuiGridActivity extends Activity
{
    private static final String LOGTAG = "wordgrid.guiGridActivity";
    private GridController controller;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_activity);

        LayoutInflater inflater = getLayoutInflater();
        WordGridApp app = (WordGridApp)getApplication();

        GridView gridArea = (GridView)findViewById(R.id.gridArea);
        LinearLayout clueArea = (LinearLayout)findViewById(R.id.clueArea);

        controller = new GridController(this, inflater, app, gridArea, clueArea);
    }

    public void cellClicked(View txtCell) {
        controller.select(txtCell);
    }

    public void clueClicked(View clueBtn) {
        controller.checkSelection(clueBtn);
    }
}
