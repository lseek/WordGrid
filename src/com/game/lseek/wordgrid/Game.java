/*
 * Data Model:
 *
 * Game contains [1..*]Round
 * Round contains [1..*]Goal
 * Goal contains word, [0..1]clue
 */
package com.game.lseek.wordgrid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.game.lseek.wordgrid.Constants.*;


public class Game {
    private static final String LOGTAG = "wordgrid.Game";

    private int roundCount = 0;

    // use a map to store game info (title, etc.) so that they can be
    // accessed via a simple key (rather than using a switch-case).
    public byte calculatedSz;
    public Map<HeaderType, String> gameInfo;
    public ArrayList<Round> rounds;


    public Game(File gameFile, Level gameLevel) {
        BufferedReader gameFd = null;
        try {
            gameFd = new BufferedReader(new FileReader(gameFile));
        } catch (IOException e) {
            LOG.e(LOGTAG, "Error opening:%s:%s", gameFile.getPath(), e);
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
                    LOG.e(LOGTAG, "Error reading from:%s:%s", gameFile.getPath(), e);
                    break;
                }
                if (line == null) {
                    LOG.d(LOGTAG, "Finished parsing:%s", gameFile.getPath());

                    Round tailData = parser.unprocessedRound();
                    if (!tailData.isEmpty()) {
                        LOG.d(LOGTAG, "Adding:%s", tailData.roundTitle);
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
        guessGridSize(gameLevel);
    }


    public Round getNewRound(Round currRound, String rTitle) {
        if (currRound != null) {
            if (!currRound.isEmpty()) {
                LOG.d(LOGTAG, "---- Adding round:" + currRound.roundTitle);
                rounds.add(currRound);
                currRound = new Round(rTitle, roundCount++);
            } else {
                currRound.setTitle(rTitle);
            }
        } else {
            currRound = new Round(rTitle, roundCount++);
        }

        return currRound;
    }


    public void guessGridSize(Level gameLevel) {
        /*
         * Assume there are 'n' SQUARES of identical size to be fitted into a
         * grid. If each SQUARE can be thought of as one unit, then we would
         * need a grid size of int(sqrt(n))+1 units to fit all the SQUARES.
         *
         * Now assume that the average length of all the words in a round is L.
         * Assume that there are N words. Furthermore, assume that the
         * probability of a 'square' layout is P (0 <= P <= 1). Therefore it is
         * most likely that P*N of the words will use a 'SQUARE' layout
         * (diagonal up/down) with an average "size" of LxL CELLS.
         *
         * To fit these P*N SQUARES we need a grid that of size int(sqrt(P*N)) +
         * 1 SQUARES.
         *
         * Therefore the grid size required is L*(int(sqrt(P*N)) + 1) CELLS.
         *
         * To avoid rebuilding the grid each round, we actually average out the
         * length of ALL the words in the game and also average the number of
         * words in each round - we assume each round has approximately the same
         * number of words.
         */
        int totalLen, nWords, maxLen;
        totalLen = nWords = maxLen = 0;

        for (Round r : rounds) {
            totalLen += r.wordLenSum;
            nWords += r.goalList.size();
            maxLen = Math.max(maxLen, r.maxLen);
        }
        int avgWordLen = (int)(totalLen/nWords);;
        int avgRoundSz = (int)(nWords/rounds.size());
        int totalWeight, squareWeight;
        float squarePercent;
        Map<Direction, Integer> weightMap = levelWeights.get(gameLevel);
        totalWeight = weightMap.get(Direction.HORIZONTAL)
                      + weightMap.get(Direction.VERTICAL)
                      + weightMap.get(Direction.DIAG_UP)
                      + weightMap.get(Direction.DIAG_DOWN);
        squarePercent = (weightMap.get(Direction.DIAG_UP)
                         + weightMap.get(Direction.DIAG_DOWN)) / totalWeight;
        int nSquares = (int)(avgRoundSz * squarePercent);

        calculatedSz = (byte)(avgWordLen * ((int)Math.sqrt(nSquares) + 1));

        calculatedSz = (byte)Math.max(calculatedSz, minGridSzMap.get(gameLevel));
        LOG.d(LOGTAG, "avgWordLen:%d, avgRoundSz:%d calculatedSz:%d",
              avgWordLen, avgRoundSz, calculatedSz);
    }


    public void print() {
        for (Round r : rounds) {
            r.print();
        }
    }
}
