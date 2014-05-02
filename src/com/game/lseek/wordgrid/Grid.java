package com.game.lseek.wordgrid;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import static com.game.lseek.wordgrid.Constants.*;


class Grid {
    private static final String LOGTAG = "wordgrid.Grid";

    private Level gameLevel;
    private Random rndGen;

    public  Cell[][] entries;
    public boolean revealed = false;
    public byte size;

    class WeightInfo {
        public Direction d;
        public int from, to;

        public WeightInfo(Direction d, int from, int to) {
            this.d = d;
            this.from = from;
            this.to = to;
        }


        public boolean contains(int i) {
            return (from <= i) && (i <= to);
        }
    }


    class DirectionGenerator {
        private static final int AMPLIFY_FACTOR = 1000;

        private Random rndGen;
        private WeightInfo[] intervals;
        private int totalWeight;

        /*
         * The basic logic is as follows:
         *
         * Add up the weights and multiply everything by a factor of 1000
         * (arbitrarily chosen) to make up a "weight space". Within this space each
         * direction occupies some number of points (its interval) as determined by
         * its weight - the higher the weight, the larger the interval it occupies.
         * Therefore, a random number within the "weight space" is more likely to
         * pick one of these points than a direction with less weight.
         *
         * An example: Assume HORIZ:4 VERT:3 DIAG_DOWN:2 DIAG:UP:1
         *
         * "ampflified" weights: HORIZ:4000 VERT:3000 DIAG_DOWN:2000 DIAG:UP:1000
         *
         * Total weight space: 10000
         *
         * Therefore we assume:
         *  [0, 3999]   -> HORIZ
         *  [4000, 6999]-> VERT
         *  [7000, 8999]-> DIAG_DOWN
         *  [9000, 9999]-> DIAG_UP
         */
        public DirectionGenerator(Map<Direction, Integer> weightMap) {
            intervals = new WeightInfo[4];
            int i, from, weight;
            i = from = totalWeight = 0;
            for (Direction d : weightMap.keySet()) {
                weight = weightMap.get(d) * AMPLIFY_FACTOR;
                from = totalWeight;
                totalWeight += weight;
                intervals[i] = new WeightInfo(d, from, totalWeight-1);
                i++;
            }
            rndGen = new Random();
        }


        public Direction randomDirection() {
            int point = rndGen.nextInt(totalWeight);
            for (WeightInfo w : intervals) {
                if (w.contains(point)) {
                    return w.d;
                }
            }
            // should never come here but java wants it
            return Direction.UNDEFINED;
        }
    }



    public Grid(byte sz, Level l) {
        entries = new Cell[sz][sz];
        rndGen = new Random();
        size = sz;
        gameLevel = l;

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

        DirectionGenerator dgen = new DirectionGenerator(levelWeights.get(gameLevel));
        for (byte i = 0; i < Constants.MAX_ATTEMPTS; i++) {
            d = dgen.randomDirection();
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
