package net.jeremybrooks.readsy.activites;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.dropbox.core.android.Auth;

import net.jeremybrooks.readsy.Constants;
import net.jeremybrooks.readsy.R;

/**
 * Activity allowing user to authorize Dropbox access.
 */
public class DropboxActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox);
        setTitle(R.string.net_jeremybrooks_readsy_connectTitle);
        Button connectButton = (Button)findViewById(R.id.buttonConnect);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIntent().removeExtra("revoke");
                Auth.startOAuth2Authentication(DropboxActivity.this, getString(R.string.app_key));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // if there is an extra with revoke=true, we got here
        // after the user disconnected Dropbox, so skip the
        // check for a valid token. This will force the user to
        // connect again.
        // This avoid the problem where a user connected, then disconnected immediately,
        // but the Auth object still had a valid token so this activity just finished.
        boolean revoked = getIntent().getBooleanExtra("revoke", false);

        if (!revoked) {
            SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE);
            String accessToken = prefs.getString(Constants.KEY_ACCESS_TOKEN, null);
            if (accessToken == null) {
                accessToken = Auth.getOAuth2Token();
                if (accessToken != null) {
                    prefs.edit().putString(Constants.KEY_ACCESS_TOKEN, accessToken).apply();
                    finish(); // back to main, where we will init dropbox helper and load content
                } else {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(this);
                    }
                    builder.setTitle(getString(R.string.net_jeremybrooks_readsy_errorTitle))
                            .setMessage(getString(R.string.net_jeremybrooks_readsy_errorMessageNoToken))
                            .setNeutralButton(android.R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        }
    }
}
