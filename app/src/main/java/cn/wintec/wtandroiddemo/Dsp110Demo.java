package cn.wintec.wtandroiddemo;


import cn.wintec.wtandroidjar.ComIO;
import cn.wintec.wtandroidjar.Dsp110;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.ToggleButton;

public class Dsp110Demo extends Activity {
	
	Dsp110 dsp=null;
	String devicePath;
	ComIO.Baudrate baudrate;
	Spinner spinner1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dsp110);
		
		//Get the serial parameters saved
		SharedPreferences mSharedPreferences=getPreferences(MODE_PRIVATE);
        devicePath=mSharedPreferences.getString("devicePath", "/dev/ttySAC3");
		baudrate= ComIO.Baudrate.valueOf(mSharedPreferences.getString("baudrate", "BAUD_9600"));
		
		dsp=new Dsp110(devicePath,baudrate);

		SeekBar brightnessSeekBar=(SeekBar)findViewById(R.id.seekBar1);
		brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				dsp.DSP_SetBrightness(progress);
			}
		});
		
		ToggleButton cursorStateTottleButton=(ToggleButton)findViewById(R.id.toggleButton1);
		cursorStateTottleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
					dsp.DSP_SetCursorState(1);
				else
					dsp.DSP_SetCursorState(0);
			}
		});
		
		SeekBar words1SeekBar=(SeekBar)findViewById(R.id.seekBar3);
		words1SeekBar.setProgress(words1SeekBar.getMax());
		words1SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				dsp.DSP_SetWords1State(progress);
			}
		});
			
		ToggleButton words2TottleButton=(ToggleButton)findViewById(R.id.toggleButton2);
		words2TottleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
					dsp.DSP_SetWords2State(1);
				else
					dsp.DSP_SetWords2State(0);
			}
		});
		
		spinner1=(Spinner)findViewById(R.id.spinner1);	  
		spinner1.setSelection(Integer.parseInt(devicePath.substring(11)),true);
		spinner1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					devicePath=spinner1.getSelectedItem().toString();
					dsp=new Dsp110(devicePath,baudrate);
					SharedPreferences mSharedPreferences=getPreferences(MODE_PRIVATE);
	         		mSharedPreferences.edit().putString("devicePath", devicePath).commit();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
	        });
	}
	
	public void mybuttonlistener(View target){
		Button bt=(Button)target;
		switch (bt.getId()){ 
		case R.id.button1:
			EditText editText1=(EditText)findViewById(R.id.editText1);
			dsp.DSP_Dispay(editText1.getText().toString(),"GBK");
			break;
		case R.id.button2:
			dsp.DSP_Init();
			break;
		case R.id.button3:
			dsp.DSP_ClearScreen();
			break;
		case R.id.button4:
			dsp.DSP_ClearLine();
			break;
		case R.id.button5:
			devicePath=spinner1.getSelectedItem().toString();
        	dsp=new Dsp110(devicePath);	     	       
            Bundle bundle= new Bundle();
            bundle.putString("device_path",devicePath);
            bundle.putSerializable("baudrate", baudrate);
            Intent intent=new Intent();
            intent.putExtras(bundle);	    		
            intent.setClass(Dsp110Demo.this,SetSerialPort.class);
            startActivityForResult(intent,0x00);
            break;
		case R.id.button6:
			this.finish();
			break;
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
         		mSharedPreferences.edit().putString("baudrate",data.getStringExtra("baudrate")).commit();
         		baudrate= ComIO.Baudrate.valueOf(mSharedPreferences.getString("baudrate", "BAUD_9600"));
			}
		}
	}

}
