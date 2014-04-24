package com.game.lseek.wordgrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


class Round {
    private static final String LOGTAG = "wordgrid.Round";
    private static int roundCount = 0;

    public String roundTitle;

    class GoalInfo {
        byte row, col;  // position of word in the grid.
        Goal goal;
    }

    // maintain a word<->goal mapping for each level.
    private Map<Constants.ItemLevel, Map<String, GoalInfo>> goals;
    int roundNum;

    // A level<->grid mapping for each level
    private Map<Constants.ItemLevel, ArrayList<String>> grids;

    public Round(String rTitle) {
        roundNum = roundCount++;
        goals = new HashMap<Constants.ItemLevel, Map<String, Goal>>();
        for (Constants.ItemLevel l : Constants.ItemLevel.values()) {
            goals.put(l, new HashMap<String, Goal>());
        }
        setTitle(rTitle);
        grids = new HashMap<Constants.ItemLevel, ArrayList<String>>();
    }


    public void addGoal(TaggedLine line) {
        GoalInfo g = new GoalInfo();
        g.goal = new Goal(line);

        for (Constants.ItemLevel l : Constants.ItemLevel.values()) {
            if (l.in(g.goal.level)) {
                goals.get(l).put(g.goal.word, g);
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


    /* Generate a grid for the specified level and place words in the grid */
    public genGrid(Constants.ItemLevel l) {
        // We don't allow words to intersect

    }


    public void print() {
        LOG.d(LOGTAG, "===%s===", roundTitle);
        Map<String, Goal> m;
        for (Constants.ItemLevel l : Constants.ItemLevel.values()) {
            LOG.d(LOGTAG, "  Items for level:%s", l);
            m = goals.get(l);
            for (String key : m.keySet()) {
                LOG.d(LOGTAG, "    %s", m.get(key).goal);
            }
        }
    }
}
