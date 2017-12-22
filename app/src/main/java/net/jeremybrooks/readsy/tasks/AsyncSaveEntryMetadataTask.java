package net.jeremybrooks.readsy.tasks;

import android.os.AsyncTask;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.WriteMode;

import net.jeremybrooks.readsy.DropboxHelper;
import net.jeremybrooks.readsy.R;
import net.jeremybrooks.readsy.activites.ShowContentActivity;
import net.jeremybrooks.readsy.Utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;


/**
 * Task to save the metadata for a readsy entry.
 */

public class AsyncSaveEntryMetadataTask extends AsyncTask<Void, Void, Boolean> {

    private ShowContentActivity activity;
    private Properties entryMetadata;
    private String errorMessage;

    public AsyncSaveEntryMetadataTask(ShowContentActivity activity, Properties entryMetadata) {
        this.activity = activity;
        this.entryMetadata = entryMetadata;
    }

    @Override
    protected void onPreExecute() {
        this.activity.setBusy(true);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean success = true;
        DbxClientV2 client = DropboxHelper.instance().getClient();

        String filePath = "/" + this.entryMetadata.getProperty("shortDescription") + "/metadata";

        StringBuilder sb = new StringBuilder();
        sb.append("#readsy Android").append('\n');
        sb.append("#").append(new Date()).append('\n');
        sb.append("version=").append(this.entryMetadata.getProperty("version")).append('\n');
        sb.append("year=").append(this.entryMetadata.getProperty("year")).append('\n');
        sb.append("description=").append(this.entryMetadata.getProperty("description")).append('\n');
        sb.append("read=").append(this.entryMetadata.getProperty("read")).append('\n');
        sb.append("shortDescription=").append(this.entryMetadata.getProperty("shortDescription")).append('\n');

        InputStream in = null;
        try {
            in = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
            client.files().uploadBuilder(filePath).withMode(WriteMode.OVERWRITE).uploadAndFinish(in);
        } catch (Exception e) {
            errorMessage = e.getMessage();
            success = false;
        } finally {
            Utils.close(in);
        }
        return success;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        this.activity.setBusy(false);
        if (!result) {
            this.activity.showMessage(activity.getString(R.string.net_jeremybrooks_readsy_errorTitle),
                    activity.getString(R.string.net_jeremybrooks_readsy_errorMessageUpload, errorMessage));
        }
    }

}
