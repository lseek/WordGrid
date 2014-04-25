package com.game.lseek.wordgrid;

import android.app.Application;
import android.os.Bundle;
import java.util.HashMap;
import java.util.Map;


public class WordGridApp extends Application
{
    private static final String LOGTAG = "wordgrid.Game";

    public byte gridSize = 12;
    Game currGame;

    public void startGame() {
        String appDir = getExternalFilesDir(null).getPath();
        currGame = new Game(String.format("%s/testGame.txt", appDir));

        Grid grid = new Grid(gridSize);
        Round activeRound = currGame.rounds.get(1);
        Map<String, Goal> goals = activeRound.filter(Constants.ItemLevel.EASY);
        if (grid.generate(goals) != null) {
            grid.printSolution();
        } else {
            LOG.e(LOGTAG, "genGrid failed");
        }
    }
}
