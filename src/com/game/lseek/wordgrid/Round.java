package com.game.lseek.wordgrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


class Round {
    private static final String LOGTAG = "wordgrid.Round";
    private static int roundCount = 0;

    public String roundTitle;

    // maintain a word<->goal mapping for each level.
    private Map<Constants.ItemLevel, Map<String, Goal>> goals;
    int roundNum;


    public Round(String rTitle) {
        roundNum = roundCount++;
        goals = new HashMap<Constants.ItemLevel, Map<String, Goal>>();
        for (Constants.ItemLevel l : Constants.ItemLevel.values()) {
            goals.put(l, new HashMap<String, Goal>());
        }
        setTitle(rTitle);
    }


    public void addGoal(TaggedLine line) {
        Goal g = new Goal(line);
        for (Constants.ItemLevel l : Constants.ItemLevel.values()) {
            if (l.in(g.level)) {
                goals.get(l).put(g.word, g);
            }
        }
    }

    /*
     * Return only those items that match specified level
     */
    public Map<String, Goal> filter(Constants.ItemLevel level) {
        return goals.get(level);
    }

    public boolean isEmpty() {
        return (goals.size() == 0);
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
        Map<String, Goal> m;
        for (Constants.ItemLevel l : Constants.ItemLevel.values()) {
            LOG.d(LOGTAG, "  Items for level:%s", l);
            m = goals.get(l);
            for (String key : m.keySet()) {
                LOG.d(LOGTAG, "    %s", m.get(key));
            }
        }
    }
}
