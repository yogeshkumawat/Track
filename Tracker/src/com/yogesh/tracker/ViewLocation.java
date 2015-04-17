package com.yogesh.tracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


public class ViewLocation extends Activity{

	private GoogleMap googleMap;
	private LatLng myLoc = new LatLng(29, 79);
	private String mBoyName;
	private LatLng yourLoc = new LatLng(30,80);
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.mapview);
		
		Intent intent = getIntent();
		
		try { 
            if (googleMap == null) {
               googleMap = ((MapFragment) getFragmentManager().
               findFragmentById(R.id.mapview)).getMap();
            }
         googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

         if(intent.getIntExtra("which", -1)==0) {
        	 mBoyName = intent.getStringExtra("name");
        	 double latitude = intent.getDoubleExtra("GeoLatitude", -1);
        	 double longitude = intent.getDoubleExtra("GeoLongitude", -1);
        	 Log.v("yogesh", "Map Point: "+latitude/1000000+" : "+longitude/1000000);
        	 myLoc = new LatLng(latitude/1000000, longitude/1000000);
        	 googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 12.0f));
        	 MarkerOptions options1 = new MarkerOptions();
        	 options1.position(myLoc).title(mBoyName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        	 googleMap.addMarker(options1);
         }
         else if(intent.getIntExtra("which", -1)==1) {
        	 MarkerOptions options1 = new MarkerOptions();
        	 MarkerOptions options2 = new MarkerOptions();
        	 double latArray[] = intent.getDoubleArrayExtra("LocationArray");
        	 myLoc = new LatLng(latArray[0]/1000000, latArray[1]/1000000);
        	 yourLoc = new LatLng(latArray[2]/1000000, latArray[3]/1000000);
        	 googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 10.0f));
        	 options1.position(myLoc).title("Origin").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        	 options2.position(yourLoc).title("Destination").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        	 
        	 googleMap.addMarker(options1);
        	 googleMap.addMarker(options2);
        	 String Url = getDirectionsUrl(myLoc, yourLoc);
        	 Log.v("yogesh", "Url : "+Url);
        	 DownloadTask mDownloadTask = new DownloadTask();
        	 mDownloadTask.execute(Url);
        	 
         }

      } catch (Exception e) {
         e.printStackTrace();
      }
	}
	
	private String getDirectionsUrl(LatLng origin,LatLng dest){
        
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        
        String str_dest = "destination="+dest.latitude+","+dest.longitude;        
        
        String sensor = "sensor=false";            
                    
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
                    
        String output = "json";
        
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        
        return url;
    }
	
	private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
                URL url = new URL(strUrl);

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.connect();

                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb  = new StringBuffer();

                String line = "";
                while( ( line = br.readLine())  != null){
                        sb.append(line);
                }
                
                data = sb.toString();

                br.close();

        }catch(Exception e){
                Log.d("Exception while downloading url", e.toString());
        }finally{
                iStream.close();
                urlConnection.disconnect();
        }
        return data;
     }
	 private class DownloadTask extends AsyncTask<String, Void, String>{            
         
	        // Downloading data in non-ui thread
	        @Override
	        protected String doInBackground(String... url) {
	                
	            // For storing data from web service
	            String data = "";
	                    
	            try{
	                // Fetching the data from web service
	                data = downloadUrl(url[0]);
	                Log.v("yogesh", "data: "+data);
	            }catch(Exception e){
	                Log.d("Background Task",e.toString());
	            }
	            return data;        
	        }
	        
	        // Executes in UI thread, after the execution of
	        // doInBackground()
	        @Override
	        protected void onPostExecute(String result) {            
	            super.onPostExecute(result);            
	            
	            ParserTask parserTask = new ParserTask();
	            
	            // Invokes the thread for parsing the JSON data
	            parserTask.execute(result);
	                
	        }        
	    }
	 
	 private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
	        
	        // Parsing the data in non-ui thread        
	        @Override
	        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
	            
	            JSONObject jObject;    
	            List<List<HashMap<String, String>>> routes = null;                       
	            
	            try{
	                jObject = new JSONObject(jsonData[0]);
	                DirectionsJSONParser parser = new DirectionsJSONParser();
	                
	                // Starts parsing data
	                routes = parser.parse(jObject);    
	            }catch(Exception e){
	                e.printStackTrace();
	            }
	            return routes;
	        }
	        
	        // Executes in UI thread, after the parsing process
	        @Override
	        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
	            ArrayList<LatLng> points = null;
	            PolylineOptions lineOptions = null;
	            MarkerOptions markerOptions = new MarkerOptions();
	            
	            // Traversing through all the routes
	            for(int i=0;i<result.size();i++){
	                points = new ArrayList<LatLng>();
	                lineOptions = new PolylineOptions();
	                
	                // Fetching i-th route
	                List<HashMap<String, String>> path = result.get(i);
	                
	                // Fetching all the points in i-th route
	                for(int j=0;j<path.size();j++){
	                    HashMap<String,String> point = path.get(j);                    
	                    
	                    double lat = Double.parseDouble(point.get("lat"));
	                    double lng = Double.parseDouble(point.get("lng"));
	                    LatLng position = new LatLng(lat, lng);    
	                    
	                    points.add(position);                        
	                }
	                
	                // Adding all the points in the route to LineOptions
	                lineOptions.addAll(points);
	                lineOptions.width(4);
	                lineOptions.color(Color.RED);    
	                
	            }
	            
	            // Drawing polyline in the Google Map for the i-th route
	            Log.v("yogesh", "Route draw");
	            googleMap.addPolyline(lineOptions);                            
	        }            
	    }   

}
