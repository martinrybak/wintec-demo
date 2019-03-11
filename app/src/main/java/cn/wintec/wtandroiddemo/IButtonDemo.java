package cn.wintec.wtandroiddemo;


import cn.wintec.wtandroidjar.ComIO;
import cn.wintec.wtandroidjar.IButton;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class IButtonDemo extends Activity{
	
	IButton ibutton=null;
	String devicePath;//"/dev/ttymxc4";
	ComIO.Baudrate baudrate;
	private Spinner Spinnerserport;
	Thread rxThread = null;
	Handler myHandler; 
	static final int MSG_ADD_RX_DATA        = 0;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ibutton);
	        
        //Get the serial parameters saved
        SharedPreferences mSharedPreferences=getPreferences(MODE_PRIVATE);
        devicePath=mSharedPreferences.getString("devicePath", "/dev/ttySAC3");
		baudrate=ComIO.Baudrate.valueOf(mSharedPreferences.getString("baudrate", "BAUD_9600"));
	        
		ibutton=new IButton(devicePath,baudrate);
	       
        Spinnerserport=(Spinner)findViewById(R.id.spinner_Serport);	  
        Spinnerserport.setSelection(Integer.parseInt(devicePath.substring(11)),true);
        Spinnerserport.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				devicePath=Spinnerserport.getSelectedItem().toString();
				ibutton=new IButton(devicePath,baudrate);
				SharedPreferences mSharedPreferences=getPreferences(MODE_PRIVATE);
         		mSharedPreferences.edit().putString("devicePath", devicePath).commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        });
	       
        Button button_read=(Button)findViewById(R.id.button_read);
        final EditText textviewRX = (EditText)findViewById(R.id.editText_Ibuttoninfo);	
        button_read.setOnClickListener(new View.OnClickListener() {
				
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//onStart();
				
				 byte [] content=new byte[10];
				 String content_ = new String();
				 int length=ibutton.IBTN_GetSN(content);
				 if(length>0)
				 {
				  
                   for(int i = 0; i < length; ++i)
                       content_ += String.format("%02X ", (int)content[i] & 0xFF);
                   
				 }
				 else
				 {
					 content_="";
				 }
				textviewRX.setText(content_);
			}
		});
        
        Button buttonSetting = (Button)findViewById(R.id.button_setting);
        buttonSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {	
            	devicePath=Spinnerserport.getSelectedItem().toString();
            	ibutton=new IButton(devicePath);
                Bundle bundle= new Bundle();
                bundle.putString("device_path",devicePath);
                bundle.putSerializable("baudrate", baudrate);
                Intent intent=new Intent();
                intent.putExtras(bundle);	    		
                intent.setClass(IButtonDemo.this,SetSerialPort.class);
                startActivityForResult(intent,0x00);
            }
        });	 
	        
        Button buttonCancel = (Button)findViewById(R.id.button_back);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	ibutton.IBTN_Close();
            	IButtonDemo.this.finish();
            }
        });
	      
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
 				mSharedPreferences.edit().putString("baudrate",data.getStringExtra("baudrate")).commit();
 				baudrate=ComIO.Baudrate.valueOf(mSharedPreferences.getString("baudrate", "BAUD_9600"));
			}
		}
	 }

}
