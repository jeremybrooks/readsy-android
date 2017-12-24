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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import net.jeremybrooks.readsy.Constants;
import net.jeremybrooks.readsy.DropboxHelper;
import net.jeremybrooks.readsy.MainListViewAdapter;
import net.jeremybrooks.readsy.R;
import net.jeremybrooks.readsy.tasks.AsyncFindContentTask;

import java.util.List;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setTitle(R.string.net_jeremybrooks_readsy_mainTitle);

    final ListView listView = (ListView) findViewById(R.id.listView);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
        Properties properties = (Properties) adapter.getItemAtPosition(position);

        Intent intent = new Intent(MainActivity.this, ShowContentActivity.class);
        intent.putExtra(Constants.INTENT_MESSAGE_CONTENT_PROPERTIES, properties);
        startActivity(intent);
      }
    });
    listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> adapter, View v,
                                     int position, long id) {
        Properties properties = (Properties) adapter.getItemAtPosition(position);
        Intent intent = new Intent(MainActivity.this, ShowStatusActivity.class);
        intent.putExtra(Constants.INTENT_MESSAGE_CONTENT_PROPERTIES, properties);
        startActivity(intent);
        return true;
      }
    });

    Properties properties = new Properties();
    try {
      properties.load(getResources().openRawResource(R.raw.secrets));
    } catch (Exception e) {
      showMessage(getString(R.string.net_jeremybrooks_readsy_errorTitle),
              getString(R.string.net_jeremybrooks_readsy_errorReadingSecrets, e.getMessage()));
    }

    // set up some preferences if needed
    final SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
    String fontSize = preferences.getString(Constants.KEY_FONT_SIZE, null);
    if (fontSize == null) {
      preferences.edit().putString(Constants.KEY_FONT_SIZE, "14").apply();
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    // see if we have a saved auth token
    final SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
    String token = preferences.getString(Constants.KEY_ACCESS_TOKEN, null);
    if (token == null) {
      // no token, so show dropbox activity
      Intent intent = new Intent(MainActivity.this, DropboxActivity.class);
      startActivity(intent);
    } else {
      DropboxHelper.instance().init(token);
      try {
        AsyncFindContentTask task = new AsyncFindContentTask(this);
        task.execute();
      } catch (Exception e) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
          builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(this.getString(R.string.net_jeremybrooks_readsy_errorTitle))
                .setMessage(this.getString(R.string.net_jeremybrooks_readsy_errorMessageBadToken))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove(Constants.KEY_DROPBOX_KEY);
                    editor.apply();
                  }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.menu, menu);
    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    MenuItem item = menu.findItem(R.id.itemDropbox);
    if (item != null) {
      SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
      if (preferences.contains(Constants.KEY_ACCESS_TOKEN)) {
        item.setTitle(getString(R.string.net_jeremybrooks_readsy_menuDropboxDisconnect));
      } else {
        item.setTitle(getString(R.string.net_jeremybrooks_readsy_menuDropboxConnect));
      }
    }
    return true;
  }


  /**
   * Respond to clicks on the Dropbox menu item.
   *
   * @param menuItem menu item.
   */
  public void menuItemDropboxClicked(MenuItem menuItem) {
    Intent intent = new Intent(MainActivity.this, DropboxActivity.class);
    SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
    if (preferences.contains(Constants.KEY_ACCESS_TOKEN)) {
      preferences.edit().remove(Constants.KEY_ACCESS_TOKEN).apply();
      intent.putExtra("revoke", true);
    }
    startActivity(intent);
  }

  /**
   * Respond to taps on the font menu.
   *
   * @param menuItem menu item.
   */
  public void menuItemFontClicked(MenuItem menuItem) {
    Intent intent = new Intent(MainActivity.this, FontActivity.class);
    startActivity(intent);
  }

  /**
   * Reset messages menu item. Should probably be in the settings....
   *
   * @param menuItem
   */
  public void menuItemResetClicked(MenuItem menuItem) {
    // TODO implement in settings
    final SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    editor.remove(Constants.KEY_WELCOME_SHOWN);
    editor.apply();
  }

  /**
   * Respond to taps on the help menu.
   *
   * @param menuItem menu item.
   */
  public void menuItemHelpClicked(MenuItem menuItem) {
    Intent intent = new Intent(MainActivity.this, HelpActivity.class);
    startActivity(intent);
  }


  /**
   * Callback used to set the busy indicator progress bar. Call from the
   * onPreExecute and onPostExecute methods, so it's on the GUI thread.
   *
   * @param busy flag indicating if the progress bar should be shown.
   */
  public void setBusy(boolean busy) {
    if (busy) {
      this.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    } else {
      this.findViewById(R.id.progressBar).setVisibility(View.GONE);
    }
  }

  /**
   * Callback used to indicate that new content should be displayed in the list view.
   * Called from the onPreExecute or onPostExecute methods, so it's on the GUI thread.
   *
   * @param list list of properties to display.
   */
  public void updateList(List<Properties> list) {
    MainListViewAdapter adapter = new MainListViewAdapter(this, list);
    ListView listView = (ListView) findViewById(R.id.listView);
    listView.setAdapter(adapter);
  }

  /**
   * Show a message using the Toast API.
   *
   * @param message the message to show.
   */
  public void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
  }

  /**
   * Show an alert dialog message.
   *
   * @param title   title for the dialog.
   * @param message message for the dialog.
   */
  public void showMessage(String title, String message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
    } else {
      builder = new AlertDialog.Builder(this);
    }
    builder.setTitle(title)
            .setMessage(message)
            .setNeutralButton(android.R.string.ok, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
  }
}
