package edu.uta.ucs;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DatabaseService extends IntentService {

    public DatabaseService() {
        super("DatabaseService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this,"Service has been created", Toast.LENGTH_LONG).show();
        Log.d("Service Test", "Service has been created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this,"Service has been started", Toast.LENGTH_LONG).show();
        Log.d("Service Test", "Service has been started");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("Service Test", "from onHandleIntent method");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this,"Service has been stopped", Toast.LENGTH_LONG).show();
        Log.d("Service Test", "Service has been stopped");
    }
}
