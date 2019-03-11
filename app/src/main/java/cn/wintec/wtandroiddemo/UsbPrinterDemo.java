package cn.wintec.wtandroiddemo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.wintec.wtandroidjar.ComIO;
import cn.wintec.wtandroidjar.Printer;
import cn.wintec.wtandroidjar.UsbPrinter;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class UsbPrinterDemo extends Activity {

//	 UsbPrinter printer = new UsbPrinter(1046,30016);//210 IDT800
//	UsbPrinter printer = new UsbPrinter(8401, 28679);
//	UsbPrinter printer = new UsbPrinter(8137,8214);
//	UsbPrinter printer = new UsbPrinter(4070, 33054);
	UsbPrinter printer=new UsbPrinter(1155, 30016);
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usbprinter);
		copyBmpFile();

		Button button_back = (Button) findViewById(R.id.button_back);
		button_back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		Button button_feedpaper = (Button) findViewById(R.id.button_feedpaper);
		button_feedpaper.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
//				printer.PRN_Selftest();
//				printer.PRN_PrintAndFeedLine(1);
//				printer.PRN_PrintBarCode(72, "A123132123");
//				printer.PRN_Print("aaaa", "gbk");
				printer.PRN_PrintAndFeedLine(3);
//				printer.PRN_PrintAndFeedLine(11);
				// printer.HalfCutPage();
			}
		});

		// print
		Button button_print = (Button) findViewById(R.id.button_print);
		button_print.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				printer.PRN_Print("Ji'nan Kuangshan wholesale market", "gbk");
				printer.PRN_Print("Trade confirmations < buyer associated >",
						"gbk");
				printer.PRN_Print("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", "gbk");
				printer.PRN_Print("bill no:3702564897456212000001", "gbk");
				printer.PRN_Print("saler:liangxiaohong", "gbk");
				printer.PRN_Print("buyer:zhanghanyang", "gbk");
				printer.PRN_Print("trade date:" + sdf.format(new Date()), "gbk");
				printer.PRN_Print("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", "gbk");
				printer.PRN_Print("trace code:37010000000000000001", "gbk");
				printer.PRN_Print("goods:ginger", "gbk");
				printer.PRN_Print("weight/qty:10.00", "gbk");
				printer.PRN_Print("price:15.25yuan/kg", "gbk");
				printer.PRN_Print("amount:152.5yuan", "gbk");
				printer.PRN_Print("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", "gbk");
				printer.PRN_PrintYuan();
				printer.PRN_Print("total amount:152.5yuan", "gbk");

				printer.PRN_PrintAndFeedLine(6);
				// print.PRN_HalfCutPage();

			}
		});

		// half cut page
		Button button_CutPage = (Button) findViewById(R.id.button_CutPage);
		button_CutPage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				printer.PRN_HalfCutPaper();
			}
		});

		// enable bold font
		Button button_BoldFont = (Button) findViewById(R.id.button_BoldFont);
		button_BoldFont.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				printer.PRN_EnableBoldFont(3);
			}
		});

		// disable bold font
		Button button_DisableBoldFont = (Button) findViewById(R.id.button_Disable);
		button_DisableBoldFont.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				printer.PRN_EnableBoldFont(0);
			}
		});

		// enable double width
		Button button_DoubleWidth = (Button) findViewById(R.id.button_DoubleWidth);
		button_DoubleWidth.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				printer.PRN_EnableFontDoubleWidth();
			}
		});
		Button button_OpenDrw = (Button) findViewById(R.id.button_opendrw);
		button_OpenDrw.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				printer.PRN_OpenDrw();
			}
		});
		// disable double widt
		Button button_DisableFontDoubleWidth = (Button) findViewById(R.id.button_DisableFontDoubleWidth);
		button_DisableFontDoubleWidth
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						printer.PRN_DisableFontDoubleWidth();
					}
				});

		// enable double height
		Button button_EnableFontDoubleHeight = (Button) findViewById(R.id.button_EnableFontDoubleHeight);
		button_EnableFontDoubleHeight
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						printer.PRN_EnableFontDoubleHeight();
					}
				});

		// disable double height
		Button button_DisableFontDoubleHeight = (Button) findViewById(R.id.button_DisableFontDoubleHeight);
		button_DisableFontDoubleHeight
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						printer.PRN_DisableFontDoubleHeight();
					}
				});

		// underline
		Button button_Underline = (Button) findViewById(R.id.button_Underline);
		button_Underline.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				printer.PRN_EnableFontUnderline();
			}
		});

		// disable underline
		Button button_disableUnderline = (Button) findViewById(R.id.button_disableUnderline);
		button_disableUnderline.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				printer.PRN_DisableFontUnderline();
			}
		});

		Button button_no = (Button) findViewById(R.id.button_DisableDoubleWidthandHeight);
		button_no.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				printer.PRN_DisableDoubleWidthandHeight();
			}
		});

		Button button_DoubleWidthandHeight = (Button) findViewById(R.id.button_DoubleWidthandHeight);
		button_DoubleWidthandHeight
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						printer.PRN_EnableDoubleWidthandHeight();
					}
				});

		Button button_PrintBmp = (Button) findViewById(R.id.button_PrintBmp);
		button_PrintBmp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// printer.PRN_PrintDotBitmap("file:///android_asset/Lion.bmp",
				// 0);
				
				int i;
				i=1;
				for(i=1;i<2;i++)
				{
			//	printer.PRN_PrintDotBitmap("/data"
			//			+ Environment.getDataDirectory().getAbsolutePath()
			//			+ "/cn.wintec.wtandroiddemo/Lion.bmp", 0);
			//	printer.PRN_Print("PrintDotbitmap() method,  Number:"+Integer.toString(i),"gbk");
			//	printer.PRN_PrintAndFeedLine(3);
	
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
				printer.PRN_PrintDotBitmap1("/data"
						+ Environment.getDataDirectory().getAbsolutePath()
						+ "/cn.wintec.wtandroiddemo/Lion.bmp", 0, 0);
				
				printer.PRN_Print("PrintDotbitmap1() method,  Number:"+Integer.toString(i),"gbk");
				printer.PRN_PrintAndFeedLine(3);
				//printer.PRN_CutPaper();
				}
			}
		});

		Button button_PrintBarcode = (Button) findViewById(R.id.button_PrintBarcode);
		button_PrintBarcode.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				byte coding =(byte)Integer.parseInt(11+"", 16);
				Log.e("13", coding+" ");
				printer.PRN_PrintBarCode(72, "123456789");
				printer.PRN_Print("123456789","gbk");
				printer.PRN_PrintAndFeedLine(3);
			}
		});
		Button button_PrintQRcode = (Button) findViewById(R.id.button_PrintQRcode);
		button_PrintQRcode.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub			
				printer.PRN_PrintQRCode("01234567890",6);
				printer.PRN_Print("01234567890","gbk");
				printer.PRN_PrintAndFeedLine(3);

			}
		});
		Button button_getStatus=(Button)findViewById(R.id.button_getStatus);
		button_getStatus.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub\
				printer.PRN_Init();
				if(printer.PRN_GetPaperStatus()==false)
				{
					Toast.makeText(UsbPrinterDemo.this, "缺纸", Toast.LENGTH_LONG).show();
				}
				else
				{
					Toast.makeText(UsbPrinterDemo.this, "不缺纸", Toast.LENGTH_LONG).show();
				}
			//	Toast.makeText(UsbPrinterDemo.this, (printer.PRN_GetPaperStatus()?"锟斤拷纸zhut":"锟斤拷纸zt"), Toast.LENGTH_LONG).show();
			}
			
		});

	}

	boolean copyBmpFile() {
		String bmpFileName = "Lion.bmp";
		String bmpFilePath = "/data"
				+ Environment.getDataDirectory().getAbsolutePath()
				+ "/cn.wintec.wtandroiddemo/";
		if (!new File(bmpFilePath + bmpFileName).exists()) {
			try {
				InputStream is = getBaseContext().getAssets().open(bmpFileName);
				OutputStream os = new FileOutputStream(bmpFilePath
						+ bmpFileName);

				byte[] buffer = new byte[1024];
				int count;
				while ((count = is.read(buffer)) > 0) {
					os.write(buffer, 0, count);
				}

				os.flush();
				os.close();
				is.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}
