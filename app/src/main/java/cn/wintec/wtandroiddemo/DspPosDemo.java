package cn.wintec.wtandroiddemo;


import cn.wintec.wtandroidjar.DspPos;
import cn.wintec.wtandroidjar.ComIO;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class DspPosDemo extends Activity {
	
	DspPos dsp=null;
	String devicePath;
	ComIO.Baudrate baudrate;
	int model=0;
	private Spinner mySpinner,Spinnerserport;	
	  
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dsppos);	
	        
		//Get the serial parameters saved
        SharedPreferences mSharedPreferences=getPreferences(MODE_PRIVATE);
        devicePath=mSharedPreferences.getString("devicePath", "/dev/ttySAC3");
		baudrate= ComIO.Baudrate.valueOf(mSharedPreferences.getString("baudrate", "BAUD_9600"));
	        
		dsp=new DspPos(devicePath,baudrate);	       	 
	       
        mySpinner=(Spinner)findViewById(R.id.spinner1);
        
        Spinnerserport=(Spinner)findViewById(R.id.spinner_Serport);
        Spinnerserport.setSelection(Integer.parseInt(devicePath.substring(11)),true);
        Spinnerserport.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				devicePath=Spinnerserport.getSelectedItem().toString();
				dsp=new DspPos(devicePath,baudrate);
				SharedPreferences mSharedPreferences=getPreferences(MODE_PRIVATE);
         		mSharedPreferences.edit().putString("devicePath", devicePath).commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        });
        
        Button button_back = (Button)findViewById(R.id.button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	dsp.DSP_Close();
            	DspPosDemo.this.finish();	
            }
        });
        
        //display data 
        Button button_Input=(Button)findViewById(R.id.button_input);
        button_Input.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.editText1);
                String oriContent = et.getText().toString();
            	if(dsp.DSP_Dispay(oriContent,"GBK")){
                    Toast.makeText(DspPosDemo.this,"Successed!", Toast.LENGTH_SHORT).show();
            	}else{
            		Toast.makeText(DspPosDemo.this,"failed!", Toast.LENGTH_SHORT).show();    
            	}
            }
        });
	 
        //move curso left one bit
        Button button_left=(Button)findViewById(R.id.button_left);
        button_left.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dsp.DSP_MoveCursorLeft();
			}
		});
	        
        //clear screen
        Button button_Clear=(Button)findViewById(R.id.Button_Clear);
        button_Clear.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				dsp.DSP_ClearScreen();					
			}
		});
	        
        //move cursor right one bit
        Button button_Right=(Button)findViewById(R.id.button_right);
        button_Right.setOnClickListener(new View.OnClickListener() {				
			public void onClick(View v) {					
				// TODO Auto-generated method stub	
				dsp.DSP_MoveCursorRight();
			}
		});
	        
        //move cursor down
        Button button_Down=(Button)findViewById(R.id.button_down);
        button_Down.setOnClickListener(new View.OnClickListener() {				
			public void onClick(View v) {					
				// TODO Auto-generated method stub
				dsp.DSP_MoveCursorDown();
			}
		});
	       
        //move cursor up
        Button button_Up=(Button)findViewById(R.id.button_up);
        button_Up.setOnClickListener(new View.OnClickListener() {				
			public void onClick(View v) {					
				// TODO Auto-generated method stub
				dsp.DSP_MoveCursorUp();					
			}
		});
	     
        //move cursor home
        Button button_Home=(Button)findViewById(R.id.button_home);
        button_Home.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {					
				// TODO Auto-generated method stub
				dsp.DSP_CursorHome();
				}
		});
	     
        //move cursor end left
        Button button_EndLeft=(Button)findViewById(R.id.button_endleft);
        button_EndLeft.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dsp.DSP_MoveCursorEndLeft();
			}
		});
	     
        //move cursor end right
        Button button_EndRight=(Button)findViewById(R.id.button_Endright);
        button_EndRight.setOnClickListener(new View.OnClickListener() {				
			public void onClick(View v) {					
				// TODO Auto-generated method stub
				dsp.DSP_MoveCursorEndRight();
			}
		});
        
        //move cursor end
        Button button_End=(Button)findViewById(R.id.button_End);
        button_End.setOnClickListener(new View.OnClickListener() {				
			public void onClick(View v) {					
				// TODO Auto-generated method stub
				dsp.DSP_MoveCursorEnd();
			}
		});
	    
        //clear current line
        Button button_Clearline=(Button)findViewById(R.id.button_clearline);
        button_Clearline.setOnClickListener(new View.OnClickListener() {				
			public void onClick(View v) {					
				// TODO Auto-generated method stub
				dsp.DSP_ClearLine();
			}
		});
        
        //move cursor to x row x column
        Button button_Move=(Button)findViewById(R.id.button_Move);
        button_Move.setOnClickListener(new View.OnClickListener() {				
			public void onClick(View arg0) {					
				// TODO Auto-generated method stub
				   EditText editrow=(EditText)findViewById(R.id.editrow);
			       EditText editcolumn=(EditText)findViewById(R.id.editcolumn);
			       dsp.DSP_MoveCursor(Integer.parseInt(editrow.getText().toString()),Integer.parseInt(editcolumn.getText().toString()));				
			}
        });
	       
       //switching in English
        Button button_model=(Button)findViewById(R.id.button_model);
        button_model.setOnClickListener(new View.OnClickListener() {				
			public void onClick(View arg0) {
				// TODO Auto-generated method stub									  
				if(model==0){
					dsp.DSP_SetDspMode(1);
					model=1;
				}else{
					dsp.DSP_SetDspMode(0);
					model=0;
				}					
			}
		});
	        
        //display mode
        Button button_displaymode=(Button)findViewById(R.id.button_displaymode);
        button_displaymode.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int index=	mySpinner.getSelectedItemPosition()+1;
				dsp.DSP_DisplayMode(index);
			}
		});
	        
        Button buttonSetting = (Button)findViewById(R.id.button_setting);
        buttonSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {	
            	devicePath=Spinnerserport.getSelectedItem().toString();
            	dsp=new DspPos(devicePath);	     	       
                Bundle bundle= new Bundle();
                bundle.putString("device_path",devicePath);
                bundle.putSerializable("baudrate", baudrate);
                Intent intent=new Intent();
                intent.putExtras(bundle);	    		
                intent.setClass(DspPosDemo.this,SetSerialPort.class);
                startActivityForResult(intent,0x00);
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
		 		baudrate= ComIO.Baudrate.valueOf(mSharedPreferences.getString("baudrate", "BAUD_9600"));
			}
		}
	 }
}

