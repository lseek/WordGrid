package com.game.lseek.wordgrid;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


class Goal {
    private static final String LOGTAG = "wordgrid.Goal";
    public String word;
    public String clue;
    public byte level;
    public int left, top, right, bottom;
    public Constants.Direction direction;


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
        direction = Constants.Direction.UNDEFINED;
        left = -1;
        top = -1;
        right = -1;
        bottom = -1;
    }


    // place word in the grid at the specified position
    public void place(char[][] grid) {
        // TODO: raise exception if direction is undefined (i.e. this word
        // hasn't been assigned a place in the grid). For now, just skip the
        // word
        int i;

        switch (direction) {
        case HORIZONTAL:
            for (i = 0; i < word.length(); i++) {
                grid[top][left + i] = word.charAt(i);
            }
            break;
        case VERTICAL:
            for (i = 0; i < word.length(); i++) {
                grid[top + i][left] = word.charAt(i);
            }
            break;
        case DIAG_UP:
            for (i = 0; i < word.length(); i++) {
                grid[bottom -i][left + i] = word.charAt(i);
            }
            break;
        case DIAG_DOWN:
            for (i = 0; i < word.length(); i++) {
                grid[top + i][left + i] = word.charAt(i);
            }
            break;
        case UNDEFINED:
            LOG.e(LOGTAG, "Word:%s does not have a position in the grid", word);
            return;
        }
    }


    // Does the box occupied by this word intersect with the box defined by o*
    // coordinates?
    public boolean intersects(int oLeft, int oTop, int oRight, int oBottom) {
        boolean yAxisIntersect;
        boolean xAxisIntersect;

        yAxisIntersect = ((left <= oLeft) && (oLeft <= right)) ||
                          ((oLeft <= left) && (left <= oRight));
        xAxisIntersect = ((top <= oTop) && (oTop <= bottom)) ||
                          ((oTop <= top) && (top <= oBottom));

        return yAxisIntersect && xAxisIntersect;
    }


    public void setPos(int left, int top, int right, int bottom, Constants.Direction d) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.direction = d;
    }


    public boolean isPlaced() {
        return (direction != Constants.Direction.UNDEFINED);
    }

    public String toString() {
        return String.format("Word:%s, Clue:%s--", word, clue); }
}
