/*
 * Data Model:
 *
 * Game contains [1..*]Round
 * Round contains [1..*]Goal
 * Goal contains word, [0..1]clue
 */
package com.game.lseek.wordgrid;

import android.util.Log;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Game {
    private String LOGTAG = "wordgrid.Game";

    private byte gameLevel = Constants.ItemLevel.NORMAL.intVal();
    public String title;
    public ArrayList<Round> rounds;
    Round currRound;
    private int roundCount = 1;


    class Round {
        public String title;
        public Map<String, Goal> goals;
        int roundNum;

        public Round(String title) {
            roundNum = roundCount++;
            goals = new HashMap<String, Goal>();
            setTitle(title);
        }

        public void addGoal(TaggedLine line) {
            Goal g = new Goal(line);
            if ((gameLevel & g.level) != 0) {
                goals.put(g.word, g);
            }
        }

        public boolean isEmpty() {
            return (goals.size() == 0);
        }

        public Round setTitle(String title) {
            if ((title != null) && (title.length() > 0)) {
                this.title = title;
            } else {
                // TODO: Make the format string a resource
                this.title = String.format("Round %d", roundNum);
            }
            return this;
        }

        public void print() {
            Log.d(LOGTAG, String.format("===Round:%d===", roundNum));
            for (String key : goals.keySet()) {
                Log.d(LOGTAG, String.format("  Word:%s, Clue:%s", key, goals.get(key)));
            }
        }
    }


    class Goal {
        public String word;
        public String clue;
        public byte level = Constants.ItemLevel.NORMAL.intVal();


        public Goal(TaggedLine line) {
            level = line.level;

            String[] parts = line.data.split("\\s+", 2);
            Matcher m;
            word = parts[0];
            clue = (parts.length == 2) ? parts[1] : "";
            m = Constants.ALPHA_RE.matcher(word);
            if (m.matches()) {
                word = word.toUpperCase();
                Log.d(LOGTAG, String.format("Parsed:(%s, %s)", word, clue));
            } else {
                // TODO: Raise exception
                Log.d(LOGTAG, String.format("%s contains non alphabet characters", word));
            }
        }
    }


    public Game(String gameFilePath) {
        BufferedReader gameFd = null;
        try {
            gameFd = new BufferedReader(new FileReader(gameFilePath));
        } catch (IOException e) {
            Log.e(LOGTAG, String.format("Error opening:%s:%s", gameFilePath, e));
            // TODO: raise an exception
        }

        GameFileParser parser = new GameFileParser();
        TaggedLine tokenizedLine = null;
        String line = null;

        while (true) {
            if (tokenizedLine == null) {
                try {
                    line = gameFd.readLine();
                } catch (IOException e) {
                    Log.e(LOGTAG, String.format("Error reading from:%s:%s", gameFilePath, e));
                }
                if (line == null) {
                    Log.d(LOGTAG, String.format("Finished parsing:%s", gameFilePath));
                    break;
                }
                tokenizedLine = new TaggedLine(line);
            }
            tokenizedLine = parser.process(tokenizedLine, this);
        }
    }

    public Round getCurrentRound() {
        return currRound;
    }

    public Round getNewRound(String roundTitle) {
        if (currRound != null) {
            if (!currRound.isEmpty()) {
                rounds.add(currRound);
                currRound = new Round(roundTitle);
            } else {
                currRound.setTitle(title);
            }
        } else {
            currRound = new Round(roundTitle);
        }
        return currRound;
    }
}
