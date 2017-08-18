package th.or.nectec.party.offline.android.Partii2Go.helper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.jetbrains.annotations.NotNull;
import th.or.nectec.partii.embedded.android.EmbeddedUtils.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kotlin.Pair;
import th.or.nectec.partii.embedded.android.EmbeddedUtils.ModelUtil;
import th.or.nectec.party.offline.android.Partii2Go.R;
import th.or.nectec.party.offline.android.Partii2Go.util.DilaogUtil;

import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by suwan on 11/19/2016 AD.
 */

public class LangModelHelper {

    private ModelUtil mUtil = new ModelUtil();

    public void buildLanguageModel(final Context context,
                                   final AppCompatActivity app, final File deviceDir){

        final String apikeyStr = PreferenceManager.getDefaultSharedPreferences(context).getString("apikey_preference", null);

        if(apikeyStr == null || apikeyStr.trim().length() <= 0) {
            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.prompts, null);
            final EditText userInput = (EditText) promptsView.findViewById(R.id.api_key);

            final TextView label = (TextView) promptsView.findViewById(R.id.apikey_label);
            label.setText(Constant.APIKEY_SUMMARY);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setView(promptsView);

            // set dialog message
            alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Download",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String apikey = userInput.getText().toString().trim();
                                mUtil.startDownload(context, app, deviceDir, apikey);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

            // create alert dialog
            final AlertDialog downloadDialog = alertDialogBuilder.create();

            downloadDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {
                    if (userInput.getText().toString().length() > 0) {
                        downloadDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    } else {
                        downloadDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    }
                }
            });

            // show it
            downloadDialog.show();

            final TextWatcher tt = new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        downloadDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("apikey_preference", s.toString());
                        editor.commit();
                    }
                    else {
                        downloadDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    }
                }
            };
            userInput.addTextChangedListener(tt);
        }
        else{

            final AlertDialog.Builder downloadDialog = new AlertDialog.Builder(context)
                .setTitle("Download Model")
                .setMessage(Constant.DOWNLOAD_CONFIRM)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mUtil.startDownload(context, app, deviceDir, apikeyStr.trim());
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info);

            downloadDialog.show();
        }
    }

    private String getPathWithSlashEnding(String path){
        path = path.trim();
        if(!path.endsWith("/")){
            path += "/";
        }

        return path;
    }

}
