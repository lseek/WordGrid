package com.game.lseek.wordgrid;

import android.app.Activity;
import android.os.Bundle;

import android.view.Gravity;
import android.widget.*;
import android.graphics.Color;
import java.util.ArrayList;
import java.util.Random;
import android.content.res.Resources;
import android.view.LayoutInflater;


public class WordGrid extends Activity
{
    int             gridSize = 10;
    TableLayout     gridArea;
    LinearLayout    clueArea;
    Resources       res;
    String[]        words;

    private void createClueArea() {
        LayoutInflater inflater = getLayoutInflater();
        Button b;

        for (String s : words) {
            b = (Button)inflater.inflate(R.layout.clue_button_view, clueArea, false);
            b.setText(s);
            clueArea.addView(b);
        }
    }

    private TextView mkCell(TableRow row) {
        LayoutInflater inflater = getLayoutInflater();
        TextView t;
        char c;
        Random rnd = new Random();

        c = (char)('A' + rnd.nextInt(25));
        t = (TextView)inflater.inflate(R.layout.grid_text_view, row, false);
        t.setText(Character.toString(c));
        return t;
    }

    private void createWordGrid() {
        TableRow r;
        TableRow.LayoutParams cellParams;
        TableLayout.LayoutParams rowParams;
        int i, j;

        cellParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        cellParams.weight = 1;
        rowParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        rowParams.weight = 1;

        for (i = 0; i < gridSize; i++) {
            r = new TableRow(this);
            r.setGravity(Gravity.CENTER);
            for (j = 0; j < gridSize; j++) {
                r.addView(mkCell(r), cellParams);
            }
            gridArea.addView(r, rowParams);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        res = getResources();

        gridArea = (TableLayout)findViewById(R.id.gridArea);
        clueArea = (LinearLayout)findViewById(R.id.clueArea);
        words = res.getStringArray(R.array.lorem);

        createWordGrid();
        createClueArea();
    }
}
