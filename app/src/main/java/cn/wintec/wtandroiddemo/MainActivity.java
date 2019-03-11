package cn.wintec.wtandroiddemo;


import cn.wintec.wtandroiddemo.multiinone.RFID_DEV;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button ButtonDrw=(Button)findViewById(R.id.ButtonDrw);
		ButtonDrw.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MainActivity.this,
						DrwDemo.class));
			}
		});
		
		Button btnusbprint=(Button)findViewById(R.id.ButtonUsbPrint);
		btnusbprint.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent =new Intent(MainActivity.this, UsbPrinterDemo.class);
				startActivity(intent);
			}
		});
		Button btnprint=(Button)findViewById(R.id.ButtonPrint);
		btnprint.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent =new Intent(MainActivity.this, PrinterDemo.class);
				startActivity(intent);
			}
		});
		
		Button btndsp320=(Button)findViewById(R.id.ButtonDsp320);
		btndsp320.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent =new Intent(MainActivity.this, DspPosDemo.class);
				startActivity(intent);
			}
		});
		
		Button btndsp110=(Button)findViewById(R.id.ButtonDsp110);
		btndsp110.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent =new Intent(MainActivity.this, Dsp110Demo.class);
				startActivity(intent);
			}
		});
		
		Button btnIbutton=(Button)findViewById(R.id.ButtonIbutton);
		btnIbutton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent =new Intent(MainActivity.this, IButtonDemo.class);
				startActivity(intent);
			}
		});
		
		Button btnquit=(Button)findViewById(R.id.ButtonQuit);
		btnquit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.this.finish();
			}
		});
		
	/*	Button ButtonMsr=(Button)findViewById(R.id.ButtonMsr);
		ButtonMsr.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent =new Intent(MainActivity.this, MsrDemo.class);
				startActivity(intent);
			}
		});*/
		
		Button ButtonIFID=(Button)findViewById(R.id.ButtonIFID);
		ButtonIFID.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent =new Intent(MainActivity.this, RfidDemo.class);
				startActivity(intent);
			}
		});
		
		Button ButtonMultiInOne=(Button)findViewById(R.id.ButtonMultiInOne);
		ButtonMultiInOne.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent =new Intent(MainActivity.this,cn.wintec.wtandroiddemo.multiinone.MainActivity.class);
				startActivity(intent);
			}
		});
		Button ButtonScale=(Button)findViewById(R.id.ButtonScale);
		ButtonScale.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent =new Intent(MainActivity.this,ScaleDemo.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
