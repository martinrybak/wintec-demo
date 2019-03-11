package cn.wintec.wtandroiddemo;

import cn.wintec.wtandroidjar.ComIO;
import cn.wintec.wtandroidjar.Rfid;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RfidDemo extends Activity{
	
	String devicePath;///dev/ttySAC3";	
	ComIO.Baudrate baudrate;
	Thread rxThread = null;
	Handler myHandler; 
	static final int MSG_ADD_RX_DATA        = 0;
	private Rfid rfid;
	boolean isread=true;
	private TextView textView_rfidcardinfo;
	byte[] keyvalue=new byte[]{(byte) 0xff,(byte) 0xff,(byte) 0xff,(byte)0xff,(byte)0xff,(byte)0xff};
	int numblock=0;
	private byte cPwdFlags=0x60;
	private EditText editText_block,editText_content,editText_keyvalue,editText_Money;
	private Spinner  spcPwdFlags,Spinnerserport;	 
	String[] numbers = { "0x60", "0x61" };
	 
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rfid);	   
	        
		//Get the serial parameters saved
        SharedPreferences mSharedPreferences=getPreferences(MODE_PRIVATE);
        devicePath=mSharedPreferences.getString("devicePath", "/dev/ttySAC3");
		baudrate= ComIO.Baudrate.valueOf(mSharedPreferences.getString("baudrate", "BAUD_9600"));
        rfid=new Rfid(devicePath,baudrate);	       
       
        textView_rfidcardinfo=(TextView)findViewById(R.id.textView_rfidcardinfo);
        editText_block=(EditText)findViewById(R.id.editText_block);
        editText_content=(EditText)findViewById(R.id.editText_content);	
        editText_keyvalue=(EditText)findViewById(R.id.editText_keyvalue);
        editText_Money=(EditText)findViewById(R.id.editText_Money);
        spcPwdFlags=(Spinner)findViewById(R.id.spinner_pwdflags);
        spcPwdFlags.setSelection(0);
        
        Spinnerserport=(Spinner)findViewById(R.id.spinner_Serport);
        Spinnerserport.setSelection(Integer.parseInt(devicePath.substring(11)),true);
        Spinnerserport.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				devicePath=Spinnerserport.getSelectedItem().toString();
				rfid=new Rfid(devicePath,baudrate);	     
				SharedPreferences mSharedPreferences=getPreferences(MODE_PRIVATE);
         		mSharedPreferences.edit().putString("devicePath", devicePath).commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        });
        
        editText_block.requestFocus();
        
        Spinner spinner = (Spinner) findViewById(R.id.spinner_pwdflags);  
        SpinnerAdapter adapter = new SpinnerAdapter(this,  
        		android.R.layout.simple_spinner_item, numbers);  
        spinner.setAdapter(adapter);  
        
        Button button_readRfid=(Button)findViewById(R.id.button_readRfid);
        button_readRfid.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View v) {				
				byte [] cardtype=new byte[2];
				byte [] cardnumber=new byte[4];
				byte [] Capacity=new byte[1];
				keyvalue=contentFormater(editText_keyvalue.getText().toString());
				numblock=Integer.parseInt(editText_block.getText().toString());
				if(spcPwdFlags.getSelectedItemPosition()==0)
				{
					cPwdFlags=0x60;
				}else
				{
					cPwdFlags=0x61;
				}
				textView_rfidcardinfo.setText("cardinfo...\r\n");
				if(rfid.RFID_SearchCards(cardtype))
				{
					String cardtype_="CardType:";
                	cardtype_+=String.format("%02X ", (int)cardtype[0] & 0xFF);
                	cardtype_+=String.format("%02X ", (int)cardtype[1] & 0xFF);
                	textView_rfidcardinfo.append(cardtype_+"  ");
                    if(rfid.RFID_AntiCollision(cardnumber))
                    {
                    	String cardnumber_="CardNumber：";
                    	cardnumber_+=String.format("%02X ", (int)cardnumber[0] & 0xFF);
                    	cardnumber_+=String.format("%02X ", (int)cardnumber[1] & 0xFF);
                    	cardnumber_+=String.format("%02X ", (int)cardnumber[2] & 0xFF);
                    	cardnumber_+=String.format("%02X ", (int)cardnumber[3] & 0xFF);
                    	textView_rfidcardinfo.append(cardnumber_+"\r\n");
                    	if(rfid.RFID_SelectCard(cardnumber, Capacity))
                    	{
                    		String capacity_="CardCapacity:"+String.format("%02X ", (int)Capacity[0] & 0xFF);
                    		textView_rfidcardinfo.append(capacity_+"  ");
                    		
                    		if(rfid.RFID_VerifyKey(numblock, keyvalue, cPwdFlags))
                    		{
                    			byte [] cardvalue=new byte[16];
                    			int length=rfid.RFID_ReadBlock(numblock, cardvalue);
                    			if(length>0)
                    			{
	                    			String cardvalue_=String.valueOf(numblock)+"Blockinfo:";
//	                    			for(int i=0;i<length;i++)
//	                    			{
//	                    				cardvalue_+=String.valueOf((char)cardvalue[i]);
//	                    			}
//	                    			textView_rfidcardinfo.append(cardvalue_);
	                    			textView_rfidcardinfo.append(cardvalue_+bytesToHexString(cardvalue));
                    			}else
                    			{
                    				Toast.makeText(RfidDemo.this, "Did not read the information!", Toast.LENGTH_LONG).show();
                    			}	                    			
                    		}else
                    		{
                    			Toast.makeText(RfidDemo.this, "Key authentication failed！", Toast.LENGTH_LONG).show();
                    		}
                    	}else
                    	{
                    		Toast.makeText(RfidDemo.this, "Select card failed!", Toast.LENGTH_LONG).show();
                    	}
                    	
                    }else
                    {
                    	Toast.makeText(RfidDemo.this, "Anti-collision failed!", Toast.LENGTH_LONG).show();
                    }
				}
				else
				{
					Toast.makeText(RfidDemo.this, "Search card failed!", Toast.LENGTH_LONG).show();
				}
			}
		});
	        
        Button buttonWrite=(Button)findViewById(R.id.button_Write);
        buttonWrite.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View v) {
				
				byte [] cardtype=new byte[2];
				byte [] cardnumber=new byte[4];
				byte [] Capacity=new byte[1];					
				if(editText_block.getText().toString()=="")
				{
					return ;
				}
				 keyvalue=contentFormater(editText_keyvalue.getText().toString());
				 if(spcPwdFlags.getSelectedItemPosition()==0)
				{
					cPwdFlags=0x60;
				}else
				{
					cPwdFlags=0x61;
				}
				numblock=Integer.parseInt(editText_block.getText().toString());
				if(rfid.RFID_SearchCards(cardtype))
				{
					String cardtype_="CardType:";
                	cardtype_+=String.format("%02X ", (int)cardtype[0] & 0xFF);
                	cardtype_+=String.format("%02X ", (int)cardtype[1] & 0xFF);
                	textView_rfidcardinfo.append(cardtype_);
                    if(rfid.RFID_AntiCollision(cardnumber))
                    {
                    	String cardnumber_="CardNumber：";
                    	cardnumber_+=String.format("%02X ", (int)cardnumber[0] & 0xFF);
                    	cardnumber_+=String.format("%02X ", (int)cardnumber[1] & 0xFF);
                    	cardnumber_+=String.format("%02X ", (int)cardnumber[2] & 0xFF);
                    	cardnumber_+=String.format("%02X ", (int)cardnumber[3] & 0xFF);
                    	textView_rfidcardinfo.append(cardnumber_);
                    	if(rfid.RFID_SelectCard(cardnumber, Capacity))
                    	{
                    		String capacity_="CardCapacity:"+String.format("%02X ", (int)Capacity[0] & 0xFF);
                    		textView_rfidcardinfo.append(capacity_);
                    		
                    		if(rfid.RFID_VerifyKey(numblock, keyvalue, cPwdFlags))
                    		{
	                    		String content=	editText_content.getText().toString();
	                    		byte [] contents=new byte[content.length()];
	                    		for(int i=0;i<content.length();i++)
	                    		{
	                    			contents[i]=(byte)content.charAt(i);
	                    		}
	                    		if(rfid.RFID_WriteBlock(numblock, contents))
	                    		{
	                    			Toast.makeText(RfidDemo.this, "Write card succeeded!", Toast.LENGTH_LONG).show();
	                    			textView_rfidcardinfo.append(String.valueOf(numblock)+"Blockinfo:"+content);
		                    		
	                    		}else
	                    		{
	                    			Toast.makeText(RfidDemo.this, "Write card failed!", Toast.LENGTH_LONG).show();
	                    		}
                    	}else
                		{
                			Toast.makeText(RfidDemo.this, "Key validation failed!", Toast.LENGTH_LONG).show();
                		}
                	}else
                	{
                		Toast.makeText(RfidDemo.this, "Select card failed!", Toast.LENGTH_LONG).show();
                	}
                	
                }else
                {
                	Toast.makeText(RfidDemo.this, "Anti-collision failed!", Toast.LENGTH_LONG).show();
                }
			}
			else
			{
				Toast.makeText(RfidDemo.this, "Search card failed!", Toast.LENGTH_LONG).show();
			}

			}
		});
	        
        Button button_InitWallet=(Button)findViewById(R.id.button_InitWallet);
        button_InitWallet.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View arg0) {
				
				 numblock=Integer.parseInt(editText_block.getText().toString());
				 keyvalue=contentFormater(editText_keyvalue.getText().toString());
				 if(spcPwdFlags.getSelectedItemPosition()==0)
				{
					cPwdFlags=0x60;
				}else
				{
					cPwdFlags=0x61;
				}
				byte [] cardtype=new byte[2];
				byte [] cardnumber=new byte[4];
				byte [] Capacity=new byte[1];	
				int money=Integer.parseInt(editText_Money.getText().toString());
				if(rfid.RFID_SearchCards(cardtype))
				{
                    if(rfid.RFID_AntiCollision(cardnumber))
                    {
                    	if(rfid.RFID_SelectCard(cardnumber, Capacity))
                    	{
							 if(rfid.RFID_VerifyKey(numblock, keyvalue, cPwdFlags))
							 {
								 if(rfid.RFID_InitWallet(numblock, money))
								 {
									 Toast.makeText(RfidDemo.this, "Successful initialization!", Toast.LENGTH_LONG).show();
								 }
								 else
								 {
									 Toast.makeText(RfidDemo.this, "Initialization failed!", Toast.LENGTH_LONG).show(); 
								 }
							}else
		            		{
		            			Toast.makeText(RfidDemo.this, "Key verification failed!", Toast.LENGTH_LONG).show();
		            		}
		            	}else
		            	{
		            		Toast.makeText(RfidDemo.this, "Select card failed!", Toast.LENGTH_LONG).show();
		            	}
        	
		            }else
		            {
		            	Toast.makeText(RfidDemo.this, "Anti-collision failed!", Toast.LENGTH_LONG).show();
		            }
				}else
				{
					Toast.makeText(RfidDemo.this, "search card failed!", Toast.LENGTH_LONG).show();
				}
			}    
		});
	        
        Button buttonRechargeWallet=(Button)findViewById(R.id.buttonRechargeWallet);
        buttonRechargeWallet.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View v) {
				
				numblock=Integer.parseInt(editText_block.getText().toString());
				keyvalue=contentFormater(editText_keyvalue.getText().toString());
				if(spcPwdFlags.getSelectedItemPosition()==0)
				{
					cPwdFlags=0x60;
				}else
				{
					cPwdFlags=0x61;
				}
				byte [] cardtype=new byte[2];
				byte [] cardnumber=new byte[4];
				byte [] Capacity=new byte[1];
				int money=Integer.parseInt(editText_Money.getText().toString());
				if(rfid.RFID_SearchCards(cardtype))
				{
					if(rfid.RFID_AntiCollision(cardnumber))
                    {
                    	if(rfid.RFID_SelectCard(cardnumber, Capacity))
                    	{
							 if(rfid.RFID_VerifyKey(numblock, keyvalue, cPwdFlags))
							 {
								 if(rfid.RFID_RechargeWallet(numblock, money))
								 {
									 Toast.makeText(getApplication(), "Recharge succeeded!", Toast.LENGTH_LONG).show();
								 }else{
									 Toast.makeText(getApplication(), "Recharge failed!", Toast.LENGTH_LONG).show();
								 }
							}else{
		            			Toast.makeText(RfidDemo.this, "Key verification failed!", Toast.LENGTH_LONG).show();
		            		}
		            	}else{
		            		Toast.makeText(RfidDemo.this, "Select card failed!", Toast.LENGTH_LONG).show();
		            	}
		            }else{
		            	Toast.makeText(RfidDemo.this, "Anti-collision failed!", Toast.LENGTH_LONG).show();
		            }
				}else{
					Toast.makeText(RfidDemo.this, "Search card failed!", Toast.LENGTH_LONG).show();
				}
			}
		});
	        
        //get balance 
        Button buttonreadWallet=(Button)findViewById(R.id.buttonreadWallet);
        buttonreadWallet.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View v) {					
				numblock=Integer.parseInt(editText_block.getText().toString());
				keyvalue=contentFormater(editText_keyvalue.getText().toString());
				if(spcPwdFlags.getSelectedItemPosition()==0){
					cPwdFlags=0x60;
				}else{
					cPwdFlags=0x61;
				}
				byte [] cardtype=new byte[2];
				byte [] cardnumber=new byte[4];
				byte [] Capacity=new byte[1];					
				if(rfid.RFID_SearchCards(cardtype)){
                    if(rfid.RFID_AntiCollision(cardnumber)){
                    	if(rfid.RFID_SelectCard(cardnumber, Capacity)){
							 if(rfid.RFID_VerifyKey(numblock, keyvalue, cPwdFlags)){
								 byte [] balance=new byte[8];
								 if(rfid.RFID_ReadWallet(numblock, balance)){
									 int intValue = 0; 											  
									 for (int i = 0; i < balance.length; i++) {             
										 intValue += (balance[i] & 0xFF) << (8 * (i));  
									 }
											 
									 Toast.makeText(getApplication(), "Read success!"+String.valueOf(intValue), Toast.LENGTH_LONG).show();
								 }else{
									 Toast.makeText(getApplication(), "Read failed!", Toast.LENGTH_LONG).show();
								 }
							}else{
		            			Toast.makeText(RfidDemo.this, "Key verification failed!", Toast.LENGTH_LONG).show();
		            		}
		            	}else{
		            		Toast.makeText(RfidDemo.this, "Select card failed!", Toast.LENGTH_LONG).show();
		            	}
		            }else{
		            	Toast.makeText(RfidDemo.this, "Anti-collision failed!", Toast.LENGTH_LONG).show();
		            }
				}else{
					Toast.makeText(RfidDemo.this, "Search card failed!", Toast.LENGTH_LONG).show();
				}
			}
		});
	        
        //deduct 
        Button buttonDeduct=(Button)findViewById(R.id.buttonDeduct);
        buttonDeduct.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View v) {					
				numblock=Integer.parseInt(editText_block.getText().toString());
				keyvalue=contentFormater(editText_keyvalue.getText().toString());
				if(spcPwdFlags.getSelectedItemPosition()==0)
				{
					cPwdFlags=0x60;
				}else{
					cPwdFlags=0x61;
				}
				byte [] cardtype=new byte[2];
				byte [] cardnumber=new byte[4];
				byte [] Capacity=new byte[1];
				int money=Integer.parseInt(editText_Money.getText().toString());
				if(rfid.RFID_SearchCards(cardtype))
				{
					if(rfid.RFID_AntiCollision(cardnumber))
                    {
                    	if(rfid.RFID_SelectCard(cardnumber, Capacity))
                    	{
                			if(rfid.RFID_VerifyKey(numblock, keyvalue, cPwdFlags))
							{
                				if(rfid.RFID_DeductMoney(numblock, money))
								{
                					Toast.makeText(getApplication(), "Debit success!", Toast.LENGTH_LONG).show();
								}else{
									Toast.makeText(getApplication(), "Failed charge!", Toast.LENGTH_LONG).show();
								}
							}else{
		            			Toast.makeText(RfidDemo.this, "Key verification failed!", Toast.LENGTH_LONG).show();
		            		}
		            	}else{
		            		Toast.makeText(RfidDemo.this, "Select card failed!", Toast.LENGTH_LONG).show();
		            	}
		            }else{
		            	Toast.makeText(RfidDemo.this, "Anti-collision failed!", Toast.LENGTH_LONG).show();
		            }
				}else{
					Toast.makeText(RfidDemo.this, "Search card failed!", Toast.LENGTH_LONG).show();
				}
			}
		});
        
        Button buttonClear=(Button)findViewById(R.id.button_Clear);
        buttonClear.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View v) {					
				textView_rfidcardinfo.setText("cardinfo...");
			}
		});
        
        Button button_back = (Button)findViewById(R.id.button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	rfid.RFID_Close();
            	RfidDemo.this.finish();	      
            }
        });
	        
        Button buttonSetting = (Button)findViewById(R.id.button_setting);
        buttonSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {	
            	devicePath=Spinnerserport.getSelectedItem().toString();
            	rfid=new Rfid(devicePath);	     	       
                Bundle bundle= new Bundle();
                bundle.putString("device_path",devicePath);
                bundle.putSerializable("baudrate", baudrate);
                Intent intent=new Intent();
                intent.putExtras(bundle);	    		
                intent.setClass(RfidDemo.this,SetSerialPort.class);
                startActivityForResult(intent,0x00);
            }
        });	 
	    
		Button cpuCardbtn = (Button) findViewById(R.id.Button01);
		cpuCardbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(RfidDemo.this, CpuCardDemo.class);
				startActivity(intent);
			}
		});

        myHandler = new Handler() {	           
			@Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_ADD_RX_DATA) {
                	String content_=msg.getData().getString("cardinfo");
                	textView_rfidcardinfo.append(content_+"\r\n");	                    
                }
            }
        };
	
	 }
	 
    public static byte [] contentFormater(String content){
         String []subElement=content.split(" ");	        
         byte [] contents=new byte[6];
         for(int i=0;i<subElement.length;i++){
        	 contents[i]=(byte)Integer.parseInt(subElement[i],16);
         }
         return contents;
    }
	
	private class SpinnerAdapter extends ArrayAdapter<String> {
	    Context context;
	    String[] items = new String[] {};

	    public SpinnerAdapter(final Context context,
	            final int textViewResourceId, final String[] objects) {
	        super(context, textViewResourceId, objects);
	        this.items = objects;
	        this.context = context;
	    }

	    @Override
	    public View getDropDownView(int position, View convertView,
	            ViewGroup parent) {

	        if (convertView == null) {
	            LayoutInflater inflater = LayoutInflater.from(context);
	            convertView = inflater.inflate(
	                    android.R.layout.simple_spinner_item, parent, false);
	        }

	        TextView tv = (TextView) convertView
	                .findViewById(android.R.id.text1);
	        tv.setText(items[position]);
	        tv.setTextSize(30);
	        return convertView;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        if (convertView == null) {
	            LayoutInflater inflater = LayoutInflater.from(context);
	            convertView = inflater.inflate(
	                    android.R.layout.simple_spinner_item, parent, false);
	        }
	        TextView tv = (TextView) convertView
	                .findViewById(android.R.id.text1);
	        tv.setText(items[position]);
	        tv.setTextSize(30);
	        return convertView;
	    }
	}
	
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 // TODO Auto-generated method stub
		 super.onActivityResult(requestCode, resultCode, data);
		 switch (requestCode) {
		 case 0x00:
			 if (resultCode == RESULT_OK) {
				//Save the serial port parameters
			 	SharedPreferences mSharedPreferences=getPreferences(MODE_PRIVATE);
     			mSharedPreferences.edit().putString("devicePath", devicePath).commit();
     			mSharedPreferences.edit().putString("baudrate",data.getStringExtra("baudrate")).commit();
     			baudrate= ComIO.Baudrate.valueOf(mSharedPreferences.getString("baudrate", "BAUD_9600"));
			}
		}
	 }
	 
	 public void cpucardTest(View target)
		{
			//Off Antenna
			rfid.RFID_DisableAntenna();
			//Open Antenna
			rfid.RFID_EnableAntenna();
			//search card
			byte[] cardtype=new byte[2];
			rfid.RFID_SearchCards(cardtype);
			if(cardtype[0]==0x08)//cpu card
			{
				//card reset
				byte[] rlen=new byte[1];
				byte[] receive_data=new byte[100];
				rfid.RFID_Pro_Reset((byte)0x52, rlen, receive_data);
				
				//Take four-bit random number
				byte[] command_data={0x00, (byte)0x84, 0x00, 0x00, 0x04};
				rfid.RFID_Cos_Command(5, command_data, rlen, receive_data);
				String randomNumber="random number：";
				randomNumber+=String.format("%02X ", (int)receive_data[0] & 0xFF);
				randomNumber+=String.format("%02X ", (int)receive_data[1] & 0xFF);
				randomNumber+=String.format("%02X ", (int)receive_data[2] & 0xFF);
				randomNumber+=String.format("%02X ", (int)receive_data[3] & 0xFF);
				TextView tv=(TextView)findViewById(R.id.textView1);
				tv.setText(randomNumber);
			}
			else
			{
				Toast.makeText(this, "Card is invalid!", Toast.LENGTH_SHORT).show();
			}
		}

	 public void BeijingYkt(View target){
		 ((TextView)findViewById(R.id.tvwBeijing)).setText("");
		 
		 //卡复位
		boolean ret = false;
		byte[] rlen = new byte[1];
		byte[] receive_data = new byte[255];
		ret = rfid.RFID_Pro_Reset((byte) 0x52, rlen, receive_data);
		if(!ret)
			return;
		
		//选文件
		ret = rfid.RFID_Cos_Command(7,new byte[] { 0x00, (byte) 0xA4, 0x00, 0x00, 0x02, 0x00, 0x04 }, rlen,receive_data);
		if(!ret)
			return;
		
		//读卡
		ret = rfid.RFID_Cos_Command(5, new byte[]{ 0x00, (byte) 0xB0, 0x00, 0x00, 0x78 }, rlen,receive_data);
		if(!ret)
			return;
		
		String cardInfo=bcd2Str(receive_data,rlen[0]);
		((TextView)findViewById(R.id.tvwBeijing)).setText(cardInfo);
	 }
	 
	 private static String bcd2Str(byte[] bytes,int len) {
			char[] temp = new char[len * 2];
			char val;

			for (int i = 0; i < len; i++) {
				val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
				temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

				val = (char) (bytes[i] & 0x0f);
				temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
			}
			return new String(temp);
		}
	 
	 public static final String bytesToHexString(byte[] bArray) {   
		    StringBuffer sb = new StringBuffer(bArray.length);   
		    String sTemp;   
		    for (int i = 0; i < bArray.length; i++) {   
		     sTemp = Integer.toHexString(0xFF & bArray[i]);   
		     if (sTemp.length() < 2)   
		      sb.append(0);   
		     sb.append(sTemp.toUpperCase());   
		    }   
		    return sb.toString();   
		}  
}
