package com.game.lseek.wordgrid;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import java.io.File;
import android.widget.*;


/*
public class GameChooser extends Activity
{
    private String selection;
    ListView fileListView;
    TextView infoView;
    String[] fileList;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        fileListView = (ListView)findViewById(R.id.fileList);
        infoView = (TextView)findViewById(R.id.info);

        File appDir = getExternalFilesDir(null);
        fileList = appDir.list();

        ArrayAdapter<String> fileNameAdapter;
        fileNameAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, fileList);
        fileListView.setAdapter(fileNameAdapter);

        infoView.setText(appDir.toString());

        fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                infoView.setText(String.format("Selection:%s", fileList[position]));
            }
        });

    }
}
*/
