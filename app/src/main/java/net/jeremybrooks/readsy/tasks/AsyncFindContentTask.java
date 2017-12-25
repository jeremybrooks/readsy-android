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
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import net.jeremybrooks.readsy.DropboxHelper;
import net.jeremybrooks.readsy.activites.MainActivity;
import net.jeremybrooks.readsy.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Get a list of available content from Dropbox.
 */

public class AsyncFindContentTask extends AsyncTask<Void, Void, List<Properties>> {
    private MainActivity mainActivity;

    public AsyncFindContentTask(MainActivity mainActivity) {
        super();
        this.mainActivity = mainActivity;
    }

    protected void onPreExecute() {
        this.mainActivity.setBusy(true);
        this.mainActivity.updateList(null);
        this.mainActivity.showToast("Getting content from Dropbox...");
    }

    @Override
    protected List<Properties> doInBackground(Void... params) {
        List<Properties> propertiesList = new ArrayList<>();
        DbxClientV2 client = DropboxHelper.instance().getClient();
        try {
            ListFolderResult result = client.files().listFolder("");
            List<Metadata> list = result.getEntries();
            for (Metadata metadata : list) {
                ByteArrayOutputStream out = null;
                InputStream in = null;
                String path = metadata.getPathLower() + "/metadata";
                try {
                    out = new ByteArrayOutputStream();
                    client.files().download(path).download(out);
                    Properties p = new Properties();
                    in = new ByteArrayInputStream(out.toByteArray());
                    p.load(in);
                    propertiesList.add(p);
                } catch (Exception e) {
                    System.out.println("No file at " + path);
                } finally {
                    Utils.close(in);
                    Utils.close(out);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return propertiesList;
    }

    protected void onPostExecute(List<Properties> propertiesList) {
        this.mainActivity.setBusy(false);
        this.mainActivity.updateList(propertiesList);
    }
}
