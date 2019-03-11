package cn.wintec.wtandroiddemo.multiinone;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import cn.wintec.wtandroiddemo.R;


public class MainActivity extends Activity implements OnClickListener {
	public static String TAG = "test";
	// 界面元素
	Button fm1_ok, fm1_cancel;
	RadioGroup fm1_rdg1;
	RadioButton fm1_rdmsr, fm1_rdpcsc, fm1_rdrf;
	public int fm1_index;// /用于存储radiobutton选择的设备号 0 = msr 1= pcsc 2= rfid

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		fm1_index = 0;
		super.onCreate(savedInstanceState);
		
		// ////////////////////////////////////////
		setContentView(R.layout.activity_multiinone_main);

		fm1_ok = (Button) findViewById(R.id.fm1_ok);
		fm1_cancel = (Button) findViewById(R.id.fm1_cancel);
		fm1_ok.setOnClickListener(this);
		fm1_cancel.setOnClickListener(this);
		fm1_rdg1 = (RadioGroup) findViewById(R.id.rgdev1);
		fm1_rdmsr = (RadioButton) findViewById(R.id.msrdev);
		fm1_rdpcsc = (RadioButton) findViewById(R.id.pcscdev);
		fm1_rdrf = (RadioButton) findViewById(R.id.rfiddev);
		fm1_rdg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int CheckedId) {
				// TODO Auto-generated method stub
				if (CheckedId == fm1_rdmsr.getId())
					fm1_index = 0;
				else if (CheckedId == fm1_rdpcsc.getId())
					fm1_index = 1;
				else if (CheckedId == fm1_rdrf.getId())
					fm1_index = 2;
				else
					fm1_index = 0;
			}
		});

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fm1_ok:
			// if()
			// Log.d(TAG, "Start to recorder video\n");
			if (fm1_index == 1) {
//				Intent intent_pcsc = new Intent();
//				intent_pcsc.setClass(MainActivity.this, PCSC_DEV.class);
//				startActivity(intent_pcsc);
			} else if (fm1_index == 2) {
				Intent intent_rfid = new Intent();
				intent_rfid.setClass(MainActivity.this, RFID_DEV.class);
				startActivity(intent_rfid);
			}
			else if(fm1_index ==0)
			{
				Intent intent_msr = new Intent();
				intent_msr.setClass(MainActivity.this, MsrActivity.class);
				startActivity(intent_msr);
			}
			break;
		case R.id.fm1_cancel:
//			// System.exit(0);
//			RM2091Application.getInstance().exit();
			this.finish();
			break;
		default:
			break;
		}
	}

}
