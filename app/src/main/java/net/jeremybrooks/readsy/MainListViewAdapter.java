/*
 * readsy - read something new every day <http://jeremybrooks.net/readsy>
 *
 * Copyright (c) 2017  Jeremy Brooks
 *
 * This file is part of readsy for Android.
 *
 * readsy for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * readsy for Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with readsy for Android.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package net.jeremybrooks.readsy;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class MainListViewAdapter extends ArrayAdapter<Properties> {
    private final Context context;
    private final List<Properties> values;
    private final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

    public MainListViewAdapter(Context context, List<Properties> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Properties p = values.get(position);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.main_list_layout, parent, false);

        TextView firstLine = rowView.findViewById(R.id.firstLine);
        firstLine.setText(p.getProperty("description"));

        TextView secondLine = rowView.findViewById(R.id.secondLine);
        BitHelper bitHelper = new BitHelper(p.getProperty("read"));
        Date today = new Date();
        if (p.getProperty("year").equals("0") ||
                yearFormat.format(today).equals(p.getProperty("year"))) {

            if (bitHelper.isRead(today)) {
                secondLine.setText(R.string.net_jeremybrooks_readsy_upToDate);
            } else {
                int count = bitHelper.getUnreadItemCount(today, p.getProperty("year"));
                Resources r = context.getResources();
                if (count == 1) {
                    secondLine.setText(r.getString(R.string.net_jeremybrooks_readsy_unreadItem, count));
                } else {
                    secondLine.setText(r.getString(R.string.net_jeremybrooks_readsy_unreadItems, count));
                }
            }
        } else {
            secondLine.setText(R.string.net_jeremybrooks_readsy_nothingThisYear);
        }

        return rowView;
    }

    @Override
    public Properties getItem(int position){
        return values.get(position);
    }

}
