/*
 * Constants/enums shared between game model, game file parser and tokenizer.
 */
package com.game.lseek.wordgrid;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class Constants {
    public static final Pattern ITEM_RE = Pattern.compile("^(@(EASY|NORMAL|HARD):)?\\s*(.*)$");
    public static final int LEVEL_GRP = 2; // RE group that gives the list of levels
    public static final int ITEM_DATA = 3; // RE group that gives the item data in case of a match

    public static final Pattern SECTION_RE = Pattern.compile("^@(TITLE|ROUND):\\s*(.*)$");
    public static final int SECTION_GRP = 1; // RE group that gives the section type
    public static final int SECTION_DATA = 2; // RE group that gives the section data

    public static final Pattern ALPHA_RE = Pattern.compile("[a-zA-Z]+");

    public static final byte MAX_ATTEMPTS = 10;

    public static enum TagType {
        TITLE, ROUND_TITLE, ROUND_ITEM, BLANK_LINE
    };


    public static enum ItemLevel {
        EASY(1),
        NORMAL(2),
        HARD(4);

        private byte enumVal;

        ItemLevel(int level) {
            enumVal = (byte)level;
        }

        byte intVal() {
            return enumVal;
        }

        boolean in(byte levelMask) {
            return ((enumVal & levelMask) != 0);
        }
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
    }


    public static final Map<String, TagType> tagMap;
    static {
        tagMap = new HashMap<String, TagType>();
        tagMap.put("TITLE", TagType.TITLE);
        tagMap.put("ROUND", TagType.ROUND_TITLE);
    }
    public static final Map<String, ItemLevel> levelMap;
    static {
        levelMap = new HashMap<String, ItemLevel>();
        levelMap.put("EASY", ItemLevel.EASY);
        levelMap.put("NORMAL", ItemLevel.NORMAL);
        levelMap.put("HARD", ItemLevel.HARD);
    }

    private Constants() {}
}
