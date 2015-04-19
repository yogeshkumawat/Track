package com.yogesh.tracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
	SharedPreferences mPreferences;
	private static final String MyPREFERENCES = "trackIP";
	private static String SYSTEM_IP;
	private boolean mErrorInConnection = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tracker);
		mContext = this;
		mPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		if(!mPreferences.contains("mIsTableExist") && !mPreferences.getBoolean("mIsTableExist", false)) {
			createTable();
					
		}
		
		if(mPreferences.contains("SystemIP")) {
			SYSTEM_IP = mPreferences.getString("SystemIP", "");
		}
		else
			showIpDialog();
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
	
	private void showIpDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Enter Server IP");
        final EditText input = new EditText(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
             LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(40, 80, 80, 40);

        layout.addView(input, params);
        alertDialogBuilder.setView(layout);
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog,int id) {
                   // go to a new activity of the app
            	   String ip = input.getText().toString();
                   Editor editor = mPreferences.edit();
                   editor.putString("SystemIP", ip);
                   editor.commit();
                   SYSTEM_IP = ip;
               }
             });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog,int id) {
                   // cancel the alert box and put a Toast to the user
                   dialog.cancel();
                   
               }
           });
        
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
	
	private List<Boy> loadDummyData() {
		List<Boy> tempValues = new ArrayList<Boy>();
		Boy mBoy1 = new Boy("Yogesh",28587546,77362555,15000);
		Boy mBoy2 = new Boy("Lokesh",29000000,77362555,55000);
		Boy mBoy3 = new Boy("Rocky", 28597500, 77362555, 30000);
		Boy mBoy4 = new Boy("Ram", 28597600, 77362655, 20000);
		Boy mBoy5 = new Boy("Shyam", 28597700, 77362455, 25000);
		tempValues.add(mBoy1);
		tempValues.add(mBoy2);
		tempValues.add(mBoy3);
		tempValues.add(mBoy4);
		tempValues.add(mBoy5);
		return tempValues;
	}
	
	private void createTable() {
		new CreateTableTask().execute();
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
	private void showDummyDataLoadDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Error");
        alertDialogBuilder.setMessage("Can not connect to server. Want to load dummy data?");
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog,int id) {
            
            	showListButton.setVisibility(View.GONE);
   			    mList.setVisibility(View.VISIBLE);
   			    mValues = loadDummyData();
   			    mListAdapter = new ListAdapter(mContext, mValues);
   			    showList();
               }
             });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog,int id) {
                   // cancel the alert box and put a Toast to the user
                   dialog.cancel();
                   
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
			HttpGet httpGet = new HttpGet("http://"+SYSTEM_IP+"/demo.php");

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
			mErrorInConnection = false;
			} catch (ClientProtocolException e) {
			e.printStackTrace();
			
			} catch (IOException e) {
				final Exception e1 = e;
			e.printStackTrace();
			mErrorInConnection = true;
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
			if(mErrorInConnection)
				showDummyDataLoadDialog();
		}
		 
	 }
	 
	 class CreateTableTask extends AsyncTask<Void, String, String> {

		 @Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(mContext);
			pDialog.setTitle("Loading...");
			pDialog.setMessage("Setting up server");
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

			// TODO Auto-generated method stub
			String qResult = "";
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpGet httpGet = new HttpGet("http://"+SYSTEM_IP+"/create.php");

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
			Log.v("yogesh", "table create: "+result);
			if(result.contains("Database created successfully")) {
				Editor editor = mPreferences.edit();
				editor.putBoolean("mIsTableExist", true);
				editor.commit();
			}
			if(pDialog !=null)
				pDialog.dismiss();
		}
		 
	 }
}
