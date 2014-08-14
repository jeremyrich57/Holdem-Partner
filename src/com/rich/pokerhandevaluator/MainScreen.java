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
import android.view.View;
import android.content.Intent;

import com.rich.pokerhandevaluator.R;

public class MainScreen extends Activity {

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
	}
	
	public boolean onOptionsItemsSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.exit:
			finish();
			default:
				return super.onOptionsItemSelected(item);
		}
		
	}
	
	public void choosePlayers (View view)
	{
		
		Intent intent = new Intent(this, ChoosePlayers.class);
		startActivity(intent);
	}
	
	public void pokerRanks (View view)
	{
		Intent intent = new Intent(this, PokerRanks.class);
		startActivity(intent);
	}
	
	public void odds (View view)
	{
		Intent intent = new Intent(this, ChoosePlayersOdds.class);
		startActivity(intent);
	}

}
