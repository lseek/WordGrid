package com.game.lseek.wordgrid;

import android.app.Application;
import android.os.Bundle;
import java.util.ArrayList;


public class WordGridApp extends Application
{
    private static final String LOGTAG = "wordgrid.Game";

    public byte gridSize = 12;
    public Constants.ItemLevel level;
    public Game currGame;

    public void initGame() {
        String appDir = getExternalFilesDir(null).getPath();
        level = Constants.ItemLevel.EASY;

        currGame = new Game(String.format("%s/testGame.txt", appDir));
    }
}
