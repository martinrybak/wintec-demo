package cn.wintec.wtandroiddemo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


import cn.wintec.wtandroidjar.ComIO;
import cn.wintec.wtandroidjar.ComIO.Baudrate;
import cn.wintec.wtandroidjar.Printer;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class PrinterDemo extends Activity {

	Printer printer = null;
	String devicePath;
	ComIO.Baudrate baudrate;
	private Spinner Spinnerserport;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.printer);

		copyBmpFile();

		// Get the serial parameters saved
		SharedPreferences mSharedPreferences = getPreferences(MODE_PRIVATE);
		devicePath = mSharedPreferences.getString("devicePath", "/dev/ttySAC1");
		baudrate = ComIO.Baudrate.valueOf(mSharedPreferences.getString(
				"baudrate", "BAUD_38400"));
		baudrate = ComIO.Baudrate.BAUD_38400;
		printer = new Printer(devicePath, baudrate);

		Spinnerserport = (Spinner) findViewById(R.id.spinner_Serport);
		Spinnerserport.setSelection(Integer.parseInt(devicePath.substring(11)),true);
		Spinnerserport
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						devicePath = Spinnerserport.getSelectedItem()
								.toString();
						printer = new Printer(devicePath, baudrate);
						SharedPreferences mSharedPreferences = getPreferences(MODE_PRIVATE);
						mSharedPreferences.edit()
								.putString("devicePath", devicePath).commit();
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});

		ComIO.Param param = new ComIO(devicePath).new Param();
		param.baudrate = baudrate;
		new ComIO(devicePath).setSerialBaudrate(param.baudrate);

		Button button_back = (Button) findViewById(R.id.button_back);
		button_back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				printer.PRN_Close();
				finish();
			}
		});

		Button button_feedpaper = (Button) findViewById(R.id.button_feedpaper);
		button_feedpaper.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				printer.PRN_PrintAndFeedLine(10);
			}
		});

		// print
		Button button_print = (Button) findViewById(R.id.button_print);
		button_print.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				printer.PRN_Print("Ji'nan Kuangshan wholesale market", "gbk");
				printer.PRN_Print("Trade confirmations < buyer associated >", "gbk");
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
				printer.PRN_Print("total amount:152.5yuan", "gbk");
//				printer.PRN_Print("weight(kg)   price(yuan/kg)", "gbk");
//				printer.PRN_Print("1kg       15yuan", "gbk");
//				printer.PRN_PrintBarCode(72, "86667889");
//				printer.PRN_Print("Date of production:"+sdf.format(new Date())+"Shelf life:"+sdf.format(new Date())+"", "gbk");
//				printer.PRN_Print("Total price：15yuan", "gbk");
//				printer.PRN_Feedinit();
//				print.PRN_HalfCutPage();

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
				 //printer.PRN_PrintDotBitmap("file:///android_asset/Lion.bmp",
				 //0);
			//	printer.PRN_PrintDotBitmap("/data" + Environment.getDataDirectory().getAbsolutePath() + "/cn.wintec.wtandroiddemo/Lion.bmp", 0);
			//	printer.PRN_PrintDotBitmap1("/data" + Environment.getDataDirectory().getAbsolutePath() + "/cn.wintec.wtandroiddemo/Lion.bmp", 0, 0);
			//	printer.PRN_PrintAndFeedLine(3);
				int i;
				i=1;
				for(i=1;i<2;i++)
				{
				printer.EnableRTSCTS();
				printer.PRN_PrintDotBitmap1("/data"
						+ Environment.getDataDirectory().getAbsolutePath()
						+ "/cn.wintec.wtandroiddemo/ngsu.jpg", 0,0);
				printer.PRN_PrintDotBitmap1("/data"
						+ Environment.getDataDirectory().getAbsolutePath()
						+ "/cn.wintec.wtandroiddemo/ngsu.jpg", 0,0);
				printer.PRN_PrintDotBitmap1("/data"
						+ Environment.getDataDirectory().getAbsolutePath()
						+ "/cn.wintec.wtandroiddemo/ngsu.jpg", 0,0);
				printer.PRN_PrintAndFeedLine(3);
				//printer.PRN_CutPaper();
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

		Button button_PrintBarcode = (Button) findViewById(R.id.button_PrintBarcode);
		button_PrintBarcode.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				printer.PRN_SetBarCodeHRI(1);
				printer.PRN_SetBarCodeWidth(2);
				printer.PRN_PrintBarCode(65, "012345678900");
				printer.PRN_Print("012345678900","gbk");
				printer.PRN_PrintAndFeedLine(3);

			}
		});
		Button button_PrintQRcode = (Button) findViewById(R.id.button_PrintQRcode);
		button_PrintQRcode.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub			
				printer.PRN_PrintQRCode("01234567890",5);
				printer.PRN_Print("01234567890","gbk");
				printer.PRN_PrintAndFeedLine(3);

			}
		});
		Button buttonSetting = (Button) findViewById(R.id.button_setting);
		buttonSetting.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				devicePath = Spinnerserport.getSelectedItem().toString();
				printer = new Printer(devicePath);
				Bundle bundle = new Bundle();
				bundle.putString("device_path", devicePath);
				bundle.putSerializable("baudrate", baudrate);
				Intent intent = new Intent();
				intent.putExtras(bundle);
				intent.setClass(PrinterDemo.this, SetSerialPort.class);
				startActivityForResult(intent, 0x00);
			}
		});
		
		Button btnPrintHebrew=(Button)findViewById(R.id.btnPrintHebrew);
		btnPrintHebrew.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				printer.PRN_DisableChinese();
				printer.PRN_SetCodePage(31);
				printer.PRN_Print("诪讘专讱 讗转 讛砖讬诪讜砖 讘诪讜爪专", "ISO8859-8");
				printer.PRN_PrintAndFeedLine(3);
				printer.PRN_CutPaper();

			}
		});
		
		Button btnPrintArabic=(Button)findViewById(R.id.btnPrintArabic);
		btnPrintArabic.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				printer.PRN_DisableChinese();
				printer.PRN_SetCodePage(22);
				printer.PRN_Print("鬲乇丨亘 亘丕爻鬲禺丿丕賲 丕賱賲賳鬲噩","ISO8859-6");
				printer.PRN_PrintAndFeedLine(3);
				printer.PRN_CutPaper();
			}
		});
		
		Button btnPrintTurkish=(Button)findViewById(R.id.btnPrintTurkish);
		btnPrintTurkish.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				printer.PRN_DisableChinese();
				printer.PRN_SetCodePage(90);
				printer.PRN_Print("脺r眉n眉n kullan谋m谋 a臒谋rl谋yor", "ISO8859-9");
				printer.PRN_PrintAndFeedLine(3);
				printer.PRN_CutPaper();
			}
		});
		
		Button btnPrintThai=(Button)findViewById(R.id.btnPrintThai);
		btnPrintThai.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				printer.PRN_DisableChinese();
				printer.PRN_SetCodePage(70);
				printer.PRN_Print("喔⑧复喔權笖喔掂笗喙夃腑喔權福喔编笟喔佮覆喔｀箖喔娻箟喔囙覆喔權競喔竾喔溹弗喔脆笗喔犩副喔撪笐喙�", "ISO8859-11");
				printer.PRN_PrintAndFeedLine(3);
				printer.PRN_CutPaper();
			}
		});
		
		Button btnPrintHebrewWT=(Button)findViewById(R.id.btnPrintHebrewWT);
		btnPrintHebrewWT.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				printer.PRN_DisableChinese();
				
				ComIO print = new ComIO("/dev/ttySAC1");
				print.setSerialBaudrate(ComIO.Baudrate.BAUD_38400);
				try {
					print.write(new byte[]{0x1B,0x74,0x20});
					print.write(("诪讘专讱 讗转 讛砖讬诪讜砖 讘诪讜爪专" + "\r\n").getBytes("ISO8859-8"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//printer.PRN_SetCodePage(0x20);
				//printer.PRN_Print("诪讘专讱 讗转 讛砖讬诪讜砖 讘诪讜爪专", "ISO8859-8");
				printer.PRN_PrintAndFeedLine(3);
				printer.PRN_CutPaper();

			}
		});
		
		Button btnPrintArabicWT=(Button)findViewById(R.id.btnPrintArabicWT);
		btnPrintArabicWT.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				printer.PRN_DisableChinese();
				printer.PRN_SetCodePage(0x21);//
				printer.PRN_Print("鬲乇丨亘 亘丕爻鬲禺丿丕賲 丕賱賲賳鬲噩","ISO8859-6");
				printer.PRN_PrintAndFeedLine(3);
				printer.PRN_CutPaper();
			}
		});
		
		Button btnPrintTurkishWT=(Button)findViewById(R.id.btnPrintTurkishWT);
		btnPrintTurkishWT.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				printer.PRN_DisableChinese();
				printer.PRN_SetCodePage(0x31);
				printer.PRN_Print("脺r眉n眉n kullan谋m谋 a臒谋rl谋yor", "ISO8859-9");
				printer.PRN_PrintAndFeedLine(3);
				printer.PRN_CutPaper();
			}
		});
		
		Button btnPrintThaiWT=(Button)findViewById(R.id.btnPrintThaiWT);
		btnPrintThaiWT.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				printer.PRN_DisableChinese();
				printer.PRN_SetCodePage(0x28);
				printer.PRN_Print("喔⑧复喔權笖喔掂笗喙夃腑喔權福喔编笟喔佮覆喔｀箖喔娻箟喔囙覆喔權競喔竾喔溹弗喔脆笗喔犩副喔撪笐喙�", "ISO8859-11");
				printer.PRN_PrintAndFeedLine(3);
				printer.PRN_CutPaper();
			}
		});
		
		Button btnPrintVietnamWT=(Button)findViewById(R.id.btnPrintVietnamWT);
		btnPrintVietnamWT.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				printer.PRN_DisableChinese();
				printer.PRN_SetCodePage(27);
				printer.PRN_Print("Ch脿o m峄玭g, s峄� d峄g s岷 ph岷﹎ 膽贸.", "vietnam");
//				printer.PRN_Print("膽i峄乽 膽贸 c贸 ngh末a r岷眓g t岷 c岷� ti岷縩g vi峄噒 kh么ng th峄� 膽瓢峄", "vietnam");
//				printer.PRN_Print("a 膬 芒 d e 锚 i o 么 贸 u 瓢 y h p w", "vietnam");
//				printer.PRN_Print("谩 岷� 岷� 膽 茅 岷� 铆 贸 峄� 峄� 煤 峄� 媒 j q x", "vietnam");
//				printer.PRN_Print("脿 岷� 岷� b 猫 峄� 矛 貌 峄� 峄� 霉 峄� 峄� k r z", "vietnam");
//				printer.PRN_Print("岷� 岷� 岷� c 岷� 峄� 峄� 峄� 峄� 峄� 峄� 峄� 峄� l s  ", "vietnam");
//				printer.PRN_Print("岷� 岷� 岷� f 岷� 峄� 末 玫 峄� 峄� 农 峄� 峄� m t _", "vietnam");
//				printer.PRN_Print("岷� 岷� 岷� g 岷� 峄� 峄� 峄� 峄� 峄� 峄� 峄� 峄� n v \\", "vietnam");
//				printer.PRN_Print("A 膫 脗 D E 脢 I O 脭 茽 U  漂 Y H P W", "vietnam");
//				printer.PRN_Print("脕 岷� 岷� 膼 脡 岷� 脥 脫 峄� 峄� 脷 峄� 脻 J Q X", "vietnam");
//				printer.PRN_Print("脌 岷� 岷� B 脠 峄� 脤 脪 峄� 峄� 脵 峄� 峄� K R Z", "vietnam");
//				printer.PRN_Print("岷� 岷� 岷� C 岷� 峄� 峄� 峄� 峄� 峄� 峄� 峄� 峄� L S ", "vietnam");
//				printer.PRN_Print("脙 岷� 岷� F 岷� 峄� 抹 脮 峄� 峄� 浓 峄� 峄� M T ^", "vietnam");
//				printer.PRN_Print("岷� 岷� 岷� G 岷� 峄� 峄� 峄� 峄� 峄� 峄� 峄� 峄� N V ~", "vietnam");
//				printer.PRN_Print("! \" # $ % & ' ( ) * + , - . / @", "vietnam");
//				printer.PRN_Print("0 1 2 3 4 5 6 7 8 9 : ; < = > ?", "vietnam");
//				printer.PRN_Print("鈹傗敜 鈹� 鈹� 鈹� 鈹� 鈹� 鈹� 鈹� 鈹樷攲鈱� 卢 { | }", "vietnam");
//				printer.PRN_Print("[ 卤 鈮� 鈮� ] 鈭� 掳 鈮� 梅 桅 危 陆 戮 鈧� 漏 庐 ", "vietnam");
				printer.PRN_PrintAndFeedLine(3);
				printer.PRN_CutPaper();
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
				// Save the serial port parameters
				SharedPreferences mSharedPreferences = getPreferences(MODE_PRIVATE);
				mSharedPreferences.edit().putString("devicePath", devicePath)
						.commit();
				mSharedPreferences.edit()
						.putString("baudrate", data.getStringExtra("baudrate"))
						.commit();
				baudrate = ComIO.Baudrate.valueOf(mSharedPreferences.getString(
						"baudrate", "BAUD_38400"));
			}
		}
	}

	boolean copyBmpFile() {
		String bmpFileName = "ngsu.jpg";
		String bmpFilePath = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/cn.wintec.wtandroiddemo/";
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
	public void saveBitmap(Bitmap bmp,String picName) {
        File f = new File("/sdcard/", picName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	public void cbx_click(View v){
		CheckBox checkBox1=(CheckBox)v;
		if(checkBox1.isChecked())
			printer.EnableRTSCTS();
		else
			printer.DisableRTSCTS();
	}
}
