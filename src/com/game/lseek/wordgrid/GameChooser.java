package com.game.lseek.wordgrid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.io.File;
import java.util.ArrayList;

import com.game.lseek.wordgrid.Constants.HeaderType;


class GameListAdapter extends BaseAdapter {
    private static final String LOGTAG = "wordgrid.GameListAdapter";
    private Context context;
    private ArrayList<GameInfo> gameList;
    private LayoutInflater inflater;

    private static class ViewHolder {
        TextView gameTitle;
        TextView gameLevel;
        TextView gameDescr;
    }


    public GameListAdapter(LayoutInflater inflater, Context context,
                       ArrayList<GameInfo> gameList) {
        this.inflater = inflater;
        this.context = context;
        this.gameList = gameList;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.game_item, null);
            viewHolder = new ViewHolder();
            viewHolder.gameTitle = (TextView)convertView.findViewById(R.id.gameNameLabel);
            viewHolder.gameLevel = (TextView)convertView.findViewById(R.id.gameLevelLabel);
            viewHolder.gameDescr = (TextView)convertView.findViewById(R.id.gameDescription);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        GameInfo game = gameList.get(position);
        if (game.gameInfo.containsKey(HeaderType.TITLE)) {
            viewHolder.gameTitle.setText(game.gameInfo.get(HeaderType.TITLE));
        } else {
            viewHolder.gameTitle.setText(game.gameFile.getName());
        }
        if (game.gameInfo.containsKey(HeaderType.LEVEL)) {
            viewHolder.gameLevel.setText(game.gameInfo.get(HeaderType.LEVEL));
        } else {
            viewHolder.gameLevel.setText(R.string.normalLevelStr);
        }
        if (game.gameInfo.containsKey(HeaderType.DESCR)) {
            viewHolder.gameDescr.setText(game.gameInfo.get(HeaderType.DESCR));
        } else {
            viewHolder.gameDescr.setText(R.string.noneStr);
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return gameList.size();
    }

    @Override
    public Object getItem(int position) {
        return gameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}


public class GameChooser extends Activity
{
    private static final String LOGTAG = "wordgrid.GameChooser";
    private String selection;
    ArrayList<GameInfo> gameList;
    private Context activityObj; // for passing to ItemClickListener
    private WordGridApp app;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_list_activity);

        app = (WordGridApp)getApplication();
        gameList = new ArrayList<GameInfo>();
        String filePath;
        for (String fileName : app.dataDir.list()) {
            filePath = String.format("%s/%s", app.dataDir.getPath(), fileName);
            GameInfo g = new GameInfo(new File(filePath));
            gameList.add(g);
        }


        ListView gameListView;
        GameListAdapter adapter;
        adapter = new GameListAdapter(getLayoutInflater(), this, gameList);
        gameListView = (ListView)findViewById(R.id.gameList);
        gameListView.setAdapter(adapter);
        activityObj = this;

        gameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LOG.d(LOGTAG, "Playing game:%s",
                      gameList.get(position).gameInfo.get(HeaderType.TITLE));

                app.loadGame(gameList.get(position).gameFile);
                Intent showGridIntent = new Intent(activityObj, GuiGridActivity.class);
                startActivity(showGridIntent);
            }
        });

    }
}
