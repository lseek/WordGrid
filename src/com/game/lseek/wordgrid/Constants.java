/*
 * Constants/enums shared between game model, game file parser and tokenizer.
 */
package com.game.lseek.wordgrid;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.EnumSet;
import java.util.Set;


class Constants {
    public static final Pattern HEADER_RE = Pattern.compile("^@(TITLE|LEVEL|SIZE):\\s*(.*)$");
    public static final Pattern ROUND_RE = Pattern.compile("^@(ROUND):\\s*(.*)$");

    public static final int TYPE_GRP = 1; // RE group that gives the type
    public static final int DATA_GRP = 2; // RE group that gives the data

    public static final Pattern ALPHA_RE = Pattern.compile("[a-zA-Z]+");

    public static final byte MAX_ATTEMPTS = 30;
    public static final byte MIN_GRID_SIZE = 10;
    public static final byte MAX_GRID_SIZE = 25;

    public static enum TagType {
        HEADER, ROUND_TITLE, ROUND_ITEM, BLANK_LINE
    }

    public static enum HeaderType {
        NA, LEVEL, TITLE, SIZE
    }


    public static enum Direction {
        UNDEFINED,
        OVERLAP,
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
