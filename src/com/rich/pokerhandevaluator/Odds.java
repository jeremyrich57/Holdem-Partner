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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
//import java.util.HashSet;
//import java.util.Set;
//import java.util.Random;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.widget.*;

public class Odds extends Activity {

	LinearLayout llCards, llPlayer1, llTable;
	TextView txtTable, txtCards, txtOdds, txtPlayer1, txtTie, txtLose, txtAcc;
	ImageButton ibView, iView;
	ArrayList<Card> lstPlayer1 = new ArrayList<Card>();	
	ArrayList<Card> lstTable = new ArrayList<Card>();
	ArrayList<Card> lstHold = new ArrayList<Card>();
	ArrayList<ArrayList<Card>> lstAllHands = new ArrayList<ArrayList<Card>>();
	ArrayList<Card> lstPlayer1Odds = new ArrayList<Card>();
	ArrayList<Card> lstPlayer2Odds = new ArrayList<Card>();
	ArrayList<Card> lstPlayer3Odds = new ArrayList<Card>();
	ArrayList<Card> lstPlayer4Odds = new ArrayList<Card>();
	ArrayList<Card> lstPlayer5Odds = new ArrayList<Card>();
	ArrayList<Card> lstPlayer6Odds = new ArrayList<Card>();
	ArrayList<Card> lstPlayer7Odds = new ArrayList<Card>();
	ArrayList<Card> lstPlayer8Odds = new ArrayList<Card>();
	ArrayList<Card> lstPlayer9Odds = new ArrayList<Card>();
	ArrayList<Card> lstTableOdds = new ArrayList<Card>();
	DecimalFormat decOdds = new DecimalFormat("00.00");
	ArrayList<Integer> intHandRank = new ArrayList<Integer>();
	long mLastClick = 0;
	Card[] cards = new Card[52];
	Integer intIndex = 0, intClub = 0, intSpade = 0, intDiamond = 0, intHeart = 0, intRandom, intTotalGames = 0, 
			intPlayer1Wins = 0, intPlayerRankHold = 0, intTotalPlayers = 0, intTie = 0, intLose = 0,
			intBackCard = R.drawable.back_card, intCardSrc = 0, intAccuracy = 5000, scrollX;
	String strOdds ="", strTie = "", strLose ="";
	Double dblOdds = 0.00, dblTie = 0.00, dblLose = 0.00;
	AsyncTaskRunner runner = new AsyncTaskRunner();
	Boolean FlagCancelled = false;
	Boolean bolPlayer1Wins = true;
	Boolean bolTie = false;
	Boolean bolGame = true;
	ArrayList<Integer> lstStraight = new ArrayList<Integer>();
	ArrayList<Integer> lstStrIndex = new ArrayList<Integer>();
	ArrayList<Integer> lstRandom = new ArrayList<Integer>();
	ImageView left_arrow, right_arrow;
	HorizontalScrollView hsv;
	ArrayList<Card> lstBurn = new ArrayList<Card>();
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_odds);
		// Show the Up button in the action bar.
		setupActionBar();
		
		//Gets number of players
		Intent intent = getIntent();
		String message = intent.getStringExtra(ChoosePlayers.strPlayers);
		intTotalPlayers = Integer.parseInt(message);
		
		//Get the layout views
		llCards = (LinearLayout) findViewById(R.id.linOddsCards);
		llPlayer1 = (LinearLayout) findViewById(R.id.linOddsPlayer1);
		llTable = (LinearLayout) findViewById(R.id.linOddsTables);
		txtOdds = (TextView) findViewById(R.id.txtOddsWin);
		txtTie = (TextView) findViewById(R.id.txtOddsTie);
		txtLose = (TextView) findViewById(R.id.txtOddsLose);
		txtCards = (TextView) findViewById(R.id.txtOddsCards);
		txtAcc = (TextView) findViewById(R.id.txtOddsAccuracy);
		hsv = (HorizontalScrollView) findViewById(R.id.oddsScrollView);
		left_arrow = (ImageView) findViewById(R.id.left_arrow);
		right_arrow = (ImageView) findViewById(R.id.right_arrow);
		
		//Loads the deck and cards to the main view
		for (int i = 0; i < 52; i++)
		{
			cards[i] = new Card(i);
			ibView = new ImageButton(this);
			ibView.setImageResource(cards[i].pic_rsc);
			ibView.setPadding(4, 4, 4, 4);
			ibView.setOnClickListener(addCard(ibView));
			llCards.addView(ibView);
			
			//Adds list for random shuffle
			lstRandom.add(i);
		}
		
		//Changes opacity of the face down cards to ~50%
		iView = (ImageButton) findViewById(R.id.ibOddsP1Card1);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibOddsP1Card2);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibOddsTableCard1);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibOddsTableCard2);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibOddsTableCard3);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibOddsTableCard4);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibOddsTableCard5);
		iView.setImageAlpha(127);
		
		//Makes sure async task stops if the program has been exited and started up again
		runner.cancel(true);
		
		txtOdds.setText("Win: ");
		txtTie.setText("Tie: ");
		txtLose.setText("Lose: ");
		
		//Initial Tutorial Pop up
		helpDialog();
		
		hsv.getViewTreeObserver().addOnScrollChangedListener(new OnScrollChangedListener() {
			
			@Override
			public void onScrollChanged() {
				// TODO Auto-generated method stub
				scrollX = hsv.getScrollX();
				
				scrollArrows();
				
			}
			
		});
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
		getMenuInflater().inflate(R.menu.odds, menu);
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
			
		case R.id.PokerRanks:
			Intent intent = new Intent(this, PokerRanks.class);
			startActivity(intent);
			finish();
			return true;
		case R.id.main:
			Intent intent2 = new Intent(this, MainScreen.class);
			startActivity(intent2);
			finish();
			return true;			
		case R.id.holdem:
			Intent intent3 = new Intent(this, ChoosePlayers.class);
			startActivity(intent3);
			finish();
			return true;
		case R.id.reset:
			Reset();
			return true;
		case R.id.OddschangePlayers:
			Intent intent4 = new Intent(this, ChoosePlayersOdds.class);
			startActivity(intent4);
			finish();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	
	
	
	
	//OnClick Listener for adding cards
	View.OnClickListener addCard(final ImageButton card) {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				//Error handles for a double click so the click doesn't get queued twice and add the same card twice
				if (SystemClock.elapsedRealtime() - mLastClick < 100){
					return;
				}
				else if (bolGame)
				{
					//Resets values
					intTotalGames = 0;
					intPlayer1Wins = 0;
					intTie = 0;
					intLose = 0;
					dblLose = 0.00;
					dblTie = 0.00;
					dblOdds = 0.00;
					mLastClick = SystemClock.elapsedRealtime();
					runner.cancel(false);
					
					//Adds first card to Player 1's hand
					if (lstPlayer1.size() == 0)
					{
						ibView = (ImageButton) findViewById(R.id.ibOddsP1Card1);
						ibView.setImageResource(cards[llCards.indexOfChild(v)].pic_rsc);
						ibView.setLongClickable(true);
						ibView.setOnLongClickListener(swapCard(ibView));
						ibView.setImageAlpha(255);
						lstPlayer1.add(cards[llCards.indexOfChild(v)]);
						
						//Darkens the image after clicking and disables it
						iView = (ImageButton) v;
						iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
						v.setEnabled(false);
						
						intIndex++;
					}
					
					//Adds second card
					else if (lstPlayer1.size() == 1)
					{
						ibView = (ImageButton) findViewById(R.id.ibOddsP1Card2);
						ibView.setImageResource(cards[llCards.indexOfChild(v)].pic_rsc);
						ibView.setLongClickable(true);
						ibView.setOnLongClickListener(swapCard(ibView));
						ibView.setImageAlpha(255);
						lstPlayer1.add(cards[llCards.indexOfChild(v)]);
						
						//Darkens the image after clicking and disables it
						iView = (ImageButton) v;
						iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
						v.setEnabled(false);
						
						txtCards.setText(R.string.pick_flop_cards);
						
						intIndex++;
					}
					
					//Adds table cards
					else if (lstTable.size() == 0)
					{
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard1);
						ibView.setImageResource(cards[llCards.indexOfChild(v)].pic_rsc);
						ibView.setLongClickable(true);
						ibView.setOnLongClickListener(swapCard(ibView));
						ibView.setImageAlpha(255);
						lstTable.add(cards[llCards.indexOfChild(v)]);
						
						//Darkens the image after clicking and disables it
						iView = (ImageButton) v;
						iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
						v.setEnabled(false);
						
						intIndex++;
					}
					else if (lstTable.size() == 1)
					{
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard2);
						ibView.setImageResource(cards[llCards.indexOfChild(v)].pic_rsc);
						ibView.setLongClickable(true);
						ibView.setOnLongClickListener(swapCard(ibView));
						ibView.setImageAlpha(255);
						lstTable.add(cards[llCards.indexOfChild(v)]);
						
						//Darkens the image after clicking and disables it
						iView = (ImageButton) v;
						iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
						v.setEnabled(false);
						
						intIndex++;
					}
					else if (lstTable.size() == 2)
					{
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard3);
						ibView.setImageResource(cards[llCards.indexOfChild(v)].pic_rsc);
						ibView.setLongClickable(true);
						ibView.setOnLongClickListener(swapCard(ibView));
						ibView.setImageAlpha(255);
						lstTable.add(cards[llCards.indexOfChild(v)]);
						
						//Darkens the image after clicking and disables it
						iView = (ImageButton) v;
						iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
						v.setEnabled(false);
						
						txtCards.setText(R.string.pick_turn);
						
						intIndex++;
					}
					else if (lstTable.size() == 3)
					{
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard4);
						ibView.setImageResource(cards[llCards.indexOfChild(v)].pic_rsc);
						ibView.setLongClickable(true);
						ibView.setOnLongClickListener(swapCard(ibView));
						ibView.setImageAlpha(255);
						lstTable.add(cards[llCards.indexOfChild(v)]);
						
						//Darkens the image after clicking and disables it
						iView = (ImageButton) v;
						iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
						v.setEnabled(false);
						
						txtCards.setText(R.string.pick_river);
						
						intIndex++;
					}
					else if (lstTable.size() == 4)
					{
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard5);
						ibView.setImageResource(cards[llCards.indexOfChild(v)].pic_rsc);
						ibView.setLongClickable(true);
						ibView.setOnLongClickListener(swapCard(ibView));
						ibView.setImageAlpha(255);
						lstTable.add(cards[llCards.indexOfChild(v)]);
						
						//Darkens the image after clicking and disables it
						iView = (ImageButton) v;
						iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
						v.setEnabled(false);
						
						intIndex++;
					}
					
					//Runs async task
					runner.cancel(true);
					runner.cancel(false);
					runner = new AsyncTaskRunner();
					runner.execute();
			
					//Sets boolean game false when lists are max so button can't run
					if (lstTable.size() == 5 && lstPlayer1.size() == 2)
					{
						bolGame = false;
					}
				}
			}
			
		};
	}
	
	//Long click to replace cards in deck
	View.OnLongClickListener swapCard(final ImageButton card) {
		return new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				
				iView = card;
				card.setLongClickable(false);
				
				if (card.getParent() == llPlayer1)
				{
					for(int i = 0; i < 52; i++)
					{
						if (lstPlayer1.get(llPlayer1.indexOfChild(card)).pic_rsc == cards[i].pic_rsc)
						{						
							iView.setImageResource(intBackCard);
							iView = (ImageButton) llCards.getChildAt(i);
							lstPlayer1.remove(llPlayer1.indexOfChild(card));
							bolGame = true;
							break;
						}
	//					
					}
					if (lstPlayer1.size() == 1)
					{
						ibView = (ImageButton) findViewById(R.id.ibOddsP1Card1);
						ibView.setImageResource(lstPlayer1.get(0).pic_rsc);
						ibView.setClickable(false);
						ibView.setLongClickable(true);
						
						ibView = (ImageButton) findViewById(R.id.ibOddsP1Card2);
						ibView.setImageResource(intBackCard);
						ibView.setImageAlpha(127);
						ibView.setClickable(false);
						ibView.setLongClickable(false);
					}
					else if (lstPlayer1.size() == 0)
					{					
						ibView = (ImageButton) findViewById(R.id.ibOddsP1Card1);
						ibView.setImageResource(intBackCard);
						ibView.setImageAlpha(127);
						ibView.setClickable(false);
						ibView.setLongClickable(false);
					}
				}
				
				else if(card.getParent() == llTable)
				{
					for(int i = 0; i < 52; i++)
					{
						if (lstTable.get(llTable.indexOfChild(card)).pic_rsc == cards[i].pic_rsc)
							{
								iView.setImageResource(intBackCard);
								iView = (ImageButton) llCards.getChildAt(i);
								lstTable.remove(llTable.indexOfChild(card));
								bolGame = true;
								break;
							}
					}
					
					if (lstTable.size() == 0)
					{
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard1);
						ibView.setImageResource(intBackCard);
						ibView.setImageAlpha(127);
						ibView.setClickable(false);
						ibView.setLongClickable(false);
					}
					else if (lstTable.size() == 1)
					{
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard1);
						ibView.setImageResource(lstTable.get(0).pic_rsc);
						ibView.setClickable(false);
						ibView.setLongClickable(true);
						
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard2);
						ibView.setImageResource(intBackCard);
						ibView.setImageAlpha(127);
						ibView.setClickable(false);
						ibView.setLongClickable(false);
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard3);
						ibView.setImageResource(intBackCard);
						ibView.setImageAlpha(127);
						ibView.setClickable(false);
						ibView.setLongClickable(false);
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard4);
						ibView.setImageResource(intBackCard);
						ibView.setImageAlpha(127);
						ibView.setClickable(false);
						ibView.setLongClickable(false);
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard5);
						ibView.setImageResource(intBackCard);
						ibView.setImageAlpha(127);
						ibView.setClickable(false);
						ibView.setLongClickable(false);
					}
					
					else if (lstTable.size() == 2)
					{
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard1);
						ibView.setImageResource(lstTable.get(0).pic_rsc);
						ibView.setClickable(false);
						ibView.setLongClickable(true);
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard2);
						ibView.setImageResource(lstTable.get(1).pic_rsc);		
						ibView.setClickable(false);
						ibView.setLongClickable(true);
						
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard3);
						ibView.setImageResource(intBackCard);
						ibView.setImageAlpha(127);
						ibView.setClickable(false);
						ibView.setLongClickable(false);
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard4);
						ibView.setImageResource(intBackCard);
						ibView.setImageAlpha(127);
						ibView.setClickable(false);
						ibView.setLongClickable(false);
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard5);
						ibView.setImageResource(intBackCard);
						ibView.setImageAlpha(127);
						ibView.setClickable(false);
						ibView.setLongClickable(false);
					}
					
					else if (lstTable.size() == 3)
					{
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard1);
						ibView.setImageResource(lstTable.get(0).pic_rsc);
						ibView.setClickable(false);
						ibView.setLongClickable(true);
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard2);
						ibView.setImageResource(lstTable.get(1).pic_rsc);	
						ibView.setClickable(false);
						ibView.setLongClickable(true);
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard3);
						ibView.setImageResource(lstTable.get(2).pic_rsc);
						ibView.setClickable(false);
						ibView.setLongClickable(true);
						
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard4);
						ibView.setImageResource(intBackCard);
						ibView.setImageAlpha(127);
						ibView.setClickable(false);
						ibView.setLongClickable(false);
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard5);
						ibView.setImageResource(intBackCard);
						ibView.setImageAlpha(127);
						ibView.setClickable(false);
						ibView.setLongClickable(false);
					}
					
					else if (lstTable.size() == 4)
					{
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard1);
						ibView.setImageResource(lstTable.get(0).pic_rsc);
						ibView.setClickable(false);
						ibView.setLongClickable(true);
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard2);
						ibView.setImageResource(lstTable.get(1).pic_rsc);		
						ibView.setClickable(false);
						ibView.setLongClickable(true);
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard3);
						ibView.setImageResource(lstTable.get(2).pic_rsc);	
						ibView.setClickable(false);
						ibView.setLongClickable(true);
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard4);
						ibView.setImageResource(lstTable.get(3).pic_rsc);
						ibView.setClickable(false);
						ibView.setLongClickable(true);
						
						ibView = (ImageButton) findViewById(R.id.ibOddsTableCard5);
						ibView.setImageResource(intBackCard);
						ibView.setImageAlpha(127);
						ibView.setClickable(false);
						ibView.setLongClickable(false);
					}
				}
				
				if(lstPlayer1.size() < 2)
				{
					txtCards.setText(R.string.odds_choose_cards);
				}
				else if (lstTable.size() < 3)
				{
					txtCards.setText(R.string.pick_flop_cards);
				}
				else if (lstTable.size() == 3)
				{
					txtCards.setText(R.string.pick_turn);
				}
				else
				{
					txtCards.setText(R.string.pick_river);
				}
				
				iView.setColorFilter(null);
				iView.setEnabled(true);
				iView.setClickable(true);
				
				intTotalGames = 0;
				intPlayer1Wins = 0;
				intTie = 0;
				intLose = 0;
				dblLose = 0.00;
				dblTie = 0.00;
				dblOdds = 0.00;
				
				if (!lstPlayer1.isEmpty() && !lstTable.isEmpty())
				{
					runner.cancel(true);
					runner.cancel(false);
					runner = new AsyncTaskRunner();
					runner.execute();
				}
				else if (lstPlayer1.isEmpty() && lstTable.isEmpty())
				{
					runner.cancel(true);
					txtOdds.setText("Win: ");
					txtTie.setText("Tie: ");
					txtLose.setText("Lose: ");
				}
				
				return true;
			}
			
		};
	}
	
	//Sorts the cards based on Rank - Highest to Lowest
	class CardRankSort implements Comparator<Card> {
		public int compare(Card card1, Card card2) {
			return card2.getRank().compareTo(card1.getRank());
		}		
	}
	
	//Sorts cards by suit - Clubs, Diamonds, Spade, Hearts
	class CardSuitSort implements Comparator<Card> {
		public int compare(Card card1, Card card2) {
			return card1.getSuit().compareTo(card2.getSuit());
		}
	}
	
	
	/***************************Async Task to calculate odds of winning************************************/
	private class AsyncTaskRunner extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub			
			try {			
				for (int i = 0; i < intAccuracy; i++)
				{
					synchronized(this){
					checkWinner();
					}
					if(i % 100 == 0)
					{
						publishProgress(strOdds, strTie, strLose);
//						publishProgress(strTie);
//						publishProgress(strLose);
					}
				
					if (isCancelled() || FlagCancelled)
					{
						
						break;
					}
					
					
					
				}
			}
			catch (Exception e)
			{
				strOdds = e.toString(); //String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber());
				
			}	
			
			return null;
		}
		
		@Override
		protected void onProgressUpdate(String...text)
		{
			txtOdds.setText(text[0]);
			txtTie.setText(text[1]);
			txtLose.setText(text[2]);
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			txtOdds.setText(strOdds);
			txtTie.setText(strTie);
			txtLose.setText(strLose);
			Odds.this.isFinishing();
		}

		@Override
		protected void onPreExecute()
		{
			txtOdds.setText(strOdds);
		}
		
		@Override
		protected void onCancelled()
		{
			return;
		}
	}
	
	
	/*********************************Method to run through simulation******************************************/
	public void checkWinner()
	{
		lstAllHands.clear();
		intHandRank.clear();
		lstPlayer2Odds.clear();
		lstPlayer3Odds.clear();
		lstPlayer4Odds.clear();
		lstPlayer5Odds.clear();
		lstPlayer6Odds.clear();
		lstPlayer7Odds.clear();
		lstPlayer8Odds.clear();
		lstPlayer9Odds.clear();
		lstTableOdds.clear();
		lstPlayer1Odds.clear();
		lstBurn.clear();
		intPlayerRankHold = 0;		
		dblOdds = 0.00;
		dblTie = 0.00;
		dblLose = 0.00;
		
		lstPlayer1Odds.addAll(lstPlayer1);
		lstTableOdds.addAll(lstTable);
		
		Collections.shuffle(lstRandom);
				
		
		//Adds another card to player 1's hand if two have not been selected
		while(lstPlayer1Odds.size() < 2)
		{
//			intRandom = rnd.nextInt(52);
			Collections.shuffle(lstRandom);
			if(!lstPlayer1Odds.contains(cards[lstRandom.get(0)]) && !lstTableOdds.contains(cards[lstRandom.get(0)])
					&& lstBurn.add(cards[lstRandom.get(0)]))
			{
				lstPlayer1Odds.add(cards[lstRandom.get(0)]);
			}
		}
		
		if (intTotalPlayers > 1)
		{

			//selects two random cards for the player 2's hand
			while(lstPlayer2Odds.size() < 2)
			{
//				intRandom = rnd.nextInt(52);
				Collections.shuffle(lstRandom);
				if(!lstPlayer1Odds.contains(cards[lstRandom.get(0)]) && !lstPlayer2Odds.contains(cards[lstRandom.get(0)])
						&& !lstTableOdds.contains(cards[lstRandom.get(0)]) && lstBurn.add(cards[lstRandom.get(0)]))
				{
					lstPlayer2Odds.add(cards[lstRandom.get(0)]);
				}
			}
		}
		
		
		if (intTotalPlayers > 2)
		{

			//selects two random cards for the player 2's hand
			while(lstPlayer3Odds.size() < 2)
			{
//				intRandom = rnd.nextInt(52);
				Collections.shuffle(lstRandom);
				if(!lstPlayer1Odds.contains(cards[lstRandom.get(0)]) && !lstPlayer2Odds.contains(cards[lstRandom.get(0)])
						&& !lstPlayer3Odds.contains(cards[lstRandom.get(0)]) && !lstTableOdds.contains(cards[lstRandom.get(0)])
						&& lstBurn.add(cards[lstRandom.get(0)]))
				{
					lstPlayer3Odds.add(cards[lstRandom.get(0)]);
				}
			}						
		}
		
		if (intTotalPlayers > 3)
		{

			//selects two random cards for the player 2's hand
			while(lstPlayer4Odds.size() < 2)
			{
//				intRandom = rnd.nextInt(52);
				Collections.shuffle(lstRandom);
				if(!lstPlayer1Odds.contains(cards[lstRandom.get(0)]) && !lstPlayer2Odds.contains(cards[lstRandom.get(0)])
						&& !lstPlayer3Odds.contains(cards[lstRandom.get(0)]) && !lstPlayer4Odds.contains(cards[lstRandom.get(0)])
						&& !lstTableOdds.contains(cards[lstRandom.get(0)]) && lstBurn.add(cards[lstRandom.get(0)]))
				{
					lstPlayer4Odds.add(cards[lstRandom.get(0)]);
				}
			}						
		}
		
		if (intTotalPlayers > 4)
		{

			//selects two random cards for the player 2's hand
			while(lstPlayer5Odds.size() < 2)
			{
//				intRandom = rnd.nextInt(52);
				Collections.shuffle(lstRandom);
				if(!lstPlayer1Odds.contains(cards[lstRandom.get(0)]) && !lstPlayer2Odds.contains(cards[lstRandom.get(0)])
						&& !lstPlayer3Odds.contains(cards[lstRandom.get(0)]) && !lstPlayer4Odds.contains(cards[lstRandom.get(0)])
						&& !lstPlayer5Odds.contains(cards[lstRandom.get(0)]) && !lstTableOdds.contains(cards[lstRandom.get(0)])
						&& lstBurn.add(cards[lstRandom.get(0)]))
				{
					lstPlayer5Odds.add(cards[lstRandom.get(0)]);
				}
			}						
		}
		
		if (intTotalPlayers > 5)
		{

			//selects two random cards for the player 2's hand
			while(lstPlayer6Odds.size() < 2)
			{
//				intRandom = rnd.nextInt(52);
				Collections.shuffle(lstRandom);
				if(!lstPlayer1Odds.contains(cards[lstRandom.get(0)]) && !lstPlayer2Odds.contains(cards[lstRandom.get(0)])
						&& !lstPlayer3Odds.contains(cards[lstRandom.get(0)]) && !lstPlayer4Odds.contains(cards[lstRandom.get(0)])
						&& !lstPlayer5Odds.contains(cards[lstRandom.get(0)]) && !lstPlayer6Odds.contains(cards[lstRandom.get(0)])
						&& !lstTableOdds.contains(cards[lstRandom.get(0)]) && lstBurn.add(cards[lstRandom.get(0)]))
				{
					lstPlayer6Odds.add(cards[lstRandom.get(0)]);
				}
			}						
		}
		
		if (intTotalPlayers > 6)
		{
			//selects two random cards for the player 2's hand
			while(lstPlayer7Odds.size() < 2)
			{
//				intRandom = rnd.nextInt(52);
				Collections.shuffle(lstRandom);
				if(!lstPlayer1Odds.contains(cards[lstRandom.get(0)]) && !lstPlayer2Odds.contains(cards[lstRandom.get(0)])
						&& !lstPlayer3Odds.contains(cards[lstRandom.get(0)]) && !lstPlayer4Odds.contains(cards[lstRandom.get(0)])
						&& !lstPlayer5Odds.contains(cards[lstRandom.get(0)])&& !lstPlayer6Odds.contains(cards[lstRandom.get(0)])
						&& !lstPlayer7Odds.contains(cards[lstRandom.get(0)])&& !lstTableOdds.contains(cards[lstRandom.get(0)])
						&& lstBurn.add(cards[lstRandom.get(0)]))
				{
					lstPlayer7Odds.add(cards[lstRandom.get(0)]);
				}
			}						
		}
		
		if (intTotalPlayers > 7)
		{
			//selects two random cards for the player 2's hand
			while(lstPlayer8Odds.size() < 2)
			{
				Collections.shuffle(lstRandom);
				
				if(!lstPlayer1Odds.contains(cards[lstRandom.get(0)]) && !lstPlayer2Odds.contains(cards[lstRandom.get(0)])
						&& !lstPlayer3Odds.contains(cards[lstRandom.get(0)]) && !lstPlayer4Odds.contains(cards[lstRandom.get(0)])
						&& !lstPlayer5Odds.contains(cards[lstRandom.get(0)]) && !lstPlayer6Odds.contains(cards[lstRandom.get(0)])
						&& !lstPlayer7Odds.contains(cards[lstRandom.get(0)]) && !lstPlayer8Odds.contains(cards[lstRandom.get(0)])
						&& !lstTableOdds.contains(cards[lstRandom.get(0)]) && lstBurn.add(cards[lstRandom.get(0)]))
				{
					lstPlayer8Odds.add(cards[lstRandom.get(0)]);
				}
			}						
		}
		
		if (intTotalPlayers > 8)
		{
			
			//selects two random cards for the player 2's hand
			while(lstPlayer9Odds.size() < 2)
			{
				Collections.shuffle(lstRandom);
				
				if(!lstPlayer1Odds.contains(cards[lstRandom.get(0)]) && !lstPlayer2Odds.contains(cards[lstRandom.get(0)])
						&& !lstPlayer3Odds.contains(cards[lstRandom.get(0)])&& !lstPlayer4Odds.contains(cards[lstRandom.get(0)])
						&& !lstPlayer5Odds.contains(cards[lstRandom.get(0)])&& !lstPlayer6Odds.contains(cards[lstRandom.get(0)])
						&& !lstPlayer7Odds.contains(cards[lstRandom.get(0)]) && !lstPlayer8Odds.contains(cards[lstRandom.get(0)])
						&& !lstPlayer9Odds.contains(cards[lstRandom.get(0)]) && !lstTableOdds.contains(cards[lstRandom.get(0)])
						&& lstBurn.add(cards[lstRandom.get(0)]))
				{
					lstPlayer9Odds.add(cards[lstRandom.get(0)]);
				}
			}						
		}
		
		//Add cards to table if 5 haven't already been selected
		while(lstTableOdds.size() < 5)
		{
			//Burn cards
			if (lstTableOdds.size() < 1 && lstBurn.size() == 0)
			{
				lstBurn.add(cards[lstRandom.get(0)]);
			}
			else if (lstTableOdds.size() == 3 && lstBurn.size() == 1)
			{
				lstBurn.add(cards[lstRandom.get(0)]);
			}
			else if(lstTableOdds.size() == 4 && lstBurn.size() == 2)
			{
				lstBurn.add(cards[lstRandom.get(0)]);
			}
			
			Collections.shuffle(lstRandom);
			if(!lstTableOdds.contains(cards[lstRandom.get(0)]) && !lstPlayer1Odds.contains(cards[lstRandom.get(0)])
					&& lstBurn.add(cards[lstRandom.get(0)]))
			{
				lstTableOdds.add(cards[lstRandom.get(0)]);//				
			}
		}
		
		//Adds each player's cards to the master list of all hands
		if (!lstPlayer1Odds.isEmpty())
		{
			lstPlayer1Odds.addAll(lstTableOdds);
			lstAllHands.add(lstPlayer1Odds);			
			
		}
		if (!lstPlayer2Odds.isEmpty())
		{
			lstPlayer2Odds.addAll(lstTableOdds);
			lstAllHands.add(lstPlayer2Odds);
		}
		if (!lstPlayer3Odds.isEmpty())
		{
			lstPlayer3Odds.addAll(lstTableOdds);
			lstAllHands.add(lstPlayer3Odds);
		}
		if (!lstPlayer4Odds.isEmpty())
		{
			lstPlayer4Odds.addAll(lstTableOdds);
			lstAllHands.add(lstPlayer4Odds);
		}
		if (!lstPlayer5Odds.isEmpty())
		{
			lstPlayer5Odds.addAll(lstTableOdds);
			lstAllHands.add(lstPlayer5Odds);
		}
		if (!lstPlayer6Odds.isEmpty())
		{
			lstPlayer6Odds.addAll(lstTableOdds);
			lstAllHands.add(lstPlayer6Odds);
		}
		if (!lstPlayer7Odds.isEmpty())
		{
			lstPlayer7Odds.addAll(lstTableOdds);
			lstAllHands.add(lstPlayer7Odds);
		}
		if (!lstPlayer8Odds.isEmpty())
		{
			lstPlayer8Odds.addAll(lstTableOdds);
			lstAllHands.add(lstPlayer8Odds);
		}
		if (!lstPlayer9Odds.isEmpty())
		{
			lstPlayer9Odds.addAll(lstTableOdds);
			lstAllHands.add(lstPlayer9Odds);
		}		

		for(ArrayList<Card> lstPlayerHand : lstAllHands){
			//Adds cards on table to each player's hand
//			lstPlayerHand.addAll(lstTable);
			//Sorts Rank
			Collections.sort(lstPlayerHand, new CardRankSort());
			
			//Adds blank rank so later set methods work
			intHandRank.add(0);	
			
			//Clears the Hold list for each player
			lstHold.removeAll(lstHold);
			
			//Resets suit count
			intClub = 0;
			intDiamond = 0;
			intSpade = 0;
			intHeart = 0;
			
/***************************Straight Flush & Flush ***************************/
			//Loop to count the number of each suit and then add the card to Hold list
			for(Card cards : lstPlayerHand) {						
				
				if (cards.getSuit() == 1)
				{
					intClub++;
					lstHold.add(cards);					
				}
				
				if (cards.getSuit() == 2)
				{
					intDiamond++;
					lstHold.add(cards);					
					
				}
				
				if (cards.getSuit() == 3)
				{
					intSpade++;
					lstHold.add(cards);					
					
				}
				
				if (cards.getSuit() == 4)
				{
					intHeart++;
					lstHold.add(cards);					
				}
				
			}
			
			if (intClub > 4 || intDiamond > 4 || intSpade > 4 || intHeart > 4)
			{
				//Sorts Suit - Club, Diamond, Spade, then Hearts
				Collections.sort(lstHold, new CardSuitSort());
				
				//Checks straight flush
				for (int i = 0; i < lstHold.size() - 4; i++)
				{
					if((lstHold.get(i).Rank - lstHold.get(i + 1).Rank == 1) &&
							(lstHold.get(i + 1).Rank - lstHold.get(i + 2).Rank == 1) &&
							(lstHold.get(i + 2).Rank - lstHold.get(i + 3).Rank == 1) &&
							(lstHold.get(i + 3).Rank - lstHold.get(i + 4).Rank == 1) &&
							(lstHold.get(i).Suit == lstHold.get(i + 1).Suit) &&
							(lstHold.get(i).Suit == lstHold.get(i + 2).Suit) &&
							(lstHold.get(i).Suit == lstHold.get(i + 3).Suit) &&
							(lstHold.get(i).Suit == lstHold.get(i + 4).Suit))
					{
						lstPlayerHand.removeAll(lstPlayerHand);
						lstPlayerHand.add(lstHold.get(i));
						lstPlayerHand.add(lstHold.get(i + 1));
						lstPlayerHand.add(lstHold.get(i + 2));
						lstPlayerHand.add(lstHold.get(i + 3));
						lstPlayerHand.add(lstHold.get(i + 4));
						intHandRank.set(intPlayerRankHold, 8);
						break;
					}
				}	
				
				if (intHandRank.get(intPlayerRankHold) != 8)
				{
					//Checks Ace 2 straight flush if Ace is in first index
					for (int i = 0; i < lstHold.size() - 3; i++)
					{
						if (lstHold.get(i).Rank == 4 && lstHold.get(i + 1).Rank == 3 &&
								lstHold.get(i + 2).Rank == 2 && lstHold.get(i + 3).Rank == 1 &&
								(lstHold.get(0).Rank == 13 && 
										lstHold.get(0).Suit == lstHold.get(i).Suit) &&
										(lstHold.get(i).Suit == lstHold.get(i + 1).Suit) &&
										(lstHold.get(i).Suit == lstHold.get(i + 2).Suit) &&
										(lstHold.get(i).Suit == lstHold.get(i + 3).Suit))
						{
							lstPlayerHand.removeAll(lstPlayerHand);
							lstPlayerHand.add(lstHold.get(i));
							lstPlayerHand.add(lstHold.get(i + 1));
							lstPlayerHand.add(lstHold.get(i + 2));
							lstPlayerHand.add(lstHold.get(i + 3));
							lstPlayerHand.add(lstHold.get(0));
							intHandRank.set(intPlayerRankHold, 8);
							break;
						}
					}
					
					//Checks Ace 2 straight flush if Ace is in second index
					for (int i = 0; i < lstHold.size() -3; i++)
					{
						if (lstHold.get(i).Rank == 4 && lstHold.get(i + 1).Rank == 3 &&
								lstHold.get(i + 2).Rank == 2 && lstHold.get(i + 3).Rank == 1 &&
								(lstHold.get(1).Rank == 13 && 
										lstHold.get(1).Suit == lstHold.get(i).Suit) &&
										(lstHold.get(i).Suit == lstHold.get(i + 1).Suit) &&
										(lstHold.get(i).Suit == lstHold.get(i + 2).Suit) &&
										(lstHold.get(i).Suit == lstHold.get(i + 3).Suit))
						{
							lstPlayerHand.removeAll(lstPlayerHand);
							lstPlayerHand.add(lstHold.get(i));
							lstPlayerHand.add(lstHold.get(i + 1));
							lstPlayerHand.add(lstHold.get(i + 2));
							lstPlayerHand.add(lstHold.get(i + 3));
							lstPlayerHand.add(lstHold.get(1));
							intHandRank.set(intPlayerRankHold, 8);
							break;
						}
					}
				}
/***********************************FLUSH***************************************/					
				if(intHandRank.get(intPlayerRankHold) < 5)
				{
					
					if (intClub > 4)
					{
						lstPlayerHand.removeAll(lstPlayerHand);
						for (int j = 0; j < lstHold.size(); j ++)
						{
							if (lstHold.get(j).Suit == 1)
							{
								lstPlayerHand.add(lstHold.get(j));
							}
							if (lstPlayerHand.size() == 5)
							{
								intHandRank.set(intPlayerRankHold, 5);
								break;
							}
						}
					}
					if (intDiamond > 4)
					{
						lstPlayerHand.removeAll(lstPlayerHand);
						for (int j = 0; j < lstHold.size(); j ++)
						{
							if (lstHold.get(j).Suit == 2)
							{
								lstPlayerHand.add(lstHold.get(j));
							}
							if (lstPlayerHand.size() == 5)
							{
								intHandRank.set(intPlayerRankHold, 5);
								break;
							}
						}
					}
					if (intSpade > 4)
					{
						lstPlayerHand.removeAll(lstPlayerHand);
						for (int j = 0; j < lstHold.size(); j ++)
						{
							if (lstHold.get(j).Suit == 3)
							{
								lstPlayerHand.add(lstHold.get(j));
							}
							if (lstPlayerHand.size() == 5)
							{
								intHandRank.set(intPlayerRankHold, 5);
								break;
							}
						}
					}
					if (intHeart > 4)
					{
						lstPlayerHand.removeAll(lstPlayerHand);
						for (int j = 0; j < lstHold.size(); j ++)
						{
							if (lstHold.get(j).Suit == 4)
							{
								lstPlayerHand.add(lstHold.get(j));
							}
							if (lstPlayerHand.size() == 5)
							{
								intHandRank.set(intPlayerRankHold, 5);
								break;
							}
						}
					}
				}
			}
	/**************************Four of a Kind**********************************/
			if (intHandRank.get(intPlayerRankHold) < 7)
			{
				for (int i = 0; i < lstHold.size() - 3; i++)
				{
					if (lstHold.get(i).Rank == lstHold.get(i + 1).Rank &&
							lstHold.get(i + 1).Rank == lstHold.get(i + 2).Rank &&
							lstHold.get(i + 2).Rank == lstHold.get(i + 3).Rank)
					{
						
						intHandRank.set(intPlayerRankHold, 7);
						
						lstPlayerHand.removeAll(lstPlayerHand);
						lstPlayerHand.add(lstHold.get(i));
						lstPlayerHand.add(lstHold.get(i + 1));
						lstPlayerHand.add(lstHold.get(i + 2));
						lstPlayerHand.add(lstHold.get(i + 3));						
						break;
					}
				}
				
				//Adds 5th card if four of a kind
				if (intHandRank.get(intPlayerRankHold) == 7)
				{
					for (int i = 0; i < lstHold.size(); i++)
					{
						if (!lstPlayerHand.contains(lstHold.get(i)))
						{
							lstPlayerHand.add(lstHold.get(i));							
							break;
						}					
					}
				}
			}
/***************************************Full House******************************/
			if (intHandRank.get(intPlayerRankHold) < 6)
			{
				synchronized(this){
				//lstPlayerHand.removeAll(lstPlayerHand);
				
				//First Loop to get the three of a kind
				for (int i = 0; i < lstHold.size() - 2; i++)
				{													
					if (lstHold.get(i).Rank == lstHold.get(i + 1).Rank &&
						lstHold.get(i).Rank == lstHold.get(i + 2).Rank &&
						(!lstPlayerHand.contains(lstHold.get(i).Rank)))
					{
						lstPlayerHand.removeAll(lstPlayerHand);
						lstPlayerHand.add(lstHold.get(i));
						lstPlayerHand.add(lstHold.get(i + 1));
						lstPlayerHand.add(lstHold.get(i + 2));
						intHandRank.set(intPlayerRankHold, 6);
						break;
					}					
				}
				
				if(intHandRank.get(intPlayerRankHold) == 6)
				{
					//Second Loop to check the pair
					for (int i = 0; i < lstHold.size() - 1; i++)
					{
						if (!lstPlayerHand.contains(lstHold.get(i)) && 
								lstHold.get(i).Rank == lstHold.get(i + 1).Rank)
						{
							lstPlayerHand.add(lstHold.get(i));
							lstPlayerHand.add(lstHold.get(i + 1));
						}
						if (lstPlayerHand.size() == 5)
						{
							intHandRank.set(intPlayerRankHold, 6);
							break;
						}
						
					}
					if(lstPlayerHand.size() != 5 || intHandRank.get(intPlayerRankHold) == 5)
					{
						intHandRank.set(intPlayerRankHold, 0);
					}
				}}
			}
/**********************************Straight*************************************/
			if (intHandRank.get(intPlayerRankHold) < 4)
			{
				ArrayList<Integer> lstStraight = new ArrayList<Integer>();
				ArrayList<Integer> lstStrIndex = new ArrayList<Integer>();
								
				for (int j = 0; j < lstHold.size(); j++)
				{
					if (!lstStraight.contains(lstHold.get(j).Rank))
					{
						lstStraight.add(lstHold.get(j).Rank);
						lstStrIndex.add(j);
					}
				}
				
				
				if (lstStraight.size() > 4)
				{
					//Loop to check normal straight
					for (int i = 0; i < lstStraight.size() - 4; i++)
					{
						if((lstStraight.get(i) - lstStraight.get(i + 1) == 1) &&
								(lstStraight.get(i + 1) - lstStraight.get(i + 2) == 1) &&
								(lstStraight.get(i + 2) - lstStraight.get(i + 3) == 1) &&
								(lstStraight.get(i + 3) - lstStraight.get(i + 4) == 1))
						{
							lstPlayerHand.removeAll(lstPlayerHand);
							lstPlayerHand.add(lstHold.get(lstStrIndex.get(i)));
							lstPlayerHand.add(lstHold.get(lstStrIndex.get(i + 1)));
							lstPlayerHand.add(lstHold.get(lstStrIndex.get(i + 2)));
							lstPlayerHand.add(lstHold.get(lstStrIndex.get(i + 3)));
							lstPlayerHand.add(lstHold.get(lstStrIndex.get(i + 4)));
							intHandRank.set(intPlayerRankHold, 4);
							break;
						}				
					}
					
					if (intHandRank.get(intPlayerRankHold) != 4)
					{
						//loop to check Ace 2 straight
						for (int i = 0; i < lstStraight.size() - 3; i++)
						{
							if (lstStraight.get(i) == 4 && lstStraight.get(i + 1) == 3 &&
									lstStraight.get(i + 2) == 2 && lstStraight.get(i + 3) == 1 &&
											lstStraight.get(0) == 13)
							{
								lstPlayerHand.removeAll(lstPlayerHand);
								lstPlayerHand.add(lstHold.get(lstStrIndex.get(i)));
								lstPlayerHand.add(lstHold.get(lstStrIndex.get(i + 1)));
								lstPlayerHand.add(lstHold.get(lstStrIndex.get(i + 2)));
								lstPlayerHand.add(lstHold.get(lstStrIndex.get(i + 3)));
								lstPlayerHand.add(lstHold.get(lstStrIndex.get(0)));
								intHandRank.set(intPlayerRankHold, 4);
								break;
							}
						}
					}
				}
			}
			
/************************************Three of a Kind*****************************/
			if (intHandRank.get(intPlayerRankHold) < 3)
			{
				//Loop to find three of a kind
				for (int i = 0; i < lstHold.size() - 2; i++)
				{													
					if (lstHold.get(i).Rank == lstHold.get(i + 1).Rank &&
						lstHold.get(i).Rank == lstHold.get(i + 2).Rank &&
						(!lstPlayerHand.contains(lstHold.get(i).Rank)))
					{
						lstPlayerHand.removeAll(lstPlayerHand);
						lstPlayerHand.add(lstHold.get(i));
						lstPlayerHand.add(lstHold.get(i + 1));
						lstPlayerHand.add(lstHold.get(i + 2));
						intHandRank.set(intPlayerRankHold, 3);
						break;
					}					
				}
				//second loop to add the next two highest cards
				if (intHandRank.get(intPlayerRankHold) == 3)
				{
					for (int i = 0; i < lstHold.size(); i++)
					{
						if (!lstPlayerHand.contains(lstHold.get(i)))
						{
							lstPlayerHand.add(lstHold.get(i));
						}
						if (lstPlayerHand.size() == 5)
						{
							
							break;
						}
					}
				}
			}
			
/*********************************Two Pair**************************************/
			if (intHandRank.get(intPlayerRankHold) < 2)
			{
				lstPlayerHand.removeAll(lstPlayerHand);
				
				//First Loop to get the first pair
				for (int i = 0; i < lstHold.size() - 1; i++)
				{													
					if (lstHold.get(i).Rank == lstHold.get(i + 1).Rank &&						
						(!lstPlayerHand.contains(lstHold.get(i).Rank)))
					{
						lstPlayerHand.removeAll(lstPlayerHand);
						lstPlayerHand.add(lstHold.get(i));
						lstPlayerHand.add(lstHold.get(i + 1));
						break;
					}					
				}
				
				//Second Loop to check the second pair
				for (int i = 0; i < lstHold.size() - 1; i++)
				{
					if (!lstPlayerHand.contains(lstHold.get(i)) && 
							lstHold.get(i).Rank == lstHold.get(i + 1).Rank)
					{
						lstPlayerHand.add(lstHold.get(i));
						lstPlayerHand.add(lstHold.get(i + 1));
						intHandRank.set(intPlayerRankHold, 2);
						break;
					}					
				}
				
				//Adds highest card as last
				if (intHandRank.get(intPlayerRankHold) == 2)
				{
					for (int i = 0; i < lstHold.size(); i++)
					{
						if (!lstPlayerHand.contains(lstHold.get(i)))
						{
							lstPlayerHand.add(lstHold.get(i));							
							break;
						}					
					}
				}
			}
			
/**********************************One Pair*************************************/
			if (intHandRank.get(intPlayerRankHold) < 2)
			{
				//Loop to find a pair
				for (int i = 0; i < lstHold.size() - 1; i++)
				{													
					if (lstHold.get(i).Rank == lstHold.get(i + 1).Rank &&						
						(!lstPlayerHand.contains(lstHold.get(i).Rank)))
					{
						lstPlayerHand.removeAll(lstPlayerHand);
						lstPlayerHand.add(lstHold.get(i));
						lstPlayerHand.add(lstHold.get(i + 1));
						intHandRank.set(intPlayerRankHold, 1);
						break;
					}					
				}
				
				//Loop to add the next three cards
				if (intHandRank.get(intPlayerRankHold) == 1)
				{
					for (int i = 0; i < lstHold.size(); i++)
					{
						if (!lstPlayerHand.contains(lstHold.get(i)))
						{
							lstPlayerHand.add(lstHold.get(i));
						}
						if (lstPlayerHand.size() == 5)
						{						
							break;
						}
					}
				}
			}
			
/*************************************No Hand /High Card*************************/
			if (intHandRank.get(intPlayerRankHold) < 1)
			{
				lstPlayerHand.removeAll(lstPlayerHand);
				
				//Loop to add cards
				for (int i = 0; i < 5; i++)
				{
					if (!lstPlayerHand.contains(lstHold.get(i)))
					{
						lstPlayerHand.add(lstHold.get(i));
					}	
				}	
				
				intHandRank.set(intPlayerRankHold, 0);			
			}
			
			lstAllHands.set(intPlayerRankHold, lstPlayerHand);
			
			//Increment the player's rank position in list
			intPlayerRankHold++;
		}
		
		
		
		//Increments game count
		intTotalGames++;
		
		bolPlayer1Wins = true;
		bolTie = false;
		
		for (int i = 1; i < lstAllHands.size(); i++)
		{
			if(intHandRank.get(0) < intHandRank.get(i))
			{
				bolPlayer1Wins = false;
				break;
			}
			else if(intHandRank.get(0) == intHandRank.get(i))
			{				
				for (int j = 0; j < lstAllHands.get(i).size(); j++)
				{
					if ((intHandRank.get(0) == 8 && intHandRank.get(i) == 8) && 
							lstAllHands.get(0).get(4).Rank != 13 && lstAllHands.get(i).get(4).Rank == 13)
					{
						break;
						
					}
					
					if (lstAllHands.get(0).get(j).Rank < lstAllHands.get(i).get(j).Rank)
					{
						bolPlayer1Wins = false;
						break;
					}	
					else if (lstAllHands.get(0).get(j).Rank > lstAllHands.get(i).get(j).Rank)
					{
						break;
					}
					
					if (lstAllHands.get(0).get(0).Rank == lstAllHands.get(i).get(0).Rank && 
							lstAllHands.get(0).get(1).Rank == lstAllHands.get(i).get(1).Rank && 
							lstAllHands.get(0).get(2).Rank == lstAllHands.get(i).get(2).Rank && 
							lstAllHands.get(0).get(3).Rank == lstAllHands.get(i).get(3).Rank && 
							lstAllHands.get(0).get(4).Rank == lstAllHands.get(i).get(4).Rank)
					{
						bolPlayer1Wins = false;
						bolTie = true;
						break;
					}
				}
			}
//			if (!bolPlayer1Wins)
//			{
//				break;
//			}
		}
		
//		if (intHandRank.get(0) < intHandRank.get(1) || intHandRank.get(0) == intHandRank.get(1))
//		{
//			bolPlayer1Wins = false;
//		}
		
		if (bolPlayer1Wins)
		{
			intPlayer1Wins++;
		}
		
		else if(bolTie)
		{
			intTie++;
		}
		
		else
		{
			intLose++;
		}
		
		
		dblOdds = (((double)intPlayer1Wins / (double)intTotalGames) * 100.0);
		dblTie = (((double)intTie / (double)intTotalGames) * 100.0);
		dblLose = (((double)intLose / (double)intTotalGames) * 100.0);

		strOdds = "Win: " + decOdds.format(dblOdds) + "%";
		strTie = "Tie: " + decOdds.format(dblTie) + "%";
		strLose = "Lose: " + decOdds.format(dblLose) + "%";
		
	}

	@Override
	protected void onPause()
	{
		FlagCancelled = true;
		runner.cancel(true);
		super.onPause();
		
	}
	
	public void increaseAcc(View view)
	{
		if (intAccuracy < 10000 ) //&& lstPlayer1.size() > 0
		{
			intAccuracy += 500;
			txtAcc.setText("Accuracy (" + String.valueOf(intAccuracy) + " Games)");
			
			//Runs async task
//			runner.cancel(true);
//			runner.cancel(false);
//			runner = new AsyncTaskRunner();
//			runner.execute();
		}
	}
	
	public void decreaseAcc(View view)
	{
		if (intAccuracy > 500)// && lstPlayer1.size() > 0)
		{
			intAccuracy -= 500;
			txtAcc.setText("Accuracy (" + String.valueOf(intAccuracy) + " Games)");
			
//			//Runs async task
//			runner.cancel(true);
//			runner.cancel(false);
//			runner = new AsyncTaskRunner();
//			runner.execute();
		}
	}
	

	public void helpDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle(R.string.odds_help).setMessage(R.string.odds_dialog)
		.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		}).create().show();
	}
	
	public void scrollArrows()
	{
		if (!hsv.canScrollHorizontally(-1))
		{
			left_arrow.setVisibility(View.INVISIBLE);			
		}
		else
		{
			left_arrow.setVisibility(View.VISIBLE);
		}
		
		if(!hsv.canScrollHorizontally(1))
		{
			right_arrow.setVisibility(View.INVISIBLE);
		}
		else
		{
			right_arrow.setVisibility(View.VISIBLE);
		}
	}
	
	public void Reset()
	{
		runner.cancel(false);
		
		intIndex = 0;
		intTotalGames = 0;
		intPlayer1Wins = 0;
		intTie = 0;
		intLose = 0;
		dblLose = 0.00;
		dblTie = 0.00;
		dblOdds = 0.00;
		txtOdds.setText("Win: ");
		txtTie.setText("Tie: ");
		txtLose.setText("Lose: ");
		lstPlayer1.clear();	
		lstTable.clear();	
		lstHold.clear();	
		lstAllHands.clear();	
		lstPlayer1Odds.clear();	
		lstPlayer2Odds.clear();	
		lstPlayer3Odds.clear();	
		lstPlayer4Odds.clear();	
		lstPlayer5Odds.clear();	
		lstPlayer6Odds.clear();	
		lstPlayer7Odds.clear();	
		lstPlayer8Odds.clear();	
		lstPlayer9Odds.clear();	
		lstTableOdds.clear();	
		intHandRank.clear();	
		bolGame = true;
		
		txtCards.setText(R.string.odds_choose_cards);
		
		
		//Changes opacity of the face down cards to ~50%
		iView = (ImageButton) findViewById(R.id.ibOddsP1Card1);
		iView.setImageAlpha(127);
		iView.setImageResource(intBackCard);
		iView = (ImageButton) findViewById(R.id.ibOddsP1Card2);
		iView.setImageAlpha(127);
		iView.setImageResource(intBackCard);
		iView = (ImageButton) findViewById(R.id.ibOddsTableCard1);
		iView.setImageAlpha(127);
		iView.setImageResource(intBackCard);
		iView = (ImageButton) findViewById(R.id.ibOddsTableCard2);
		iView.setImageAlpha(127);
		iView.setImageResource(intBackCard);
		iView = (ImageButton) findViewById(R.id.ibOddsTableCard3);
		iView.setImageAlpha(127);
		iView.setImageResource(intBackCard);
		iView = (ImageButton) findViewById(R.id.ibOddsTableCard4);
		iView.setImageAlpha(127);
		iView.setImageResource(intBackCard);
		iView = (ImageButton) findViewById(R.id.ibOddsTableCard5);
		iView.setImageAlpha(127);	
		iView.setImageResource(intBackCard);
		
		for (int i = 0; i < 52; i++)
		{			
			ibView = (ImageButton) llCards.getChildAt(i);
//			ibView.setImageResource(cards[i].pic_rsc);	
			ibView.setColorFilter(null);		
			ibView.setEnabled(true);
		}
		
	}

}
