package com.game.lseek.wordgrid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.lang.Integer;


class GridAdapter extends BaseAdapter {
    private static final String LOGTAG = "wordgrid.GridAdapter";

    private Context context;
    private Grid grid;
    private LayoutInflater inflater;


    public GridAdapter(LayoutInflater inflater, Context context) {
        this.context = context;
        this.grid = null;
        this.inflater = inflater;
    }


    public GridAdapter setSrc(Grid src) {
        grid = src;
        return this;
    }


    public byte row(int position) {
        return (byte)(position / grid.size);
    }


    public byte col(int position) {
        return (byte)(position % grid.size);
    }


    public byte rowFromId(int id) {
        return (byte)(id >> 8);
    }


    public byte colFromId(int id) {
        return (byte)(id & 0x000000ff);
    }


    private void initCell(TextView cell, Cell src) {
        cell.setText(String.valueOf(src.value));
        if (src.revealed) {
            cell.setTextAppearance(context, R.style.revealedText);
            cell.setBackgroundResource(R.color.revealedCellBg);
            cell.setClickable(false);
        } else if (src.selected) {
            cell.setClickable(true);
            cell.setTextAppearance(context, R.style.selectedText);
            cell.setBackgroundResource(R.color.selectedCellBg);
        } else {
            cell.setClickable(true);
            cell.setTextAppearance(context, R.style.normalText);
            cell.setBackgroundResource(R.color.normalCellBg);
        }
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        TextView entry;
        long itemId = (row(position) << 8) | col(position);

        if (convertView == null) {
            entry = (TextView)inflater.inflate(R.layout.grid_text_view, null);
        } else {
            entry = (TextView)convertView;
        }

        entry.setTag(new Integer((row(position) << 8) | col(position)));
        if (grid != null) {
            initCell(entry, grid.entries[row(position)][col(position)]);
        }
        return entry;
    }

    @Override
    public int getCount() {
        return (grid == null) ? 0 : grid.size * grid.size;
    }

    @Override
    public Object getItem(int position) {
        return (grid == null) ? "." : grid.entries[row(position)][col(position)];
    }

    @Override
    public long getItemId(int position) {
        long itemId = (row(position) << 8) | col(position);
        LOG.d(LOGTAG, "getItemId(%d):%d", position, itemId);
        return itemId;
    }
}
