

package utiles;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.ricardopazdemiquel.moviles.MainActivity;
import com.example.ricardopazdemiquel.moviles.MainActivityConductor;
import com.example.ricardopazdemiquel.moviles.R;

public class ForegroundService extends Service {
    private static final String LOG_TAG = "ForegroundService";
    private static int FOREGROUND_ID=1338;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_TAG, "comenxo ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Start Foreground Intent ");


            startForeground(FOREGROUND_ID,
                    buildForegroundNotification("descarga"));

        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "In onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }
    private Notification buildForegroundNotification(String filename) {
        NotificationCompat.Builder b=new NotificationCompat.Builder(this);

        b.setOngoing(true);

        b.setContentTitle("asdas")
                .setContentText(filename)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setTicker("asdasd");

        return(b.build());
    }
}
