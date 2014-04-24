package com.game.lseek.wordgrid;

import java.lang.Math;


class Box {
    // NOTE: endRow may be < startRow (for DIAG_UP)
    int left, startRow, right, endRow;

    // Does this box intersect with the box defined by o*?
    boolean intersects(int oLeft, int oTop, int oRight, int oBottom) {
        boolean yAxisIntersect;
        boolean xAxisIntersect;

        int top = Math.min(startRow, endRow);
        int bottom = Math.max(startRow, endRow);

        yAxisIntersect = ((left <= oLeft) && (oLeft <= right)) ||
                          ((oLeft <= left) && (left <= oRight));
        xAxisIntersect = ((top <= oTop) && (oTop <= bottom)) ||
                          ((oTop <= top) && (top <= oBottom));

        return yAxisIntersect && xAxisIntersect;
    }
}
