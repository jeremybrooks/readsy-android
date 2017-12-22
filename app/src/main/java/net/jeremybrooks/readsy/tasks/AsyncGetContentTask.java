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
