package cn.wintec.wtandroiddemo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;


import cn.wintec.wtandroidjar.Drw;
import cn.wintec.wtandroidjar.ComIO;
import cn.wintec.wtandroidjar.GPIO;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

public class DrwDemo extends Activity{
	
	Drw drw=null;
	String devicePath;
	ComIO.Baudrate baudrate;
	private Spinner	Spinnerserport;
	private RadioButton radioCom,radioGPIO;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drw);	  
        
        //Get the serial parameters saved
        SharedPreferences mSharedPreferences=getPreferences(MODE_PRIVATE);
        devicePath=mSharedPreferences.getString("devicePath", "/dev/ttySAC3");
		baudrate= ComIO.Baudrate.valueOf(mSharedPreferences.getString("baudrate", "BAUD_9600"));
        
		drw=new Drw(devicePath,baudrate);
 
        Spinnerserport=(Spinner)findViewById(R.id.spinner_Serport);
        Spinnerserport.setSelection(Integer.parseInt(devicePath.substring(11)),true);
        Spinnerserport.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				devicePath=Spinnerserport.getSelectedItem().toString();
				drw=new Drw(devicePath,baudrate);
				SharedPreferences mSharedPreferences=getPreferences(MODE_PRIVATE);
         		mSharedPreferences.edit().putString("devicePath", devicePath).commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        });
        
        radioCom=(RadioButton)findViewById(R.id.radioCom);
        radioGPIO=(RadioButton)findViewById(R.id.radioGpio);
        
        Button button_open=(Button)findViewById(R.id.button_open);
        button_open.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(radioCom.isChecked())	
				{
					if(drw.DRW_Open())
					{
						Toast.makeText(DrwDemo.this, "OPEN Successed", Toast.LENGTH_SHORT).show();
					}else
					{
						Toast.makeText(DrwDemo.this, "OPEN Failed", Toast.LENGTH_SHORT).show();
					}
				}else if(radioGPIO.isChecked())
				{
					String state;
					GPIO gpio = new GPIO();
					try{
						gpio.setValue(0);
						state="open success";
						Toast.makeText(DrwDemo.this,state,Toast.LENGTH_SHORT).show();
						
					}catch(IOException e){
						Toast.makeText(DrwDemo.this,"Fail to open GPIO"+e,Toast.LENGTH_SHORT).show();					
						return ;
					}
					
					try {
						try {
							Thread.sleep(150);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						gpio.setValue(1);
						state="close success";
						Toast.makeText(DrwDemo.this,state,Toast.LENGTH_SHORT).show();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
        
        Button button_Staus=(Button)findViewById(R.id.button_Staus);
        button_Staus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				byte [] status=new byte[1];									
				if(radioCom.isChecked()){
					int type=0;
					if(devicePath.equals("/dev/ttySAC3"))//poscard
						type=2;
					else if(devicePath.equals("/dev/ttySAC1"))//printer
						type=1;
					if(drw.DRW_GetStatus(status, type)){
						Toast.makeText(DrwDemo.this, "Status:"+((int)status[0]==0?"close":"open"), Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(DrwDemo.this, "GetStatus Failed", Toast.LENGTH_SHORT).show();
					}
				}else{					
					GPIO gpio= new GPIO();
					try{
						int flag=gpio.getValue();
						if(flag==0){
							Toast.makeText(DrwDemo.this,"low",Toast.LENGTH_SHORT).show();						
						}else if(flag==1){
							Toast.makeText(DrwDemo.this,"high",Toast.LENGTH_SHORT).show();							
						}
					}catch(IOException e){}
				}
			}
		});
        
        Button button_back = (Button)findViewById(R.id.button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	drw.DRW_Close();
            	DrwDemo.this.finish();	  
            }
        });
        
        Button buttonSetting = (Button)findViewById(R.id.button_setting);
        buttonSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {	
            	devicePath=Spinnerserport.getSelectedItem().toString();
            	drw=new Drw(devicePath);     	       
                Bundle bundle= new Bundle();
                bundle.putString("device_path",devicePath);
                bundle.putSerializable("baudrate", baudrate);
                Intent intent=new Intent();
                intent.putExtras(bundle);	    		
                intent.setClass(DrwDemo.this,SetSerialPort.class);
                startActivityForResult(intent,0x00);
            }
        });
	}
	public int getValue() throws IOException{
		String filePath="/sys/devices/platform/gemo_gpio.12/iolevel";
		char[] cb=new char[64];
		File file = new File (filePath);
		FileReader reader = new FileReader(file);
		int len = reader.read(cb);
		String ss=new String(cb,0,len-1);
		int num=Integer.parseInt(ss);
		reader.close();
		return num;
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
         		baudrate= ComIO.Baudrate.valueOf(mSharedPreferences.getString("baudrate", "BAUD_9600"));
			}
		}
	}
	
}
