package net.makielski.botscriptservice;

import net.makielski.botscript.BotscriptServer;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BotService extends Service {

	private int notificationId = 123456;

	private BotscriptServer nativeObject = new BotscriptServer();

	private Thread runner = new Thread(new Runnable() {
		@Override
		public void run() {
			nativeObject.start();
		}
	});

	@Override
	public void onCreate() {
		System.out.println("onCreate()");

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				MainActivity.openUiIntent, 0);

		Notification noti = new Notification.Builder(this)
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
		if (runner.isAlive()) {
			nativeObject.stop();
			stopSelf();
		} else {
			runner.start();
		}

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// no binding
		return null;
	}

}
