package cn.wintec.wtandroiddemo;


import cn.wintec.wtandroidjar.ComIO;
import cn.wintec.wtandroidjar.Msr;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MsrDemo extends Activity{
	
	Msr msr=null;
	String devicePath;///dev/ttySAC3";	
	ComIO.Baudrate baudrate;
	Thread rxThread = null;
	Handler myHandler; 
	static final int MSG_ADD_RX_DATA        = 0;
	private Spinner Spinnerserport;	
	private EditText editTxtTrackstart1,editTxtTrackstart2,editTxtTrackstart3,editTxtTrackend1,editTxtTrackend2,editTxtTrackend3;
	private CheckBox cb1,cb2,cb3,cbBeep,cbIbm;
	boolean isread=true;
	byte enterTrack1,enterTrack2,enterTrack3;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msr);
	        
        //Get the serial parameters saved
        SharedPreferences mSharedPreferences=getPreferences(MODE_PRIVATE);
        devicePath=mSharedPreferences.getString("devicePath", "/dev/ttySAC3");
		baudrate= ComIO.Baudrate.valueOf(mSharedPreferences.getString("baudrate", "BAUD_9600"));
        
		msr=new Msr(devicePath,baudrate);	
	        
        GetControl();	  
	        
        Spinnerserport.setSelection(Integer.parseInt(devicePath.substring(11)),true);
        Spinnerserport.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				devicePath=Spinnerserport.getSelectedItem().toString();
				msr=new Msr(devicePath,baudrate);	
				SharedPreferences mSharedPreferences=getPreferences(MODE_PRIVATE);
         		mSharedPreferences.edit().putString("devicePath", devicePath).commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        });
	        
        try {
			LoadMsr();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
       
        Button button_back = (Button)findViewById(R.id.button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	isread=false;
            	rxThread.interrupt();
            	msr.MSR_Close();
            	MsrDemo.this.finish();	  
            }
        });
        
        Button button_read=(Button)findViewById(R.id.button_read);
        button_read.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View arg0) {
				isread=true;
				onStart();				
			}
		});
         
        final EditText textviewRX = (EditText)findViewById(R.id.editText_Cardinfo);	
	  	  
        Button button_load=(Button)findViewById(R.id.button_load);
        button_load.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View v) {
				isread=false;
				byte [] content=new byte[300];
				int length = 0;
				try {
					Thread.sleep(150);
					length = msr.MSR_GetConfiguration(content);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(content[1]!=0x30)
				{
					return ;
				}
				 String content_ = new String();					 
				  for(int i = 0; i < length; ++i)
                 {
                     content_ += String.format("%02X ", (int)content[i] & 0xFF);                        
                 }
                 textviewRX.setText(content_);                 
			}
		});
        
        Button buttonSetting = (Button)findViewById(R.id.button_setting);
        buttonSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {	
            	devicePath=Spinnerserport.getSelectedItem().toString();
            	msr=new Msr(devicePath);	     	      
                Bundle bundle= new Bundle();
                bundle.putString("device_path",devicePath);
                bundle.putSerializable("baudrate", baudrate);
                Intent intent=new Intent();
                intent.putExtras(bundle);	    		
                intent.setClass(MsrDemo.this,SetSerialPort.class);
                startActivityForResult(intent,0x00);
            }
        });	 
	        
        Button button_Save=(Button)findViewById(R.id.button_Save);
        button_Save.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View v) {
				isread=false;
				byte [] content=new byte[300];
				int length = 0;
				length = msr.MSR_GetConfiguration(content);
				byte [] newContent=new byte[length+1];
				if(content[1]!=0x30)
				{
					return ;
				}else
				{		
					content[1]=0x2A;
					char start1=editTxtTrackstart1.getText().charAt(0); 
					char end1=editTxtTrackend1.getText().charAt(0);
					content[6]=(byte)Integer.parseInt(Integer.toHexString((int)start1),16);//The first track starts identifier 第一磁道开始标识符
					content[7]=(byte)Integer.parseInt(Integer.toHexString((int)end1),16);//The first end of track identifier第一磁道结束标识符
					char start2=editTxtTrackstart2.getText().charAt(0);
					char end2=editTxtTrackend2.getText().charAt(0);
					content[8]=(byte)Integer.parseInt(Integer.toHexString((int)start2),16);//The second track begins identifier第二磁道开始标识符
					content[9]=(byte)Integer.parseInt(Integer.toHexString((int)end2),16);//The second end of track identifier第二磁道结束标识符
					
					char start3=editTxtTrackstart3.getText().charAt(0);
					char end3=editTxtTrackend3.getText().charAt(0);
					content[10]=(byte)Integer.parseInt(Integer.toHexString((int)start3),16);//The third track starts identifier第三磁道开始标识符
					content[11]=(byte)Integer.parseInt(Integer.toHexString((int)end3),16);//The third end of track identifier第三磁道结束标识符
					if(cb1.isChecked())
					{
						content[13]=0x35;
					}else
					{
						content[13]=0x31;
					}
					if(cb2.isChecked())
					{								
						content[14]=0x36;
					}else
					{
						content[14]=0x32;
					}
					if(cb3.isChecked())
					{							
						content[15]=0x37;
					}
					else
					{
						content[15]=0x33;
						
					}
					
					if(cbBeep.isChecked())
					{
						content[12]=(byte) (content[12]|0x01);
					}
					if(cbIbm.isChecked())
					{
						content[12]=(byte) (content[12]|0x04);
					}
					newContent[0]=content[0];
					newContent[1]=0x2A;
					newContent[2]=content[2];
					newContent[3]=(byte) (content[3]+0x01);
					newContent[4]=0x7E;
					for(int i=5;i<length+1;i++)
					{
						newContent[i]=content[i-1];
					}
					msr.MSR_Write(newContent);
					byte [] result=new byte[3];
					msr.read(result, 0,3,500);
					if(result[1]!=0x30)
					{
						Toast.makeText(MsrDemo.this, "save failed！", Toast.LENGTH_SHORT).show();
						return ;
					}else
					{
						Toast.makeText(MsrDemo.this, "save successed！", Toast.LENGTH_SHORT).show();							
					}
				}
			}
		});
	        
        Button button_Clear=(Button)findViewById(R.id.button_Clear);
        button_Clear.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				textviewRX.setText("Cardinfo...");
			}
		});
        
        myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_ADD_RX_DATA) {	                  
                	String content_=msg.getData().getString("content");
                    textviewRX.setText(content_+"\r\n");	                    
                }
            }
        };
	 }

	 /**
	  * get and display msr configuration
	 * @throws InterruptedException 
	  */
	private void LoadMsr() throws InterruptedException {
		byte[] content=new byte[100];
		msr.MSR_GetConfiguration(content);
		if(content[1]!=0x30)
		{
			return ;
		}
      // short i=99;char c=(char)i;System.out.println("output:"+c); 
		char start1=(char)content[6];
		editTxtTrackstart1.setText(String.valueOf(start1));
		char start2=(char)content[8];
		editTxtTrackstart2.setText(String.valueOf(start2));
		char start3=(char)content[10];
		editTxtTrackstart2.setText(String.valueOf(start3));
		char end1=(char)content[7];
		editTxtTrackend1.setText(String.valueOf(end1));
		char end2=(char)content[9];
		editTxtTrackend2.setText(String.valueOf(end2));
		char end3=(char)content[11];
		editTxtTrackend3.setText(String.valueOf(end3));
		
		enterTrack1=content[13];
		enterTrack2=content[14];
		enterTrack3=content[15];
		if(content[13]==0x35)
		{
			cb1.setChecked(true);
		}
		if(content[14]==0x36)
		{
			cb2.setChecked(true);
		}
		if(content[15]==0x37)
		{
			cb3.setChecked(true);
		}
		if(((byte)content[12]&0x01)!=0x00)
		{
			cbBeep.setChecked(true);
		} 
		if((byte) (content[12]&0x04)!=0x00)
		{
			cbIbm.setChecked(true);
		}
	}

	private void GetControl() {
		editTxtTrackstart1=(EditText)findViewById(R.id.editTxtTrackstart1);
		editTxtTrackstart2=(EditText)findViewById(R.id.editTxtTrackstart2);
		editTxtTrackstart3=(EditText)findViewById(R.id.editTxtTrackstart3);
		editTxtTrackend1=(EditText)findViewById(R.id.editTxtTrackend1);
		editTxtTrackend2=(EditText)findViewById(R.id.editTxtTrackend2);
		editTxtTrackend3=(EditText)findViewById(R.id.editTxtTrackend3);
		cb1=(CheckBox)findViewById(R.id.cbenter1);
		cb2=(CheckBox)findViewById(R.id.cbenter2);
		cb3=(CheckBox)findViewById(R.id.cbenter3);
		cbBeep=(CheckBox)findViewById(R.id.cbBeep);
		cbIbm=(CheckBox)findViewById(R.id.cbSubIBM);
		Spinnerserport=(Spinner)findViewById(R.id.spinner_Serport);
	}
	 
	protected void onStart() {
		rxThread = new Thread(new Runnable() {	            	            
			public void run() {	                               
                while (isread) {	                      
                    String str=msr.MSR_GetTrackData(enterTrack1,enterTrack2,enterTrack3);
                    if(str.length()>0){
                    	 Bundle bundle = new Bundle();	 
                    	 bundle.putString("content", str); 
                    	 Message message = new Message();
                         message.setData(bundle);
                         message.what = MSG_ADD_RX_DATA;
                         myHandler.sendMessage(message);
                    }
                }
                Log.v("Core Test", "UART RX thread finished.");
            }
        });	        
        rxThread.start();
        super.onStart();
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
