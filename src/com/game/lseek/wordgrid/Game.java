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
import java.util.ArrayList;


public class Game {
    private String LOGTAG = "wordgrid.Game";

    public String gameTitle;
    public ArrayList<Round> rounds;


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

        rounds = new ArrayList<Round>();

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
                tokenizedLine = new TaggedLine(line);
            }
            tokenizedLine = parser.process(tokenizedLine, this);
        }
        LOG.d(LOGTAG, "Finished constructing game object");
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


    public void print() {
        for (Round r : rounds) {
            r.print();
        }
    }
}
