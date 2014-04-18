package net.makielski.botscriptservice;

import net.makielski.botscript.BotscriptServer;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class BotService extends Service {

	private int notificationId = 123456;

	private BotscriptServer nativeObject = null;

	@Override
	public void onCreate() {
		System.out.println("onCreate()");
		
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				MainActivity.openUiIntent, 0);

		Notification noti = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.invader)
				.setContentTitle("Makielski's BotScript")
				.setContentText("Antippen um das Interface zu öffnen")
				.setContentIntent(contentIntent)
				.build();
		startForeground(notificationId, noti);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("onStartCommand()");
		if (nativeObject == null) {
			nativeObject = new BotscriptServer(getExternalFilesDir(null).getAbsolutePath());
			nativeObject.start();
		} else {
			nativeObject.stop();
			nativeObject = null;
			stopSelf();
		}

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// no binding
		return null;
	}

}
