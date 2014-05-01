/*
 * A simplified class to hold basic game info for display in the opening
 * activity.
 *
 * It's a modified version of the Game class.
 */
package com.game.lseek.wordgrid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.game.lseek.wordgrid.Constants.HeaderType;


public class GameInfo {
    private static final String LOGTAG = "wordgrid.GameInfo";

    // use a map to store game info (title, etc.) so that they can be
    // accessed via a simple key (rather than using a switch-case).
    public Map<HeaderType, String> gameInfo;
    public File gameFile;


    public GameInfo(File gameFile) {
        LOG.e(LOGTAG, "Getting info about:%s", gameFile.getPath());

        this.gameFile = gameFile;
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
                    LOG.e(LOGTAG, "File:%s has no game data", gameFile.getPath());
                } else {
                    line = line.trim();
                    if (line.length() == 0) {
                        // blank line - subsequent lines (should) have only data.
                        LOG.d(LOGTAG, "Finished processing game header");
                        break;
                    }
                    LOG.d(LOGTAG, "Read new line: " + line);
                    lineNum++;
                    tokenizedLine = new TaggedLine(line.trim(), lineNum);
                }
            }
            tokenizedLine = parser.processHeaders(tokenizedLine, this);
        }
        LOG.d(LOGTAG, "Finished parsing game headers");
    }
}
