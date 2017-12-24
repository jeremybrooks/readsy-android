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
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import net.jeremybrooks.readsy.BitHelper;
import net.jeremybrooks.readsy.Constants;
import net.jeremybrooks.readsy.R;
import net.jeremybrooks.readsy.tasks.AsyncGetContentTask;
import net.jeremybrooks.readsy.tasks.AsyncSaveEntryMetadataTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class ShowContentActivity extends AppCompatActivity implements View.OnClickListener, ReadsyActivity {
    // gesture/motion constants
    private static final int SWIPE_MAX_OFF_PATH = 30;
    private static final int SWIPE_MIN_DISTANCE = 150;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    private static final int SHAKE_THRESHOLD = 5;

    private static final long DAY_MILLIS = 24 * 60 * 60 * 1000;

    // shake detection
    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity

    // gesture detection
    GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

    private SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US);
    private SimpleDateFormat fileFormatter = new SimpleDateFormat("MMdd", Locale.US);
    private SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy", Locale.US);
    private Date date;
    Properties entryMetadata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_content);

        gestureDetector = new GestureDetector(this, new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };

        // shake detection
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        // respond to gestures on the text view
        findViewById(R.id.contentTextView).setOnClickListener(ShowContentActivity.this);
        findViewById(R.id.contentTextView).setOnTouchListener(gestureListener);

        // by default display content for the current date
        this.date = new Date();

        // the Properties defining the entry metadata are passed as a HashMap using Extras,
        // so convert to a Properties object here.
        Map<String, String> extras = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.INTENT_MESSAGE_CONTENT_PROPERTIES);
        this.entryMetadata = new Properties();
        this.entryMetadata.putAll(extras);

        setTitle(this.entryMetadata.getProperty(Constants.METADATA_KEY_SHORT_DESCRIPTION));

        // set font size
        float fontSize;
        try {
            String size = getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE)
                    .getString(Constants.KEY_FONT_SIZE, "14");
            fontSize = Float.valueOf(size);
        } catch (Exception e) {
            fontSize = 14f;
        }
        TextView date = this.findViewById(R.id.dateTextView);
        date.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        TextView content = this.findViewById(R.id.contentTextView);
        content.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        CheckBox checkBox = this.findViewById(R.id.checkBox);
        checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

        // get the content. This should be the last call in onCreate
        kickOffTask();
    }

    /*
     * Update display and start a background task to get an entry.
     * This should be called when the activity is displayed, and after the user navigates to a
     * different day by swiping/shaking.
     */
    private void kickOffTask() {
        ((TextView) findViewById(R.id.dateTextView)).setText(sdf.format(date));
        ((TextView) findViewById(R.id.contentTextView)).setText(R.string.net_jeremybrooks_readsy_loading);
        String year = this.entryMetadata.getProperty(Constants.METADATA_KEY_YEAR);
        if (year.equals("0") ||
                this.yearFormatter.format(date).equals(year)) {
            String filePath = "/" + entryMetadata.getProperty(Constants.METADATA_KEY_SHORT_DESCRIPTION) + "/" + fileFormatter.format(this.date);
            AsyncGetContentTask task = new AsyncGetContentTask(this, filePath);
            task.execute();
        } else {
            CheckBox checkBox = (CheckBox) this.findViewById(R.id.checkBox);
            checkBox.setEnabled(false);
            Map<String, String> content = new HashMap<>();
            content.put("title", this.getString(R.string.net_jeremybrooks_readsy_noEntryFound));
            content.put("content", "");
            this.setContent(content);
        }
    }

    /**
     * Respond to taps on the "Read" checkbox.
     * Updates the metadata, changing the state of the "read" bit for the entry currently displayed,
     * then starts a background task to upload the metadata to Dropbox.
     *
     * @param view the view that was tapped.
     */
    public void markRead(View view) {
        CheckBox checkBox = (CheckBox) view;
        BitHelper bitHelper = new BitHelper(this.entryMetadata.getProperty(Constants.METADATA_KEY_READ));
        bitHelper.setRead(this.date, checkBox.isChecked());
        this.entryMetadata.setProperty(Constants.METADATA_KEY_READ, bitHelper.toString());
        AsyncSaveEntryMetadataTask task = new AsyncSaveEntryMetadataTask(this, this.entryMetadata);
        task.execute();
    }

    /**
     * Callback used to indicate that new content should be displayed.
     * Called from the onPreExecute or onPostExecute methods, so it's on the GUI thread.
     *
     * @param content map with the content to display.
     */
    public void setContent(Map<String, String> content) {
        boolean isRead = new BitHelper(this.entryMetadata.getProperty(Constants.METADATA_KEY_READ)).isRead(date);
        ((CheckBox) findViewById(R.id.checkBox)).setChecked(isRead);

        String title = content.get("title");
        String body = content.get("content");

        SpannableString spanString = new SpannableString(title + "\n\n" + body);
        spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, title.length(), 0);
        ((TextView) findViewById(R.id.contentTextView)).setText(spanString);
    }

    /**
     * Callback used to set the busy indicator progress bar. Call from the
     * onPreExecute and onPostExecute methods, so it's on the GUI thread.
     *
     * @param busy flag indicating if the progress bar should be shown.
     */
    public void setBusy(boolean busy) {
        if (busy) {
            findViewById(R.id.contentViewProgressBar).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.contentViewProgressBar).setVisibility(View.GONE);
        }
    }

    /**
     * Show a message dialog.
     *
     * @param title the title for the dialog.
     * @param message the message for the dialog.
     */
    public void showMessage(String title, String message) {
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
    public void onClick(View v) {
        // empty implementation
    }


    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                    return false;
                }

                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    date = new Date(date.getTime() + DAY_MILLIS);
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    date = new Date(date.getTime() - DAY_MILLIS);
                }
                kickOffTask();
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter
            if (mAccel > SHAKE_THRESHOLD) {
                Calendar now = new GregorianCalendar();
                Calendar c2 = new GregorianCalendar();
                c2.setTime(date);
                if (c2.get(Calendar.YEAR) != now.get(Calendar.YEAR) ||
                        c2.get(Calendar.MONTH) != now.get(Calendar.MONTH) ||
                        c2.get(Calendar.DAY_OF_MONTH) != now.get(Calendar.DAY_OF_MONTH)) {
                    date = new Date();
                    kickOffTask();
                }
            }
        }
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    public Context getContext() {
        return this;
    }
}
