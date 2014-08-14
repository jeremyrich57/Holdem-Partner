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
//import android.os.CountDownTimer;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnScrollChangedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
//import java.util.TimerTask;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.widget.*;

import com.rich.pokerhandevaluator.R;

public class Texas_Holdem extends Activity {

	TextView txtCards, txtTable, txtPlayer1, txtPlayer2, txtPlayer3, txtPlayer4, 
	txtPlayer5, txtPlayer6, txtPlayer7, txtPlayer8, txtPlayer9;
	int intTotalPlayers = 0, intPlayer = -1, intSameSuit = 0, intClub = 0,
			intDiamond = 0, intSpade = 0, intHeart = 0, intPlayerRankHold = 0, 
			intBackCard = R.drawable.back_card, scrollX;
	ImageButton iView, iSwap;
	LinearLayout ll, llTable, llPlayer1, llPlayer2, llPlayer3, llPlayer4, 
	llPlayer5, llPlayer6, llPlayer7, llPlayer8, llPlayer9, llPT, llDisplay;
	ArrayList<ImageView> lstImages = new ArrayList<ImageView>();
	ArrayList<Card> lstTable = new ArrayList<Card>();
	ArrayList<Card> lstPlayer1 = new ArrayList<Card>();
	ArrayList<Card> lstPlayer2 = new ArrayList<Card>();
	ArrayList<Card> lstPlayer3 = new ArrayList<Card>();
	ArrayList<Card> lstPlayer4 = new ArrayList<Card>();
	ArrayList<Card> lstPlayer5 = new ArrayList<Card>();
	ArrayList<Card> lstPlayer6 = new ArrayList<Card>();
	ArrayList<Card> lstPlayer7 = new ArrayList<Card>();
	ArrayList<Card> lstPlayer8 = new ArrayList<Card>();
	ArrayList<Card> lstPlayer9 = new ArrayList<Card>();
	ArrayList<Card> lstHold = new ArrayList<Card>();
	ArrayList<ArrayList<Card>> lstAllHands = new ArrayList<ArrayList<Card>>();
	ArrayList<Integer> intHandRank = new ArrayList<Integer>();
	Card[] cards = new Card[52];
	String[] strPlayer = new String[] {"Player 1", "Player 2", "Player 3", "Player 4",
			"Player 5", "Player 6", "Player 7", "Player 8", "Player 9"};
	String[] strHands = new String[] { "High Card", "One Pair", "Two Pair", 
			"Three of a Kind",  "Straight", "Flush", "Full House", "Four of a Kind", 
			"Straight Flush"};
	Boolean bolFlush = false, bolGame = true, bolWinner = false;
	private long mLastClick = 0;
	View bar, barPlayer1, barPlayer2, barPlayer3, barPlayer4, barPlayer5, barPlayer6, barPlayer7, barPlayer8,
		barPlayer9;
	ImageView left_arrow, right_arrow;
	HorizontalScrollView hsv;
	Button btnCompare, btnCompare2;
	Timer t = new Timer();
	ScrollView sv;
	Integer intScrollPos;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_texas__holdem);
		// Show the Up button in the action bar.
		setupActionBar();
		
		//Retrieves Player count from previous screen
		Intent intent = getIntent();
		String message = intent.getStringExtra(ChoosePlayers.strPlayers);
		intTotalPlayers = Integer.parseInt(message);
		
		//Sets the text to pick flop cards
		txtCards = (TextView) findViewById(R.id.txtCards);
		txtCards.setText(R.string.pick_flop_cards);
		
		llDisplay = (LinearLayout) findViewById(R.id.linDisplay);
		llTable = (LinearLayout)findViewById(R.id.linTable);
		llPlayer1 = (LinearLayout) findViewById(R.id.linPlayer1);
		llPlayer2 = (LinearLayout) findViewById(R.id.linPlayer2);
		llPlayer3 = (LinearLayout) findViewById(R.id.linPlayer3);
		llPlayer4 = (LinearLayout) findViewById(R.id.linPlayer4);
		llPlayer5 = (LinearLayout) findViewById(R.id.linPlayer5);
		llPlayer6 = (LinearLayout) findViewById(R.id.linPlayer6);
		llPlayer7 = (LinearLayout) findViewById(R.id.linPlayer7);
		llPlayer8 = (LinearLayout) findViewById(R.id.linPlayer8);
		llPlayer9 = (LinearLayout) findViewById(R.id.linPlayer9);
		ll = (LinearLayout)findViewById(R.id.linCards);
		llPT = (LinearLayout)findViewById(R.id.linPlayerTable);
		txtCards = (TextView) findViewById(R.id.txtCards);
		txtPlayer3 = (TextView) findViewById(R.id.txtPlayer3);
		txtPlayer4 = (TextView) findViewById(R.id.txtPlayer4);
		txtPlayer5 = (TextView) findViewById(R.id.txtPlayer5);
		txtPlayer6 = (TextView) findViewById(R.id.txtPlayer6);
		txtPlayer7 = (TextView) findViewById(R.id.txtPlayer7);
		txtPlayer8 = (TextView) findViewById(R.id.txtPlayer8);
		txtPlayer9 = (TextView) findViewById(R.id.txtPlayer9);
		txtTable = (TextView) findViewById(R.id.txtTable);
		hsv = (HorizontalScrollView) findViewById(R.id.horizontalScrollView1);
		left_arrow = (ImageView) findViewById(R.id.left_arrow);
		right_arrow = (ImageView) findViewById(R.id.right_arrow);
		sv = (ScrollView) findViewById(R.id.scrollView1);
		barPlayer1 = (View) findViewById(R.id.barPlayer1);
		barPlayer2 = (View) findViewById(R.id.barPlayer2);
		barPlayer3 = (View) findViewById(R.id.barPlayer3);
		barPlayer4 = (View) findViewById(R.id.barPlayer4);
		barPlayer5 = (View) findViewById(R.id.barPlayer5);
		barPlayer6 = (View) findViewById(R.id.barPlayer6);
		barPlayer7 = (View) findViewById(R.id.barPlayer7);
		barPlayer8 = (View) findViewById(R.id.barPlayer8);
		barPlayer9 = (View) findViewById(R.id.barPlayer9);
		
		
		//Loads the deck of cards
		for (int i = 0; i < 52; i++)
		{
			cards[i] = new Card(i);
			iView = new ImageButton(this);
			iView.setImageResource(cards[i].pic_rsc);
			iView.setPadding(4, 4, 4, 4);
			iView.setOnClickListener(addCard(iView));
			ll.addView(iView);
		}
		
		//Set opacity for cards ~50%
		iView = (ImageButton) findViewById(R.id.ibTableCard1);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibTableCard2);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibTableCard3);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibTableCard4);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibTableCard5);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibPlayer1Card1);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibPlayer1Card2);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibPlayer2Card1);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibPlayer2Card2);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibPlayer3Card1);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibPlayer3Card2);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibPlayer4Card1);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibPlayer4Card2);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibPlayer5Card1);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibPlayer5Card2);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibPlayer6Card1);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibPlayer6Card2);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibPlayer7Card1);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibPlayer7Card2);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibPlayer8Card1);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibPlayer8Card2);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibPlayer9Card1);
		iView.setImageAlpha(127);
		iView = (ImageButton) findViewById(R.id.ibPlayer9Card2);
		iView.setImageAlpha(127);
		
		//Removes non player views
		if(intTotalPlayers == 2)
		{
			llPT.removeView(llPlayer3);
			llPT.removeView(llPlayer4);
			llPT.removeView(llPlayer5);
			llPT.removeView(llPlayer6);
			llPT.removeView(llPlayer7);
			llPT.removeView(llPlayer8);
			llPT.removeView(llPlayer9);
			llPT.removeView(txtPlayer3);
			llPT.removeView(txtPlayer4);
			llPT.removeView(txtPlayer5);
			llPT.removeView(txtPlayer6);
			llPT.removeView(txtPlayer7);
			llPT.removeView(txtPlayer8);
			llPT.removeView(txtPlayer9);

			llPT.removeView(barPlayer3);
			llPT.removeView(barPlayer4);
			llPT.removeView(barPlayer5);
			llPT.removeView(barPlayer6);
			llPT.removeView(barPlayer7);
			llPT.removeView(barPlayer8);
			llPT.removeView(barPlayer9);
		}
		else if (intTotalPlayers == 3)
		{
			llPT.removeView(llPlayer4);
			llPT.removeView(llPlayer5);
			llPT.removeView(llPlayer6);
			llPT.removeView(llPlayer7);
			llPT.removeView(llPlayer8);
			llPT.removeView(llPlayer9);
			llPT.removeView(txtPlayer4);
			llPT.removeView(txtPlayer5);
			llPT.removeView(txtPlayer6);
			llPT.removeView(txtPlayer7);
			llPT.removeView(txtPlayer8);
			llPT.removeView(txtPlayer9);

			llPT.removeView(barPlayer4);

			llPT.removeView(barPlayer5);

			llPT.removeView(barPlayer6);

			llPT.removeView(barPlayer7);

			llPT.removeView(barPlayer8);	

			llPT.removeView(barPlayer9);
		}
		else if (intTotalPlayers == 4)
		{
			llPT.removeView(llPlayer5);
			llPT.removeView(llPlayer6);
			llPT.removeView(llPlayer7);
			llPT.removeView(llPlayer8);
			llPT.removeView(llPlayer9);
			llPT.removeView(txtPlayer5);
			llPT.removeView(txtPlayer6);
			llPT.removeView(txtPlayer7);
			llPT.removeView(txtPlayer8);
			llPT.removeView(txtPlayer9);

			llPT.removeView(barPlayer5);

			llPT.removeView(barPlayer6);

			llPT.removeView(barPlayer7);

			llPT.removeView(barPlayer8);	

			llPT.removeView(barPlayer9);
		}
		else if (intTotalPlayers == 5)
		{
			llPT.removeView(llPlayer6);
			llPT.removeView(llPlayer7);
			llPT.removeView(llPlayer8);
			llPT.removeView(llPlayer9);
			llPT.removeView(txtPlayer6);
			llPT.removeView(txtPlayer7);
			llPT.removeView(txtPlayer8);
			llPT.removeView(txtPlayer9);

			llPT.removeView(barPlayer6);

			llPT.removeView(barPlayer7);

			llPT.removeView(barPlayer8);	

			llPT.removeView(barPlayer9);
		}
		else if (intTotalPlayers == 6)
		{
			llPT.removeView(llPlayer7);
			llPT.removeView(llPlayer8);
			llPT.removeView(llPlayer9);
			llPT.removeView(txtPlayer7);
			llPT.removeView(txtPlayer8);
			llPT.removeView(txtPlayer9);

			llPT.removeView(barPlayer7);

			llPT.removeView(barPlayer8);	

			llPT.removeView(barPlayer9);
		}
		else if (intTotalPlayers == 7)
		{
			llPT.removeView(llPlayer8);
			llPT.removeView(llPlayer9);
			llPT.removeView(txtPlayer8);
			llPT.removeView(txtPlayer9);

			llPT.removeView(barPlayer8);	
			llPT.removeView(barPlayer9);
		}
		else if (intTotalPlayers == 8)
		{
			llPT.removeView(llPlayer9);
			llPT.removeView(txtPlayer9);

			llPT.removeView(barPlayer9);
		}
		
		lstTable.clear();
		lstPlayer1.clear();
		lstPlayer2.clear();
		lstPlayer3.clear();
		lstPlayer4.clear();
		lstPlayer5.clear();
		lstPlayer6.clear();
		lstPlayer7.clear();
		lstPlayer8.clear();
		lstPlayer9.clear();
		
		
		
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
		getMenuInflater().inflate(R.menu.texas__holdem, menu);
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
		case R.id.reset:
			reset();
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
		case R.id.odds:
			Intent intent3 = new Intent (this, ChoosePlayersOdds.class);
			startActivity(intent3);
			finish();
			return true;
		case R.id.changePlayers:
			Intent intent4 = new Intent(this, ChoosePlayers.class);
			startActivity(intent4);
			finish();			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//Puts card back on table after long click
	View.OnLongClickListener swapCard(final ImageButton card) {
		return new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub		
//				View btnEval = findViewById(R.id.btnCheck);
				if (!bolWinner)
				{
					iView = (ImageButton) v;
					card.setLongClickable(false);
					
					btnCompare2 = (Button) findViewById(R.id.btnCompare2);
					btnCompare2.setVisibility(View.INVISIBLE);
					
					if(card.getParent() == llTable)
					{
						for(int i = 0; i < 52; i++)
						{
							if (lstTable.get(llTable.indexOfChild(card)).pic_rsc == cards[i].pic_rsc)
								{
									iView.setImageResource(intBackCard);
									iSwap = (ImageButton) ll.getChildAt(i);
									lstTable.remove(llTable.indexOfChild(card));
									bolGame = true;
									break;
								}
						}
						if (lstTable.size() == 0)
						{
							iView = (ImageButton) findViewById(R.id.ibTableCard1);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
						}
						else if (lstTable.size() == 1)
						{
							iView = (ImageButton) findViewById(R.id.ibTableCard1);
							iView.setImageResource(lstTable.get(0).pic_rsc);
							iView.setClickable(false);
							iView.setLongClickable(true);
							
							iView = (ImageButton) findViewById(R.id.ibTableCard2);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
							iView = (ImageButton) findViewById(R.id.ibTableCard3);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
							iView = (ImageButton) findViewById(R.id.ibTableCard4);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
							iView = (ImageButton) findViewById(R.id.ibTableCard5);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
						}
						
						else if (lstTable.size() == 2)
						{
							iView = (ImageButton) findViewById(R.id.ibTableCard1);
							iView.setImageResource(lstTable.get(0).pic_rsc);
							iView.setClickable(false);
							iView.setLongClickable(true);
							iView = (ImageButton) findViewById(R.id.ibTableCard2);
							iView.setImageResource(lstTable.get(1).pic_rsc);		
							iView.setClickable(false);
							iView.setLongClickable(true);
							
							iView = (ImageButton) findViewById(R.id.ibTableCard3);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
							iView = (ImageButton) findViewById(R.id.ibTableCard4);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
							iView = (ImageButton) findViewById(R.id.ibTableCard5);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
						}
						
						else if (lstTable.size() == 3)
						{
							iView = (ImageButton) findViewById(R.id.ibTableCard1);
							iView.setImageResource(lstTable.get(0).pic_rsc);
							iView.setClickable(false);
							iView.setLongClickable(true);
							iView = (ImageButton) findViewById(R.id.ibTableCard2);
							iView.setImageResource(lstTable.get(1).pic_rsc);	
							iView.setClickable(false);
							iView.setLongClickable(true);
							iView = (ImageButton) findViewById(R.id.ibTableCard3);
							iView.setImageResource(lstTable.get(2).pic_rsc);
							iView.setClickable(false);
							iView.setLongClickable(true);
							
							iView = (ImageButton) findViewById(R.id.ibTableCard4);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
							iView = (ImageButton) findViewById(R.id.ibTableCard5);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
						}
						
						else if (lstTable.size() == 4)
						{
							iView = (ImageButton) findViewById(R.id.ibTableCard1);
							iView.setImageResource(lstTable.get(0).pic_rsc);
							iView.setClickable(false);
							iView.setLongClickable(true);
							iView = (ImageButton) findViewById(R.id.ibTableCard2);
							iView.setImageResource(lstTable.get(1).pic_rsc);		
							iView.setClickable(false);
							iView.setLongClickable(true);
							iView = (ImageButton) findViewById(R.id.ibTableCard3);
							iView.setImageResource(lstTable.get(2).pic_rsc);	
							iView.setClickable(false);
							iView.setLongClickable(true);
							iView = (ImageButton) findViewById(R.id.ibTableCard4);
							iView.setImageResource(lstTable.get(3).pic_rsc);
							iView.setClickable(false);
							iView.setLongClickable(true);
							
							iView = (ImageButton) findViewById(R.id.ibTableCard5);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
						}
					}
					else if (card.getParent() == llPlayer1)
					{
						for(int i = 0; i < 52; i++)
						{
							if (lstPlayer1.get(llPlayer1.indexOfChild(card)).pic_rsc == cards[i].pic_rsc)
							{						
								iView.setImageResource(intBackCard);
								iSwap = (ImageButton) ll.getChildAt(i);
								lstPlayer1.remove(llPlayer1.indexOfChild(card));
								bolGame = true;
								break;
							}
		//					
						}
						if (lstPlayer1.size() == 1)
						{
							iView = (ImageButton) findViewById(R.id.ibPlayer1Card1);
							iView.setImageResource(lstPlayer1.get(0).pic_rsc);
							iView.setClickable(false);
							iView.setLongClickable(true);
							
							iView = (ImageButton) findViewById(R.id.ibPlayer1Card2);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
						}
						else
						{
							iView = (ImageButton) findViewById(R.id.ibPlayer1Card1);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
						}
					}
					else if (card.getParent() == llPlayer2)
					{
						for(int i = 0; i < 52; i++)
						{
							if (lstPlayer2.get(llPlayer2.indexOfChild(card)).pic_rsc == cards[i].pic_rsc)
							{						
								iView.setImageResource(intBackCard);
								iSwap = (ImageButton) ll.getChildAt(i);
								lstPlayer2.remove(llPlayer2.indexOfChild(card));
								bolGame = true;
								break;
							}
		//					
						}
						if (lstPlayer2.size() == 1)
						{
							iView = (ImageButton) findViewById(R.id.ibPlayer2Card1);
							iView.setImageResource(lstPlayer2.get(0).pic_rsc);
							iView.setClickable(false);
							iView.setLongClickable(true);
							
							iView = (ImageButton) findViewById(R.id.ibPlayer2Card2);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
						}
						else
						{
							iView = (ImageButton) findViewById(R.id.ibPlayer2Card1);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
						}
					}
					else if (card.getParent() == llPlayer3)
					{
						for(int i = 0; i < 52; i++)
						{
							if (lstPlayer3.get(llPlayer3.indexOfChild(card)).pic_rsc == cards[i].pic_rsc)
							{						
								iView.setImageResource(intBackCard);
								iSwap = (ImageButton) ll.getChildAt(i);
								lstPlayer3.remove(llPlayer3.indexOfChild(card));
								bolGame = true;
								break;
							}
		//					
						}
						if (lstPlayer3.size() == 1)
						{
							iView = (ImageButton) findViewById(R.id.ibPlayer3Card1);
							iView.setImageResource(lstPlayer3.get(0).pic_rsc);
							iView.setClickable(false);
							iView.setLongClickable(true);
							
							iView = (ImageButton) findViewById(R.id.ibPlayer3Card2);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
						}
						else
						{
							iView = (ImageButton) findViewById(R.id.ibPlayer3Card1);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
						}
					}
					else if (card.getParent() == llPlayer4)
					{
						for(int i = 0; i < 52; i++)
						{
							if (lstPlayer4.get(llPlayer4.indexOfChild(card)).pic_rsc == cards[i].pic_rsc)
							{						
								iView.setImageResource(intBackCard);
								iSwap = (ImageButton) ll.getChildAt(i);
								lstPlayer4.remove(llPlayer4.indexOfChild(card));
								bolGame = true;
								break;
							}
		//					
						}
						if (lstPlayer4.size() == 1)
						{
							iView = (ImageButton) findViewById(R.id.ibPlayer4Card1);
							iView.setImageResource(lstPlayer4.get(0).pic_rsc);
							iView.setClickable(false);
							iView.setLongClickable(true);
							
							iView = (ImageButton) findViewById(R.id.ibPlayer4Card2);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
						}
						else
						{
							iView = (ImageButton) findViewById(R.id.ibPlayer4Card1);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
						}
					}
					else if (card.getParent() == llPlayer5)
					{
						for(int i = 0; i < 52; i++)
						{
							if (lstPlayer5.get(llPlayer5.indexOfChild(card)).pic_rsc == cards[i].pic_rsc)
							{						
								iView.setImageResource(intBackCard);
								iSwap = (ImageButton) ll.getChildAt(i);
								lstPlayer5.remove(llPlayer5.indexOfChild(card));
								bolGame = true;
								break;
							}
		//					
						}
						if (lstPlayer5.size() == 1)
						{
							iView = (ImageButton) findViewById(R.id.ibPlayer5Card1);
							iView.setImageResource(lstPlayer5.get(0).pic_rsc);
							iView.setClickable(false);
							iView.setLongClickable(true);
							
							iView = (ImageButton) findViewById(R.id.ibPlayer5Card2);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
						}
						else
						{
							iView = (ImageButton) findViewById(R.id.ibPlayer5Card1);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
						}
					}
					else if (card.getParent() == llPlayer6)
					{
						for(int i = 0; i < 52; i++)
						{
							if (lstPlayer6.get(llPlayer6.indexOfChild(card)).pic_rsc == cards[i].pic_rsc)
							{						
								iView.setImageResource(intBackCard);
								iSwap = (ImageButton) ll.getChildAt(i);
								lstPlayer6.remove(llPlayer6.indexOfChild(card));
								bolGame = true;
								break;
							}
		//					
						}
						if (lstPlayer6.size() == 1)
						{
							iView = (ImageButton) findViewById(R.id.ibPlayer6Card1);
							iView.setImageResource(lstPlayer6.get(0).pic_rsc);
							iView.setClickable(false);
							iView.setLongClickable(true);
							
							iView = (ImageButton) findViewById(R.id.ibPlayer6Card2);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
						}
						else
						{
							iView = (ImageButton) findViewById(R.id.ibPlayer6Card1);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
						}
					}
					else if (card.getParent() == llPlayer7)
					{
						for(int i = 0; i < 52; i++)
						{
							if (lstPlayer7.get(llPlayer7.indexOfChild(card)).pic_rsc == cards[i].pic_rsc)
							{						
								iView.setImageResource(intBackCard);
								iSwap = (ImageButton) ll.getChildAt(i);
								lstPlayer7.remove(llPlayer7.indexOfChild(card));
								bolGame = true;
								break;
							}
		//					
						}
						if (lstPlayer7.size() == 1)
						{
							iView = (ImageButton) findViewById(R.id.ibPlayer7Card1);
							iView.setImageResource(lstPlayer7.get(0).pic_rsc);
							iView.setClickable(false);
							iView.setLongClickable(true);
							
							iView = (ImageButton) findViewById(R.id.ibPlayer7Card2);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
						}
						else
						{
							iView = (ImageButton) findViewById(R.id.ibPlayer7Card1);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
						}
					}
					else if (card.getParent() == llPlayer8)
					{
						for(int i = 0; i < 52; i++)
						{
							if (lstPlayer8.get(llPlayer8.indexOfChild(card)).pic_rsc == cards[i].pic_rsc)
							{						
								iView.setImageResource(intBackCard);
								iSwap = (ImageButton) ll.getChildAt(i);
								lstPlayer8.remove(llPlayer8.indexOfChild(card));
								bolGame = true;
								break;
							}
		//					
						}
						if (lstPlayer8.size() == 1)
						{
							iView = (ImageButton) findViewById(R.id.ibPlayer8Card1);
							iView.setImageResource(lstPlayer8.get(0).pic_rsc);
							iView.setClickable(false);
							iView.setLongClickable(true);
							
							iView = (ImageButton) findViewById(R.id.ibPlayer8Card2);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
						}
						else
						{
							iView = (ImageButton) findViewById(R.id.ibPlayer8Card1);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
						}
					}
					else if (card.getParent() == llPlayer9)
					{
						for(int i = 0; i < 52; i++)
						{
							if (lstPlayer9.get(llPlayer9.indexOfChild(card)).pic_rsc == cards[i].pic_rsc)
							{						
								iView.setImageResource(intBackCard);
								iSwap = (ImageButton) ll.getChildAt(i);
								lstPlayer9.remove(llPlayer9.indexOfChild(card));
								bolGame = true;
								break;
							}
		//					
						}
						if (lstPlayer9.size() == 1)
						{
							iView = (ImageButton) findViewById(R.id.ibPlayer9Card1);
							iView.setImageResource(lstPlayer9.get(0).pic_rsc);
							iView.setClickable(false);
							iView.setLongClickable(true);
							
							iView = (ImageButton) findViewById(R.id.ibPlayer9Card2);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
						}
						else
						{
							iView = (ImageButton) findViewById(R.id.ibPlayer9Card1);
							iView.setImageResource(intBackCard);
							iView.setImageAlpha(127);
							iView.setClickable(false);
							iView.setLongClickable(false);
						}
					}
					
					iSwap.setColorFilter(null);		
					iSwap.setEnabled(true);
					iSwap.setClickable(true);
					
					if (lstTable.size() < 3)
					{
						txtCards.setText(R.string.pick_flop_cards);
						intPlayer = -1;
					}
					else if (lstTable.size() == 3)
					{
						txtCards.setText(R.string.pick_turn);
						intPlayer = -1;
					}
					else if (lstTable.size() == 4)
					{
						txtCards.setText(R.string.pick_river);
						intPlayer = -1;
					}
					else if(lstPlayer1.size() < 2)
					{
						txtCards.setText(R.string.player1_cards);
						intPlayer = 0;
					}
					else if(lstPlayer2.size() < 2)
					{
						txtCards.setText(R.string.player2_cards);
						intPlayer = 1;
					}
					else if(lstPlayer3.size() < 2)
					{
						txtCards.setText(R.string.player3_cards);
						intPlayer = 2;
					}
					else if(lstPlayer4.size() < 2)
					{
						txtCards.setText(R.string.player4_cards);
						intPlayer = 3;
					}
					else if(lstPlayer5.size() < 2)
					{
						txtCards.setText(R.string.player5_cards);
						intPlayer = 4;
					}
					else if(lstPlayer6.size() < 2)
					{
						txtCards.setText(R.string.player6_cards);
						intPlayer = 5;
					}
					else if(lstPlayer7.size() < 2)
					{
						txtCards.setText(R.string.player7_cards);
						intPlayer = 6;
					}
					else if(lstPlayer8.size() < 2)
					{
						txtCards.setText(R.string.player8_cards);
						intPlayer = 7;
					}
					else if(lstPlayer9.size() < 2)
					{
						txtCards.setText(R.string.player9_cards);
						intPlayer = 8;
					}
				}
				
				
				return true;
			}
		};
		
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
		//Adds the cards
		else
		{
			mLastClick = SystemClock.elapsedRealtime();
			if(intPlayer < intTotalPlayers)
			{
				v.setClickable(false);				
				
				if(lstTable.size() == 0)
				{
					lstTable.add(cards[ll.indexOfChild(v)]);
					iView = (ImageButton) findViewById(R.id.ibTableCard1);
					iView.setImageResource(cards[ll.indexOfChild(v)].pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					iView.setLongClickable(true);
					iView.setOnLongClickListener(swapCard(iView));
					iView.setImageAlpha(255);
					
					//Darkens the image after clicking and disables it
					iView = (ImageButton) v;
					iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
					v.setEnabled(false);
				}
				else if (lstTable.size() == 1)
				{
					lstTable.add(cards[ll.indexOfChild(v)]);
					iView = (ImageButton) findViewById(R.id.ibTableCard2);
					iView.setImageResource(cards[ll.indexOfChild(v)].pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					iView.setLongClickable(true);
					iView.setOnLongClickListener(swapCard(iView));
					iView.setImageAlpha(255);
					
					//Darkens the image after clicking and disables it
					iView = (ImageButton) v;
					iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
					v.setEnabled(false);
				}
				else if (lstTable.size() == 2)
				{
					lstTable.add(cards[ll.indexOfChild(v)]);
					iView = (ImageButton) findViewById(R.id.ibTableCard3);
					iView.setImageResource(cards[ll.indexOfChild(v)].pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					iView.setLongClickable(true);
					iView.setOnLongClickListener(swapCard(iView));
					iView.setImageAlpha(255);
					
					//Darkens the image after clicking and disables it
					iView = (ImageButton) v;
					iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
					v.setEnabled(false);
				}
				else if (lstTable.size() == 3)
				{
					lstTable.add(cards[ll.indexOfChild(v)]);
					iView = (ImageButton) findViewById(R.id.ibTableCard4);
					iView.setImageResource(cards[ll.indexOfChild(v)].pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					iView.setLongClickable(true);
					iView.setOnLongClickListener(swapCard(iView));
					iView.setImageAlpha(255);
					
					//Darkens the image after clicking and disables it
					iView = (ImageButton) v;
					iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
					v.setEnabled(false);
				}
				else if (lstTable.size() == 4)
				{
					lstTable.add(cards[ll.indexOfChild(v)]);
					iView = (ImageButton) findViewById(R.id.ibTableCard5);
					iView.setImageResource(cards[ll.indexOfChild(v)].pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					iView.setLongClickable(true);
					iView.setOnLongClickListener(swapCard(iView));
					iView.setImageAlpha(255);
					
					//Darkens the image after clicking and disables it
					iView = (ImageButton) v;
					iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
					v.setEnabled(false);
				}
				else if (lstPlayer1.size() == 0)
				{
					lstPlayer1.add(cards[ll.indexOfChild(v)]);
					iView = (ImageButton) findViewById(R.id.ibPlayer1Card1);
					iView.setImageResource(cards[ll.indexOfChild(v)].pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					iView.setLongClickable(true);
					iView.setOnLongClickListener(swapCard(iView));
					iView.setImageAlpha(255);
					
					//Darkens the image after clicking and disables it
					iView = (ImageButton) v;
					iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
					v.setEnabled(false);
				}
				else if (lstPlayer1.size() == 1)
				{
					lstPlayer1.add(cards[ll.indexOfChild(v)]);
					iView = (ImageButton) findViewById(R.id.ibPlayer1Card2);
					iView.setImageResource(cards[ll.indexOfChild(v)].pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					iView.setLongClickable(true);
					iView.setOnLongClickListener(swapCard(iView));
					iView.setImageAlpha(255);
					
					//Darkens the image after clicking and disables it
					iView = (ImageButton) v;
					iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
					v.setEnabled(false);
					
					//Automatic Scroll to Player 2's cards
					sv.scrollTo(0, barPlayer1.getBottom());
				}
				else if (lstPlayer2.size() == 0)
				{
					lstPlayer2.add(cards[ll.indexOfChild(v)]);
					iView = (ImageButton) findViewById(R.id.ibPlayer2Card1);
					iView.setImageResource(cards[ll.indexOfChild(v)].pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					iView.setLongClickable(true);
					iView.setOnLongClickListener(swapCard(iView));
					iView.setImageAlpha(255);
					
					//Darkens the image after clicking and disables it
					iView = (ImageButton) v;
					iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
					v.setEnabled(false);
				}
				else if (lstPlayer2.size() == 1)
				{
					lstPlayer2.add(cards[ll.indexOfChild(v)]);
					iView = (ImageButton) findViewById(R.id.ibPlayer2Card2);
					iView.setImageResource(cards[ll.indexOfChild(v)].pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					iView.setLongClickable(true);
					iView.setOnLongClickListener(swapCard(iView));
					iView.setImageAlpha(255);
					
					//Darkens the image after clicking and disables it
					iView = (ImageButton) v;
					iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
					v.setEnabled(false);
					
					//Automatic Scroll to Player 3's cards
					sv.scrollTo(0, barPlayer2.getBottom());
				}
				else if (lstPlayer3.size() == 0)
				{
					lstPlayer3.add(cards[ll.indexOfChild(v)]);
					iView = (ImageButton) findViewById(R.id.ibPlayer3Card1);
					iView.setImageResource(cards[ll.indexOfChild(v)].pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					iView.setLongClickable(true);
					iView.setOnLongClickListener(swapCard(iView));
					iView.setImageAlpha(255);
					
					//Darkens the image after clicking and disables it
					iView = (ImageButton) v;
					iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
					v.setEnabled(false);
				}
				else if (lstPlayer3.size() == 1)
				{
					lstPlayer3.add(cards[ll.indexOfChild(v)]);
					iView = (ImageButton) findViewById(R.id.ibPlayer3Card2);
					iView.setImageResource(cards[ll.indexOfChild(v)].pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					iView.setLongClickable(true);
					iView.setOnLongClickListener(swapCard(iView));
					iView.setImageAlpha(255);
					
					//Darkens the image after clicking and disables it
					iView = (ImageButton) v;
					iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
					v.setEnabled(false);
					
					//Automatic Scroll to Player 4's cards
					sv.scrollTo(0, barPlayer3.getBottom());
				}
				else if (lstPlayer4.size() == 0)
				{
					lstPlayer4.add(cards[ll.indexOfChild(v)]);
					iView = (ImageButton) findViewById(R.id.ibPlayer4Card1);
					iView.setImageResource(cards[ll.indexOfChild(v)].pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					iView.setLongClickable(true);
					iView.setOnLongClickListener(swapCard(iView));
					iView.setImageAlpha(255);
					
					//Darkens the image after clicking and disables it
					iView = (ImageButton) v;
					iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
					v.setEnabled(false);
				}
				else if (lstPlayer4.size() == 1)
				{
					lstPlayer4.add(cards[ll.indexOfChild(v)]);
					iView = (ImageButton) findViewById(R.id.ibPlayer4Card2);
					iView.setImageResource(cards[ll.indexOfChild(v)].pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					iView.setLongClickable(true);
					iView.setOnLongClickListener(swapCard(iView));
					iView.setImageAlpha(255);
					
					//Darkens the image after clicking and disables it
					iView = (ImageButton) v;
					iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
					v.setEnabled(false);
					
					//Automatic Scroll to Player 5's cards
					sv.scrollTo(0, barPlayer4.getBottom());
				}
				else if (lstPlayer5.size() == 0)
				{
					lstPlayer5.add(cards[ll.indexOfChild(v)]);
					iView = (ImageButton) findViewById(R.id.ibPlayer5Card1);
					iView.setImageResource(cards[ll.indexOfChild(v)].pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					iView.setLongClickable(true);
					iView.setOnLongClickListener(swapCard(iView));
					iView.setImageAlpha(255);
					
					//Darkens the image after clicking and disables it
					iView = (ImageButton) v;
					iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
					v.setEnabled(false);
				}
				else if (lstPlayer5.size() == 1)
				{
					lstPlayer5.add(cards[ll.indexOfChild(v)]);
					iView = (ImageButton) findViewById(R.id.ibPlayer5Card2);
					iView.setImageResource(cards[ll.indexOfChild(v)].pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					iView.setLongClickable(true);
					iView.setOnLongClickListener(swapCard(iView));
					iView.setImageAlpha(255);
					
					//Darkens the image after clicking and disables it
					iView = (ImageButton) v;
					iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
					v.setEnabled(false);
					
					//Automatic Scroll to Player 6's cards
					sv.scrollTo(0, barPlayer5.getBottom());
				}
				else if (lstPlayer6.size() == 0)
				{
					lstPlayer6.add(cards[ll.indexOfChild(v)]);
					iView = (ImageButton) findViewById(R.id.ibPlayer6Card1);
					iView.setImageResource(cards[ll.indexOfChild(v)].pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					iView.setLongClickable(true);
					iView.setOnLongClickListener(swapCard(iView));
					iView.setImageAlpha(255);
					
					//Darkens the image after clicking and disables it
					iView = (ImageButton) v;
					iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
					v.setEnabled(false);
				}
				else if (lstPlayer6.size() == 1)
				{
					lstPlayer6.add(cards[ll.indexOfChild(v)]);
					iView = (ImageButton) findViewById(R.id.ibPlayer6Card2);
					iView.setImageResource(cards[ll.indexOfChild(v)].pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					iView.setLongClickable(true);
					iView.setOnLongClickListener(swapCard(iView));
					iView.setImageAlpha(255);
					
					//Darkens the image after clicking and disables it
					iView = (ImageButton) v;
					iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
					v.setEnabled(false);
					
					//Automatic Scroll to Player 7's cards
					sv.scrollTo(0, barPlayer6.getBottom());
				}
				else if (lstPlayer7.size() == 0)
				{
					lstPlayer7.add(cards[ll.indexOfChild(v)]);
					iView = (ImageButton) findViewById(R.id.ibPlayer7Card1);
					iView.setImageResource(cards[ll.indexOfChild(v)].pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					iView.setLongClickable(true);
					iView.setOnLongClickListener(swapCard(iView));
					iView.setImageAlpha(255);
					
					//Darkens the image after clicking and disables it
					iView = (ImageButton) v;
					iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
					v.setEnabled(false);
				}
				else if (lstPlayer7.size() == 1)
				{
					lstPlayer7.add(cards[ll.indexOfChild(v)]);
					iView = (ImageButton) findViewById(R.id.ibPlayer7Card2);
					iView.setImageResource(cards[ll.indexOfChild(v)].pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					iView.setLongClickable(true);
					iView.setOnLongClickListener(swapCard(iView));
					iView.setImageAlpha(255);
					
					//Darkens the image after clicking and disables it
					iView = (ImageButton) v;
					iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
					v.setEnabled(false);
					
					//Automatic Scroll to Player 8's cards
					sv.scrollTo(0, barPlayer7.getBottom());
				}
				else if (lstPlayer8.size() == 0)
				{
					lstPlayer8.add(cards[ll.indexOfChild(v)]);
					iView = (ImageButton) findViewById(R.id.ibPlayer8Card1);
					iView.setImageResource(cards[ll.indexOfChild(v)].pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					iView.setLongClickable(true);
					iView.setOnLongClickListener(swapCard(iView));
					iView.setImageAlpha(255);
					
					//Darkens the image after clicking and disables it
					iView = (ImageButton) v;
					iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
					v.setEnabled(false);
				}
				else if (lstPlayer8.size() == 1)
				{
					lstPlayer8.add(cards[ll.indexOfChild(v)]);
					iView = (ImageButton) findViewById(R.id.ibPlayer8Card2);
					iView.setImageResource(cards[ll.indexOfChild(v)].pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					iView.setLongClickable(true);
					iView.setOnLongClickListener(swapCard(iView));
					iView.setImageAlpha(255);
					
					//Darkens the image after clicking and disables it
					iView = (ImageButton) v;
					iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
					v.setEnabled(false);
					
					//Automatic Scroll to Player 9's cards
					sv.scrollTo(0, barPlayer8.getBottom());
				}
				else if (lstPlayer9.size() == 0)
				{
					lstPlayer9.add(cards[ll.indexOfChild(v)]);
					iView = (ImageButton) findViewById(R.id.ibPlayer9Card1);
					iView.setImageResource(cards[ll.indexOfChild(v)].pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					iView.setLongClickable(true);
					iView.setOnLongClickListener(swapCard(iView));
					iView.setImageAlpha(255);
					
					//Darkens the image after clicking and disables it
					iView = (ImageButton) v;
					iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
					v.setEnabled(false);
				}
				else if (lstPlayer9.size() == 1)
				{
					lstPlayer9.add(cards[ll.indexOfChild(v)]);
					iView = (ImageButton) findViewById(R.id.ibPlayer9Card2);
					iView.setImageResource(cards[ll.indexOfChild(v)].pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					iView.setLongClickable(true);
					iView.setOnLongClickListener(swapCard(iView));
					iView.setImageAlpha(255);
					
					//Darkens the image after clicking and disables it
					iView = (ImageButton) v;
					iView.setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_OVER);		
					v.setEnabled(false);
				}				
				
				if (lstTable.size() < 3)
				{
					txtCards.setText(R.string.pick_flop_cards);
					intPlayer = -1;
				}
				else if (lstTable.size() == 3)
				{
					txtCards.setText(R.string.pick_turn);
					intPlayer = -1;
				}
				else if (lstTable.size() == 4)
				{
					txtCards.setText(R.string.pick_river);
					intPlayer = -1;
				}
				else if(lstPlayer1.size() < 2)
				{
					txtCards.setText(R.string.player1_cards);
					intPlayer = 0;
				}
				else if(lstPlayer2.size() < 2 && intPlayer != intTotalPlayers)
				{
					txtCards.setText(R.string.player2_cards);
					intPlayer = 1;
				}
				else if(lstPlayer3.size() < 2 && intPlayer != intTotalPlayers)
				{
					txtCards.setText(R.string.player3_cards);
					intPlayer = 2;
				}
				else if(lstPlayer4.size() < 2 && intPlayer != intTotalPlayers)
				{
					txtCards.setText(R.string.player4_cards);
					intPlayer = 3;
				}
				else if(lstPlayer5.size() < 2 && intPlayer != intTotalPlayers)
				{
					txtCards.setText(R.string.player5_cards);
					intPlayer = 4;
				}
				else if(lstPlayer6.size() < 2 && intPlayer != intTotalPlayers)
				{
					txtCards.setText(R.string.player6_cards);
					intPlayer = 5;
				}
				else if(lstPlayer7.size() < 2 && intPlayer != intTotalPlayers)
				{
					txtCards.setText(R.string.player7_cards);
					intPlayer = 6;
				}
				else if(lstPlayer8.size() < 2 && intPlayer != intTotalPlayers)
				{
					txtCards.setText(R.string.player8_cards);
					intPlayer = 7;
				}
				else if(lstPlayer9.size() < 2 && intPlayer != intTotalPlayers)
				{
					txtCards.setText(R.string.player9_cards);
					intPlayer = 8;
				}	
				else if(lstPlayer9.size() == 2)
				{
					intPlayer = 9;
				}
				
				//Changes text when all players cards have been selected
				if (intPlayer == intTotalPlayers)
				{
					txtCards.setText(R.string.check_win);
					bolGame = false;
//					btnCompare = (Button) findViewById(R.id.btnCompare);
//					btnCompare.setVisibility(View.VISIBLE);
					checkWinButton();
				}							
			}	
		}
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
	
	public void checkWinButton(View view)
	{
//		t.schedule(testTask(), 5000);
		if(!bolGame)
		{
			handEval();
		}
	}
	
//	public TimerTask testTask()
//	{
//		handEval();
//		return null;
//		
//	}
	
	//Compares the hands
	public void handEval()
	{
	
		
		bolGame = false;
		ll = (LinearLayout)findViewById(R.id.linCards);
		ll.removeAllViews();
		
		//Adds each player's cards to the master list of all hands
		if (!lstPlayer1.isEmpty())
		{
			lstAllHands.add(lstPlayer1);
		}
		if (!lstPlayer2.isEmpty())
		{
			lstAllHands.add(lstPlayer2);
		}
		if (!lstPlayer3.isEmpty())
		{
			lstAllHands.add(lstPlayer3);
		}
		if (!lstPlayer4.isEmpty())
		{
			lstAllHands.add(lstPlayer4);
		}
		if (!lstPlayer5.isEmpty())
		{
			lstAllHands.add(lstPlayer5);
		}
		if (!lstPlayer6.isEmpty())
		{
			lstAllHands.add(lstPlayer6);
		}
		if (!lstPlayer7.isEmpty())
		{
			lstAllHands.add(lstPlayer7);
		}
		if (!lstPlayer8.isEmpty())
		{
			lstAllHands.add(lstPlayer8);
		}
		if (!lstPlayer9.isEmpty())
		{
			lstAllHands.add(lstPlayer9);
		}
		
		//For loop to add the tables cards to each player's hands and then
		//sorts them. Then evaluates the cards for the highest rank hand.
		for(ArrayList<Card> lstPlayerHand : lstAllHands){
			//Adds cards on table to each player's hand
			lstPlayerHand.addAll(lstTable);
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
					for (int i = 1; i < lstHold.size() - 3; i++)
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
					for (int i = 2; i < lstHold.size() -3; i++)
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
					lstPlayerHand.removeAll(lstPlayerHand);
					if (intClub > 4)
					{
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
						break;
					}					
				}
				
				if(lstPlayerHand.size() == 3)
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
				}
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
		
		
/*************************************Test Display****************************/		
//		for (int i = 0; i < lstPlayer2.size(); i++)
//		{
//			iView = new ImageButton(this);
//			iView.setImageResource(lstPlayer2.get(i).pic_rsc);
//			iView.setPadding(3, 3, 3, 3);
//			ll.addView(iView);
//		}
//
//		txtCards = (TextView) findViewById(R.id.txtCards);		
//		
//		txtCards.setText(String.valueOf(lstPlayer2.size()) + ", " + 
//		String.valueOf(intHandRank.get(1)));		
		
		checkWinner();
	}
	
	public void checkWinner()
	{
		ll = (LinearLayout)findViewById(R.id.linCards);
		txtCards = (TextView) findViewById(R.id.txtCards);
		ArrayList<Integer> intIndexforRanks = new ArrayList<Integer>();
		intIndexforRanks.add(-1);
		int Ranks = -1;
		String strPlayers = "";
		
		//Loops to check if the ranks to see which is highest
		//If new rank is higher, it sets Ranks for new high rank and keeps the
		//index of that player's hand.
		for (int i = 0; i < intHandRank.size(); i++)
		{
			if(intHandRank.get(i) > Ranks)
			{
				intIndexforRanks.removeAll(intIndexforRanks);
				intIndexforRanks.add(i);
				Ranks = intHandRank.get(i);
			}
			else if(intHandRank.get(i) == Ranks)
			{				
				intIndexforRanks.add(i);
			}
			
		
		}
		
		//Checks the kickers. Removes the indexes from the list if they are lower than Index 0
		if(intIndexforRanks.size() > 1)
		{
			int k = 1;
			Boolean bolTest = true;
			while (bolTest)
			{
				//Loops through index 0 and k to compare hands
				for (int j = 0; j < 5; j++)
				{
					if (lstAllHands.get(intIndexforRanks.get(0)).get(j).Rank < lstAllHands.get(intIndexforRanks.get(k)).get(j).Rank)
					{
						intIndexforRanks.set(0, intIndexforRanks.get(k));
						intIndexforRanks.remove(k);						
						break;
					}
					else if (lstAllHands.get(intIndexforRanks.get(0)).get(j).Rank > lstAllHands.get(intIndexforRanks.get(k)).get(j).Rank)
					{
						intIndexforRanks.remove(k);
						break;
					}	
					
				}
				
				//if hands 0 and k are the same this increments k to check the next hand
				if (intIndexforRanks.size() > 1)
				{
					if(lstAllHands.get(intIndexforRanks.get(0)).get(0).Rank == lstAllHands.get(intIndexforRanks.get(k)).get(0).Rank &&	
						lstAllHands.get(intIndexforRanks.get(0)).get(1).Rank == lstAllHands.get(intIndexforRanks.get(k)).get(1).Rank &&
						lstAllHands.get(intIndexforRanks.get(0)).get(2).Rank == lstAllHands.get(intIndexforRanks.get(k)).get(2).Rank &&
						lstAllHands.get(intIndexforRanks.get(0)).get(3).Rank == lstAllHands.get(intIndexforRanks.get(k)).get(3).Rank &&
						lstAllHands.get(intIndexforRanks.get(0)).get(4).Rank == lstAllHands.get(intIndexforRanks.get(k)).get(4).Rank)
					{
						k++;
					}
				}
				
				//Ends loop when k reaches number of hands it has to compare or all but 1 hand is eliminated
				if(k >= intIndexforRanks.size() || intIndexforRanks.size() == 1)
				{
					bolTest = false;
				}
			}
		}
		
		
		//Displays the winner
		if(intIndexforRanks.size() == 1)
		{
			for (int i = 0; i < lstAllHands.get(intIndexforRanks.get(0)).size(); i++)
			{
				iView = new ImageButton(this);
				iView.setImageResource(lstAllHands.get(intIndexforRanks.get(0)).get(i).pic_rsc);
				iView.setPadding(3, 3, 3, 3);
				ll.addView(iView);
			}					
			
			txtCards.setText(String.valueOf(strPlayer[intIndexforRanks.get(0)]) + " wins! "
					+ strHands[intHandRank.get(intIndexforRanks.get(0))]);
		}
		
	/***********************************Check Ties*****************************************/			
		//If everyone ties
		else if (intIndexforRanks.size() == lstAllHands.size() && lstAllHands.size() > 2)
		{
			txtCards.setText("Everyone ties! " + strHands[intHandRank.get(intIndexforRanks.get(0))]);
			
			for (int i = 0; i < lstAllHands.get(intIndexforRanks.get(0)).size(); i++)
			{
				iView = new ImageButton(this);
				iView.setImageResource(lstAllHands.get(intIndexforRanks.get(0)).get(i).pic_rsc);
				iView.setPadding(3, 3, 3, 3);
				ll.addView(iView);
			}
		}
		
		//Displays string and cards if two people tie
		else if(intIndexforRanks.size() == 2)
		{
			strPlayers = strPlayer[intIndexforRanks.get(0)] + " and " + strPlayer[intIndexforRanks.get(1)];
	
		
			txtCards.setText(strPlayers + " tie! " + strHands[intHandRank.get(intIndexforRanks.get(0))]);
		
			for (int i = 0; i < lstAllHands.get(intIndexforRanks.get(0)).size(); i++)
			{
					iView = new ImageButton(this);
					iView.setImageResource(lstAllHands.get(intIndexforRanks.get(0)).get(i).pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					ll.addView(iView);
			}
		}
		
		//Sets the string and cards images for when there's a tie of more than two players	
		else if (intIndexforRanks.size() > 2 && intIndexforRanks.size() != lstAllHands.size())
		{
				
			for (int i = 0; i < intIndexforRanks.size(); i++)
			{
				if(i != intIndexforRanks.size() - 1)
				{
					strPlayers += strPlayer[intIndexforRanks.get(i)] + ", ";
				}
				
				else if(i == intIndexforRanks.size() - 1)
				{
					strPlayers += "and " + strPlayer[intIndexforRanks.get(i)];
				}					
			}
				
			txtCards.setText(strPlayers + " tie! " + strHands[intHandRank.get(intIndexforRanks.get(0))]);
			
			for (int i = 0; i < lstAllHands.get(intIndexforRanks.get(0)).size(); i++)
			{
					iView = new ImageButton(this);
					iView.setImageResource(lstAllHands.get(intIndexforRanks.get(0)).get(i).pic_rsc);
					iView.setPadding(3, 3, 3, 3);
					ll.addView(iView);
			}
		}
		
		bolWinner = true;
		bolGame = true;
		//txtCards.setText(String.valueOf(lstAllHands.get(intIndexforRanks.get(0)).size()));
		
	}
	
public void reset()
{
//	Intent intent = getIntent();
//	startActivity(intent);
	
	lstTable.clear();
	lstPlayer1.clear();
	lstPlayer2.clear();
	lstPlayer3.clear();
	lstPlayer4.clear();
	lstPlayer5.clear();
	lstPlayer6.clear();
	lstPlayer7.clear();
	lstPlayer8.clear();
	lstPlayer9.clear();
	lstAllHands.clear();
	lstHold.clear();
	bolGame = true;

	bolFlush = false;
	txtCards.setText(R.string.pick_flop_cards);
	intPlayer = -1;
	intPlayerRankHold = 0;
	intHandRank.clear();
	
	btnCompare = (Button) findViewById(R.id.btnCompare2);
	btnCompare.setVisibility(View.INVISIBLE);
	
	if (bolWinner)
	{
		ll.removeAllViews();
		//Loads the deck of cards
		for (int i = 0; i < 52; i++)
		{
			cards[i] = new Card(i);
			iView = new ImageButton(this);
			iView.setImageResource(cards[i].pic_rsc);
			iView.setPadding(4, 4, 4, 4);
			iView.setOnClickListener(addCard(iView));
			ll.addView(iView);
		}
	}
	else
	{
		for (int i = 0; i < 52; i++)
		{
			iView = (ImageButton) ll.getChildAt(i);
			iView.setColorFilter(null);
			iView.setEnabled(true);
			iView.setClickable(true);
			iView.setOnClickListener(addCard(iView));
		}
	}
		
	iView = (ImageButton) findViewById(R.id.ibTableCard1);
	iView.setImageResource(intBackCard);
	iView.setImageAlpha(127);
	iView.setClickable(false);
	iView.setLongClickable(false);
	iView = (ImageButton) findViewById(R.id.ibTableCard2);
	iView.setImageResource(intBackCard);
	iView.setImageAlpha(127);
	iView.setClickable(false);
	iView.setLongClickable(false);
	iView = (ImageButton) findViewById(R.id.ibTableCard3);
	iView.setImageResource(intBackCard);
	iView.setImageAlpha(127);
	iView.setClickable(false);
	iView.setLongClickable(false);
	iView = (ImageButton) findViewById(R.id.ibTableCard4);
	iView.setImageResource(intBackCard);
	iView.setImageAlpha(127);
	iView.setClickable(false);
	iView.setLongClickable(false);
	iView = (ImageButton) findViewById(R.id.ibTableCard5);
	iView.setImageResource(intBackCard);
	iView.setImageAlpha(127);
	iView.setClickable(false);
	iView.setLongClickable(false);
	
	iView = (ImageButton) findViewById(R.id.ibPlayer1Card1);
	iView.setImageResource(intBackCard);
	iView.setImageAlpha(127);
	iView.setClickable(false);
	iView.setLongClickable(false);
	iView = (ImageButton) findViewById(R.id.ibPlayer1Card2);
	iView.setImageResource(intBackCard);
	iView.setImageAlpha(127);
	iView.setClickable(false);
	iView.setLongClickable(false);
	
	iView = (ImageButton) findViewById(R.id.ibPlayer2Card1);
	iView.setImageResource(intBackCard);
	iView.setImageAlpha(127);
	iView.setClickable(false);
	iView.setLongClickable(false);
	iView = (ImageButton) findViewById(R.id.ibPlayer2Card2);
	iView.setImageResource(intBackCard);
	iView.setImageAlpha(127);
	iView.setClickable(false);
	iView.setLongClickable(false);

	if (intTotalPlayers == 3)
	{
		iView = (ImageButton) findViewById(R.id.ibPlayer3Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer3Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
	}
	
	else if (intTotalPlayers == 4)
	{
		iView = (ImageButton) findViewById(R.id.ibPlayer3Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer3Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		
		iView = (ImageButton) findViewById(R.id.ibPlayer4Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer4Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
	}
	
	else if (intTotalPlayers == 5)
	{
		iView = (ImageButton) findViewById(R.id.ibPlayer3Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer3Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		
		iView = (ImageButton) findViewById(R.id.ibPlayer4Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer4Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		
		iView = (ImageButton) findViewById(R.id.ibPlayer5Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer5Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
	}
	
	else if (intTotalPlayers == 6)
	{
		iView = (ImageButton) findViewById(R.id.ibPlayer3Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer3Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		
		iView = (ImageButton) findViewById(R.id.ibPlayer4Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer4Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		
		iView = (ImageButton) findViewById(R.id.ibPlayer5Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer5Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		
		iView = (ImageButton) findViewById(R.id.ibPlayer6Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer6Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
	}
	
	else if (intTotalPlayers == 7)
	{
		iView = (ImageButton) findViewById(R.id.ibPlayer3Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer3Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		
		iView = (ImageButton) findViewById(R.id.ibPlayer4Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer4Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		
		iView = (ImageButton) findViewById(R.id.ibPlayer5Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer5Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		
		iView = (ImageButton) findViewById(R.id.ibPlayer6Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer6Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		
		iView = (ImageButton) findViewById(R.id.ibPlayer7Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer7Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
	}
	
	else if (intTotalPlayers == 8)
	{
		iView = (ImageButton) findViewById(R.id.ibPlayer3Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer3Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		
		iView = (ImageButton) findViewById(R.id.ibPlayer4Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer4Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		
		iView = (ImageButton) findViewById(R.id.ibPlayer5Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer5Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		
		iView = (ImageButton) findViewById(R.id.ibPlayer6Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer6Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		
		iView = (ImageButton) findViewById(R.id.ibPlayer7Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer7Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		
		iView = (ImageButton) findViewById(R.id.ibPlayer8Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer8Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
	}
	
	else if (intTotalPlayers == 9)
	{
		iView = (ImageButton) findViewById(R.id.ibPlayer3Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer3Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		
		iView = (ImageButton) findViewById(R.id.ibPlayer4Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer4Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		
		iView = (ImageButton) findViewById(R.id.ibPlayer5Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer5Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		
		iView = (ImageButton) findViewById(R.id.ibPlayer6Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer6Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		
		iView = (ImageButton) findViewById(R.id.ibPlayer7Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer7Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		
		
		iView = (ImageButton) findViewById(R.id.ibPlayer8Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer8Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		
		iView = (ImageButton) findViewById(R.id.ibPlayer9Card1);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
		iView = (ImageButton) findViewById(R.id.ibPlayer9Card2);
		iView.setImageResource(intBackCard);
		iView.setImageAlpha(127);
		iView.setClickable(false);
		iView.setLongClickable(false);
	}
		
	bolWinner = false;
	
	sv.scrollTo(0, txtTable.getTop());
}

	public void helpDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle(R.string.compare_help).setMessage(R.string.compare_dialog)
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
	
	public void checkWinButton()
	{
		btnCompare2 = (Button) findViewById(R.id.btnCompare2);
		btnCompare2.setVisibility(View.VISIBLE);		
		
		sv.scrollTo(0, btnCompare2.getBottom());
	}

}
