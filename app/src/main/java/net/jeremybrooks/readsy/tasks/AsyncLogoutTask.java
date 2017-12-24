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
