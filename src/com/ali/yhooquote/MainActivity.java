package com.ali.yhooquote;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/************************************************************************
 * the MainActivity will load when the application is started
 * @author Mazahir Clipwala
 *  @since Feb 19, 2015 
 ************************************************************************/
public class MainActivity extends ActionBarActivity {
	
	private static EditText searchET;
	SharedPreferences stockPreferences;
	TableLayout resultsTL;
	
	/************************************************************************
	 * the onCreate function gets called when the Activity is loaded
	 ************************************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/* set the contents view to the activity_main.xml*/
		setContentView(R.layout.activity_main);
		
		/* get a reference/handle to the "resultsTL" and "searchET" 
		 * widget. look at the activity_main.xml for the widget definition */	
		resultsTL = (TableLayout) findViewById (R.id.resultsTL);
		searchET = (EditText)findViewById (R.id.searchET);
		
		/* get or create the local database for the application by the name "yhooQuote" in private mode */
		stockPreferences = getSharedPreferences ("yhooQuote", Context.MODE_PRIVATE);
		
		/* get the data from the database, iterate it and display it */
		String [] stocks = stockPreferences.getAll ().keySet().toArray(new String [0]);
		for (String stock: stocks) {
			insertStockToView (stock);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/***************************************************************************
	 * the searchAction event handler is called when the Search button widget is
	 * clicked. look at the activity_main.xml for the event handler definition
	 ***************************************************************************/	
	public void searchAction (View view) {
		String stock = searchET.getText ().toString ();
		
		/* insert the stock to the local database and add it to the view too*/
		insertStockToPreferences (stock);
		insertStockToView (stock);
	}
	
	/***************************************************************************
	 * the clearAllAction event handler is called when the Clear All button is
	 * clicked. look at the activity_main.xml for the event handler definition
	 ***************************************************************************/	
	public void clearAllAction (View view) {
		resultsTL.removeAllViews ();
		
		SharedPreferences.Editor prefEditor = stockPreferences.edit ();
		prefEditor.clear ();
		prefEditor.commit();
	}
	
	
	/***************************************************************************
	 * insert the stock to the local database
	 ***************************************************************************/	
	private void insertStockToPreferences (String stock) {
		SharedPreferences.Editor prefEditor = stockPreferences.edit ();
		prefEditor.putString(stock, stock);
		prefEditor.commit();
	}
	
	/***************************************************************************
	 * insert the stock to the view
	 ***************************************************************************/	
	private void insertStockToView (String stock) {
		InputMethodManager imm  = (InputMethodManager) getSystemService (Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow (searchET.getWindowToken (), 0);
        
		LayoutInflater li = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		
		View stockRowView = li.inflate (R.layout.activity_stock_row, null);
		TextView resultTV = (TextView) stockRowView.findViewById (R.id.resultTV);
		
		/* get the reference to the "Show" button and 
		 * bind the "click" event handler for the "Show" button in the activity_stock_row.xml */
		Button showBT = (Button)stockRowView.findViewById (R.id.showBT);
		showBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				/* the parameter "view" has the reference to the "Show" button
				 * get the parent of the "Show" button, which will return a reference to the "TableRow" (see activity_stock_row.xml)
				 * then run "findViewById" to find the resultTV TextView (see activity_stock_row.xml)*/
				TableRow tableRow = (TableRow) view.getParent ();
				TextView resultTV = (TextView) tableRow.findViewById (R.id.resultTV);
				String stockSymbol = resultTV.getText ().toString ();
				
				/* get the intent for the ShowStockActivity class 
				 * passing data to the ShowStockActivity class via the Intent (showStockIntent) 
				 * see the strings.xml file for the definition of "R.string.stockName" */
				Intent showStockIntent = new Intent(getApplication(), ShowStockActivity.class);

				showStockIntent.putExtra (getString (R.string.stockName), stockSymbol);
				
				startActivity (showStockIntent);
				
			}
		});
		
		resultTV.setText (stock);
		resultsTL.addView (stockRowView);
	}
}