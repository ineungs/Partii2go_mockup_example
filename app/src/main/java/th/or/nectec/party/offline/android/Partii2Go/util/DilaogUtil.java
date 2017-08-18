package th.or.nectec.party.offline.android.Partii2Go.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import mehdi.sakout.aboutpage.AboutPage;
import th.or.nectec.partii.embedded.android.EmbeddedUtils.Constant;
import th.or.nectec.party.offline.android.Partii2Go.R;

/**
 * Created by suwan on 11/18/2016 AD.
 */

public class DilaogUtil {
    ProgressDialog barProgressDialog;
    Handler updateBarHandler;

    public DilaogUtil(){
        updateBarHandler = new Handler();
    }

    public void showMessageDialog(Context context, String title, String message){
        new AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                }
            })
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }

    public ProgressDialog launchRingDialog(AppCompatActivity app, String title, String bodyText) {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(app, title,	bodyText, false);
        ringProgressDialog.setCancelable(false);

        return ringProgressDialog;
    }

    public ProgressDialog getProgress(AppCompatActivity app) {
        barProgressDialog = new ProgressDialog(app);
        barProgressDialog.setCancelable(false);
        barProgressDialog.setTitle(Constant.DOWNLOAD_STATUS);
        barProgressDialog.setMessage(Constant.DOWNLOAD_INPROGRESS);
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(100);

        return barProgressDialog;
    }

    public void showAboutDialog(Context context){
        View aboutPage = new AboutPage(context)
            .isRTL(false)
            .setImage(R.drawable.party)
            //.addItem(versionElement)
            //.addItem(adsElement)
            .setDescription(Constant.PARTII_DESCRIPTION)
            .addEmail("info@nectec.or.th")
            .addWebsite("https://www.nectec.or.th/")
            .addFacebook("nectec")
            .addTwitter("nectec")
            .create();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
            context) .setTitle("About us")
            .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                }
            })
            .setIcon(android.R.drawable.ic_dialog_info);

        alertDialogBuilder.setView(aboutPage);

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();

    }
}
