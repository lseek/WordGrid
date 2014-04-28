package com.game.lseek.wordgrid;

import android.app.Application;
import android.os.Bundle;
import java.util.ArrayList;


public class WordGridApp extends Application
{
    private static final String LOGTAG = "wordgrid.Game";

    public byte gridSize = 12;
    public Game currGame;
    public Grid currGrid;
    public ArrayList<Goal> currGoals;

    public void initGame(int round) {
        String appDir = getExternalFilesDir(null).getPath();
        currGame = new Game(String.format("%s/testGame.txt", appDir));

        currGrid = new Grid(gridSize);
        currGoals = currGame.rounds.get(round).filter(Constants.ItemLevel.EASY);
        currGrid.generate(currGoals);
        currGrid.printSolution();
        currGrid.print();
    }
}
