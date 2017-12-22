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

    // todo set initial position of spinner
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
