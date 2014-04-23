package com.game.lseek.wordgrid;

import android.app.Application;
import android.os.Bundle;


public class WordGridApp extends Application
{
    public int gridSize = 10;
    Game currGame;

    public void startGame() {
        String appDir = getExternalFilesDir(null).getPath();
        currGame = new Game(String.format("%s/testGame.txt", appDir));
    }
}
