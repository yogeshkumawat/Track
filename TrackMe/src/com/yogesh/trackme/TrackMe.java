package com.yogesh.trackme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class TrackMe extends Activity {

	
	private Button mStartTrackButton, mStopTrackingButton, okButton, mEnterButton, mExitButton;
	private Context mContext;
	private Intent intent;
	private EditText mNameEdit;
	private SharedPreferences sharedpreferences;
	public static final String MyPREFERENCES = "Tracker";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_me);
		mContext = this;
		sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		mStartTrackButton = (Button) findViewById(R.id.startTracking);
		mStopTrackingButton = (Button) findViewById(R.id.stopTracking);
		okButton = (Button) findViewById(R.id.okButton);
		mNameEdit = (EditText) findViewById(R.id.boyName);
		mEnterButton = (Button) findViewById(R.id.enter);
		mExitButton = (Button) findViewById(R.id.exit);
		
		okButton.setOnClickListener(clickListener);
		mStartTrackButton.setOnClickListener(clickListener);
		mStopTrackingButton.setOnClickListener(clickListener);
		mEnterButton.setOnClickListener(clickListener);
		mExitButton.setOnClickListener(clickListener);
		
		if(sharedpreferences.contains("name")) {
			Log.v("yogesh", sharedpreferences.getString("name", "No name"));
			okButton.setVisibility(View.INVISIBLE);
			mNameEdit.setVisibility(View.INVISIBLE);
			mEnterButton.setVisibility(View.VISIBLE);
			mExitButton.setVisibility(View.VISIBLE);
			TrackService.BOY_NAME = sharedpreferences.getString("name", "");
		}
		else {
			mStartTrackButton.setVisibility(View.INVISIBLE);
			mStopTrackingButton.setVisibility(View.INVISIBLE);
			mEnterButton.setVisibility(View.GONE);
			mExitButton.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.track_me, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		return super.onOptionsItemSelected(item);
	}
	
	private OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(arg0.getId() == R.id.startTracking){
				intent = new Intent(mContext, TrackService.class);
				startService(intent);
			}
			else if(arg0.getId() == R.id.stopTracking) {
				if(intent != null)
				    stopService(intent);
			}
			else if(arg0.getId() == R.id.okButton) {
				showConfirmDialog();
			}
			else if(arg0.getId() == R.id.enter) {
				Intent i =  new Intent();
				i.setAction("com.yogesh.enter");
				sendBroadcast(i);
			}
			else if(arg0.getId() == R.id.exit) {
				Intent i =  new Intent();
				i.setAction("com.yogesh.exit");
				sendBroadcast(i);
			}
		}
	};
	
	private void showConfirmDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
		         alertDialogBuilder.setTitle(this.getTitle()+ " decision");
		         alertDialogBuilder.setMessage("Are you sure?");
		         // set positive button: Yes message
		         alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog,int id) {
		                    // go to a new activity of the app
		                    String mBoyName = mNameEdit.getText().toString();
		                    Editor editor = sharedpreferences.edit();
		                    editor.putString("name", mBoyName);
		                    editor.commit();
		                    
		                    TrackService.BOY_NAME = mBoyName;
		                    okButton.setVisibility(View.INVISIBLE);
		                    mNameEdit.setVisibility(View.INVISIBLE);
		                    mStartTrackButton.setVisibility(View.VISIBLE);
		                    mStopTrackingButton.setVisibility(View.VISIBLE);
		                    mEnterButton.setVisibility(View.VISIBLE);
		        			mExitButton.setVisibility(View.VISIBLE);
		                }
		              });
		         // set negative button: No message
		         alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog,int id) {
		                    // cancel the alert box and put a Toast to the user
		                    dialog.cancel();
		                    
		                }
		            });
		         
		         AlertDialog alertDialog = alertDialogBuilder.create();
		         alertDialog.show();


	}
}
