package com.game.lseek.wordgrid;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.grid_activity_menu, menu);
        controller.notifyMenuReady();
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (controller.isFirstRound()) {
            LOG.d(LOGTAG, "Disabling prevRound menu");
            menu.findItem(R.id.prevRound).setEnabled(false);
        } else {
            menu.findItem(R.id.prevRound).setEnabled(true);
        }
        if (controller.isLastRound()) {
            LOG.d(LOGTAG, "Disabling nextRound menu");
            menu.findItem(R.id.nextRound).setEnabled(false);
        } else {
            menu.findItem(R.id.nextRound).setEnabled(true);
        }

        super.onPrepareOptionsMenu(menu);
        return true;
    }


    public void doReveal(MenuItem item) {
        controller.reveal();
    }


    public void doPrevRound(MenuItem item) {
        controller.skipToPrevRound();
    }


    public void doNextRound(MenuItem item) {
        controller.skipToNextRound();
    }
}
