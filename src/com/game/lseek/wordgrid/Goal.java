package com.game.lseek.wordgrid;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


class Goal {
    private static final String LOGTAG = "wordgrid.Goal";
    public String word;
    public String clue;
    public byte level;


    public Goal(TaggedLine line) {
        level = line.level;

        String[] parts = line.data.split("\\s+", 2);
        Matcher m;
        word = parts[0];
        clue = (parts.length == 2) ? parts[1] : "";
        m = Constants.ALPHA_RE.matcher(word);
        if (m.matches()) {
            word = word.toUpperCase();
            LOG.d(LOGTAG, "Parsed:(%s, %s)", word, clue);
        } else {
            // TODO: Raise exception
            LOG.d(LOGTAG, "%s contains non alphabet characters", word);
        }
    }

    public String toString() {
        return String.format("Word:%s, Clue:%s--", word, clue); }
}
