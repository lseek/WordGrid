package com.game.lseek.wordgrid;

import java.util.regex.Matcher;


class TaggedLine {
    Constants.TagType lineType;
    byte level;    // bitmap of ORed levels. Meaningful only for ROUND_ITEM types
    String data;

    // NOTE: line should have been trim()med.
    public TaggedLine(String line) {
        if (line.length() == 0) {
            lineType = Constants.TagType.BLANK_LINE;
            data = null;
        } else {
            Matcher m = Constants.SECTION_RE.matcher(line);
            if (m.matches()) {
                // TITLE or ROUND spec
                lineType = Constants.tagMap.get(m.group(Constants.SECTION_GRP));
                data = m.group(Constants.SECTION_DATA);
            } else {
                // Round item
                lineType = Constants.TagType.ROUND_ITEM;
                m = Constants.ITEM_RE.matcher(line);
                if (m.matches()) {
                    int inLevel;
                    for (String lvl : m.group(Constants.LEVEL_GRP).split("@")) {
                        if (lvl.length() == 0) {
                            /*
                             * Since a list of levels starts with "@"
                             * therefore the first member of the split list
                             * will be empty and should be skipped over.
                             * Alternatively, we could first strip the
                             * leading "@".
                             */
                            continue;
                        }
                        level |= Constants.levelMap.get(lvl).intVal();
                    }
                    data = m.group(Constants.ITEM_DATA);
                } else {
                    level = Constants.ItemLevel.EASY.intVal();
                    data = line;
                }
            }
        }
    }
}

