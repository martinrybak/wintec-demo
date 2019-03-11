package cn.wintec.wtandroiddemo.multiinone;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import cn.wintec.wtandroiddemo.R;
import cn.wintec.wtandroidjar.UsbIO;
import cn.wintec.wtandroidjar.multiinone.Rfid;

public class RFID_DEV extends Activity implements OnClickListener {

	Spinner fm4_spinner;
	Button fm4_search;
	Button fm4_read;
	Button fm4_write;
	Button fm4_Initialize;
	Button fm4_Increase;
	Button fm4_Decrease;
	Button fm4_Balance;
	Button fm4_cancel;
	EditText fm4_accno;
	EditText fm4_keytext;
	EditText fm4_keytext1;
	EditText fm4_blocktext1;
	EditText fm4_blocktext2;
	EditText fm4_blocktext3;
	EditText fm4_blocktext4;
	EditText fm4_value1;
	EditText fm4_value2;
	Spinner fm4_sector1;
	Spinner fm4_block1;
	Spinner fm4_sector2;
	Spinner fm4_block2;
	RadioGroup fm4_rdg1;
	RadioGroup fm4_rdg2;
	RadioButton fm4_Akey1, fm4_Bkey1, fm4_Akey2, fm4_Bkey2;
	public int fm4_index1;// /用于存储radiobutton选择的设备号 0 = A密钥 1= B密钥 读写数据
	public int fm4_index2;// /用于存储radiobutton选择的设备号 0 = A密钥 1= B密钥 钱包操作
	private String character[] = { "MifareOne", };
	private String character_sector[] = { "0", "1", "2", "3", "4", "5", "6",
			"7", "8", "9", "10", "11", "12", "13", "14", "15", };
	private String character_block[] = { "0", "1", "2", "3", };
	private List<String> list = new ArrayList<String>();
	private List<String> list_sector = new ArrayList<String>();
	private List<String> list_block = new ArrayList<String>();

	cn.wintec.wtandroidjar.multiinone.Rfid rfid=new cn.wintec.wtandroidjar.multiinone.Rfid(0x262D, 0x0208);
	
	@Override
	// //加入设置vid pid/////////////////////////////
	// ///////////////////////////
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multiinone_rfid);
		fm4_spinner = (Spinner) findViewById(R.id.card_type);
		fm4_sector1 = (Spinner) findViewById(R.id.card_sector1);
		fm4_sector2 = (Spinner) findViewById(R.id.card_sector2);
		fm4_block1 = (Spinner) findViewById(R.id.card_block1);
		fm4_block2 = (Spinner) findViewById(R.id.card_block2);

		fm4_search = (Button) findViewById(R.id.fmrfid_search);
		fm4_read = (Button) findViewById(R.id.fmrfid_readcard);
		fm4_write = (Button) findViewById(R.id.fmrfid_writecard);
		fm4_Initialize = (Button) findViewById(R.id.fmrfid_init);
		fm4_Increase =(Button)findViewById(R.id.fmrfid_increase);
		fm4_Decrease =(Button)findViewById(R.id.fmrfid_drease);
		fm4_Balance =(Button)findViewById(R.id.fmrfid_balance);
		fm4_cancel =(Button)findViewById(R.id.fmrfid_cancel);

		fm4_accno = (EditText) findViewById(R.id.fmrfid_idtext);
		fm4_keytext = (EditText) findViewById(R.id.fmrfid_keytext1);
		fm4_keytext1 = (EditText) findViewById(R.id.fmrfid_keytext2);
		fm4_blocktext1 = (EditText) findViewById(R.id.fmrfid_blocktext1);
		fm4_blocktext2 = (EditText) findViewById(R.id.fmrfid_blocktext2);
		fm4_blocktext3 = (EditText) findViewById(R.id.fmrfid_blocktext3);
		fm4_blocktext4 = (EditText) findViewById(R.id.fmrfid_blocktext4);
		fm4_value1 = (EditText) findViewById(R.id.fmrfid_valuetext1);
		fm4_value2 = (EditText) findViewById(R.id.fmrfid_valuetext2);

		fm4_Akey1 = (RadioButton) findViewById(R.id.rfid_akey1);
		fm4_Akey2 = (RadioButton) findViewById(R.id.rfid_akey2);
		fm4_Bkey1 = (RadioButton) findViewById(R.id.rfid_bkey1);
		fm4_Bkey2 = (RadioButton) findViewById(R.id.rfid_bkey2);
		fm4_rdg1 = (RadioGroup) findViewById(R.id.rfid_key1);
		fm4_rdg2 = (RadioGroup) findViewById(R.id.rfid_key2);
		fm4_search.setOnClickListener(this);
		fm4_read.setOnClickListener(this);
		fm4_write.setOnClickListener(this);
		fm4_Initialize.setOnClickListener(this);
		fm4_Increase.setOnClickListener(this);
		fm4_Decrease.setOnClickListener(this);
		fm4_Balance.setOnClickListener(this);
		fm4_cancel.setOnClickListener(this);
		
		for (int i = 0; i < character.length; i++) {
			list.add(character[i]);

		}
		for (int i = 0; i < character_sector.length; i++) {
			list_sector.add(character_sector[i]);

		}
		for (int i = 0; i < character_block.length; i++) {
			list_block.add(character_block[i]);

		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, list);
		ArrayAdapter<String> adapter_sector = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, list_sector);
		ArrayAdapter<String> adapter_block = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, list_block);
		fm4_spinner.setAdapter(adapter);
		fm4_sector1.setAdapter(adapter_sector);
		fm4_sector2.setAdapter(adapter_sector);
		fm4_block1.setAdapter(adapter_block);
		fm4_block2.setAdapter(adapter_block);
		fm4_sector2.setSelection(1);

		fm4_rdg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int CheckedId) {
				// TODO Auto-generated method stub
				if (CheckedId == fm4_Akey1.getId())
					fm4_index1 = 0;
				else if (CheckedId == fm4_Bkey1.getId())
					fm4_index1 = 1;
				else
					fm4_index1 = 0;
			}
		});
		fm4_rdg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int CheckedId) {
				// TODO Auto-generated method stub
				if (CheckedId == fm4_Akey2.getId())
					fm4_index2 = 0;
				else if (CheckedId == fm4_Bkey2.getId())
					fm4_index2 = 1;
				else
					fm4_index2 = 0;
			}
		});
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		boolean flag = false;
		int mode;
		int[] key = new int[12];
		int blockindex;
		int[] cardData = new int[128];
		int[] cardDatalen = new int[2];
		int cardDatalen1;
		int[] tmpmoney = new int[4];
		long tmpmoney1;
		String tmpstr = new String();
		switch (arg0.getId()) {
		case R.id.fmrfid_search:
			int[] configData = new int[9 * 1024];
			// int[] tmpconfig= new int [128];

			flag = rfid.protoAntenna(false);
			fm4_accno.setText("");
			if (flag != true) {
				Toast.makeText(RFID_DEV.this,
						getString(R.string.antenna_close_fail), Toast.LENGTH_SHORT)
						.show(); // /关闭天线失败
				break;
			}

			flag = rfid.protoType(0x41);
			if (flag != true) {

				Toast.makeText(RFID_DEV.this,
						getString(R.string.set_type_fail), Toast.LENGTH_SHORT)
						.show(); // /设置模块工作于typeA 模式下失败
				break;
			}
			flag = rfid.protoAntenna(true);
			if (flag != true) {
				Toast.makeText(RFID_DEV.this,
						getString(R.string.antenna_open_fail), Toast.LENGTH_SHORT)
						.show(); // /打开天线失败
				break;
			}
			SystemClock.sleep(100);
			flag = rfid.protoRequest(1, configData);
			if (flag != true) {
				Toast.makeText(RFID_DEV.this,
						getString(R.string.request_fail), Toast.LENGTH_SHORT)
						.show(); // /获取卡片类型
				break;
			}
			SystemClock.sleep(100);
			rfid.protoanticoll(4, configData);
			if (flag != true) {
				Toast.makeText(RFID_DEV.this,
						getString(R.string.anticol_fail), Toast.LENGTH_SHORT)
						.show(); // /防冲撞并获取卡号失败
				break;

			}
			SystemClock.sleep(100);
			flag = rfid.protoselect(4, configData);
			if (flag != true) {
				Toast.makeText(RFID_DEV.this,
						getString(R.string.select_rfid_fail), Toast.LENGTH_SHORT)
						.show(); // /选择卡片失败
				break;

			}
			// System.arraycopy(configData, 14, configData, 0, configDataLen-4);

			tmpstr = UsbIO.convert2String(configData, 4);
			fm4_accno.setText(tmpstr);
			Toast.makeText(RFID_DEV.this, getString(R.string.seach_rfid_success),
					Toast.LENGTH_SHORT).show(); // /寻卡成功
			break;

		case R.id.fmrfid_readcard:
			// /验证密钥

			// String tmpstr;

			if (fm4_index1 == 1)
				mode = 0x42;
			else
				mode = 0x41;
			blockindex = (int) (fm4_sector1.getSelectedItemId() * 4 + fm4_block1
					.getSelectedItemId());

			key = UsbIO.convertStringToIntArr(fm4_keytext.getText().toString());
			flag = rfid.protoauthentication(mode, blockindex, 6, key);
			if (flag != true) {
				Toast.makeText(RFID_DEV.this,
						getString(R.string.authentication_fail), Toast.LENGTH_SHORT)
						.show(); // /密钥验证失败
				break;

			}
			flag = rfid.protoread(blockindex, cardDatalen, cardData);
			if (flag != true) {
				Toast.makeText(RFID_DEV.this,
						getString(R.string.read_rfid_fail), Toast.LENGTH_SHORT)
						.show(); // /读卡失败
				break;

			}
			fm4_blocktext1.setText("");
			fm4_blocktext2.setText("");
			fm4_blocktext3.setText("");
			fm4_blocktext4.setText("");
			tmpstr = UsbIO.convert2String(cardData, cardDatalen[0]);
			
			if(fm4_block1.getSelectedItemId()==1)
				fm4_blocktext2.setText(tmpstr);
			else if(fm4_block1.getSelectedItemId()==2)
				fm4_blocktext3.setText(tmpstr);
			else if(fm4_block1.getSelectedItemId()==3)
				fm4_blocktext4.setText(tmpstr);
			else
				fm4_blocktext1.setText(tmpstr);
			Toast.makeText(RFID_DEV.this, getString(R.string.read_rfid_success),
					Toast.LENGTH_SHORT).show(); // /读卡成功
			break;

		case R.id.fmrfid_writecard:
			if (fm4_index1 == 1)
				mode = 0x42;
			else
				mode = 0x41;
			blockindex = (int) (fm4_sector1.getSelectedItemId() * 4 + fm4_block1
					.getSelectedItemId());

			key = UsbIO.convertStringToIntArr(fm4_keytext.getText().toString());
			flag = rfid.protoauthentication(mode, blockindex, 6, key);
			if (flag != true) {
				Toast.makeText(RFID_DEV.this,
						getString(R.string.authentication_fail), Toast.LENGTH_SHORT)
						.show(); // /验证密钥失败
				break;

			}

			cardDatalen1 = 16;
			for (int i = 0; i < cardData.length; i++)
				cardData[i] = 0x00;
			if(fm4_block1.getSelectedItemId()==1)
			{
				if (fm4_blocktext2.getText().length() > 0)
					cardData = UsbIO.convertStringToIntArr(fm4_blocktext2.getText()
							.toString());	
			}
			else if(fm4_block1.getSelectedItemId()==2)
			{
				if (fm4_blocktext3.getText().length() > 0)
					cardData = UsbIO.convertStringToIntArr(fm4_blocktext3.getText()
							.toString());	
				
			}
			else if(fm4_block1.getSelectedItemId()==2)
			{
				if (fm4_blocktext4.getText().length() > 0)
					cardData = UsbIO.convertStringToIntArr(fm4_blocktext4.getText()
							.toString());	
				
			}
			else
			{
				if (fm4_blocktext1.getText().length() > 0)
					cardData = UsbIO.convertStringToIntArr(fm4_blocktext1.getText()
							.toString());	
				
			}
			
			flag = rfid.protowrite(blockindex, cardDatalen1, cardData);
			if (flag != true) {
				Toast.makeText(RFID_DEV.this,
						getString(R.string.write_rfid_fail), Toast.LENGTH_SHORT)
						.show(); // /写卡失败
				break;

			}
			Toast.makeText(RFID_DEV.this, getString(R.string.write_rfid_success),
					Toast.LENGTH_SHORT).show(); // /写卡成功
			break;
		case R.id.fmrfid_init:

			if (fm4_value1.getText().length() <= 0)
				break;
			tmpmoney1 = Long.parseLong(fm4_value1.getText().toString());
			tmpmoney[0] = (int) tmpmoney1 % 256;
			tmpmoney[1] = (int) (tmpmoney1 / 256) % 256;
			tmpmoney[2] = (int) (tmpmoney1 / (256 * 256)) % 256;
			tmpmoney[3] = (int) (tmpmoney1 / (256 * 256 * 256)) % 256;
			tmpstr = UsbIO.convert2String(tmpmoney, 4);
			fm4_value2.setText(tmpstr);
			//Toast.makeText(RFID_DEV.this, getString(R.string.read_success),
			//		Toast.LENGTH_SHORT).show(); // /
			if (fm4_index2 == 1)
				mode = 0x42;
			else
				mode = 0x41;

			blockindex = (int) (fm4_sector2.getSelectedItemId() * 4 + fm4_block2
					.getSelectedItemId());

			key = UsbIO
					.convertStringToIntArr(fm4_keytext1.getText().toString());
			flag = rfid.protoauthentication(mode, blockindex, 6, key);
			if (flag != true) {
				Toast.makeText(RFID_DEV.this,
						getString(R.string.authentication_fail), Toast.LENGTH_SHORT)
						.show(); // /验证密钥失败
				break;

			}
			flag = rfid.protoInitialize(blockindex, tmpmoney);
			if (flag != true) {
				Toast.makeText(RFID_DEV.this,
						getString(R.string.initialize_fail), Toast.LENGTH_SHORT)
						.show(); // /初始化钱包失败
				break;

			}
			Toast.makeText(RFID_DEV.this, getString(R.string.initialize_success),
					Toast.LENGTH_SHORT).show(); // /初始化钱包成功

			break;
		case R.id.fmrfid_increase:
			if (fm4_value1.getText().length() <= 0)
				break;
			tmpmoney1 = Long.parseLong(fm4_value1.getText().toString());
			tmpmoney[0] = (int) tmpmoney1 % 256;
			tmpmoney[1] = (int) (tmpmoney1 / 256) % 256;
			tmpmoney[2] = (int) (tmpmoney1 / (256 * 256)) % 256;
			tmpmoney[3] = (int) (tmpmoney1 / (256 * 256 * 256)) % 256;
			tmpstr = UsbIO.convert2String(tmpmoney, 4);
			fm4_value2.setText(tmpstr);
			//Toast.makeText(RFID_DEV.this, getString(R.string.read_success),
			//		Toast.LENGTH_SHORT).show(); // 
			if (fm4_index2 == 1)
				mode = 0x42;
			else
				mode = 0x41;

			blockindex = (int) (fm4_sector2.getSelectedItemId() * 4 + fm4_block2
					.getSelectedItemId());

			key = UsbIO
					.convertStringToIntArr(fm4_keytext1.getText().toString());
			flag = rfid.protoauthentication(mode, blockindex, 6, key);
			if (flag != true) {
				Toast.makeText(RFID_DEV.this,
						getString(R.string.authentication_fail), Toast.LENGTH_SHORT)
						.show(); // 验证密钥失败
				break;

			}
			flag = rfid.protoIncrease(blockindex, tmpmoney);
			if (flag != true) {
				Toast.makeText(RFID_DEV.this,
						getString(R.string.increase_fail), Toast.LENGTH_SHORT)
						.show(); // /钱包充值失败
				break;

			}
			Toast.makeText(RFID_DEV.this, getString(R.string.increase_success),
					Toast.LENGTH_SHORT).show(); // 钱包充值成功

			break;
		case R.id.fmrfid_drease:
			if (fm4_value1.getText().length() <= 0)
				break;
			tmpmoney1 = Long.parseLong(fm4_value1.getText().toString());
			tmpmoney[0] = (int) tmpmoney1 % 256;
			tmpmoney[1] = (int) (tmpmoney1 / 256) % 256;
			tmpmoney[2] = (int) (tmpmoney1 / (256 * 256)) % 256;
			tmpmoney[3] = (int) (tmpmoney1 / (256 * 256 * 256)) % 256;
			tmpstr = UsbIO.convert2String(tmpmoney, 4);
			fm4_value2.setText(tmpstr);
			//Toast.makeText(RFID_DEV.this, getString(R.string.read_success),
			//		Toast.LENGTH_SHORT).show(); // 
			if (fm4_index2 == 1)
				mode = 0x42;
			else
				mode = 0x41;

			blockindex = (int) (fm4_sector2.getSelectedItemId() * 4 + fm4_block2
					.getSelectedItemId());

			key = UsbIO
					.convertStringToIntArr(fm4_keytext1.getText().toString());
			flag = rfid.protoauthentication(mode, blockindex, 6, key);
			if (flag != true) {
				Toast.makeText(RFID_DEV.this,
						getString(R.string.authentication_fail), Toast.LENGTH_SHORT)
						.show(); // 验证密钥失败
				break;

			}
			flag = rfid.protoDecrease(blockindex, tmpmoney);
			if (flag != true) {
				Toast.makeText(RFID_DEV.this,
						getString(R.string.decrease_fail), Toast.LENGTH_SHORT)
						.show(); // /钱包扣款失败
				break;

			}
			Toast.makeText(RFID_DEV.this, getString(R.string.decrease_success),
					Toast.LENGTH_SHORT).show(); // 钱包扣款成功

			break;
		case R.id.fmrfid_balance:
			tmpmoney1=0;
			fm4_value1.setText("");
			fm4_value2.setText("");
			for(int i=0;i<4;i++)
			{
				tmpmoney[i]=0;
			}
			if (fm4_index2 == 1)
				mode = 0x42;
			else
				mode = 0x41;

			blockindex = (int) (fm4_sector2.getSelectedItemId() * 4 + fm4_block2
					.getSelectedItemId());
			key = UsbIO
					.convertStringToIntArr(fm4_keytext1.getText().toString());
			flag = rfid.protoauthentication(mode, blockindex, 6, key);
			if (flag != true) {
				Toast.makeText(RFID_DEV.this,
						getString(R.string.authentication_fail), Toast.LENGTH_SHORT)
						.show(); // 密钥验证失败
				break;

			}
			flag = rfid.protoBalance(blockindex, tmpmoney);
			if (flag != true) {
				Toast.makeText(RFID_DEV.this,
						getString(R.string.balance_fail), Toast.LENGTH_SHORT)
						.show(); // 查询钱包余额失败
				break;

			}
			tmpstr = UsbIO.convert2String(tmpmoney, 4);
			fm4_value2.setText(tmpstr);
			tmpmoney1=tmpmoney[0]+tmpmoney[1]*256+tmpmoney[2]*256*256+tmpmoney[3]*256*256*256;
			tmpstr=String.valueOf(tmpmoney1);
			fm4_value1.setText(tmpstr);
			Toast.makeText(RFID_DEV.this, getString(R.string.balance_success),
					Toast.LENGTH_SHORT).show(); // /防冲撞并获取卡号成功
			break;
			
		case R.id.fmrfid_cancel:
			
			finish();
			break;
		default:
			break;
		}
		return;

	}

}
