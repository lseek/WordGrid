package com.game.lseek.wordgrid;

import android.util.Log;
import java.util.regex.Matcher;


class TaggedLine {
    private String LOGTAG = "wordgrid.Game.TaggedLine";

    Constants.TagType lineType;
    byte level;    // bitmap of ORed levels. Meaningful only for ROUND_ITEM types
    String data;

    // NOTE: line should have been trim()med.
    public TaggedLine(String line) {
        Log.d(LOGTAG, "Parsing line:" + line);
        if (line.length() == 0) {
            Log.d(LOGTAG, "  Empty line");
            lineType = Constants.TagType.BLANK_LINE;
            data = null;
        } else {
            Matcher m = Constants.SECTION_RE.matcher(line);
            if (m.matches()) {
                Log.d(LOGTAG, "  TITLE or ROUND title");
                // TITLE or ROUND spec
                lineType = Constants.tagMap.get(m.group(Constants.SECTION_GRP));
                data = m.group(Constants.SECTION_DATA);
            } else {
                Log.d(LOGTAG, "  ROUND item");
                // Round item
                lineType = Constants.TagType.ROUND_ITEM;
                m = Constants.ITEM_RE.matcher(line);
                if (m.matches()) {
                    String levelStr = m.group(Constants.LEVEL_GRP);
                    if (levelStr != null) {
                        Log.d(LOGTAG, "  Level explicitly specified:" + levelStr);
                        level |= Constants.levelMap.get(levelStr).intVal();
                        data = m.group(Constants.ITEM_DATA);
                        Log.d(LOGTAG, "  Data:" + data);
                    } else {
                        Log.d(LOGTAG, "  Implicit EASY level entry, Data:" + line);
                        level = Constants.ItemLevel.EASY.intVal();
                        data = line;
                    }
                } else {
                    Log.e(LOGTAG, "Syntax error:" + line);
                }
            }
        }
    }
}

