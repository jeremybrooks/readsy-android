package net.jeremybrooks.readsy.activites;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

import net.jeremybrooks.readsy.R;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        setTitle(R.string.net_jeremybrooks_readsy_helpTitle);

        String version = "unknown";
        int versionCode = 0;

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            version = "unknown";
        } finally {
            String source = getString(R.string.net_jeremybrooks_readsy_help);
            source = source.replace("${version}", version);
            source = source.replace("${versionCode}", Integer.toString(versionCode));
            ((TextView)findViewById(R.id.helpTextView)).setText(Html.fromHtml(source));
        }
    }
}
