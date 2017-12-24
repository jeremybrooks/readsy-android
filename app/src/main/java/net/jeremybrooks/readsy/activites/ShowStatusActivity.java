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
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import net.jeremybrooks.readsy.BitHelper;
import net.jeremybrooks.readsy.Constants;
import net.jeremybrooks.readsy.R;
import net.jeremybrooks.readsy.tasks.AsyncSaveEntryMetadataTask;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ShowStatusActivity extends AppCompatActivity implements ReadsyActivity {

  private Properties entryMetadata;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_show_status);

    // the Properties defining the entry metadata are passed as a HashMap using Extras,
    // so convert to a Properties object here.
    Map<String, String> extras = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.INTENT_MESSAGE_CONTENT_PROPERTIES);
    this.entryMetadata = new Properties();
    this.entryMetadata.putAll(extras);

    setTitle(R.string.net_jeremybrooks_readsy_statusTitle);

    ((TextView) findViewById(R.id.statusDescriptionTextView)).setText(entryMetadata.getProperty(Constants.METADATA_DESCRIPTION));
    String year = entryMetadata.getProperty(Constants.METADATA_KEY_YEAR);
    if (year.equals("0")) {
      ((TextView) findViewById(R.id.statusValidTextView)).setText(
              getResources().getString(R.string.net_jeremybrooks_readsy_statusValidAnyYear));
    } else {
      ((TextView) findViewById(R.id.statusValidTextView)).setText(
              getResources().getString(R.string.net_jeremybrooks_readsy_statusValid, year));
    }
    ((TextView) findViewById(R.id.statusVersionTextView)).setText(
            getResources().getString(R.string.net_jeremybrooks_readsy_statusVersion,
                    entryMetadata.getProperty(Constants.METADATA_KEY_VERSION)));

    updateStatus();
  }

  private void updateStatus() {
    String year = entryMetadata.getProperty(Constants.METADATA_KEY_YEAR);
    BitHelper bitHelper = new BitHelper(entryMetadata.getProperty(Constants.METADATA_KEY_READ));
    GregorianCalendar lastDayOfYear = new GregorianCalendar();
    float currentDayOfYear = lastDayOfYear.get(Calendar.DAY_OF_YEAR);
    // set to dec 31
    lastDayOfYear.set(GregorianCalendar.MONTH, GregorianCalendar.DECEMBER);
    lastDayOfYear.set(GregorianCalendar.DAY_OF_MONTH, 31);

    int unread = bitHelper.getUnreadItemCount(lastDayOfYear.getTime(), year);
    int daysInYear = lastDayOfYear.get(Calendar.DAY_OF_YEAR);
    int percentComplete = (int) (((float) daysInYear - unread) / daysInYear * 100);

    ((TextView) findViewById(R.id.statusPercent)).setText(
            getResources().getString(R.string.net_jeremybrooks_readsy_statusRead, percentComplete));

    int percentTarget = (int) (((float) daysInYear - (daysInYear - currentDayOfYear)) / daysInYear * 100);
    if (percentComplete < percentTarget) {
      ((TextView) findViewById(R.id.statusTarget)).setText(
              getResources().getString(R.string.net_jeremybrooks_readsy_statusTarget, percentTarget,
                      daysInYear - (daysInYear - currentDayOfYear), daysInYear));
    } else {
      ((TextView) findViewById(R.id.statusTarget)).setText(
              getResources().getString(R.string.net_jeremybrooks_readsy_statusOnSchedule));
    }
  }

  /**
   * Mark all previous days as "read".
   *
   * @param view
   */
  public void markPrevious(View view) {
    AlertDialog.Builder builder;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
    } else {
      builder = new AlertDialog.Builder(this);
    }
    final ReadsyActivity activity = this;
    builder.setTitle(this.getString(R.string.net_jeremybrooks_readsy_status_markRead_title))
            .setMessage(this.getString(R.string.net_jeremybrooks_readsy_status_markRead_message))
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                BitHelper bitHelper = new BitHelper(entryMetadata.getProperty(Constants.METADATA_KEY_READ));
                Calendar cal = new GregorianCalendar();
                int today = cal.get(Calendar.DAY_OF_YEAR);
                cal.set(Calendar.DAY_OF_YEAR, 1);
                while (cal.get(Calendar.DAY_OF_YEAR) != today) {
                  bitHelper.setRead(cal.getTime(), true);
                  cal.add(Calendar.DAY_OF_YEAR, 1);
                }
                entryMetadata.setProperty(Constants.METADATA_KEY_READ, bitHelper.toString());
                AsyncSaveEntryMetadataTask task = new AsyncSaveEntryMetadataTask(activity, entryMetadata);
                task.execute();
              }
            })
            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                // do nothing
              }
            })
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
  }


  /**
   * Mark all days as "unread".
   *
   * @param view
   */
  public void resetStatus(View view) {
    AlertDialog.Builder builder;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
    } else {
      builder = new AlertDialog.Builder(this);
    }
    final ReadsyActivity activity = this;
    builder.setTitle(this.getString(R.string.net_jeremybrooks_readsy_status_reset_title))
            .setMessage(this.getString(R.string.net_jeremybrooks_readsy_status_reset_message))
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                entryMetadata.setProperty(Constants.METADATA_KEY_READ, new BitHelper().toString());
                AsyncSaveEntryMetadataTask task = new AsyncSaveEntryMetadataTask(activity, entryMetadata);
                task.execute();
              }
            })
            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                // do nothing
              }
            })
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
  }

  @Override
  public void setBusy(boolean busy) {
    if (busy) {
      findViewById(R.id.statusViewProgressBar).setVisibility(View.VISIBLE);
    } else {
      findViewById(R.id.statusViewProgressBar).setVisibility(View.GONE);
      updateStatus();
    }
  }

  @Override
  public void showMessage(String title, String message) {
    updateStatus();
    AlertDialog.Builder builder;
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

  @Override
  public Context getContext() {
    return this;
  }
}
