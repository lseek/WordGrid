/*
 * A straight line of cells in the grid.
 *
 * The chain is always sorted in left->right column order except for when the
 * direction is vertical in which case it is sorted from top to bottom.
 */
package com.game.lseek.wordgrid;

import java.util.LinkedList;


class CellLine {
    private static final String LOGTAG = "wordgrid.CellLine";

    private Constants.Direction direction = Constants.Direction.UNDEFINED;
    private LinkedList<Cell> chain;


    public CellLine() {
        chain = new LinkedList<Cell>();
    }


    private void newSequence(Cell newCell) {
        LOG.d(LOGTAG, "Starting new chain from:(%d, %d)", newCell.row, newCell.col);
        for (Cell c : chain) {
            c.deselect();
        }
        direction = Constants.Direction.UNDEFINED;
        chain.clear();
        chain.add(newCell.select());
    }


    public void add(Cell newCell) {
        if (chain.size() == 0) {
            LOG.d(LOGTAG, "Adding first member:(%d, %d) to chain", newCell.row, newCell.col);
            chain.add(newCell.select());
            return;
        }

        if (chain.contains(newCell)) {
            LOG.d(LOGTAG, "newCell:(%d, %d) already in chain", newCell.row, newCell.col);
            return;
        }

        Cell head = chain.getFirst();
        Cell tail = chain.getLast();
        Cell.CellRelation hRelation = head.relationTo(newCell);
        Cell.CellRelation tRelation = tail.relationTo(newCell);

        if (hRelation.notDefined() && tRelation.notDefined()) {
            // not adjacent to head or tail - new sequence
            LOG.d(LOGTAG, "(%d, %d): not connected to head or tail", newCell.row, newCell.col);
            newSequence(newCell);
            return;
        }

        // adjacent to head or tail
        if (!hRelation.notDefined()) {
            if (!direction.notDefined()) {
                if (hRelation.direction != direction) {
                    // current direction broken - new sequence.
                    LOG.d(LOGTAG, "(%d, %d): direction:%s broken at head", newCell.row, newCell.col, direction);
                    newSequence(newCell);
                } else {
                    // must be "before" the head (else it would have already been selected).
                    LOG.d(LOGTAG, "Adding:(%d, %d) to head", newCell.row, newCell.col);
                    chain.addFirst(newCell.select());
                }
            } else {
                // existing chain contains only one element
                if (hRelation.order == Cell.CmpOrder.LT) {
                    LOG.d(LOGTAG, "Adding:(%d, %d) to head, defining dir:%s", newCell.row, newCell.col, hRelation.direction);
                    chain.addFirst(newCell.select());
                } else {
                    LOG.d(LOGTAG, "Adding:(%d, %d) to tail, defining dir:%s", newCell.row, newCell.col, hRelation.direction);
                    chain.addLast(newCell.select());
                }
                direction = hRelation.direction;
            }
        } else {
            // adjacent to tail && chain.size() > 1 (else would have been handled above).
            if (tRelation.direction != direction) {
                LOG.d(LOGTAG, "(%d, %d): direction:%s broken at tail", newCell.row, newCell.col, direction);
                newSequence(newCell);
            } else {
                // must be "after" the tail (else it would have already been selected).
                LOG.d(LOGTAG, "Adding:(%d, %d) to tail", newCell.row, newCell.col);
                chain.addLast(newCell.select());
            }
        }
    }


    public void reveal() {
        for (Cell cell : chain) {
            cell.revealed = true;
        }
        chain.clear();
        direction = Constants.Direction.UNDEFINED;
    }


    public void clear() {
        for (Cell cell : chain) {
            cell.deselect();
        }
        chain.clear();
        direction = Constants.Direction.UNDEFINED;
    }


    public String toString() {
        StringBuilder line = new StringBuilder(chain.size());
        for (Cell c : chain) {
            line.append(c.value);
        }
        return line.toString();
    }
}


