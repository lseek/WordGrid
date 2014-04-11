/*
 * Data Model:
 *
 * Game contains [1..*]Round
 * Round contains [1..*]Goal
 * Goal contains word, [0..1]clue
 */
package com.game.wordgrid;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;


public class Model {
    class Goal {
        private String word;
        private String clue;

        public Goal(String inputStr) {
            String[] parts = inputStr.split("\\s+", 2);
            LOG.info("inputStr:%s", inputStr);
            word = parts[0];
            clue = (parts.length == 2) ? parts[1] : "";
            LOG.info("Parsed:(%s,%s)", word, clue);
        }

        public void print() {
            System.out.println(String.format("word:%s, clue:%s", word, clue));
        }
    }


    class Round {
        private String name;
        private Goal[] goals;
        private static final String NAME_PFX = "@NAME:";
        private static final int PFX_LEN = 6;

        public Round(String[] goalStrs) {
            int i, startOffset;

            // If first line of input begins with @NAME: then it is the name of the
            // round.
            if (goalStrs[0].startsWith(NAME_PFX)) {
                name = (goalStrs[0].length() > PFX_LEN) ? goalStrs[0].substring(PFX_LEN) : "";
                name = name.trim();
                startOffset = 1;
                LOG.info("Round Name:'%s'", name);
            } else {
                startOffset = 0;
                LOG.info("No Round name");
            }
            goals = new Goal[goalStrs.length - startOffset];
            for (i = startOffset; i < goalStrs.length; i++)  {
                LOG.info("goalStr:%s", goalStrs[i]);
                goals[i - startOffset] = new Goal(goalStrs[i]);
            }
            LOG.info("Finished building round");
        }

        public void print() {
            System.out.println(String.format("---Round:%s---", name));
            for (Goal g : goals) {
                g.print();
            }
            System.out.println("");
        }
    }


    public class Game {
        private ArrayList<Round> rounds;

        public Game(String gameFile) {
            BufferedReader gameFd = null;
            LOG.info("Reading game from:%s", gameFile);

            rounds = new ArrayList<Round>();

            try {
                gameFd = new BufferedReader(new FileReader(gameFile));

                String line;
                ArrayList<String> currRound = new ArrayList<String>();
                while ((line = gameFd.readLine()) != null) {
                    LOG.info("line:%s", line);
                    line = line.trim();
                    if (line.length() == 0) {
                        if (currRound.size() > 0) {
                            LOG.info("Completing a round, currRound:%s", currRound);
                            rounds.add(new Round(currRound.toArray(new String[0])));
                            currRound = null;
                            currRound = new ArrayList<String>();
                        }
                    } else {
                        currRound.add(line);
                    }
                }
                if (currRound.size() > 0) {
                    LOG.info("Completing last round:%s", currRound);
                    rounds.add(new Round(currRound.toArray(new String[0])));
                }
            } catch (IOException e) {
                LOG.warning("Error reading input file:%s:%s", gameFile, e);
            } finally {
                if (gameFd != null) {
                    try {
                        gameFd.close();
                    } catch (IOException e) {
                        LOG.warning("Error cleaning up:%s:%s", gameFile, e);
                    }
                }
            }
        }

        public void print() {
            for (Round r : rounds) {
                r.print();
            }
        }
    }
}
