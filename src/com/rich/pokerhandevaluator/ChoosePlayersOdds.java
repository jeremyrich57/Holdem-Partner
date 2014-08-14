package com.rich.pokerhandevaluator;

import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChoosePlayersOdds extends Activity {

	int intPlayers = 0;
	ArrayList<String> lstDisplay = new ArrayList<String>();
	ListView lvPlayers;
	ArrayAdapter<String> playerAdapter;
	Intent intentOdds = new Intent();
	public static String strPlayers = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_players_odds);
		
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
				
				//Sets intent to call up odds screen
				intentOdds = new Intent(this, Odds.class);
				
				
				
				
				getPlayers();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_players_odds, menu);
		return true;
	}
	
	public void getPlayers ()
	{
		//On Click listener for list view to select players
		lvPlayers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView <?> arg0, View view, int position, long id){
				
				intPlayers = position + 2;			//Gets the number of players
	
				intentOdds.putExtra(strPlayers, String.valueOf(intPlayers)); //Converts players to string to send to intent for next screen				

				startActivity(intentOdds);
				finish();
				
			}
			
			
		});
		
		
	}

}
