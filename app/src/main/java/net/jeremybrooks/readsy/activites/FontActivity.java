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
package net.jeremybrooks.readsy.activites;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import net.jeremybrooks.readsy.Constants;
import net.jeremybrooks.readsy.R;

public class FontActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_font);
    setTitle(R.string.net_jeremybrooks_readsy_fontTitle);

    Spinner sizeSpinner = findViewById(R.id.spinnerSize);
    sizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        Spinner spinner = findViewById(R.id.spinnerSize);
        String size = (String) spinner.getSelectedItem();
        TextView tv = findViewById(R.id.fontSampleText);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.valueOf(size));
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(Constants.KEY_FONT_SIZE, size).apply();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parentView) {
        // nothing to do here
      }
    });

    SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
    String fontSize = sharedPreferences.getString(Constants.KEY_FONT_SIZE, "14");
    Adapter adapter = sizeSpinner.getAdapter();
    int n = adapter.getCount();
    for (int i = 0; i < n; i++) {
      String size = (String) adapter.getItem(i);
      if (size.equals(fontSize)) {
        sizeSpinner.setSelection(i);
        break;
      }
    }
  }
}
