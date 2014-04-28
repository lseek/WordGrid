package com.game.lseek.wordgrid;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;


public class GuiGridActivity extends Activity
{
    private static final String LOGTAG = "wordgrid.guiGridActivity";
    private GuiGrid grid;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_activity);

        TableLayout gridArea = (TableLayout)findViewById(R.id.gridArea);
        LinearLayout clueArea = (LinearLayout)findViewById(R.id.clueArea);
        LayoutInflater inflater = getLayoutInflater();
        WordGridApp app = (WordGridApp)getApplication();
        grid = new GuiGrid(this, gridArea, clueArea, inflater, app);
    }

    public void cellClicked(View txtCell) {
        grid.select(txtCell);
    }

    public void clueClicked(View clueBtn) {
        grid.checkSelection(clueBtn);
    }
}
