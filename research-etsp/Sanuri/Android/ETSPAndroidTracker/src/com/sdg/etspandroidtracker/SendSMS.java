package com.sdg.etspandroidtracker;

import java.io.UnsupportedEncodingException;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

//import com.sdg.compressor.LZ4Compressor;
//import com.sdg.compressor.LZ4Factory;
import android.app.Activity;
import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SendSMS extends Activity{

	   protected void sendSMSMessage(String message,Context context) { 
		   String messageCompressed=compressMessage(message);
	      try {
	    	 
	         SmsManager smsManager = SmsManager.getDefault();
	         smsManager.sendTextMessage("+94713739955", null, messageCompressed, null, null);
	        
	      } catch (Exception e) {
	         Toast.makeText(context,
	         "SMS failed:"+e.getMessage(),
	         Toast.LENGTH_LONG).show();
	      }
	   }

		private String compressMessage(String message) {
			// TODO Auto-generated method stub
			String compressedMessage=null;
			LZ4Factory factory = LZ4Factory.fastestInstance();
			byte[] data;
			try {
				data = message.getBytes("UTF-8");
	
				final int decompressedLength = data.length;
		
				// compress data
				LZ4Compressor compressor = factory.fastCompressor();
				int maxCompressedLength = compressor.maxCompressedLength(decompressedLength);
				byte[] compressed = new byte[maxCompressedLength];
				int compressedLength = compressor.compress(data, 0, decompressedLength, compressed, 0, maxCompressedLength);
				compressedMessage = compressed.toString();
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return compressedMessage;
		}
	}