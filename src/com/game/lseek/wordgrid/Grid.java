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

        public Cell() {
            value = EMPTY_CELL;
            revealed = false;
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


    // find a place for a word
    public void placeWord(Goal goal, Map<String, Goal> goalList) {
        if (goal.isPlaced()) {
            return; // already placed
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
                goal.setPos(left, top, right, bottom, d);
                return;
            }
        }
        LOG.e(LOGTAG, "Unable to place:%s after:%s attempts", goal.word, Constants.MAX_ATTEMPTS);
    }


    // fill unoccupied cells with random uppercase alphabets
    public void generate() {
        for (int row = 0; row < size ; row++) {
            for (int col = 0; col < size; col++) {
                if (entries[row][col].value == EMPTY_CELL) {
                    entries[row][col].value = (char)('A' + rndGen.nextInt(25));
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
                        entries[g.top][g.left + i].value = g.word.charAt(i);
                    }
                    break;
                case VERTICAL:
                    for (i = 0; i < g.word.length(); i++) {
                        entries[g.top + i][g.left].value = g.word.charAt(i);
                    }
                    break;
                case DIAG_UP:
                    for (i = 0; i < g.word.length(); i++) {
                        entries[g.bottom -i][g.left + i].value = g.word.charAt(i);
                    }
                    break;
                case DIAG_DOWN:
                    for (i = 0; i < g.word.length(); i++) {
                        entries[g.top + i][g.left + i].value = g.word.charAt(i);
                    }
                    break;
                case UNDEFINED:
                    LOG.e(LOGTAG, "Word:%s does not have a position in the grid", g.word);
                    return;
                }
            }
        }
    }


    public void print() {
        StringBuilder line = new StringBuilder(size);
        for (int row = 0; row < size ; row++) {
            for (int col = 0; col < size; col++) {
                line.append(entries[row][col].value);
            }
            LOG.d(LOGTAG, "%s", line);
        }
    }
}
