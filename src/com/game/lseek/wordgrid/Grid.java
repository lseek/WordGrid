package com.game.lseek.wordgrid;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


class Grid {
    private static final String LOGTAG = "wordgrid.Grid";
    private static final char EMPTY_CELL = '.';

    private Cell[][] entries;
    private Random rndGen;
    private byte size;

    class Cell {
        public boolean revealed;
        public char value;
        public boolean isSolution;

        public Cell() {
            value = EMPTY_CELL;
            revealed = false;
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
    }


    public Grid(byte sz) {
        entries = new Cell[sz][sz];
        rndGen = new Random();
        size = sz;

        for (int row = 0; row < sz ; row++) {
            for (int col = 0; col < sz; col++) {
                entries[row][col] = new Cell();
            }
        }
    }


    // find a place for a word w.r.t. a list of words
    public boolean placeWord(Goal goal, Map<String, Goal> goalList) {
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
            for (Goal g : goalList.values()) {
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


    // Place all words in goalList. Return true if all words could be placed.
    public boolean placeAllWords(Map<String, Goal> goalList) {
        for (Goal g : goalList.values()) {
            if (!placeWord(g, goalList)) {
                return false;
            }
        }
        return true;
    }


    // Generate the grid
    public Grid generate(Map<String, Goal> goals) {
        boolean allPlaced = false;
        for (int i = 0; i < Constants.MAX_ATTEMPTS; i++) {
            for (Goal g : goals.values()) {
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


    // fill unoccupied cells with random uppercase alphabets
    public void fillOtherCells() {
        for (int row = 0; row < size ; row++) {
            for (int col = 0; col < size; col++) {
                if (entries[row][col].value == EMPTY_CELL) {
                    entries[row][col].setVal((char)('A' + rndGen.nextInt(25)));
                }
            }
        }
    }

    // Fill in the words
    public void populate(Map<String, Goal> goalList) {
        int i;
        for (Goal g : goalList.values()) {
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
                line.append(c.isSolution ? c.value : EMPTY_CELL);
            }
            LOG.d(LOGTAG, line.toString());
        }
    }


    public void print() {
        StringBuilder line = new StringBuilder(size);
        for (int row = 0; row < size ; row++) {
            for (int col = 0; col < size; col++) {
                line.append(entries[row][col].value);
            }
            LOG.d(LOGTAG, line.toString());
        }
    }
}
