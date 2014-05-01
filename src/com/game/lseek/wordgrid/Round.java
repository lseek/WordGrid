package com.game.lseek.wordgrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


class Round {
    private static final String LOGTAG = "wordgrid.Round";
    private static int roundCount = 0;

    public ArrayList<Goal> goalList;
    public int roundNum;
    public int wordLenSum = 0;
    public String roundTitle;


    public Round(String rTitle) {
        roundNum = roundCount++;
        goalList = new ArrayList<Goal>();
        setTitle(rTitle);
    }


    public void addGoal(TaggedLine line) {
        Goal g;
        try {
            g = new Goal(line);
            goalList.add(g);
            wordLenSum += g.word.length();
        } catch (Goal.SyntaxException e) {
            // skip this line
            LOG.e(LOGTAG, e.getMessage());
        }
    }


    public boolean isEmpty() {
        return (goalList.size() == 0);
    }


    public Round setTitle(String rTitle) {
        if ((rTitle != null) && (rTitle.length() > 0)) {
            roundTitle = String.format("Round %d: %s", roundNum + 1, rTitle);
        } else {
            // TODO: Make the format string a resource
            roundTitle = String.format("Round %d", roundNum);
        }
        return this;
    }


    public void print() {
        LOG.d(LOGTAG, "===%s===", roundTitle);
        for (Goal g : goalList) {
            LOG.d(LOGTAG, "    %s", g);
        }
    }
}
