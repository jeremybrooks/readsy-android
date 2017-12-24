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

package net.jeremybrooks.readsy;


import android.content.Context;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.http.OkHttp3Requestor;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import java.util.List;

//import com.cloudrail.si.CloudRail;
//import com.cloudrail.si.exceptions.ParseException;
//import com.cloudrail.si.services.Dropbox;

/**
 * Created by jeremyb on 10/12/17.
 */

public class DropboxHelper {
//    private Dropbox dropbox;
    private Context context;
    private DbxClientV2 client;

    private static DropboxHelper instance;
    private String accessToken;

    private DropboxHelper() {}

    public static DropboxHelper instance() {
        if (instance == null) {
           instance = new DropboxHelper();
        }
        return instance;
    }

    public void init(String accessToken) {
        this.accessToken = accessToken;
        DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder("readsy-android")
                .withHttpRequestor(new OkHttp3Requestor(OkHttp3Requestor.defaultOkHttpClient()))
                .build();
        this.client = new DbxClientV2(requestConfig, accessToken);
//        CloudRail.setAppKey(properties.getProperty(Constants.KEY_CLOUDRAIL_LICENSE));
//        String key = properties.getProperty(Constants.KEY_DROPBOX_KEY);
//        String secret = properties.getProperty(Constants.KEY_DROPBOX_SECRET);
//        this.dropbox = new Dropbox(this.context, key, secret,
//                "https://auth.cloudrail.com/net.jeremybrooks.readsy",
//                "readsy_state");
    }

    public DbxClientV2 getClient() {
        return this.client;
    }
    public void getContentList() {
        try {
            ListFolderResult result = this.client.files().listFolder("/");
            List<Metadata> list = result.getEntries();
            for (Metadata metadata : list) {
                System.out.println(metadata);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
