package th.or.nectec.party.offline.android.Partii2Go;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import th.or.nectec.party.offline.android.Partii2Go.button.ConvertingButton;
import th.or.nectec.party.offline.android.Partii2Go.button.MainButton;
import th.or.nectec.party.offline.android.Partii2Go.button.RecordingButton;
import th.or.nectec.party.offline.android.Partii2Go.helper.LangModelHelper;
import th.or.nectec.party.offline.android.Partii2Go.util.DilaogUtil;

import android.view.Menu;
import android.view.MenuItem;
import android.content.Context;
import android.util.Log;

import th.or.nectec.partii.embedded.android.EmbeddedUtils.ModelUtil;
import th.or.nectec.partii.embedded.android.EmbeddedUtils.Constant;
import th.or.nectec.partii.embedded.android.RecognitionListener;
import th.or.nectec.partii.embedded.android.SpeechRecognizer;

public class MainActivity extends AppCompatActivity implements RecognitionListener, ModelUtil.OnReceiveStatusListener {

    private TextView messageText;
    private TextView resultText;
    private TextView resultHeader;
    private CardView resultPanel;

    MainButton defaultButton;
    RecordingButton dictatingButton;
    ConvertingButton processingButton;
    private SpeechRecognizer recognizer;

    private String decodedStr = "";
    private boolean isSetupRecognizer = false;
    private ModelUtil mUtil = new ModelUtil();


    @Override
    protected void onResume() {
        super.onResume();

        if (!isSetupRecognizer) {

        }

        String langStr = PreferenceManager.getDefaultSharedPreferences(this).getString("language_preference", null);
        if (langStr != null) {

            if (langStr.equals("TH")) {
                Constant.setThai();
            } else {
                Constant.setEnglish();
            }
        }

        messageText.setText(Constant.CONVERSION_START_MSG);
        resultHeader.setText(Constant.CONVERSION_HEADER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.bar_img);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        resultPanel = (CardView) findViewById(R.id.resultPanel);
        messageText = (TextView) findViewById(R.id.textView1);
        resultText = (TextView) findViewById(R.id.resultText);
        resultHeader = (TextView) findViewById(R.id.resultHeader);

        defaultButton = (MainButton) findViewById(R.id.main);
        defaultButton.setImageResource(R.drawable.main);

        processingButton = (ConvertingButton) findViewById(R.id.convert_start);
        processingButton.setImageResource(R.drawable.convert_start);

        dictatingButton = (RecordingButton) findViewById(R.id.sound_record);
        dictatingButton.setImageResource(R.drawable.sound_record2);

        resultHeader.setText(Constant.CONVERSION_HEADER);
        resultText.setText("");
        messageText.setText(Constant.CONVERSION_START_MSG);

        resultPanel.setVisibility(View.GONE);
        messageText.setVisibility(View.VISIBLE);

        defaultButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isSetupRecognizer) {
                    defaultButton.setVisibility(View.GONE);
                    processingButton.setVisibility(View.GONE);
                    dictatingButton.setVisibility(View.VISIBLE);
                    recognizer.startListening();
                    displayMessage(Constant.CONVERSION_SPEAKING_MSG);
                } else {
                    if (mUtil.isPermissionGranted(getApplicationContext())) {
                        File f = new File(getExternalFilesDir("") + "/sync" + "/assets.lst");
                        if (f.exists()) {
                            setUpRecognizer();

                            defaultButton.setVisibility(View.GONE);
                            processingButton.setVisibility(View.GONE);
                            dictatingButton.setVisibility(View.VISIBLE);
                            recognizer.startListening();
                            displayMessage(Constant.CONVERSION_SPEAKING_MSG);
                        } else {
                            final AlertDialog.Builder infoDialog = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Warning")
                                    .setMessage(Constant.LANG_MODEL_NOT_DOWNLOAD)
                                    .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    });
                            infoDialog.show();
                        }
                    } else {
                        mUtil.requestPermission(getApplicationContext());
                    }
                }
            }
        });

        dictatingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dictatingButton.setVisibility(View.GONE);
                defaultButton.setVisibility(View.GONE);
                processingButton.setVisibility(View.VISIBLE);

                displayMessage(Constant.CONVERSION_IN_PROGRESS);
                recognizer.stop();
            }
        });

        processingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dictatingButton.setVisibility(View.GONE);
                processingButton.setVisibility(View.GONE);
                defaultButton.setVisibility(View.VISIBLE);
                recognizer.stop();
                displayMessage(Constant.CONVERSION_START_MSG);
            }
        });

        if (mUtil.isPermissionGranted(getApplicationContext())) {
            File f = new File(getExternalFilesDir("") + "/sync");

            if (f.isDirectory() && !isSetupRecognizer) {
                setUpRecognizer();
            }
        } else {
            mUtil.requestPermission(getApplicationContext());
        }
       // System.out.println("version="+mUtil.getVersion());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.download:

                final Context context = this;
                new LangModelHelper().buildLanguageModel(context,
                        MainActivity.this, getExternalFilesDir(""));

                return (true);

            case R.id.about:
                new DilaogUtil().showAboutDialog(this);
                return (true);

            case R.id.setting:
                Intent i = new Intent(this, SettingPreferencesActivity.class);
                startActivity(i);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }

    public void setUpRecognizer() {
        Log.d("Recognizer", "Setting recognizer");

        recognizer = mUtil.getRecognizer(this);
        if (recognizer.getDecoder() == null) {
            finish();
        }
        recognizer.addListener(this);
        isSetupRecognizer = true;

    }

    private View.OnClickListener onRecordClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        }
    };

    private void hideMessage() {
        messageText.animate()
                .translationY(1000)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        messageText.setVisibility(View.GONE);
                        messageText.setTranslationY(0);
                    }
                });
    }

    private void hideResultPanel() {

        resultPanel.animate()
                .translationY(1000)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        resultPanel.setVisibility(View.GONE);
                        resultPanel.setTranslationY(0);
                    }
                });
    }

    private void displayMessage(final String message) {
        hideResultPanel();

        messageText.animate()
                .translationY(1000)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        messageText.setVisibility(View.GONE);
                        messageText.setTranslationY(0);
                        messageText.setText(message);
                        messageText.animate()
                                .alpha(1.0f)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        messageText.setVisibility(View.VISIBLE);
                                    }
                                });
                    }
                });
    }

    private void displayResultPanel(final String message) {
        System.out.println("displayResultPanel:" + message);
        hideMessage();
        resultText.setText(message);
        resultPanel.animate()
                .translationY(1000)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        resultPanel.setVisibility(View.GONE);
                        resultPanel.setTranslationY(0);

                        resultPanel.animate()
                                .alpha(1.0f)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        resultPanel.setVisibility(View.VISIBLE);
                                    }
                                });
                    }
                });
    }


    private View.OnClickListener onStopClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            recognizer.stop();
        }
    };

    private View.OnClickListener onClearClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        recognizer.cancel();
        recognizer.shutdown();
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onProgress(int i) {
    }

    @Override
    public void onEndOfSpeech() {
    }

    @Override
    public void onPartialResult(String s) {

        if (s != null) {
            if (!s.equals(SpeechRecognizer.NO_HYP) &&
                    !s.equals(SpeechRecognizer.REQUEST_NEXT)) {
                decodedStr = s + " ";
                System.out.println("onPartialResult:" + decodedStr);
            }
        }
    }

    @Override
    public void onResult(String s) {
        if (s != null) {
            if (!s.equals(SpeechRecognizer.NO_HYP) &&
                    !s.equals(SpeechRecognizer.REQUEST_NEXT)) {

                decodedStr = s + " ";

                displayResultPanel(decodedStr);

                System.out.println("onResult:" + decodedStr);
                dictatingButton.setVisibility(View.GONE);
                processingButton.setVisibility(View.GONE);
                defaultButton.setVisibility(View.VISIBLE);

            }
        }
    }

    @Override
    public void onError(Exception e) {
    }

    @Override
    public void onTimeout() {
    }

    @Override
    public void onReceiveDownloadComplete() {
        recognizer.stop();
        recognizer.shutdown();
        setUpRecognizer();
    }

    @Override
    public void onReceiveDownloadFailed() {

    }

}
