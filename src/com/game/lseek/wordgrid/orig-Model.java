/*
 * Data Model:
 *
 * Game contains [1..*]Round
 * Round contains [1..*]Goal
 * Goal contains word, [0..1]clue
 */
/*
package com.game.lseek.wordgrid;

import android.util.Log;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;


public class Model {
    private String TAG = "wordgrid.Model";


    class Goal {
        private String[] levelTags = { "@EASY:", "@NORMAL:", "@HARD:" };

        private String word;
        private String clue;
        private int level;


        public Goal(String inputStr) {
            // in input string is of the form:
            //   @<LEVEL>: word clue
            // Where LEVEL is a gameLevel.
            // If it is not specified then the level is assumed to be EASY.
            String levelStr;
            for (level = 0; level < levelTags.length ; level++) {
                levelStr = levelTags[level];
                if (inputStr.startsWith(levelStr)) {
                    inputStr = inputStr.substring(levelStr.length()).trim();
                    break;
                }
            }
            parseInput(inputStr);
        } */


        /**
         * Parse an input string with any optional "level" info already removed.
         */
/*
        private void parseInput(String inputStr) {
            String[] parts = inputStr.split("\\s+", 2);
            Log.d(TAG, String.format("inputStr:%s", inputStr));
            word = parts[0];
            clue = (parts.length == 2) ? parts[1] : "";
            // TODO: verify word is made up of only [a-zA-Z] and throw exception if not so.
            Log.d(TAG, String.format("Parsed:(%s, %s)", word, clue));
        }
    }


    class Round {
        private String name;
        private Goal[] goals;
        private String NAME_PFX = "@NAME:";
        private int PFX_LEN = 6;

        public Round(ArrayList<String> goalStrs) {
            int i, startOffset;

            // If first line of input begins with @NAME: then it is the name of the
            // round.
            String currLine = goalStrs.get(0);
            if (currLine.startsWith(NAME_PFX)) {
                name = (currLine.length() > PFX_LEN) ? currLine.substring(PFX_LEN) : "";
                name = name.trim();
                startOffset = 1;
                Log.d(TAG, String.format("Round Name:'%s'", name));
            } else {
                startOffset = 0;
                Log.d(TAG, "No Round name");
            }
            goals = new Goal[goalStrs.length - startOffset];
            for (i = startOffset; i < goalStrs.size(); i++)  {
                currLine = goalStrs.get(i);
                Log.d(TAG, String.format("goalStr:%s", currLine));
                goals[i - startOffset] = new Goal(currLine);
            }
            Log.d(TAG, "Finished building round");
        }
    }


    public class Game {
        private ArrayList<Round> rounds;

        *//**
         * Read an entire text file into an ArrayList (and return the ArrayList).
         *//*
        ArrayList<String> slurp(String gameFilePath) {
            ArrayList<String> fileContents = new ArrayList<String>();

            try {
                BufferedReader gameFd = new BufferedReader(new FileReader(gameFilePath));

                String line;
                while ((line = gameFd.readLine()) != null) {
                    Log.d(TAG, String.format("line:%s", line));
                    line = line.trim();
                    fileContents.add(line);
                }
            } catch (IOException e) {
                Log.d(TAG, String.format("Error reading input file:%s:%s", gameFilePath, e));
                // TODO: propagate exception up
            } finally {
                if (gameFd != null) {
                    try {
                        gameFd.close();
                    } catch (IOException e) {
                        Log.d(TAG, String.format("Error closing up:%s:%s", gameFilePath, e));
                    }
                }
            }
            return fileContents;
        }


        public Game(String gameFilePath) {
            Log.d(TAG, String.format("Reading game from:%s", gameFilePath));

            ArrayList<String> fileContents = slurp(gameFilePath);
            rounds = new ArrayList<Round>();
            for (String line : fileContents) {
                Log.d(TAG, String.format("line:%s", line));
                if (line.length() == 0) {
                    if (currRound.size() > 0) {
                        Log.d(TAG, String.format("Completing a round, currRound:%s", currRound));
                        rounds.add(new Round(currRound.toArray(new String[0])));
                        currRound = null;
                        currRound = new ArrayList<String>();
                    }
                } else {
                    currRound.add(line);
                }
            }
            if (currRound.size() > 0) {
                Log.d(TAG, String.format("Completing last round:%s", currRound));
                rounds.add(new Round(currRound.toArray(new String[0])));
            }
        }
    }
}
*/
