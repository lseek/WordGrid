package com.game.lseek.wordgrid;

import java.util.Random;


class Grid {
    private char[][] entries;
    private Random rndGen;
    private byte size;

    class Box {
        byte left, top, right, bottom;
    }

    public Grid(byte sz) {
        entries = new char[sz][sz];
        rndGen = new Random();

        for (int row = 0; row < sz ; row++) {
            for (int col = 0; col < sz; col++) {
                entries[row][col] = (char)('A' + rndGen.nextInt(25));
            }
        }
        size = sz;
    }


    public void placeWord(String word) {
        byte left, top, right, bottom, wordLen;
        Constants.Direction d;
        wordLen = word.length();

        /*
         * 1. Get a random direction
         * 2. Get a random point (left, top). Direction + word length defines
         *    the bounding box
         * 3. If bounding box is within grid & doesn't intersect with other
         *    bounding boxes then accept placement else retry.
         */
        for (byte i = 0; i < Constants.MAX_ATTEMPTS; i++) {
            d = Constants.Direction.fromInt(rndGen.nextInt(4));
            left = rndGen.nextInt(size);
            top = rndGen.nextInt(size);
            // right is always > left except for VERTICAL direction.
            right = (d != Constants.Direction.VERTICAL) ? left + wordLen : left;
            // For bounding box calculation, bottom is always > top except for
            // HORIZONTAL direction
            bottom = (d != Constants.Direction.HORIZONTAL) ? top + wordLen : top;
            if (!fits(right, bottom)) {
                continue;
            } else {
            }
        }
    }


    // do the coordinates fit within the grid?
    private void fits(int col, int row) {
        return ((col < size) && (row < size));
    }


    // Fill empty cells of grid with random uppercase alphabets
    public void populate() {
    }
}
