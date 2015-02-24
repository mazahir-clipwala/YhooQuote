package com.ali.yhooquote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/************************************************************************
 * the ShowStockActivity will show detailed information about the stock
 * @author Mazahir Clipwala
 *  @since Feb 23, 2015 
 ************************************************************************/
public class ShowStockActivity extends ActionBarActivity {

	/* yahoo's url for running the REST query */
	private static String yql = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quote%20where%20symbol%20in%20(%22<<stock_symbol>>%22)&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_stock);
		
		/* get a reference to the intent of the current Activity
		 * get the extra data that was passed in from the previous Activity (MainActivity.java)
		 * replace the <<stock_symbol>> from the yql*/
		Intent intent = getIntent ();
		String stockName = intent.getStringExtra (getString (R.string.stockName));
		
		String yqlURL = yql.replace ("<<stock_symbol>>", stockName);
		
		/* call the MyAsyncTask and call the "execute" method
		 * pass in the "yqlURL" to the "execute" method 
		 * the yqlURL can be retrieved as param[0] in the "doInBackground" method of "StockAsyncTask" class*/
		new StockAsyncTask ().execute (yqlURL);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_stock, menu);
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
	
	/************************************************************************
	 * the StockAsyncTask will carry out the task to invoked the Yahoo REST 
	 * web service and display the data on this Activity 
	 * @author Mazahir Clipwala
	 *  @since Feb 23, 2015 
	 ************************************************************************/
	private class StockAsyncTask extends AsyncTask <String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			StringBuffer buffer = new StringBuffer();
			
			try {
				/* load the url and pass in the URL
				 * the value of params[0] comes while the execute method of this class is invoked*/
				URL url = new URL (params [0]);
				
				/* open the connection to the URL*/
				HttpURLConnection con = (HttpURLConnection) url.openConnection ();
				
				/* read from the input stream line by line */
				BufferedReader reader = new BufferedReader (new InputStreamReader (con.getInputStream()));
				String line = null;
				while ((line = reader.readLine()) != null) {
					buffer.append (line + "\n");
				}
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			/* the returned string can be retrieved in the "onPostExceute" method of this class */
			return buffer.toString ();
		}

		/************************************************************************
		 * the method is called after the "doInBackground" is done
		 ************************************************************************/
		@Override
		protected void onPostExecute(String result) {
			try {
				/* get the top object */
				JSONObject topObject = new JSONObject(result);
				
				/* get the "query" object from the top object */
				JSONObject queryObject = topObject.getJSONObject ("query");
				
				/* get the "results" object from the "query" object */
				JSONObject resultObject = queryObject.getJSONObject ("results");
				
				/* get the "quote" object from the "results" object */
				JSONObject quoteObject = resultObject.getJSONObject ("quote");
				
				/* get the references to the TextView on this Activity */
				TextView symbolTV = (TextView) findViewById (R.id.symbolTV);
				TextView changeTV = (TextView) findViewById (R.id.changeTV);
				TextView daysLowTV = (TextView) findViewById (R.id.daysLowTV);
				TextView daysHighTV = (TextView) findViewById (R.id.daysHighTV);
				
				/* set the text for the TextViews */
				symbolTV.setText (quoteObject.getString ("symbol"));
				changeTV.setText (quoteObject.getString ("Change"));
				daysLowTV.setText (quoteObject.getString ("DaysLow"));
				daysHighTV.setText (quoteObject.getString ("DaysHigh"));

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
