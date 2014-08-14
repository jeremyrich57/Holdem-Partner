/********************************************************************************
 * Title: Texas Holdem Buddy
 * Created by: Paltolor
 * Description: This app compares hands in poker based on user's input. It will
 * determine which hand is the highest. It also runs a simulation to determine your odds
 * of winning, losing, or tying based on what cards are shown. 
 * Credit for card pictures goes to: https://code.google.com/p/vector-playing-cards/
 * Free access to cards.
 * 
 ********************************************************************************/

package com.rich.pokerhandevaluator;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

import com.rich.pokerhandevaluator.R;

public class PokerRanks extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poker_ranks);
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.poker_ranks, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
			
		case R.id.texas_holdem:
			Intent intent = new Intent(this, ChoosePlayers.class);
			startActivity(intent);
			finish();
			return true;
			
		case R.id.main:
			Intent intent2 = new Intent(this, MainScreen.class);
			startActivity(intent2);
			finish();
			return true;
			
		case R.id.odds:
			Intent intent3 = new Intent(this, ChoosePlayersOdds.class);
			startActivity(intent3);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
