package com.game.lseek.wordgrid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import java.lang.Integer;
import java.util.ArrayList;


public class GridController {
    private static final String LOGTAG = "wordgrid.GuiGrid";

    private Context context;
    private LayoutInflater inflater;
    private WordGridApp app;
    private GridView gridArea;
    private LinearLayout clueArea;
    private Grid grid;
    private GridAdapter adapter;
    private CellLine currChain;
    private byte nLeft;
    private int currRound;
    private ArrayList<Goal> currGoals;


    public GridController(Context context, LayoutInflater inflater,
                          WordGridApp app, GridView gridArea,
                          LinearLayout clueArea) {
        LOG.d(LOGTAG, "Creating GridController");
        this.context = context;
        this.inflater = inflater;
        this.app = app;
        this.gridArea = gridArea;
        this.clueArea = clueArea;

        grid = new Grid(app.gridSize);
        currRound = 0;
        currChain = new CellLine();
        adapter = new GridAdapter(inflater, context);
        gridArea.setAdapter(adapter);
        gridArea.setNumColumns(app.gridSize);
        initRound();
    }


    private void buildClueArea(ArrayList<Goal> goals) {
        Button b;
        Goal g;

        clueArea.removeAllViews();
        for (int i = 0; i < goals.size(); i++) {
            g = goals.get(i);
            b = (Button)inflater.inflate(R.layout.clue_button_view, clueArea, false);
            b.setText(g.clue.equals("") ? g.word : g.clue);
            b.setId(i);
            clueArea.addView(b);
        }
    }


    public void select(View cell) {
        int id = ((Integer)cell.getTag()).intValue();
        byte row = adapter.rowFromId(id);
        byte col = adapter.colFromId(id);
        LOG.d(LOGTAG, "Cell:id:%x:(%d, %d) selected", id, row, col);
        currChain.add(grid.entries[row][col]);
        adapter.notifyDataSetChanged();
    }


    public void gameCompleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.congrats_title)
            .setMessage(R.string.game_complete_msg)
            .setPositiveButton(R.string.main_menu,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((Activity)context).finish();
                    }
                });
        builder.show();
    }


    public void roundCompleteDialog() {
        if (currRound < app.currGame.rounds.size()-1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.congrats_title)
                .setMessage(R.string.round_complete_msg)
                .setNegativeButton(R.string.main_menu,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ((Activity)context).finish();
                        }
                    })
                .setPositiveButton(R.string.next_round,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            currRound++;
                            initRound();
                        }
                    });
            builder.show();
        } else {
            ((Activity)context).finish();
        }
    }


    public void checkSelection(View btn) {
        Button b = (Button)btn;
        String selection = currChain.toString();
        Goal goal = currGoals.get(b.getId());
        if (selection.equals(goal.word)) {
            LOG.d(LOGTAG, "MATCH:%s:%s", goal.word, goal.clue);
            currChain.reveal();
            b.setClickable(false);
            b.setTextAppearance(context, R.style.revealedText);
            b.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_check_on, 0, 0, 0);
            nLeft--;
            if (nLeft == 0) {
                roundCompleteDialog();
            }
        } else {
            LOG.d(LOGTAG, "%s: DOESN'T MATCH:%s:%s", selection, goal.word, goal.clue);
            currChain.clear();
        }
        adapter.notifyDataSetChanged();
    }


    public void initRound() {
        currGoals = app.currGame.rounds.get(currRound).filter(app.level);
        grid.clearGrid();
        grid.generate(currGoals);
        grid.printSolution();

        adapter.setSrc(grid);
        adapter.notifyDataSetChanged();
        LOG.d(LOGTAG, "Created word grid");
        buildClueArea(currGoals);
        LOG.d(LOGTAG, "Created clue area");
        nLeft = (byte)currGoals.size();
        currChain.clear();
    }
}
