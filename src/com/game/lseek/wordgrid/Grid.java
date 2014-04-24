package com.game.lseek.wordgrid;

import java.util.ArrayList;
import java.util.Random;


class Grid {
    private char[][] entries;
    private Random rndGen;

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
    }


    public void placeWord(String word) {
        byte left, top
        for (byte i = 0; i < Constants.MAX_ATTEMPTS; i++) {

        }
    }


    // Fill empty cells of grid with random uppercase alphabets
    public void populate() {
    }


    // Do b1 and b2 intersect (or one is contained in another)?
    private intersect(Box b1, Box b2) {
        boolean leftRightIntersect;
        boolean topBottomIntersect;

        leftRightIntersect = ((b1.left <= b2.left) && (b2.left <= b1.right)) ||
                             ((b2.left <= b1.left) && (b1.left <= b2.right));
        topBottomIntersect = ((b1.top <= b2.top) && (b2.top <= b1.bottom)) ||
                             ((b2.top <= b1.top) && (b1.top <= b2.bottom));

        return leftRightIntersect && topBottomIntersect;
    }
}
