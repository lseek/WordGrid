package com.game.lseek.wordgrid;

import android.app.Application;
import android.os.Bundle;
import android.os.Environment;
import java.io.File;
import java.util.ArrayList;


public class WordGridApp extends Application
{
    private static final String LOGTAG = "wordgrid.Game";

    public Game currGame;

    public void initGame() {
        String appDir = "com.game.lseek.wordgrid";
        //String appDir = getExternalFilesDir(null).getPath();
        String SDRoot = Environment.getExternalStorageDirectory().getPath();
        String dataDir = String.format("%s/%s", SDRoot, appDir);
        LOG.d(LOGTAG, "dataDir:%s", dataDir);

        File dataDirFd = new File(dataDir);
        if (!dataDirFd.exists()) {
            LOG.d(LOGTAG, "Creating dataDir:%s", dataDir);
            if (dataDirFd.mkdir()) {
                LOG.d(LOGTAG, "Successfully created:%s", dataDir);
            } else {
                LOG.e(LOGTAG, "Unable to create:%s", dataDir);
            }
        }

        currGame = new Game(String.format("%s/testGame.txt", dataDir));
    }
}
