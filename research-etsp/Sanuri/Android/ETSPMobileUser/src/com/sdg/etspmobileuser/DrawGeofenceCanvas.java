package com.sdg.etspmobileuser;

import java.util.ArrayList;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.sdg.models.GeoFence;
import com.sdg.models.Other;
import com.sdg.util.Messages;
import com.sdg.util.Preferences;
import com.sdg.util.StoreGeoFence;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class DrawGeofenceCanvas extends Dialog{ //implements OnTouchListener{

	   private float startX;
	   private float startY;
	   private SharedPreferences sharedPreferences=null;
	   private Editor editor=null;
	   private ArrayList<LatLng> path=new ArrayList<LatLng>();
	   private Context context;
	   private GoogleMap googleMap;
	   private RelativeLayout drawLayout;
	   
	   DrawingView dv ;   
	   private Paint mPaint;
	
	public DrawGeofenceCanvas(Context context,GoogleMap googleMap) {
		super(context);
		this.context=context;
		this.googleMap=googleMap;
		sharedPreferences= context.getSharedPreferences(Preferences.MYPREFERENCES, Context.MODE_PRIVATE);
		// TODO Auto-generated constructor stub
	}

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		/*setContentView(R.layout.draw_geofence_canvas);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);//make the dialog fill screen
		drawLayout=(RelativeLayout)findViewById(R.id.drawLayout);
		drawLayout.setOnTouchListener(this);*/
	    dv = new DrawingView(context);
	    setContentView(dv);
	    mPaint = new Paint();
	    mPaint.setAntiAlias(true);
	    mPaint.setDither(true);
	    mPaint.setColor(Color.RED);
	    mPaint.setStyle(Paint.Style.STROKE);
	    mPaint.setStrokeJoin(Paint.Join.ROUND);
	    mPaint.setStrokeCap(Paint.Cap.ROUND);
	    mPaint.setStrokeWidth(5);  
	}


/*
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
        	if(sharedPreferences.getBoolean(Preferences.STARTDRAWING, false))
        	{
        		startX=event.getX();
        		startY=event.getY();
        	}
            break;
        case MotionEvent.ACTION_MOVE:
        	if(sharedPreferences.getBoolean(Preferences.STARTDRAWING, false))
        	{
	        	int currentX = (int)event.getX();
	        	int currentY = (int)event.getY();
	        	double lineLength=Math.sqrt(Math.pow((startX-currentX), 2)+Math.pow((startY-currentY), 2));
	        	if(lineLength>=5)
	        	{
	        		LatLng currentPoint = googleMap.getProjection().fromScreenLocation(new Point(currentX, currentY));
	        		path.add(currentPoint);
	        	}
        	}
        	break;
        case MotionEvent.ACTION_UP:
        	if(sharedPreferences.getBoolean(Preferences.STARTDRAWING, false)){
	        	if(startX!=event.getX() && startY!=event.getY()){//if drawing is not ended in the same started point, it is not an area
	        		path.clear();
	        		Messages.showError("ETSP", "You Must Draw an Enclosed Area!",context);
	        	}
	        	else
	        	{
	        		//save geofence
					editor=sharedPreferences.edit();
					editor.putBoolean(Preferences.STARTDRAWING, false);
					editor.commit();
	        		//clear path
	        		//show action bar and make everything to normal state
			        Toast.makeText(context, 
			        		 "Geo-fence successfully saved.", 
			                 Toast.LENGTH_LONG).show();
	        	}
        	}
            break;
        }
        return true;
	}*/
	
	
//------------------------------------------------------------------------------------------------------------------------
	public class DrawingView extends View {

	        public int width;
	        public  int height;
	        private Bitmap  mBitmap;
	        private Canvas  mCanvas;
	        private Path    mPath;
	        private Paint   mBitmapPaint;
	        Context context;
	        private Paint circlePaint;
	        private Path circlePath;
	        private boolean eraseWrong=false;
	
	        public DrawingView(Context c) {
	        super(c);
	        context=c;
	        mPath = new Path();
	        mBitmapPaint = new Paint(Paint.DITHER_FLAG);  
	        circlePaint = new Paint();
	        circlePath = new Path();
	        circlePaint.setAntiAlias(true);
	        circlePaint.setColor(Color.BLUE);
	        circlePaint.setStyle(Paint.Style.STROKE);
	        circlePaint.setStrokeJoin(Paint.Join.MITER);
	        circlePaint.setStrokeWidth(4f); 
	
	
	        }
	
	        /*@Override
	         protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	        super.onSizeChanged(w, h, oldw, oldh);
	
	        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
	        mCanvas = new Canvas(mBitmap);
	
	        }*/
	        
	        @Override
	        protected void onDraw(Canvas canvas) {
		        super.onDraw(canvas);
		        mCanvas=canvas;
		        if(eraseWrong){
		        	canvas.drawColor(Color.TRANSPARENT);
		        }
		        else{
			        //canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
			        canvas.drawPath( mPath,  mPaint);
			        canvas.drawPath( circlePath,  circlePaint);
		        }
	        }
	
	        //To enable drawing along with user's finger
	        private float mX, mY;
	        private static final float TOUCH_TOLERANCE = 4;
	
	        private void touch_start(float x, float y) {
		        mPath.reset();
		        mPath.moveTo(x, y);
		        mX = x;
		        mY = y;
	        }
	        
	        private void touch_move(float x, float y) {
		        float dx = Math.abs(x - mX);
		        float dy = Math.abs(y - mY);
		        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
		            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
		            mX = x;
		            mY = y;
		
		            circlePath.reset();
		            circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
		        }
	        }
	        
	        
	        private void touch_up() {
		        mPath.lineTo(mX, mY);
		        circlePath.reset();
		        // commit the path to our offscreen
		        //mCanvas.drawPath(mPath,  mPaint);
		        // kill this so we don't double draw
		        mPath.reset();
	        }
	
	        @Override
	        public boolean onTouchEvent(MotionEvent event) {
		        float x = event.getX();
		        float y = event.getY();
		
		        switch (event.getAction()) {
		            case MotionEvent.ACTION_DOWN:
		            	eraseWrong=false;
		            	if(sharedPreferences.getBoolean(Preferences.STARTDRAWING, false))
		            	{
		            		startX=event.getX();
		            		startY=event.getY();
		            	}
		                touch_start(x, y);
		                invalidate();
		                break;
		            case MotionEvent.ACTION_MOVE:
		            	if(sharedPreferences.getBoolean(Preferences.STARTDRAWING, false))
		            	{
		    	        	int currentX = (int)event.getX();
		    	        	int currentY = (int)event.getY();
		    	        	double lineLength=Math.sqrt(Math.pow((startX-currentX), 2)+Math.pow((startY-currentY), 2));
		    	        	if(lineLength>=5)
		    	        	{
		    	        		LatLng currentPoint = googleMap.getProjection().fromScreenLocation(new Point(currentX, currentY));
		    	        		path.add(currentPoint);
		    	        	}
		            	}
		                touch_move(x, y);
		                invalidate();
		                break;
		            case MotionEvent.ACTION_UP:
		            	if(sharedPreferences.getBoolean(Preferences.STARTDRAWING, false)){
		    	        	if(!(startX>event.getX()-10 && startX<event.getX()+10 && startY>event.getY()-10 && startY<event.getY()+10)){//if drawing is not ended in the same started point, it is not an area
		    	        		path.clear();
		    	        		mPath=new Path();
		    	        		circlePath=new Path();
		    	        		Messages.showError("ETSP", "You Must Draw an Enclosed Area!",context);
		    	        		eraseWrong=true;
		    	        	}
		    	        	else
		    	        	{
		    	        		path.add(path.size()-1, path.get(0)); //Make start and end points same so that it is enclosed
		    	        		//************************
		    	        		StringBuilder sbPath=new StringBuilder();
		    	        		for (LatLng point : path) {
		    	        			sbPath.append(point.latitude);
		    	        			sbPath.append(" ");
		    	        			sbPath.append(point.longitude);
		    	        			sbPath.append(":");
								}
		    	        		String json = sharedPreferences.getString(Preferences.CURRENTGEOFENCE, null);
		    	        		Gson gson=new Gson();
		    					GeoFence gf = gson.fromJson(json, GeoFence.class);
		    	        		//save geofence
		    	        		StoreGeoFence storeGeofence=new StoreGeoFence(context);
		    	        		
		    	        		StringBuilder sbObjects=new StringBuilder();
		    	        		for (String id : gf.getObjects()) {
		    	        			sbObjects.append(id);
		    	        			sbObjects.append(":");
								}
		    	        		
		    	        		storeGeofence.InsertValues(sbPath.toString(), gf.isInside(), sbObjects.toString());
		    	        		//************************
		    					editor=sharedPreferences.edit();
		    					editor.putBoolean(Preferences.STARTDRAWING, false);
		    					editor.putBoolean(Preferences.DRAWGEOFENCE, false);
		    					editor.commit();
		    	        		path.clear();
		    	        		//show action bar and make everything to normal state
		    	        		((MainTrackerInterface)context).getActionBar().show();
		    					((TrackObjects)((MainTrackerInterface)context).currentstatus).buttonAddGeofence.setEnabled(true);
		    			        Toast.makeText(context, 
		    			        		 "Geo-fence successfully saved.", 
		    			                 Toast.LENGTH_LONG).show();
		    			        DrawGeofenceCanvas.this.cancel();
		    	        	}
		            	}
		                touch_up();
		                invalidate();
		                break;
		        }
		        return true;
	        }  
        }

}
