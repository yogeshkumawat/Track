package com.yogesh.tracker;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

//import com.google.android.maps.ItemizedOverlay;
//import com.google.android.maps.OverlayItem;

public class MapItemOverlays /*extends ItemizedOverlay<OverlayItem> */{

	private Context mContext;
	//private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	/*public MapItemOverlays(Drawable arg0, Context context) {
		super(boundCenterBottom(arg0));
		mContext = context;
		// TODO Auto-generated constructor stub
	}*/

	/*@Override
	protected OverlayItem createItem(int arg0) {
		// TODO Auto-generated method stub
		return mOverlays.get(arg0);
	}*/

	/*@Override
	public int size() {
		// TODO Auto-generated method stub
		return mOverlays.size();
	}

	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	@Override
	protected boolean onTap(int arg0) {
		// TODO Auto-generated method stub
		  OverlayItem item = mOverlays.get(arg0);
		  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		  dialog.setTitle(item.getTitle());
		  dialog.setMessage(item.getSnippet());
		  dialog.show();
		  return true;	}*/
}
