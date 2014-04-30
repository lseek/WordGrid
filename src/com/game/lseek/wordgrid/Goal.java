package com.game.lseek.wordgrid;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


class Goal {
    private static final String LOGTAG = "wordgrid.Goal";
    public String word;
    public String clue;
    public int left, top, right, bottom;
    public Constants.Direction direction;


    public class SyntaxException extends Exception {
        public SyntaxException(String msg, int lineNum) {
            super(String.format("Syntax eror: Line %d: %s", lineNum, msg));
        }
    }


    public Goal(TaggedLine line) throws SyntaxException {
        String[] parts = line.data.split("\\s+", 2);
        Matcher m;
        word = parts[0];
        clue = (parts.length == 2) ? parts[1] : "";
        word = word.toUpperCase();
        m = Constants.ALPHA_RE.matcher(word);
        if (!m.matches()) {
            // TODO: Raise exception instead of removing non-alpha characters.
            throw new SyntaxException(
                    String.format("%s contains non alphabet characters", word),
                    line.lineNum);
        }
        clearPlacement();
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


    public void clearPlacement() {
        setPlacement(-1, -1, -1, -1, Constants.Direction.UNDEFINED);
    }

    public void setPlacement(int left, int top, int right, int bottom, Constants.Direction d) {
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
