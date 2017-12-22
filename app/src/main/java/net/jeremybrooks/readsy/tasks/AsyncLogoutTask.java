package net.jeremybrooks.readsy.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import net.jeremybrooks.readsy.Constants;
import net.jeremybrooks.readsy.activites.MainActivity;

import java.util.ArrayList;
import java.util.Properties;

//import com.cloudrail.si.services.Dropbox;

/**
 * Log out of Dropbox and delete the local token.
 */

public class AsyncLogoutTask extends AsyncTask<Void, Void, Void> {
//    private Dropbox dropbox;
    private MainActivity mainActivity;

    public AsyncLogoutTask(MainActivity mainActivity) {
//        this.dropbox = DropboxHelper.instance(mainActivity).getDropbox();
        this.mainActivity = mainActivity;
    }

    @Override
    protected void onPreExecute() {
        this.mainActivity.setBusy(true);
    }

    @Override
    protected Void doInBackground(Void... params) {
//        this.dropbox.logout();
        SharedPreferences preferences = this.mainActivity.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(Constants.KEY_ACCESS_TOKEN);
        editor.apply();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        this.mainActivity.setBusy(false);
        this.mainActivity.updateList(new ArrayList<Properties>());
    }
}
