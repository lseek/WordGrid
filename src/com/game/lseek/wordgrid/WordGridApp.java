package com.game.lseek.wordgrid;

import android.app.Application;
import android.os.Bundle;
import java.util.ArrayList;


public class WordGridApp extends Application
{
    private static final String LOGTAG = "wordgrid.Game";

    public Game currGame;

    public void initGame() {
        String appDir = getExternalFilesDir(null).getPath();
        currGame = new Game(String.format("%s/testGame.txt", appDir));
    }
}
