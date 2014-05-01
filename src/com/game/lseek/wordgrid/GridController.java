package com.game.lseek.wordgrid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import java.lang.Integer;
import java.lang.Math;
import java.util.ArrayList;

import static com.game.lseek.wordgrid.Constants.*;
import com.game.lseek.wordgrid.Constants.HeaderType;

public class GridController {
    private static final String LOGTAG = "wordgrid.GuiGrid";

    private GridAdapter adapter;
    private WordGridApp app;
    private LinearLayout clueArea;
    private Context context;
    private CellLine currChain;
    private ArrayList<Goal> currGoals;
    private int currRound;
    private Grid grid;
    private GridView gridArea;
    private byte gridSize;
    private LayoutInflater inflater;
    private boolean menuReady = false;
    private byte nLeft;
    private TextView roundTitleView;
    private boolean waitingForUserResponse = false;


    public GridController(Context context, LayoutInflater inflater,
                          WordGridApp app, GridView gridArea,
                          TextView roundTitleView, LinearLayout clueArea) {
        LOG.d(LOGTAG, "Creating GridController");
        this.context = context;
        this.inflater = inflater;
        this.app = app;
        this.gridArea = gridArea;
        this.clueArea = clueArea;
        this.roundTitleView = roundTitleView;

        gridSize = app.currGame.gameInfo.containsKey(HeaderType.SIZE) ?
                   (byte)Integer.parseInt(app.currGame.gameInfo.get(HeaderType.SIZE)) :
                   app.currGame.calculatedSz;
        gridSize = (byte)Math.min(gridSize, MAX_GRID_SIZE);
        gridSize = (byte)Math.max(gridSize, MIN_GRID_SIZE);
        LOG.d(LOGTAG, "Using grid size:%d", gridSize);
        grid = new Grid(gridSize);
        currRound = 0;
        currChain = new CellLine();
        adapter = new GridAdapter(inflater, context);
        gridArea.setAdapter(adapter);
        gridArea.setNumColumns(gridSize);
        initRound(0);
    }


    public void checkSelection(View btn) {
        Button b = (Button)btn;
        String selection = currChain.toString();
        Goal goal = currGoals.get(b.getId());
        if (selection.equals(goal.word)) {
            LOG.d(LOGTAG, "MATCH:%s:%s", goal.word, goal.clue);
            currChain.reveal();
            markBtnSolved(b);
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


    public boolean isFirstRound() {
        return currRound == 0;
    }


    public boolean isLastRound() {
        return  currRound == (app.currGame.rounds.size() - 1);
    }


    public GridController notifyMenuReady() {
        menuReady = true;
        return this;
    }


    public void reveal() {
        grid.reveal();
        currChain.clear();
        for (int i = 0; i < currGoals.size(); i++) {
            markBtnSolved((Button)clueArea.getChildAt(i));
        }
        nLeft = 0;
        adapter.notifyDataSetChanged();
    }


    public void select(View cell) {
        int id = ((Integer)cell.getTag()).intValue();
        byte row = adapter.rowFromId(id);
        byte col = adapter.colFromId(id);
        LOG.d(LOGTAG, "Cell:id:%x:(%d, %d) selected", id, row, col);
        currChain.add(grid.entries[row][col]);
        adapter.notifyDataSetChanged();
    }


    public void skipToNextRound() {
        // TODO: Add confirmation
        currRound++;
        LOG.d(LOGTAG, "Skipping to next round:%s", currRound);
        initRound(currRound);
    }


    public void skipToPrevRound() {
        // TODO: Add confirmation
        currRound--;
        LOG.d(LOGTAG, "Skipping to previous round:%s", currRound);
        initRound(currRound);
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


    private void gameCompleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.congratsTitle)
            .setMessage(R.string.gameCompleteMsg)
            .setPositiveButton(R.string.toMainMenu,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((Activity)context).finish();
                    }
                });
        builder.show();
    }


    private void gridBuildFailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        Resources res = context.getResources();

        LOG.d(LOGTAG, "Failed to build grid for round:%d", currRound);
        waitingForUserResponse = true;
        builder.setTitle(R.string.error)
            .setMessage(res.getString(R.string.gridBuildFail, currRound))
            .setPositiveButton(R.string.yesChoice,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // keep retrying
                        LOG.d(LOGTAG, "User chose to retry rebuilding grid");
                        waitingForUserResponse = false;
                        initRound(currRound);
                    }
                })
            .setNegativeButton(R.string.noChoice,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // give up
                        ((Activity)context).finish();
                    }
                });
        builder.show();
    }


    private void initRound(int roundNum) {
        currGoals = app.currGame.rounds.get(roundNum).goalList;
        grid.clearGrid();
        if (grid.generate(currGoals) == null) {
            gridBuildFailDialog();
        } else {
            grid.printSolution();

            roundTitleView.setText(app.currGame.rounds.get(roundNum).roundTitle);
            adapter.setSrc(grid);
            adapter.notifyDataSetChanged();
            LOG.d(LOGTAG, "Created word grid");
            buildClueArea(currGoals);
            LOG.d(LOGTAG, "Created clue area");
            nLeft = (byte)currGoals.size();
            currChain.clear();
        }
        if ((isFirstRound() || isLastRound()) && menuReady) {
            ((Activity)context).invalidateOptionsMenu();
        }
    }


    private void markBtnSolved(Button btn) {
        btn.setClickable(false);
        btn.setTextAppearance(context, R.style.revealedText);
        btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_check_on, 0, 0, 0);
    }


    private void roundCompleteDialog() {
        if (currRound < app.currGame.rounds.size()-1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.congratsTitle)
                .setMessage(R.string.roundCompleteMsg)
                .setNegativeButton(R.string.toMainMenu,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ((Activity)context).finish();
                        }
                    })
                .setPositiveButton(R.string.continueToNextRound,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            currRound++;
                            initRound(currRound);
                        }
                    });
            builder.show();
        } else {
            gameCompleteDialog();
        }
    }
}
