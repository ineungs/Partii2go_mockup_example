package th.or.nectec.party.offline.android.Partii2Go;

/**
 * Created by suwan on 11/14/2016 AD.
 */
import android.app.Application;
import com.karumi.dexter.Dexter;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Dexter.initialize(getApplicationContext());
    }

}