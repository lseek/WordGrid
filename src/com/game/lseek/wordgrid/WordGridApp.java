package com.game.lseek.wordgrid;

import android.app.Application;
import android.os.Bundle;
import android.os.Environment;
import java.io.File;
import java.util.ArrayList;


public class WordGridApp extends Application
{
    private static final String LOGTAG = "wordgrid.Game";
    public static final String appDir = "com.game.lseek.wordgrid";
    public static final File dataDir;
    static {
        dataDir = new File(String.format("%s/%s",
                           Environment.getExternalStorageDirectory().getPath(),
                           appDir));
    }

    public Game currGame;

    public void onCreate() {
        super.onCreate();

        if (!dataDir.exists()) {
            LOG.d(LOGTAG, "Creating dataDir:%s", dataDir.getPath());
            if (dataDir.mkdir()) {
                LOG.d(LOGTAG, "Successfully created:%s", dataDir.getPath());
            } else {
                LOG.e(LOGTAG, "Unable to create:%s", dataDir.getPath());
            }
        } else {
            LOG.e(LOGTAG, "No need to create:%s", dataDir.getPath());
        }
    }

    public void loadGame(File gameFd) {
        currGame = new Game(gameFd);
    }
}
