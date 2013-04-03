package net.makielski.botscriptservice;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	public static final Intent openUiIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://makielski.net/mobileui"));
	
	ImageView glowInvader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		glowInvader = (ImageView) findViewById(R.id.image_on);
		ToggleButton toggleBtn = (ToggleButton) findViewById(R.id.toggleServiceBtn);

		boolean isRunning = isServiceRunning();
		toggleBtn.setChecked(isRunning);
		if (!isRunning) {
			glowInvader.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void onToggleClicked(View view) {
		// Is the toggle on?
		boolean on = ((ToggleButton) view).isChecked();
		if (on) {
			Intent startIntent = new Intent(this, BotService.class);
			startIntent.setAction("net.makielski.botservice.start");
			this.startService(startIntent);

			Animation fadein = new AlphaAnimation(0.0f, 1.0f);
			fadein.setDuration(800);
			fadein.setFillAfter(true);
			
			glowInvader.setVisibility(View.VISIBLE);
			glowInvader.startAnimation(fadein);
		} else {
			Intent stopIntent = new Intent(this, BotService.class);
			stopIntent.setAction("net.makielski.botservice.stop");
			this.startService(stopIntent);

			Animation fadeout = new AlphaAnimation(1.0f, 0.0f);
			fadeout.setDuration(800);
			fadeout.setFillAfter(true);
			glowInvader.startAnimation(fadeout);
		}
	}
	
	public void onInvaderClicked(View view) {
		startActivity(openUiIntent);
	}

	private boolean isServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (BotService.class.getName().equals(
					service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
