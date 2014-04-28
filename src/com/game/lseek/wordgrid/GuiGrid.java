package com.game.lseek.wordgrid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.lang.Math;
import java.util.ArrayList;
import java.util.LinkedList;


enum CmpOrder { UNDEF, LT, GT }; // Note: will never have "equals"

// Relationship between two cells
class CellRelation {
    public Constants.Direction direction;
    public CmpOrder order;


    public CellRelation() {
        direction = Constants.Direction.UNDEFINED;
        order = CmpOrder.UNDEF;
    }

    public CellRelation(Constants.Direction d, CmpOrder o) {
        direction = d;
        order = o;
    }

    public boolean isUndefined() {
        return direction.isUndefined();
    }
}


class GuiCell {
    public Grid.Cell modelCell;
    private Context context;
    public byte row, col;
    public TextView cellGui;


    public GuiCell(Context context, LayoutInflater inflater, ViewGroup parent,
                   byte row, byte col, int id) {
        cellGui = (TextView)inflater.inflate(R.layout.grid_text_view, parent, false);
        this.row = row;
        this.col = col;
        this.context = context;

        cellGui.setClickable(true);
        cellGui.setId(id);
        clearStyles();
    }


    public GuiCell setSource(Grid.Cell src) {
        modelCell = src;
        cellGui.setText(String.valueOf(src.value));
        cellGui.setClickable(true);
        clearStyles();
        return this;
    }


    public CellRelation relationTo(GuiCell other) {
        byte dRow, dCol;
        CellRelation result = new CellRelation();

        dRow = (byte)(other.row - row);
        dCol = (byte)(other.col - col);
        if ((Math.abs(dRow) > 1) || (Math.abs(dCol) > 1)) {
            return new CellRelation();
        }

        if (dCol == 0) { // columns don't change => vertical
            return new CellRelation(Constants.Direction.VERTICAL,
                                    (dRow > 0) ? CmpOrder.GT : CmpOrder.LT);
        } else {
            // for non vertical, order is always based on colums
            CmpOrder order = (dCol > 0) ? CmpOrder.GT : CmpOrder.LT;
        
            if (dRow == 0) { // horizontal
                return new CellRelation(Constants.Direction.HORIZONTAL, order);
            } else if ((dCol * dRow) < 0) {
                return new CellRelation(Constants.Direction.DIAG_UP, order);
            } else {
                return new CellRelation(Constants.Direction.DIAG_DOWN, order);
            }
        }
    }


    public GuiCell clearStyles() {
        cellGui.setTextAppearance(context, R.style.normalText);
        cellGui.setBackgroundResource(R.color.normalCellBg);
        return this;
    }


    public GuiCell select() {
        cellGui.setTextAppearance(context, R.style.selectedText);
        cellGui.setBackgroundResource(R.color.selectedCellBg);
        return this;
    }


    public GuiCell reveal() {
        cellGui.setTextAppearance(context, R.style.revealedText);
        cellGui.setBackgroundResource(R.color.revealedCellBg);
        modelCell.revealed = true;
        cellGui.setClickable(false);
        return this;
    }
}


/*
 * A chain of cells that must be in one line in the grid.
 *
 * The chain is always sorted in left->right column order except for when the
 * direction is vertical in which case it is sorted from top to bottom.
 */
class GuiCellList {
    private static final String LOGTAG = "wordgrid.GuiCellList";

    private Constants.Direction direction = Constants.Direction.UNDEFINED;
    private LinkedList<GuiCell> chain;


    public GuiCellList() {
        chain = new LinkedList<GuiCell>();
    }


    private void newSequence(GuiCell newCell) {
        LOG.d(LOGTAG, "Starting new chain from:(%d, %d)", newCell.row, newCell.col);
        for (GuiCell c : chain) {
            c.clearStyles();
        }
        direction = Constants.Direction.UNDEFINED;
        chain.clear();
        chain.add(newCell.select());
    }


    public void add(GuiCell newCell) {
        if (chain.size() == 0) {
            LOG.d(LOGTAG, "Adding first member:(%d, %d) to chain", newCell.row, newCell.col);
            chain.add(newCell.select());
            return;
        }

        if (chain.contains(newCell)) {
            LOG.d(LOGTAG, "newCell:(%d, %d) already in chain", newCell.row, newCell.col);
            return;
        }

        GuiCell head = chain.getFirst();
        GuiCell tail = chain.getLast();
        CellRelation hRelation = head.relationTo(newCell);
        CellRelation tRelation = tail.relationTo(newCell);

        if (hRelation.isUndefined() && tRelation.isUndefined()) {
            // not adjacent to head or tail - new sequence
            LOG.d(LOGTAG, "(%d, %d): not connected to head or tail", newCell.row, newCell.col);
            newSequence(newCell);
            return;
        }

        // adjacent to head or tail
        if (!hRelation.isUndefined()) {
            if (!direction.isUndefined()) {
                if (hRelation.direction != direction) {
                    // current direction broken - new sequence.
                    LOG.d(LOGTAG, "(%d, %d): direction:%s broken at head", newCell.row, newCell.col, direction);
                    newSequence(newCell);
                } else {
                    // must be "before" the head (else it would have already been selected).
                    LOG.d(LOGTAG, "Adding:(%d, %d) to head", newCell.row, newCell.col);
                    chain.addFirst(newCell.select());
                }
            } else {
                // existing chain contains only one element
                if (hRelation.order == CmpOrder.LT) {
                    LOG.d(LOGTAG, "Adding:(%d, %d) to head, defining dir:%s", newCell.row, newCell.col, hRelation.direction);
                    chain.addFirst(newCell.select());
                } else {
                    LOG.d(LOGTAG, "Adding:(%d, %d) to tail, defining dir:%s", newCell.row, newCell.col, hRelation.direction);
                    chain.addLast(newCell.select());
                }
                direction = hRelation.direction;
            }
        } else {
            // adjacent to tail && chain.size() > 1 (else would have been handled above).
            if (tRelation.direction != direction) {
                LOG.d(LOGTAG, "(%d, %d): direction:%s broken at tail", newCell.row, newCell.col, direction);
                newSequence(newCell);
            } else {
                // must be "after" the tail (else it would have already been selected).
                LOG.d(LOGTAG, "Adding:(%d, %d) to tail", newCell.row, newCell.col);
                chain.addLast(newCell.select());
            }
        }
    }


    public void reveal() {
        for (GuiCell cell : chain) {
            cell.reveal();
        }
        chain.clear();
        direction = Constants.Direction.UNDEFINED;
    }


    public void clear() {
        for (GuiCell cell : chain) {
            cell.clearStyles();
        }
        chain.clear();
        direction = Constants.Direction.UNDEFINED;
    }


    public String toString() {
        StringBuilder line = new StringBuilder(chain.size());
        for (GuiCell c : chain) {
            line.append(c.modelCell.value);
        }
        return line.toString();
    }
}


public class GuiGrid {
    private static final String LOGTAG = "wordgrid.GuiGrid";

    private Context context;
    private TableLayout gridArea;
    private GuiCell[][] txtCells;
    private LinearLayout clueArea;
    private Grid grid;
    private LayoutInflater inflater;
    private WordGridApp app;
    private GuiCellList currChain;
    private byte nLeft;
    private int currRound;
    private ArrayList<Goal> currGoals;



    private int mkId(byte row, byte col) {
        return (int)((row << 8) | col);
    }


    private byte getRow(int widgetId) {
        return (byte)((widgetId & 0xff00) >> 8);
    }


    private byte getCol(int widgetId) {
        return (byte)(widgetId & 0x00ff);
    }

    private void createClueArea(ArrayList<Goal> goals) {
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


    private void createWordGrid(Grid g) {
        byte row, col;

        for (row = 0; row < g.size; row++) {
            for (col = 0; col < g.size; col++) {
                txtCells[row][col].setSource(g.entries[row][col]);
            }
        }
    }


    public void select(View cell) {
        int id = cell.getId();
        currChain.add(txtCells[getRow(id)][getCol(id)]);
    }


    public void roundCompleteDialog() {
        if (currRound < app.currGame.rounds.size()-1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Congratulations")
                .setMessage("Congratulations!")
                .setPositiveButton(R.string.next_round,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                currRound++;
                                initRound();
                            }
                        })
            .setNegativeButton(R.string.main_menu,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ((Activity)context).finish();
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
    }


    public void mkEmptyGrid() {
        TableRow tableRow;
        GuiCell uiCell;
        TableRow.LayoutParams cellParams;
        TableLayout.LayoutParams rowParams;
        byte row, col;

        txtCells = new GuiCell[app.gridSize][app.gridSize];
        cellParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        cellParams.weight = 1;
        rowParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        rowParams.weight = 1;

        for (row = 0; row < app.gridSize; row++) {
            tableRow = new TableRow(context);
            tableRow.setGravity(Gravity.CENTER);
            for (col = 0; col < app.gridSize; col++) {
                uiCell = new GuiCell(context, inflater, tableRow, row, col, mkId(row, col));
                tableRow.addView(uiCell.cellGui, cellParams);
                txtCells[row][col] = uiCell;
            }
            gridArea.addView(tableRow, rowParams);
        }
    }


    public void initRound() {
        currGoals = app.currGame.rounds.get(currRound).filter(app.level);
        Grid currGrid = new Grid(app.gridSize);
        currGrid.generate(currGoals);
        currGrid.printSolution();
        currGrid.print();
        createWordGrid(currGrid);
        LOG.d(LOGTAG, "Created word grid");
        createClueArea(currGoals);
        LOG.d(LOGTAG, "Created clue area");
        nLeft = (byte)currGoals.size();
        currChain.clear();
    }


    public GuiGrid(Context context, TableLayout gridArea,
                   LinearLayout clueArea, LayoutInflater inflater,
                   WordGridApp app) {
        LOG.d(LOGTAG, "Creating GuiGrid");
        this.gridArea = gridArea;
        this.clueArea = clueArea;
        this.inflater = inflater;
        this.app = app;
        this.context = context;

        currRound = 0;
        currChain = new GuiCellList();
        mkEmptyGrid();
        initRound();
    }
}
