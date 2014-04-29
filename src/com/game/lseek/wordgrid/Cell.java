package com.game.lseek.wordgrid;

import java.lang.Math;


public class Cell {
    public static final char EMPTY_CELL = '.';
    public boolean isSolution;
    public boolean revealed;
    public boolean selected;
    public char value;
    public byte row, col;


    enum CmpOrder { UNDEF, LT, GT }; // Note: will never have "equals"

    // Relationship between two adjacent cells
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

        public boolean notDefined() {
            return direction.notDefined();
        }
    }


    public Cell(byte row, byte col) {
        value = EMPTY_CELL;
        revealed = false;
        this.row = row;
        this.col = col;
    }

    public Cell setVal(char c, boolean isSolution) {
        value = c;
        this.isSolution = isSolution;
        return this;
    }

    public Cell setVal(char c) {
        value = c;
        this.isSolution = false;
        return this;
    }


    public CellRelation relationTo(Cell other) {
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


    public Cell select() {
        selected = true;
        return this;
    }


    public Cell deselect() {
        selected = false;
        return this;
    }


    public Cell reset() {
        isSolution = false;
        revealed = false;
        selected = false;
        value = EMPTY_CELL;
        return this;
    }

}
