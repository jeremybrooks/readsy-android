package net.jeremybrooks.readsy.activites;

import android.content.Context;

/**
 *
 */
public interface ReadsyActivity {
  void setBusy(boolean busy);
  void showMessage(String title, String message);
  Context getContext();
}
