package com.sdg.etspandroidtracker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.sdg.util.Preferences;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.text.format.Time;
import android.util.Log;

public class TrackerService extends IntentService{

	  private Handler mainThreadHandler = null;
	  private int result = Activity.RESULT_CANCELED;
	  private boolean serviceRunning;
	  private static final String SERVICERUNNING="running";
	  public static final String RESULT = "result";
	  public static final String NOTIFICATION = "com.sdg.etspandroidtracker.TSERVICE";
	  public static final String MODE = "Mode";
	  public static final String INTERVAL = "Interval";
	  public static final String SENSORS = "Sensors";
	  
	  public static final String Mode = "modeKey"; 
	  public static final String Sensors = "sensorsKey"; 
	  public static final String Interval = "intervalKey"; 
	
	  private String mode,sensors,message,interval;
	  private SensorEventListener mSensorListener;
	  private SensorManager sensorManager;
	  private com.sdg.etspandroidtracker.GetLocation getLocation;
	  File outputFile=null;
	  
	  
	  private float pressure=0;
	  private float humidity=0;
	  private float stepCount=0;
	  private float temperature=0;
	  private float orientation=0;
	  private float gravityVal=0;
	  
		private ArrayList<String> selectedSensors=new ArrayList<String>();
		
		private float [] gravity = new float[3];
		private float [] linear_acceleration = new float[3];
		
		
		private SharedPreferences sharedPreferences;
		private Editor editor;
		
//---------------------------------------------------Constructor----------------------------------------------------------
	  public TrackerService() {
	    super(TrackerService.class.getName());
	    mainThreadHandler = new Handler();
	  }

//----------------------------------------------------OnCreate-------------------------------------------------------------
@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		getLocation=new com.sdg.etspandroidtracker.GetLocation(TrackerService.this);
		serviceRunning=true;
		sharedPreferences= this.getSharedPreferences(Preferences.MyPREFERENCES, Context.MODE_PRIVATE); 
		sensorManager = (SensorManager)this.getSystemService(SENSOR_SERVICE);	
		mSensorListener = new SensorEventListener() {
			
		    @Override
		    public void onAccuracyChanged(Sensor arg0, int arg1) {
		    }

		    //When the values change, get these latest values to the variables
		    @Override
		    public void onSensorChanged(SensorEvent event) {
		        Sensor sensor = event.sensor;
		        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
		            // alpha is calculated as t / (t + dT)
		            // with t, the low-pass filter's time-constant
		            // and dT, the event delivery rate

		            final float alpha = 0.8f;

		            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
		            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
		            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

		            linear_acceleration[0] = event.values[0] - gravity[0];
		            linear_acceleration[1] = event.values[1] - gravity[1];
		            linear_acceleration[2] = event.values[2] - gravity[2];
		        }
		        else if (sensor.getType() == Sensor.TYPE_PRESSURE) {
		        	pressure = event.values[0];
		        }
		        else if (sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
		        	humidity = event.values[0];
		        }
		        else if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
		        	stepCount = event.values[0];
		        }
		        else if (sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
		        	temperature = event.values[0];
		        }
		        else if(sensor.getType() == Sensor.TYPE_ORIENTATION) {
		        	orientation = event.values[0];
		        }
		        else if(sensor.getType() == Sensor.TYPE_GRAVITY) {
		        	gravityVal = event.values[0];
		        }
		    }
		};
		
		//Register all sensor listeners
		registerSensorListeners();
	}


	//------------------------------------ will be called asynchronously by Android-------------------------------------------
//creates a thread and run the preparation and sending of data on this thread
	  @Override
	  protected void onHandleIntent(Intent intent) {     
		 
		  	File outputDir=null;
			//Get values
			sensors = sharedPreferences.getString(Preferences.Sensors, "");
			interval = sharedPreferences.getString(Preferences.Interval, "");
			mode = sharedPreferences.getString(Preferences.Mode, "");
			
			outputDir = this.getCacheDir(); // context being the Activity pointer
			try {
				outputFile = File.createTempFile("ETSPTempData", "txt", outputDir);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				 Log.d("com.sdg.etspandroidtracker.TrackerService",e.getMessage());
			}
			
			
			
			if(CheckNetwork.isNetworkAvailable(this))
			{
				prepareMessage();
				sendMessage();
				outputFile.delete();
				result = Activity.RESULT_OK;
			}
	  }

		@Override
		public int onStartCommand(Intent intent, int flags, int startId) {
			// TODO Auto-generated method stub
			//super.onStartCommand(intent, flags, startId);
			return super.onStartCommand(intent, startId, startId);
			//return START_STICKY; //restart service if it gets killed.
		}
//-------------------------------------------Publish results of the service-------------------------------------------	  
	  public void publishResults(int result,boolean running) {
		  android.os.Debug.waitForDebugger();
		    Intent intent = new Intent(NOTIFICATION);
		    intent.putExtra(RESULT, result);
		    intent.putExtra(SERVICERUNNING, running);
		    sendBroadcast(intent);
		  }

//----------------------------------------Prepare the message by collecting necessary data-------------------------------
	public void prepareMessage() {
		//Prepare message
		String sensorData=GetSensorData();
		Time now = new Time();
		now.setToNow();
		String dateTime=now.toString()+" ";
		message=(dateTime+","+sensorData).trim();

	}
//-------------------------Notify that service is dying--------------------------------------------------------------------
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		serviceRunning=false;
		publishResults(result,serviceRunning);
		super.onDestroy();
	}
	
//--------------------------------------Gets data from sensors and returns as a single string-------------------------
	  private String GetSensorData() {
		// TODO Auto-generated method stub
		sensors = sharedPreferences.getString(Preferences.Sensors, "");
		String[] Words=sensors.split("/");
		selectedSensors.addAll(Arrays.asList(Words));
		StringBuffer data=new StringBuffer();
		for (String sensor : selectedSensors) {
			if(sensor.equalsIgnoreCase("Temperature"))
			{
				data.append("t");
				data.append(temperature);
			}
			else if(sensor.equalsIgnoreCase("Acceleration"))
			{
				data.append("ax");
				data.append(linear_acceleration[0]);
				data.append("y");
				data.append(linear_acceleration[1]);
				data.append("z");
				data.append(linear_acceleration[2]);
			}
			else if(sensor.equalsIgnoreCase("Distance"))
			{
				data.append("d100");
			}
			else if(sensor.equalsIgnoreCase("Speed"))
			{
				data.append("v50");
			}
			else if(sensor.equalsIgnoreCase("Location"))
			{
				String latestLocation=getLocation.getLocation();
				data.append("l");
				data.append(latestLocation);
			}
			else if(sensor.equalsIgnoreCase("Pressure"))
			{
				data.append("p");
				data.append(pressure);
			}
			else if(sensor.equalsIgnoreCase("Humidity"))
			{
				data.append("h");
				data.append(humidity);
			}
			else if(sensor.equalsIgnoreCase("Orientation"))
			{
				data.append("o");
				data.append(orientation);
			}
			else if(sensor.equalsIgnoreCase("Step Count"))
			{
				data.append("n");
				data.append(stepCount);
			}
		}
		return data.toString();
	}


	//-------------------------------Registers all sensors----------------------------------------------------------------
	private void registerSensorListeners() {
		// TODO Auto-generated method stub
		List<Sensor> sensorList = getSensors();
		for (Sensor sensor : sensorList) {
			if(sensor.getType()==Sensor.TYPE_TEMPERATURE)
			{
				sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE), SensorManager.SENSOR_DELAY_FASTEST);
			}
			else if(sensor.getType()==Sensor.TYPE_ACCELEROMETER)
			{
				sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
			}
			else if(sensor.getType()==Sensor.TYPE_PRESSURE)
			{
				sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE), SensorManager.SENSOR_DELAY_FASTEST);
			}
			else if(sensor.getType()==Sensor.TYPE_RELATIVE_HUMIDITY)
			{
				sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY), SensorManager.SENSOR_DELAY_FASTEST);
			}
			else if(sensor.getType()==Sensor.TYPE_ORIENTATION)
			{
				sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_FASTEST);
			}
			else if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) 
			{
				sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER), SensorManager.SENSOR_DELAY_FASTEST);
			}
			else if (sensor.getType() == Sensor.TYPE_GRAVITY) 
			{
				sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_FASTEST);
			}
		}
		/*
				sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
				sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED), SensorManager.SENSOR_DELAY_FASTEST);	
				sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
				sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_FASTEST);
				sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_FASTEST);
				sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_FASTEST);
				sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_FASTEST);
				sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_FASTEST);
				sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_FASTEST);
				sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED), SensorManager.SENSOR_DELAY_FASTEST);
				sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE), SensorManager.SENSOR_DELAY_FASTEST);
				sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_FASTEST);
				sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY), SensorManager.SENSOR_DELAY_FASTEST);
				sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_FASTEST);
				sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION), SensorManager.SENSOR_DELAY_FASTEST);
				sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER), SensorManager.SENSOR_DELAY_FASTEST);
				sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR), SensorManager.SENSOR_DELAY_FASTEST);
			*/
		}
	
	//Get the list of available sensors on device
	private List<Sensor> getSensors() {
		
		// TODO Auto-generated method stub
		SensorManager mgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		List<Sensor> sensorList=mgr.getSensorList(Sensor.TYPE_ALL);
	    return sensorList;
	}

//-------------------------------Sends message thru the mode selected by user------------------------------------------
	public void sendMessage() {
		// TODO Auto-generated method stub
		//message="nothing";
		if(mode.equals("0")){
			SendSMS sms=new SendSMS();
			sms.sendSMSMessage(message,getBaseContext());
		}
		else{
			SendViaGPRS gprs=new SendViaGPRS();
			boolean result = gprs.sendGPRSData(message); 
			editor=sharedPreferences.edit();
			editor.putBoolean(Preferences.result,result);
			editor.commit();
		}
	}	

	} 



