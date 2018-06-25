package utiles;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class Contexto extends Application {

    private static Contexto instancia;
    public static String APP_TAG = "moviles";
    public static final String CHANNEL_ID = "ServiceMap";
    @Override
    public void onCreate() {
        super.onCreate();
        instancia = this;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Location",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public static Contexto getInstancia() {
        return instancia;
    }

}