package com.yogesh.trackme;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.gesture.GestureOverlayView;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class TrackService extends Service {

	private Boy mboy;
	private LocationManager mLocationManager;
	private double myLocationLatitude, myLocationLongitude;
	private boolean gps_enabled,network_enabled;
	private Timer timer;
	private Context mContext;
	public static String BOY_NAME = "";
	private InputStream is = null;
	private String line = null;
	private String result=null;
	private int code;
	private SharedPreferences sharedpreferences;
	public static final String MyPREFERENCES = "Tracker";
	public static boolean mIsFirst = true;
	private long myTime = 0, actualTime = 0;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.v("yogesh", "service start");
		mContext = this;
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.yogesh.enter");
		filter.addAction("com.yogesh.exit");
		registerReceiver(myReciever, filter);
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		
        getLocation();
        
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.v("yogesh", "service destroy");
		unregisterReceiver(myReciever);
		super.onDestroy();
	}
	
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private void insertValues() {
		InsertTask mInsertTask = new InsertTask();
		mInsertTask.execute();
	}
	
	
	private void getLocation() {
		
		gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!gps_enabled && !network_enabled) {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, "nothing is enabled", duration);
            toast.show();
        }
        
        if (gps_enabled)
        	mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                    locationListenerGps);
        if (network_enabled)
        	mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                    locationListenerNetwork);
         timer=new Timer();
         timer.schedule(new GetLastLocation(), 20000);
         if(sharedpreferences.getBoolean("needToInsert", true) && (gps_enabled || network_enabled)) {
             new Handler().postDelayed(new Runnable() {
 				
 				@Override
 				public void run() {
 					// TODO Auto-generated method stub
 					insertValues();
 				}
 			}, 4000);
         }
         else {
        	 if (!gps_enabled && !network_enabled) {
                 Context context = getApplicationContext();
                 int duration = Toast.LENGTH_SHORT;
                 Toast toast = Toast.makeText(context, "Please on GPS or Network", duration);
                 toast.show();
             }
        	else
         	    Toast.makeText(mContext, "You have already added, we have eye on you", Toast.LENGTH_SHORT).show();
         }
	}
	
	LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer.cancel();
            Log.v("yogesh", "129");
            myLocationLatitude =location.getLatitude();
            myLocationLongitude = location.getLongitude();
            mLocationManager.removeUpdates(this);
            mLocationManager.removeUpdates(locationListenerNetwork);

            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, "gps enabled "+myLocationLatitude + "\n" + myLocationLongitude, duration);
            toast.show();
        }

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}
	};
        
	BroadcastReceiver myReciever = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if(arg1.getAction().equals("com.yogesh.enter")) {
				myTime = System.currentTimeMillis();
			}
			else if(arg1.getAction().equals("com.yogesh.exit")) {
				actualTime = System.currentTimeMillis()-myTime;
				UpdateTask mUpdateTask = new UpdateTask();
				mUpdateTask.execute();
			}
			
		}
	};
        LocationListener locationListenerNetwork = new LocationListener() {
            public void onLocationChanged(Location location) {
                timer.cancel();
                Log.v("yogesh", "163");
                myLocationLatitude = location.getLatitude();
                myLocationLongitude = location.getLongitude();
                mLocationManager.removeUpdates(this);
                mLocationManager.removeUpdates(locationListenerGps);
                Editor editor = sharedpreferences.edit();
                if(sharedpreferences.contains("points")) {
                	boolean isSame = false;
                	StringBuilder sb = new StringBuilder();
                    String points = sharedpreferences.getString("points", "");
                    if(points != null && !points.equals("")) {
                    	sb.append(points);
                    	ArrayList<LatLong> mLatLong = new ArrayList<LatLong>();
                    	String [] tempArray = points.split(",");
                    	for(int i=0;i<(tempArray.length);i=i+2) {
                    		LatLong mTempLatLong = new LatLong();
                    		mTempLatLong.latitude = Double.parseDouble(tempArray[i]);
                    		mTempLatLong.longitude = Double.parseDouble(tempArray[i+1]);
                    		mLatLong.add(mTempLatLong);
                    	}
                        for(int i=0;i<mLatLong.size();i++) {
                             if(mLatLong.get(i).latitude == myLocationLatitude*1000000 && mLatLong.get(i).longitude == myLocationLongitude*1000000) {
                                 isSame = true;
                                 break;
                             }
                        }
                        if(!isSame) {
                            sb.append(","+myLocationLatitude*1000000+","+myLocationLongitude*1000000);
                            editor.putString("points", sb.toString());
                            new UpdatePoint().execute();
                        }
                    }
                    
                }
                else {
                    editor.putString("points", ""+myLocationLatitude*1000000+","+myLocationLongitude*1000000);
                    
                }
                editor.commit();
                
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "network enabled: "+myLocationLatitude + "\n" + myLocationLongitude, duration);
                toast.show();
            }

			@Override
			public void onProviderDisabled(String arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// TODO Auto-generated method stub
				
			}
        };
        
	
	class GetLastLocation extends TimerTask {
        @Override
        public void run() {
        	mLocationManager.removeUpdates(locationListenerGps);
        	mLocationManager.removeUpdates(locationListenerNetwork);

             Location net_loc=null, gps_loc=null;
             if(gps_enabled)
                 gps_loc=mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
             if(network_enabled)
                 net_loc=mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

             //if there are both values use the latest one
             if(gps_loc!=null && net_loc!=null){
                 if(gps_loc.getTime()>net_loc.getTime())
                {
                	 Log.v("yogesh", "211");
                    myLocationLatitude = gps_loc.getLatitude();
                    myLocationLongitude = gps_loc.getLongitude();
                    final Context context = getApplicationContext();
                    Handler handler = new Handler(Looper.getMainLooper());

                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(context,"239: "+myLocationLatitude + "\n" + myLocationLongitude,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                 else
                {
                	 Log.v("yogesh", "217");
                	 myLocationLatitude = net_loc.getLatitude();
                	 myLocationLongitude = net_loc.getLongitude();
                	 final Context context = getApplicationContext();
                     Handler handler = new Handler(Looper.getMainLooper());

                     handler.post(new Runnable() {

                         @Override
                         public void run() {
                             Toast.makeText(context,"255: "+myLocationLatitude + "\n" + myLocationLongitude,Toast.LENGTH_SHORT).show();
                         }
                     });
                }

             }

             if(gps_loc!=null){
                  {
                	  Log.v("yogesh", "226");
                	  myLocationLatitude = gps_loc.getLatitude();
                	  myLocationLongitude = gps_loc.getLongitude();
                	  final Context context = getApplicationContext();
                      Handler handler = new Handler(Looper.getMainLooper());

                      handler.post(new Runnable() {

                          @Override
                          public void run() {
                              Toast.makeText(context,"274: "+myLocationLatitude + "\n" + myLocationLongitude,Toast.LENGTH_SHORT).show();
                          }
                      });
                  }

             }
             if(net_loc!=null){
                {
                	Log.v("yogesh", "234");
                	myLocationLatitude = net_loc.getLatitude();
                	myLocationLongitude = net_loc.getLongitude();
                	 final Context context = getApplicationContext();
                     Handler handler = new Handler(Looper.getMainLooper());

                     handler.post(new Runnable() {

                         @Override
                         public void run() {
                             Toast.makeText(context,"292: "+myLocationLatitude + "\n" + myLocationLongitude,Toast.LENGTH_SHORT).show();
                         }
                     });

                }
             }
            final Context context = getApplicationContext();
            Handler handler = new Handler(Looper.getMainLooper());

            handler.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context,"no last know avilable",Toast.LENGTH_SHORT).show();
                }
            });

        }
	}
	
	
	private class InsertTask extends AsyncTask<Void, String, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
	    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	    	 
	       	nameValuePairs.add(new BasicNameValuePair("name",BOY_NAME));
	       	double mylatitude =  (myLocationLatitude * 1000000);
	       	double mylongitude = (myLocationLongitude * 1000000);
	       	Log.v("yogesh", "My add is :"+mylatitude+" : "+mylongitude);
	       	nameValuePairs.add(new BasicNameValuePair("latitude", ""+mylatitude));
	       	nameValuePairs.add(new BasicNameValuePair("longitude", ""+mylongitude));
			HttpContext localContext = new BasicHttpContext();

			try {
				HttpPost httppost = new HttpPost("http://192.168.42.138/insert.php");
				HttpClient httpclient = new DefaultHttpClient();
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
				Log.e("pass 1", "connection success ");
			}
			catch(Exception e)
			{
		        	Log.e("Fail 1", e.toString());
		        	Log.v("yogesh", "Invalid IP Address");
			}   
			
			try {
				  BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
				  StringBuilder sb = new StringBuilder();
				  while ((line = reader.readLine()) != null) {
					  sb.append(line + "\n");
				  }
				  is.close();
				  result = sb.toString();
			      Log.e("pass 2", "connection success ");
			
			}
			catch(Exception e) {
				Log.e("Fail 2", e.toString());
			}
			try
			{
		            JSONObject json_data = new JSONObject(result);
		            code=(json_data.getInt("code"));
					
		            if(code==1)
		            {
		            	Editor editor = sharedpreferences.edit();
	                    editor.putBoolean("needToInsert", false);
	                    editor.commit();
		            	Log.v("yogesh", "InsertSuccessfully");
		            }
		            else
		            {
		            	Log.v("yogesh", "Sorry, Try Again");
		            }
			}
			catch(Exception e)
			{
		            Log.e("Fail 3", e.toString());
			}
			
			return result;
		}
		
	}
	
	private class UpdateTask extends AsyncTask<Void, String, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
	    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	    	 
	       	nameValuePairs.add(new BasicNameValuePair("name",BOY_NAME));
	    	
	       	nameValuePairs.add(new BasicNameValuePair("duration", ""+actualTime));
			HttpContext localContext = new BasicHttpContext();

			try {
				HttpPost httppost = new HttpPost("http://192.168.42.138/update.php");
				HttpClient httpclient = new DefaultHttpClient();
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
				Log.e("pass 1", "connection success ");
			}
			catch(Exception e)
			{
		        	Log.e("Fail 1", e.toString());
		        	Log.v("yogesh", "Invalid IP Address");
			}   
			
			try {
				  BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
				  StringBuilder sb = new StringBuilder();
				  while ((line = reader.readLine()) != null) {
					  sb.append(line + "\n");
				  }
				  is.close();
				  result = sb.toString();
			      Log.e("pass 2", "connection success ");
			
			}
			catch(Exception e) {
				Log.e("Fail 2", e.toString());
			}
			try
			{
		            JSONObject json_data = new JSONObject(result);
		            code=(json_data.getInt("code"));
					
		            if(code==1)
		            {
		            	
		            	Log.v("yogesh", "UpdateSuccessfully");
		            }
		            else
		            {
		            	Log.v("yogesh", "Sorry, Try Again update");
		            }
			}
			catch(Exception e)
			{
		            Log.e("Fail 3", e.toString());
			}
			
			return result;
		}
	}
		private class UpdatePoint extends AsyncTask<Void, String, String> {

			@Override
			protected String doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
		    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		    	 
		       	nameValuePairs.add(new BasicNameValuePair("name",BOY_NAME));
		       	double mylatitude =  (myLocationLatitude * 1000000);
		       	double mylongitude = (myLocationLongitude * 1000000);
		       	nameValuePairs.add(new BasicNameValuePair("latitude", ""+mylatitude));
		       	nameValuePairs.add(new BasicNameValuePair("longitude", ""+mylongitude));
		       	nameValuePairs.add(new BasicNameValuePair("points", sharedpreferences.getString("points", "")));
				HttpContext localContext = new BasicHttpContext();

				try {
					HttpPost httppost = new HttpPost("http://192.168.42.138/update2.php");
					HttpClient httpclient = new DefaultHttpClient();
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
					is = entity.getContent();
					Log.e("pass 1", "connection success ");
				}
				catch(Exception e)
				{
			        	Log.e("Fail 1", e.toString());
			        	Log.v("yogesh", "Invalid IP Address");
				}   
				
				try {
					  BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
					  StringBuilder sb = new StringBuilder();
					  while ((line = reader.readLine()) != null) {
						  sb.append(line + "\n");
					  }
					  is.close();
					  result = sb.toString();
				      Log.e("pass 2", "connection success ");
				
				}
				catch(Exception e) {
					Log.e("Fail 2", e.toString());
				}
				try
				{
			            JSONObject json_data = new JSONObject(result);
			            code=(json_data.getInt("code"));
						
			            if(code==1)
			            {
			            	
			            	Log.v("yogesh", "Update points Successfully");
			            }
			            else
			            {
			            	Log.v("yogesh", "Sorry, Try Again update");
			            }
				}
				catch(Exception e)
				{
			            Log.e("Fail 3", e.toString());
				}
				
				return result;
			}
		
	
	}
}
