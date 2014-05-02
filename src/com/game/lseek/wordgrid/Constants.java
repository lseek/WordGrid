/*
 * Constants/enums shared between game model, game file parser and tokenizer.
 */
package com.game.lseek.wordgrid;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;


class Constants {
    public static final Pattern HEADER_RE = Pattern.compile("^@(TITLE|LEVEL|SIZE|DESCR):\\s*(.*)$");
    public static final Pattern ROUND_RE = Pattern.compile("^@(ROUND):\\s*(.*)$");

    public static final int TYPE_GRP = 1; // RE group that gives the type
    public static final int DATA_GRP = 2; // RE group that gives the data

    public static final Pattern ALPHA_RE = Pattern.compile("[a-zA-Z]+");

    // weights for different directions depending on the level. The weights
    // need not add up to 100 - just a relative measure.
    public static final Map<Level, Map<Direction, Integer>> levelWeights;
    static {
        levelWeights = new EnumMap<Level, Map<Direction, Integer>>(Level.class);
        Map<Direction, Integer> wmap;

        // weights for EASY level
        wmap = new EnumMap<Direction, Integer>(Direction.class);
        wmap.put(Direction.HORIZONTAL, 65);
        wmap.put(Direction.VERTICAL, 35);
        wmap.put(Direction.DIAG_UP, 0);
        wmap.put(Direction.DIAG_DOWN, 0);
        levelWeights.put(Level.EASY, wmap);

        // weights for NORMAL level
        wmap = new EnumMap<Direction, Integer>(Direction.class);
        wmap.put(Direction.HORIZONTAL, 45);
        wmap.put(Direction.VERTICAL, 25);
        wmap.put(Direction.DIAG_UP, 10);
        wmap.put(Direction.DIAG_DOWN, 20);
        levelWeights.put(Level.NORMAL, wmap);

        // weights for HARD level
        wmap = new EnumMap<Direction, Integer>(Direction.class);
        wmap.put(Direction.HORIZONTAL, 15);
        wmap.put(Direction.VERTICAL, 25);
        wmap.put(Direction.DIAG_UP, 25);
        wmap.put(Direction.DIAG_DOWN, 35);
        levelWeights.put(Level.HARD, wmap);
    }

    public static final byte MAX_ATTEMPTS = 30;
    public static final byte MIN_GRID_SIZE = 10;
    public static final byte MAX_GRID_SIZE = 22;

    // minimum map size for each level
    public static final Map<Level, Integer> minGridSzMap;
    static {
        minGridSzMap = new EnumMap<Level, Integer>(Level.class);
        minGridSzMap.put(Level.EASY, 12);
        minGridSzMap.put(Level.NORMAL, 16);
        minGridSzMap.put(Level.HARD, 20);
    }

    public static enum Level {
        EASY, NORMAL, HARD
    }

    public static enum TagType {
        HEADER, ROUND_TITLE, ROUND_ITEM, BLANK_LINE
    }

    public static enum HeaderType {
        NA, LEVEL, TITLE, SIZE, DESCR
    }


    public static enum Direction {
        UNDEFINED,
        HORIZONTAL, // left to right, one horizontal line
        VERTICAL,   // top to bottom, one vertical line
        DIAG_UP,    // left to right, bottom to top
        DIAG_DOWN;  // left to right, top to bottom

        static private Direction valueArr[] = {
            HORIZONTAL, VERTICAL, DIAG_UP, DIAG_DOWN
        };

        public static Direction fromInt(int i) {
            return valueArr[i];
        }

        public boolean notDefined() {
            return (this == UNDEFINED);
        }
    }


    public static Set<HeaderType> headerTags;
    static {
        headerTags = EnumSet.of(HeaderType.TITLE, HeaderType.SIZE, HeaderType.LEVEL);
    }

    private Constants() {}
}
