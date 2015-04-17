package com.yogesh.tracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class TrackerActivity extends Activity {

	private ListView mList;
	private ListAdapter mListAdapter;
	private List<Boy> mValues;
	private Context mContext;
	private Button showListButton;
	String mValueResult = null;
	private ProgressDialog pDialog;
	private boolean proceed = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tracker);
		mContext = this;
		mList = (ListView) findViewById(R.id.listView);
		showListButton = (Button) findViewById(R.id.showListButton);
		showListButton.setOnClickListener(showClick);
		
		mList.setOnItemClickListener(itemClick);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tracker, menu);
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
	
	private void showList() {
		mList.setAdapter(mListAdapter);
	}
	
	private List<Boy> getValues(String values) {
		List<Boy> tempValues = new ArrayList<Boy>();
		mValueResult = values;
		
		Log.v("yogesh", "Here is list values: "+mValueResult);
		if(mValueResult != null && !mValueResult.equals("")) {
			String [] temp1 = mValueResult.split("\n");
			for(int i=0;i<temp1.length;i++) {
				String[] temp2 = temp1[i].split("\"\"");
				Boy mBoy = new Boy();
				mBoy.name = temp2[0].replace("\"", "");
				
				int latitude = Integer.parseInt(temp2[1]);
				if(temp2[2].contains("\""))
					temp2[2] = temp2[2].replace("\"", "");
				if(temp2[3].contains("\""))
					temp2[3] = temp2[3].replace("\"", "");
				long duration = Long.parseLong(temp2[3]);
				int longitude = Integer.parseInt(temp2[2]);
				
				//mBoy.mGeoPoint = new GeoPoint(latitude, longitude);
				mBoy.mLatitude = latitude;
				mBoy.mLongitude = longitude;
				mBoy.duration = duration;
				tempValues.add(mBoy);
			}
		}
		
		return tempValues;
	}
	
	
	private OnClickListener showClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.getId() == R.id.showListButton) {
				new GetValue().execute();
			    /*showListButton.setVisibility(View.GONE);
			    mList.setVisibility(View.VISIBLE);
			    //mValues = getValues();
			    mListAdapter = new ListAdapter(mContext, mValues);
			    showList();*/
			}
			
		}
	};
	
	public void onBackPressed() {
		if(mList.getVisibility() == View.VISIBLE) {
			showListButton.setVisibility(View.VISIBLE);
			mList.setVisibility(View.GONE);
		}
		else
			super.onBackPressed();
			
	};
	
	private OnItemClickListener itemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			showConfirmDialog(arg1);
			
		}
	};
	
	private void showConfirmDialog(View view) {
		final View arg1 = view;
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
		        // alertDialogBuilder.setTitle(this.getTitle()+ " decision");
		         // set positive button: Yes message
		         CharSequence[] items = {"View on Map","View Route","View check in-out time"};
		         alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {

		        	@Override
		        	public void onClick(DialogInterface dialog, int which) {
		        		if(which==0) {
		        			Intent intent = new Intent(mContext, ViewLocation.class);
		        			Boy mBoy = (Boy) arg1.getTag();
		        			intent.putExtra("which", 0);
		        			intent.putExtra("name", mBoy.name);
		        			intent.putExtra("GeoLatitude", mBoy.mLatitude);
		        			intent.putExtra("GeoLongitude", mBoy.mLongitude);
		        			startActivity(intent);
		        		}
		        		else if(which==1) {
		        			Intent intent = new Intent(mContext, ViewLocation.class);
		        			Boy mBoy = (Boy) arg1.getTag();
		        			intent.putExtra("which", 1);
		        			double latArray[] = {mBoy.mLatitude,mBoy.mLongitude,28580000,77360000};
		        			intent.putExtra("LocationArray", latArray);
		        			startActivity(intent);
		        		}
		        		else if(which==2) {
		        			Boy mBoy = (Boy) arg1.getTag();
		        			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
		   		         alertDialogBuilder.setTitle("Time Duration");
		   		         alertDialogBuilder.setMessage("Time spent by "+mBoy.name+" at Agent's location is: "+mBoy.duration/1000+" seconds");
		   		         // set positive button: Yes message
		   		         alertDialogBuilder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
		   		                public void onClick(DialogInterface dialog,int id) {
		   		                    // go to a new activity of the app
		   		                   dialog.dismiss();
		   		                }
		   		              });
		   		      AlertDialog alertDialog = alertDialogBuilder.create();
				         alertDialog.show();
		        		}
		        		
		        	}
		        	
		        	});
		         
		         AlertDialog alertDialog = alertDialogBuilder.create();
		         alertDialog.show();


	}
	
	 class GetValue extends AsyncTask<Void, String, String> {

		 @Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(mContext);
			pDialog.setTitle("Loading");
			pDialog.setMessage("Connecting to server...");
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						pDialog.show();
					}
				});
		}
		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String qResult = "";
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpGet httpGet = new HttpGet("http://192.168.42.138/demo.php");

			try {
			HttpResponse response = httpClient.execute(httpGet,
			localContext);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
			InputStream inputStream = entity.getContent();
			Reader in = new InputStreamReader(inputStream);
			BufferedReader bufferedreader = new BufferedReader(in);
			StringBuilder stringBuilder = new StringBuilder();
			String stringReadLine = null;
			while ((stringReadLine = bufferedreader.readLine()) != null) {
			stringBuilder.append(stringReadLine + "\n");
			}
			qResult = stringBuilder.toString();
			}

			} catch (ClientProtocolException e) {
			e.printStackTrace();
			
			} catch (IOException e) {
				final Exception e1 = e;
			e.printStackTrace();
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(mContext, e1.getMessage(), Toast.LENGTH_SHORT).show();
				}
			});
			Log.v("yogesh", "Error: "+e.getMessage());
			if(e.getMessage().contains("Unable to resolve host")) {
				return null;
			}
			
			}
			
			return qResult;
		}
		
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result != null && !result.equals("")) {
				showListButton.setVisibility(View.GONE);
			    mList.setVisibility(View.VISIBLE);
				mValues = getValues(result);
			    mListAdapter = new ListAdapter(mContext, mValues);
			    showList();
			}
			if(pDialog !=null)
				pDialog.dismiss();
		}
		 
	 }
}
