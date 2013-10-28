package com.octagonsoftware.appolantern.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;

/**
 * Adapted from
 * http://www.donnfelker.com/android-a-simple-eula-for-your-android-apps/
 * 
 * @author mroth
 */

public class Eula
{
    private static final String LOG_TAG = "eula";

    private final String _eulaKey;
    private final SharedPreferences _prefs;
    private String EULA_PREFIX = "eula_";
    private AppOLanternActivity _activity;

    public Eula(AppOLanternActivity context) {
        _activity = context;
        _eulaKey = EULA_PREFIX + _activity.getString(R.string.eula_version);
        _prefs = PreferenceManager.getDefaultSharedPreferences(_activity);
    }

    public boolean hasBeenShown() {
        // the eulaKey changes every time you increment the version number in
        // the AndroidManifest.xml
        return _prefs.getBoolean(_eulaKey, false);
    }

    public void show()
    {
        // Show the Eula
        String title = _activity.getString(R.string.app_name) + " v" + _activity.getString(R.string.app_version);

        // Includes the updates as well so users know what changed.
        String message = _activity.getString(R.string.eula);

        AlertDialog.Builder builder = new AlertDialog.Builder(_activity).setTitle(title).setMessage(Html.fromHtml(message))
            .setPositiveButton("Accept", new Dialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    // Mark this version as read.
                    SharedPreferences.Editor editor = _prefs.edit();
                    editor.putBoolean(_eulaKey, true);
                    editor.commit();
                    dialogInterface.dismiss();
                    eulaAccepted();
                }
            }).setNegativeButton("Refuse", new Dialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // Close the activity as they have declined the EULA
                    _activity.finish();
                }
            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    // Close the activity as they have declined the EULA
                    _activity.finish();
                }
            });
        builder.create().show();
    }
    
    private void eulaAccepted() {
        _activity.onEulaAccepted();
    }
}
