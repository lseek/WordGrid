package com.game.lseek.wordgrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


class Round {
    private static final String LOGTAG = "wordgrid.Round";
    private static int roundCount = 0;

    public String roundTitle;

    // maintain a word<->goal mapping for each level.
    private Map<Constants.ItemLevel, ArrayList<Goal>> goalList;
    int roundNum;


    public Round(String rTitle) {
        roundNum = roundCount++;
        goalList = new HashMap<Constants.ItemLevel, ArrayList<Goal>>();
        for (Constants.ItemLevel l : Constants.ItemLevel.values()) {
            goalList.put(l, new ArrayList<Goal>());
        }
        setTitle(rTitle);
    }


    public void addGoal(TaggedLine line) {
        Goal g = new Goal(line);

        for (Constants.ItemLevel l : Constants.ItemLevel.values()) {
            if (l.in(g.level)) {
                goalList.get(l).add(g);
            }
        }
    }


    /*
     * Return only those items that match specified level
     */
    public ArrayList<Goal> filter(Constants.ItemLevel level) {
        return goalList.get(level);
    }


    // TODO: It may be possible that goalList is not empty for a particular
    // level but empty for another.
    public boolean isEmpty() {
        return (goalList.size() == 0);
    }


    public Round setTitle(String rTitle) {
        if ((rTitle != null) && (rTitle.length() > 0)) {
            roundTitle = rTitle;
        } else {
            // TODO: Make the format string a resource
            roundTitle = String.format("Round %d", roundNum);
        }
        return this;
    }


    public void print() {
        LOG.d(LOGTAG, "===%s===", roundTitle);
        ArrayList<Goal> m;
        for (Constants.ItemLevel l : Constants.ItemLevel.values()) {
            LOG.d(LOGTAG, "  Items for level:%s", l);
            m = goalList.get(l);
            for (Goal g : m) {
                LOG.d(LOGTAG, "    %s", g);
            }
        }
    }
}
