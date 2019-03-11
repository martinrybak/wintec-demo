package cn.wintec.wtandroiddemo;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;


import cn.wintec.CpuCardRead.CpuCardReader;
import cn.wintec.CpuCardRead.TradeInfo;
import cn.wintec.CpuCardRead.UserInfo;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * cpu card demo
 * 
 * @author Administrator
 * 
 */
public class CpuCardDemo extends Activity {

	CpuCardReader cpuCardReader = new CpuCardReader();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cpucard);

		Button btnback = (Button) findViewById(R.id.btnBack);
		btnback.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CpuCardDemo.this.finish(); 
			}
		});


		Button btnReadUserInfo = (Button) findViewById(R.id.btnReadUserInfo);
		btnReadUserInfo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (cpuCardReader.SearchCards() == 0x08)// cpu card
				{
					EditText edTxt = (EditText) findViewById(R.id.editText1);
					EditText edUserCode = (EditText) findViewById(R.id.EditText01);
					EditText edCardType = (EditText) findViewById(R.id.EditText02);
					EditText edUserType = (EditText) findViewById(R.id.EditText03);
					EditText edCertCode = (EditText) findViewById(R.id.EditText04);
					EditText edPhoneNum = (EditText) findViewById(R.id.EditText19);
					EditText edCardSerial = (EditText) findViewById(R.id.EditText20);
					UserInfo userinfo = cpuCardReader.readCardUserInfo();
					edTxt.setText(userinfo.userName);
					edUserCode.setText(userinfo.userID);
					edCardType.setText(userinfo.cardType);
					edUserType.setText(userinfo.userType);
					edCertCode.setText(userinfo.certSerial);
					edPhoneNum.setText(userinfo.phoneNum);
					edCardSerial.setText(userinfo.cardSerial);
				} else {
					Toast.makeText(CpuCardDemo.this, "Card is invalid!",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		Button btnWriteUserInfo = (Button) findViewById(R.id.btnWriteUserInfo);
		btnWriteUserInfo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				EditText edTxt = (EditText) findViewById(R.id.editText1);
				EditText edUserCode = (EditText) findViewById(R.id.EditText01);
				EditText edCardType = (EditText) findViewById(R.id.EditText02);
				EditText edUserType = (EditText) findViewById(R.id.EditText03);
				EditText edCertCode = (EditText) findViewById(R.id.EditText04);
				EditText edPhoneNum = (EditText) findViewById(R.id.EditText19);
				EditText edCardSerial = (EditText) findViewById(R.id.EditText20);
				UserInfo userinfo = new UserInfo();
				userinfo.userName = edTxt.getText().toString().trim();
				userinfo.userID = edUserCode.getText().toString().trim();
				userinfo.cardType = edCardType.getText().toString().trim();
				userinfo.userType = edUserType.getText().toString().trim();
				userinfo.certSerial = edCertCode.getText().toString().trim();
				userinfo.phoneNum = edPhoneNum.getText().toString().trim();
				userinfo.cardSerial = edCardSerial.getText().toString().trim();
				boolean rel = false;
				try {
					rel = cpuCardReader.writeCardUserInfo(userinfo);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if (rel)
					Toast.makeText(CpuCardDemo.this, "seccuss!",
							Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(CpuCardDemo.this, "false!",
							Toast.LENGTH_SHORT).show();
			}
		});

		Button btnReadSummary = (Button) findViewById(R.id.btnReadSummary);
		btnReadSummary.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SimpleDateFormat fromatter = new SimpleDateFormat("yyyyMMdd");
				String transDate = fromatter.format(new Date());
				byte[] count = new byte[] { 0 };
				transDate = cpuCardReader.readSummary(transDate, count);
				if (transDate != null)
					Toast.makeText(CpuCardDemo.this,
							"date:" + transDate + " count:" + count[0],
							Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(CpuCardDemo.this, "fail!",
							Toast.LENGTH_SHORT).show();
			}
		});

		Button btnReadTradeDetail = (Button) findViewById(R.id.btnReadTradeDetail);
		btnReadTradeDetail.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				TradeInfo tradeinfo = new TradeInfo();
				tradeinfo = cpuCardReader.readTranDetail((byte) 1);
				EditText edProCode = (EditText) findViewById(R.id.EditText05);
				EditText edBatchCode = (EditText) findViewById(R.id.EditText06);
				EditText edAreaCode = (EditText) findViewById(R.id.EditText12);
				EditText edVehicleCode = (EditText) findViewById(R.id.EditText08);
				EditText edWeight = (EditText) findViewById(R.id.EditText09);
				EditText edPrice = (EditText) findViewById(R.id.EditText10);
				EditText edCount = (EditText) findViewById(R.id.EditText11);
				EditText edFarm = (EditText) findViewById(R.id.EditText07);
				EditText edSalerId = (EditText) findViewById(R.id.EditText13);
				EditText edSalerName = (EditText) findViewById(R.id.EditText14);
				EditText edNodeId = (EditText) findViewById(R.id.EditText16);
				EditText edNodeName = (EditText) findViewById(R.id.EditText18);
				EditText edTransCode = (EditText) findViewById(R.id.EditText15);
				EditText edStatus = (EditText) findViewById(R.id.EditText17);

				edProCode.setText(tradeinfo.goodsCode);
				edBatchCode.setText(tradeinfo.batchCode);
				edAreaCode.setText(tradeinfo.areaCode);
				edVehicleCode.setText(tradeinfo.vehicleLicence);
				edWeight.setText(Double.toString(tradeinfo.goodsWeight));
				edPrice.setText(Double.toString(tradeinfo.goodsPrice));
				edCount.setText(Integer.toString(tradeinfo.goodsCount));
				edFarm.setText(tradeinfo.farmName);
				edSalerId.setText(tradeinfo.salerID);
				edSalerName.setText(tradeinfo.salerName);
				edNodeId.setText(tradeinfo.nodeID);
				edNodeName.setText(tradeinfo.nodeName);
				edTransCode.setText(tradeinfo.tranCode);
				edStatus.setText(tradeinfo.status == 0x30 ? "not registered"
						: tradeinfo.status == 0x31 ? "registered" : "Canceled");
				Toast.makeText(CpuCardDemo.this, "success!",
						Toast.LENGTH_SHORT).show();
			}
		});

		// 写交易信息事件
		Button btnWriteTradeDetail = (Button) findViewById(R.id.btnWriteTradeDetial);
		btnWriteTradeDetail.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				TradeInfo tradeinfo = new TradeInfo();
				EditText edProCode = (EditText) findViewById(R.id.EditText05);
				EditText edBatchCode = (EditText) findViewById(R.id.EditText06);
				EditText edAreaCode = (EditText) findViewById(R.id.EditText12);
				EditText edVehicleCode = (EditText) findViewById(R.id.EditText08);
				EditText edWeight = (EditText) findViewById(R.id.EditText09);
				EditText edPrice = (EditText) findViewById(R.id.EditText10);
				EditText edCount = (EditText) findViewById(R.id.EditText11);
				EditText edFarm = (EditText) findViewById(R.id.EditText07);
				EditText edSalerId = (EditText) findViewById(R.id.EditText13);
				EditText edSalerName = (EditText) findViewById(R.id.EditText14);
				EditText edNodeId = (EditText) findViewById(R.id.EditText16);
				EditText edNodeName = (EditText) findViewById(R.id.EditText18);
				EditText edTransCode = (EditText) findViewById(R.id.EditText15);
				EditText edStatus = (EditText) findViewById(R.id.EditText17);

				tradeinfo.goodsCode = edProCode.getText().toString();
				tradeinfo.batchCode = edBatchCode.getText().toString();
				tradeinfo.areaCode = edAreaCode.getText().toString();
				tradeinfo.vehicleLicence = edVehicleCode.getText().toString();
				tradeinfo.goodsWeight = Double.parseDouble(edWeight.getText()
						.toString());
				tradeinfo.goodsPrice = Double.parseDouble(edPrice.getText()
						.toString());
				tradeinfo.goodsCount = Integer.parseInt(edCount.getText()
						.toString());
				SimpleDateFormat fromatter = new SimpleDateFormat("yyyyMMdd");
				String transDate = fromatter.format(new Date());
				tradeinfo.transDate = transDate;
				tradeinfo.farmName = edFarm.getText().toString();
				tradeinfo.salerID = edSalerId.getText().toString();
				tradeinfo.salerName = edSalerName.getText().toString();
				tradeinfo.nodeID = edNodeId.getText().toString();
				tradeinfo.nodeName = edNodeName.getText().toString();
				tradeinfo.tranCode = edTransCode.getText().toString();
				tradeinfo.status = edStatus.getText().toString().trim()
						.equals("not registered") ? 0x30 : edStatus.getText().toString()
						.trim().equals("registered") ? 0x31 : (byte) 0x32;
				boolean rel = false;
				try {
					rel = cpuCardReader.appendTranDetail(tradeinfo);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if (rel)
					Toast.makeText(CpuCardDemo.this, "success!",
							Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(CpuCardDemo.this, "fail!",
							Toast.LENGTH_SHORT).show();
			}
		});

		Button btnClearUserInfo = (Button) findViewById(R.id.Button01);
		btnClearUserInfo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				EditText edTxt = (EditText) findViewById(R.id.editText1);
				EditText edUserCode = (EditText) findViewById(R.id.EditText01);
				EditText edCardType = (EditText) findViewById(R.id.EditText02);
				EditText edUserType = (EditText) findViewById(R.id.EditText03);
				EditText edCertCode = (EditText) findViewById(R.id.EditText04);
				EditText edPhoneNum = (EditText) findViewById(R.id.EditText19);
				EditText edCardSerial = (EditText) findViewById(R.id.EditText20);
				edTxt.setText("");
				edUserCode.setText("");
				edCardType.setText("");
				edUserType.setText("");
				edCertCode.setText("");
				edPhoneNum.setText("");
				edCardSerial.setText("");
				Toast.makeText(CpuCardDemo.this, "success!",
						Toast.LENGTH_SHORT).show();
			}
		});

		Button btnClearTradeDetail = (Button) findViewById(R.id.Button02);
		btnClearTradeDetail.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				EditText edProCode = (EditText) findViewById(R.id.EditText05);
				EditText edBatchCode = (EditText) findViewById(R.id.EditText06);
				EditText edAreaCode = (EditText) findViewById(R.id.EditText12);
				EditText edVehicleCode = (EditText) findViewById(R.id.EditText08);
				EditText edWeight = (EditText) findViewById(R.id.EditText09);
				EditText edPrice = (EditText) findViewById(R.id.EditText10);
				EditText edCount = (EditText) findViewById(R.id.EditText11);
				EditText edFarm = (EditText) findViewById(R.id.EditText07);
				EditText edSalerId = (EditText) findViewById(R.id.EditText13);
				EditText edSalerName = (EditText) findViewById(R.id.EditText14);
				EditText edNodeId = (EditText) findViewById(R.id.EditText16);
				EditText edNodeName = (EditText) findViewById(R.id.EditText18);
				EditText edTransCode = (EditText) findViewById(R.id.EditText15);
				EditText edStatus = (EditText) findViewById(R.id.EditText17);

				edProCode.setText("");
				edBatchCode.setText("");
				edAreaCode.setText("");
				edVehicleCode.setText("");
				edWeight.setText(Double.toString(0.00));
				edPrice.setText(Double.toString(0.00));
				edCount.setText(Integer.toString(0));
				edFarm.setText("");
				edSalerId.setText("");
				edSalerName.setText("");
				edNodeId.setText("");
				edNodeName.setText("");
				edTransCode.setText("");
				edStatus.setText("");
				Toast.makeText(CpuCardDemo.this, "success",
						Toast.LENGTH_SHORT).show();
			}
		});

	}

}