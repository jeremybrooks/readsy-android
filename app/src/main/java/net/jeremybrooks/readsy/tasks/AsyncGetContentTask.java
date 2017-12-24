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

import android.os.AsyncTask;

import com.dropbox.core.v2.DbxClientV2;

import net.jeremybrooks.readsy.DropboxHelper;
import net.jeremybrooks.readsy.activites.ShowContentActivity;
import net.jeremybrooks.readsy.Utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;


/**
 * Task to download an entry at a given file path.
 */

public class AsyncGetContentTask extends AsyncTask<Void, Void, Map<String, String>> {
    private ShowContentActivity activity;
    private String filePath;

    public AsyncGetContentTask(ShowContentActivity activity, String filePath) {
        this.activity = activity;
        this.filePath = filePath;
    }

    @Override
    protected void onPreExecute() {
       activity.setBusy(true);
    }

    @Override
    protected Map<String, String> doInBackground(Void... params) {
        Map<String, String> result = new HashMap<>();
        DbxClientV2 client = DropboxHelper.instance().getClient();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedReader in = null;
        try {
            client.files().download(this.filePath).download(out);
            in = new BufferedReader(new StringReader(out.toString("UTF-8")));
            result.put("title", in.readLine());
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = in.readLine()) != null) {
               sb.append(line).append('\n');
            }
            result.put("content", sb.toString());
        } catch (Exception e) {
            result.put("title", "Error downloading " + filePath);
            result.put("content", "There was an error while downloading content from '" + filePath +
            "'. The error message was:\n\n" + e.getLocalizedMessage() + "\n\nThis is most likely a temporary " +
                            "problem. Try again later.");
        } finally {
            Utils.close(in);
            Utils.close(out);
        }

        return result;
    }


    @Override
    protected void onPostExecute(Map<String, String> result) {
        activity.setBusy(false);
        activity.setContent(result);
    }
}
