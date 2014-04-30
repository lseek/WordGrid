/*
 * Data Model:
 *
 * Game contains [1..*]Round
 * Round contains [1..*]Goal
 * Goal contains word, [0..1]clue
 */
package com.game.lseek.wordgrid;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.game.lseek.wordgrid.Constants.HeaderType;


public class Game {
    private String LOGTAG = "wordgrid.Game";

    // use a map to store game info (title, etc.) so that they can be
    // accessed via a simple key (rather than using a switch-case).
    Map<HeaderType, String> gameInfo;
    public ArrayList<Round> rounds;
    public byte calculatedSz;


    public Game(String gameFilePath) {
        BufferedReader gameFd = null;
        try {
            gameFd = new BufferedReader(new FileReader(gameFilePath));
        } catch (IOException e) {
            LOG.e(LOGTAG, String.format("Error opening:%s:%s", gameFilePath, e));
            // TODO: raise an exception
        }

        GameFileParser parser = new GameFileParser();
        TaggedLine tokenizedLine = null;
        String line = null;
        int lineNum = 1;

        rounds = new ArrayList<Round>();
        gameInfo = new HashMap<HeaderType, String>();

        while (true) {
            if (tokenizedLine == null) {
                try {
                    line = gameFd.readLine();
                } catch (IOException e) {
                    LOG.e(LOGTAG, String.format("Error reading from:%s:%s", gameFilePath, e));
                    break;
                }
                if (line == null) {
                    LOG.d(LOGTAG, String.format("Finished parsing:%s", gameFilePath));

                    Round tailData = parser.unprocessedRound();
                    if (!tailData.isEmpty()) {
                        LOG.d(LOGTAG, String.format("Adding:%s", tailData.roundTitle));
                        rounds.add(tailData);
                    } else {
                        LOG.d(LOGTAG, "tailData is empty");
                    }
                    break;
                }
                LOG.d(LOGTAG, "Read new line: " + line);
                lineNum++;
                tokenizedLine = new TaggedLine(line.trim(), lineNum);
            }
            tokenizedLine = parser.process(tokenizedLine, this);
        }
        LOG.d(LOGTAG, "Finished constructing game object");
        guessGridSize();
    }


    public Round getNewRound(Round currRound, String rTitle) {
        if (currRound != null) {
            if (!currRound.isEmpty()) {
                LOG.d(LOGTAG, "---- Adding round:" + currRound.roundTitle);
                rounds.add(currRound);
                currRound = new Round(rTitle);
            } else {
                currRound.setTitle(rTitle);
            }
        } else {
            currRound = new Round(rTitle);
        }
        return currRound;
    }


    public void guessGridSize() {
        /*
         * Assume there are 'n' squares of identical size to be fitted into a
         * grid. If each square can be thought of as one unit, then we would
         * need a grid size of int(sqrt(n))+1 units to fit all the squares.
         *
         * Now assume that the average length of all the words in a round is L.
         * Assume that there are N words. Furthermore, assume that each layout
         * is equally likely. Therefore, it is most likely that half of the
         * words use a 'square' layout (diagonal up/down) with an average "size"
         * of LxL cells.
         *
         * To fit these N/2 squares we need a grid that of size int(sqrt(N/2)) +
         * 1.
         *
         * Therefore the grid size required is L*(int(sqrt(N/2)) + 1) cells.
         *
         * To avoid rebuilding the grid each round, we actually average out the
         * length of ALL the words in the game and also average the number of
         * words in each round - we assume each round has approximately the same
         * number of words.
         */
        int totalLen = 0;
        int nWords = 0;

        for (Round r : rounds) {
            totalLen += r.wordLenSum;
            nWords += r.goalList.size();
        }
        int avgWordLen = (int)(totalLen/nWords);;
        int avgRoundSz = (int)(nWords/rounds.size());
        calculatedSz = (byte)(avgWordLen * ((int)Math.sqrt(avgRoundSz/2) + 1));
        LOG.d(LOGTAG, "calculatedSz:%d", calculatedSz);
    }


    public void print() {
        for (Round r : rounds) {
            r.print();
        }
    }
}
