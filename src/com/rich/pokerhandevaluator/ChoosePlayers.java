/********************************************************************************
 * Title: Poker Hand Evaluator
 * Created by: Paltolor
 * Description: This app compares hands in poker based on user's input. It will
 * determine which hand is the highest. 
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
//import android.widget.TextView;
import android.view.View;
import java.util.ArrayList;


import android.widget.AdapterView;

import com.rich.pokerhandevaluator.R;

public class ChoosePlayers extends Activity {

	ArrayList<String> lstDisplay = new ArrayList<String>();
	ListView lvPlayers;
	ArrayAdapter<String> playerAdapter;
	int intPlayers = 0;
	public static String strPlayers = "";
	public static String strOdds = "";
	public static String strTexas = "";
	public static String strGame = "";
	Intent intentTexas = new Intent();
	Intent intentOdds = new Intent();
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_players);
		// Show the Up button in the action bar.
		setupActionBar();
		
		//Add player selection to list
		lstDisplay.add("2 Players");
		lstDisplay.add("3 Players");
		lstDisplay.add("4 Players");
		lstDisplay.add("5 Players");
		lstDisplay.add("6 Players");
		lstDisplay.add("7 Players");
		lstDisplay.add("8 Players");
		lstDisplay.add("9 Players");
		
		//make the list view and add the lstDisplay to view.
		lvPlayers = (ListView) findViewById(R.id.lvSelectPlayers);			
		playerAdapter = new ArrayAdapter<String>(this, R.layout.choose_players, lstDisplay);
		
		lvPlayers.setAdapter(playerAdapter);
		
		//Sets intent to call up hold em screen or odds screen
		intentTexas = new Intent(this, Texas_Holdem.class);
		intentOdds = new Intent(this, Odds.class);
		
		
		
		
		getPlayers();
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
		getMenuInflater().inflate(R.menu.choose_players, menu);
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
		case R.id.main:
			Intent intent2 = new Intent(this, MainScreen.class);
			startActivity(intent2);
			return true;		
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void getPlayers ()
	{
		//On Click listener for list view to select players
		lvPlayers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView <?> arg0, View view, int position, long id){
				
				intPlayers = position + 2;	//Gets the number of players
				intentTexas.putExtra(strPlayers, String.valueOf(intPlayers)); //Converts players to string to send to intent for next screen				
				startActivity(intentTexas);
				finish();
			}
		});

	}

}
