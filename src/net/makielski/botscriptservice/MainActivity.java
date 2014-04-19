package net.makielski.botscriptservice;

import android.app.Activity;
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

	private static String uiLink = null;
	public static Intent openUiIntent = null;
	
	private static ImageView glowInvader;
	
	private Thread statePolling = new Thread(new Runnable() {
		
		Runnable uiUpdate = new Runnable() {
			boolean firstRun = true;
			boolean lastState = false;
			
			@Override
			public void run() {
				if (firstRun) {
					firstRun = false;
					return;
				}
				if (lastState != BotService.active) {
					lastState = BotService.active;
					ToggleButton toggleBtn = (ToggleButton) findViewById(R.id.toggleServiceBtn);
					toggleBtn.setChecked(BotService.active);
					if (BotService.active) {
						Animation fadein = new AlphaAnimation(0.0f, 1.0f);
						fadein.setDuration(800);
						fadein.setFillAfter(true);
						
						glowInvader.setVisibility(View.VISIBLE);
						glowInvader.startAnimation(fadein);
					} else {
						Animation fadeout = new AlphaAnimation(1.0f, 0.0f);
						fadeout.setDuration(800);
						fadeout.setFillAfter(true);
						glowInvader.startAnimation(fadeout);
					}
				}
			}
		};
		
		@Override
		public void run() {
			while (true) {
				System.out.println("checking status");
				runOnUiThread(uiUpdate);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		uiLink = "http://makielski.net/ui/" + getString(R.string.uiversion) + "/";
		openUiIntent =new Intent(Intent.ACTION_VIEW, Uri.parse(uiLink));

		glowInvader = (ImageView) findViewById(R.id.image_on);
		ToggleButton toggleBtn = (ToggleButton) findViewById(R.id.toggleServiceBtn);

		boolean isRunning = BotService.active;
		toggleBtn.setChecked(isRunning);
		if (!isRunning) {
			glowInvader.setVisibility(View.INVISIBLE);
		}
		
		statePolling.start();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		statePolling.interrupt();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void onToggleClicked(View view) {
		Intent serviceIntent = new Intent(this, BotService.class);
		this.startService(serviceIntent);
	}
	
	public void onInvaderClicked(View view) {
		startActivity(openUiIntent);
	}

}
