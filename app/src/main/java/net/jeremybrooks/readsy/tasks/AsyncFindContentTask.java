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
        this.mainActivity.updateList(new ArrayList<Properties>());
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
