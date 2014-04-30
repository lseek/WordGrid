package com.game.lseek.wordgrid;

import java.util.ArrayList;
import java.util.Random;


class Grid {
    private static final String LOGTAG = "wordgrid.Grid";

    public  Cell[][] entries;
    public boolean revealed = false;
    private Random rndGen;
    public byte size;


    public Grid(byte sz) {
        entries = new Cell[sz][sz];
        rndGen = new Random();
        size = sz;

        for (byte row = 0; row < sz ; row++) {
            for (byte col = 0; col < sz; col++) {
                entries[row][col] = new Cell(row, col);
            }
        }
    }


    public void clearGrid() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                entries[row][col].reset();
            }
        }
    }


    // fill unoccupied cells with random uppercase alphabets
    public void fillOtherCells() {
        for (int row = 0; row < size ; row++) {
            for (int col = 0; col < size; col++) {
                if (entries[row][col].value == Cell.EMPTY_CELL) {
                    entries[row][col].setVal((char)('A' + rndGen.nextInt(25)));
                }
            }
        }
    }


    // Generate the grid
    public Grid generate(ArrayList<Goal> goals) {
        boolean allPlaced = false;
        for (int i = 0; i < Constants.MAX_ATTEMPTS; i++) {
            for (Goal g : goals) {
                g.clearPlacement();
            }
            if (placeAllWords(goals)) {
                allPlaced = true;
                LOG.i(LOGTAG, "Generated grid. Attempt:%d", i+1);
                break;
            }
            LOG.i(LOGTAG, "Unable to generate grid. Attempt:%d", i+1);
        }
        if (!allPlaced) {
            LOG.i(LOGTAG, "Unable to generate grid after:%d attempts. Giving up.",
                  Constants.MAX_ATTEMPTS);
            return null;
        }
        populate(goals);
        fillOtherCells();
        return this;
    }


    // Place all words in goalList. Return true if all words could be placed.
    public boolean placeAllWords(ArrayList<Goal> goalList) {
        for (Goal g : goalList) {
            if (!placeWord(g, goalList)) {
                return false;
            }
        }
        return true;
    }


    // find a place for a word w.r.t. a list of words
    public boolean placeWord(Goal goal, ArrayList<Goal> goalList) {
        if (goal.isPlaced()) {
            return true; // already placed
        }

        /*
         * 1. Get a random direction
         * 2. Get a random point (left, top). Direction + word length defines
         *    the bounding box
         * 3. If bounding box is within grid & doesn't intersect with other
         *    bounding boxes then accept placement else retry.
         */
        int left, top, right, bottom, wordLen;
        Constants.Direction d;
        wordLen = goal.word.length();

        if (wordLen > size) {
            LOG.e(LOGTAG, "%s is too big for the grid", goal.word);
            return false;
        }

        for (byte i = 0; i < Constants.MAX_ATTEMPTS; i++) {
            d = Constants.Direction.fromInt(rndGen.nextInt(4));
            left = rndGen.nextInt(size);
            top = rndGen.nextInt(size);
            // right is always > left except for VERTICAL direction.
            right = (d != Constants.Direction.VERTICAL) ? left + wordLen : left;
            // For bounding box calculation, bottom is always > top except for
            // HORIZONTAL direction
            bottom = (d != Constants.Direction.HORIZONTAL) ? top + wordLen : top;
            if ((right >= size) || (bottom >= size)) {
                continue;
            }

            boolean isClear = true;
            for (Goal g : goalList) {
                if (g.isPlaced() && g.intersects(left, top, right, bottom)) {
                    isClear = false;
                    break;
                }
            }
            if (isClear) {
                goal.setPlacement(left, top, right, bottom, d);
                return true;
            }
        }
        LOG.e(LOGTAG, "Unable to place:%s after:%s attempts", goal.word, Constants.MAX_ATTEMPTS);
        return false;
    }


    // Fill in the words
    public void populate(ArrayList<Goal> goalList) {
        int i;
        for (Goal g : goalList) {
            if (g.isPlaced()) {
                switch (g.direction) {
                case HORIZONTAL:
                    for (i = 0; i < g.word.length(); i++) {
                        entries[g.top][g.left + i].setVal(g.word.charAt(i), true);
                    }
                    break;
                case VERTICAL:
                    for (i = 0; i < g.word.length(); i++) {
                        entries[g.top + i][g.left].setVal(g.word.charAt(i), true);
                    }
                    break;
                case DIAG_UP:
                    for (i = 0; i < g.word.length(); i++) {
                        entries[g.bottom -i][g.left + i].setVal(g.word.charAt(i), true);
                    }
                    break;
                case DIAG_DOWN:
                    for (i = 0; i < g.word.length(); i++) {
                        entries[g.top + i][g.left + i].setVal(g.word.charAt(i), true);
                    }
                    break;
                case UNDEFINED:
                    LOG.e(LOGTAG, "Word:%s does not have a position in the grid", g.word);
                    return;
                }
            }
        }
    }


    public void printSolution() {
        Cell c;
        for (int row = 0; row < size ; row++) {
            StringBuilder line = new StringBuilder(size);
            for (int col = 0; col < size; col++) {
                c = entries[row][col];
                line.append(c.isSolution ? c.value : Cell.EMPTY_CELL);
            }
            LOG.d(LOGTAG, line.toString());
        }
    }


    public void print() {
        for (int row = 0; row < size ; row++) {
            StringBuilder line = new StringBuilder(size);
            for (int col = 0; col < size; col++) {
                line.append(entries[row][col].value);
            }
            LOG.d(LOGTAG, line.toString());
        }
    }


    public void reveal() {
        Cell c;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                c = entries[row][col];
                if (c.isSolution) {
                    c.revealed = true;
                }
            }
        }
        revealed = true;
    }
}
