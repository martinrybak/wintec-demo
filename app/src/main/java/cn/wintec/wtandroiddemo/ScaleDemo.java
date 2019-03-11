package cn.wintec.wtandroiddemo;


import cn.wintec.wtandroidjar.SCL;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ScaleDemo extends Activity {
	Button btnopen;
	Button btnclose;
	Button btnset15KG;
	Button btncalibration;
	Button btncalibrationzero;
	Button btnsetother;
	Button btnsettare;
	Button btntare;
	Button btnversion;
	Button btnzero;
	Button btngravity;
	Button btnsetting;
	Button btnback;
	private TextView mDataField;
	private TextView TxTKG;
	private TextView TxTReturn;
	private TextView TxTWD;

	boolean Read = true;
	boolean ReadOver = false;
	byte[] data = new byte[20];
	

	boolean wdzt = true;
	boolean wdOver = false;
	boolean wdbz = false;
	int time = 0;

	int calibrationnum = 0;
	double tarenum = 0;
	double gravitynum = 0;
	int x1 = -1;
	int x2 = -1;
	int x3 = -1;
	int x4 = -1;
	//int com = -1;
	String COM_Port=null;
	int COM_num=-1;
	SCL scl;
	boolean Set15KG = false;
	boolean Calibration = false;
	boolean Librationzero = false;
	boolean Setother = false;
	boolean Settare = false;
	boolean Tare = false;
	boolean Version = false;
	boolean Zero = false;
	boolean Gravity = false;
	SharedPreferences mSharedPreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scale);
		btnopen = (Button) findViewById(R.id.btn_open);
		btnclose = (Button) findViewById(R.id.btn_close);
		btnset15KG = (Button) findViewById(R.id.btn_set15KG);
		btncalibration = (Button) findViewById(R.id.btn_calibration);
		btncalibrationzero = (Button) findViewById(R.id.btn_calibration_zero);
		btnsetother = (Button) findViewById(R.id.btn_set_other);
		btnsettare = (Button) findViewById(R.id.btn_settare);
		btntare = (Button) findViewById(R.id.btn_tare);
		btnversion = (Button) findViewById(R.id.btn_version);
		btnzero = (Button) findViewById(R.id.btn_zero);
		mDataField = (TextView) findViewById(R.id.data_value);
		btngravity = (Button) findViewById(R.id.btn_gravity);
		btnsetting = (Button) findViewById(R.id.btn_setting);
		btnback = (Button) findViewById(R.id.btn_back);

		TxTKG = (TextView) findViewById(R.id.txt_KG);
		TxTReturn = (TextView) findViewById(R.id.txt_return);
		TxTWD = (TextView) findViewById(R.id.txt_wd);
		mSharedPreferences= getPreferences(MODE_PRIVATE);
		COM_Port = mSharedPreferences.getString("devicePath", "/dev/ttySAC3");
		COM_num = (int) mSharedPreferences.getLong("COM_num", 5);
		x1=mSharedPreferences.getInt("x1", 0);
		x2=mSharedPreferences.getInt("x2", 0);
		x3=mSharedPreferences.getInt("x3", 0);
		x4=mSharedPreferences.getInt("x4", 0);
		openState(false);
		btnclose.setVisibility(View.GONE);
		btnopen.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				scl= new SCL(COM_Port);
				Read = true;
				wdzt = true;
				scl.RESUME();
				new Thread(bzzl).start();
				new Thread(wd).start();
				openState(true);
				btnopen.setVisibility(View.GONE);
				btnclose.setVisibility(View.VISIBLE);
			}
		});
		btnclose.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Read = false;
				wdzt = false;
				while (ReadOver) {
				}
				while (wdOver) {
				}
				openState(false);
				btnclose.setVisibility(View.GONE);
				btnopen.setVisibility(View.VISIBLE);
			}
		});
		btnset15KG.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Set15KG = true;
				scl.send_parameter(1, 15, 6, 3, 1, 2);

			}
		});
		btncalibration.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final LayoutInflater inflater = getLayoutInflater();
				final View calibration_layout = inflater.inflate(
						R.layout.calibration_number, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ScaleDemo.this);
				builder.setTitle(getString(R.string.calibration))
						.setView(calibration_layout)
						.setNegativeButton(getString(R.string.no),
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface insert_list,
											int which) {
									}
								});
				builder.setPositiveButton(getString(R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface insert_list,
									int which) {
								EditText calibration_num = (EditText) calibration_layout
										.findViewById(R.id.calibration_num);
								// {
								if (calibration_num.getText().toString()
										.length() < 1) {
									return;
								}
								calibrationnum = Integer
										.parseInt(calibration_num.getText()
												.toString());
								Message message = new Message();
								message.what = 2;
								mHandler.sendMessage(message);
								// }
							}
						});

				builder.show();

			}
		});
		btncalibrationzero.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Librationzero = true;
				scl.calibration_zero();

			}
		});
		btnsetother.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				final LayoutInflater inflater = getLayoutInflater();
				final View other_layout = inflater.inflate(
						R.layout.other_number, null);
				final Spinner spi1 = (Spinner) other_layout
						.findViewById(R.id.spi_1);
				final Spinner spi2 = (Spinner) other_layout
						.findViewById(R.id.spi_2);
				final Spinner spi3 = (Spinner) other_layout
						.findViewById(R.id.spi_3);
				final Spinner spi4 = (Spinner) other_layout
						.findViewById(R.id.spi_4);
				spi1.setSelection(x1);
				spi2.setSelection(x2);
				spi3.setSelection(x3);
				spi4.setSelection(x4);
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ScaleDemo.this);
				builder.setTitle(getString(R.string.otherP))
						.setView(other_layout)
						
						.setNegativeButton(getString(R.string.no),
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface insert_list,
											int which) {
									}
								});
				builder.setPositiveButton(getString(R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface insert_list,
									int which) {
								
								x1 = spi1.getSelectedItemPosition();
								x2 = spi2.getSelectedItemPosition();
								x3 = spi3.getSelectedItemPosition();
								x4 = spi4.getSelectedItemPosition();
								Message message = new Message();
								message.what = 5;
								mHandler.sendMessage(message);
								//
								// }
							}
						});

				builder.show();

			}
		});
		btnsettare.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				final LayoutInflater inflater = getLayoutInflater();
				final View tare_layout = inflater.inflate(R.layout.tare_number,
						null);
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ScaleDemo.this);
				builder.setTitle(getString(R.string.tare))
						.setView(tare_layout)
						.setNegativeButton(getString(R.string.no),
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface insert_list,
											int which) {
									}
								});
				builder.setPositiveButton(getString(R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface insert_list,
									int which) {
								EditText tare_num = (EditText) tare_layout
										.findViewById(R.id.tare_num);
								// {
								if (tare_num.getText().toString().length() < 1) {
									return;
								}
								tarenum = Double.parseDouble(tare_num.getText()
										.toString());
								Message message = new Message();
								message.what = 3;
								mHandler.sendMessage(message);
								//
								// }
							}
						});

				builder.show();

			}
		});
		btntare.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Tare = true;
				scl.send_tare();

			}
		});
		btnversion.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Version = true;
				scl.get_version();

			}
		});
		btnzero.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Zero = true;
				scl.send_zero();

			}
		});
		btngravity.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				final LayoutInflater inflater = getLayoutInflater();
				final View gravity_layout = inflater.inflate(
						R.layout.gravity_number, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ScaleDemo.this);
				builder.setTitle(getString(R.string.gravity))
						.setView(gravity_layout)
						.setNegativeButton(getString(R.string.no),
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface insert_list,
											int which) {
									}
								});
				builder.setPositiveButton(getString(R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface insert_list,
									int which) {
								EditText gravity_num = (EditText) gravity_layout
										.findViewById(R.id.gravity_num);
								// {
								if (gravity_num.getText().toString().length() < 1) {
									return;
								}
								gravitynum = Double.parseDouble(gravity_num
										.getText().toString());
								Message message = new Message();
								message.what = 4;
								mHandler.sendMessage(message);
								//
								// }
							}
						});

				builder.show();
			}
		});
		btnsetting.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Read = false;
				wdzt = false;
				while (ReadOver) {
				}
				while (wdOver) {
				}
				openState(false);
				btnclose.setVisibility(View.GONE);
				btnopen.setVisibility(View.VISIBLE);
				final LayoutInflater inflater = getLayoutInflater();
				final View com_layout = inflater.inflate(
						R.layout.com_set, null);
				final Spinner com1 = (Spinner) com_layout.findViewById(R.id.com_num);
						com1.setSelection(COM_num);
						
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ScaleDemo.this);
				builder.setTitle(getString(R.string.comscl))
						.setView(com_layout)
						.setPositiveButton(getString(R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface insert_list,
									int which) {
								
								COM_num =com1.getSelectedItemPosition();
								COM_Port = getcom(COM_num);
								
								Message message = new Message();
								message.what = 8;
								mHandler.sendMessage(message);
								//
								// }
							}
						});

				builder.show();

			}
		});
		btnback.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Read = false;
				wdzt = false;
				while (ReadOver) {
				}
				while (wdOver) {
				}
				ScaleDemo.this.finish();	
			}
		});
	}

	// 锟斤拷锟斤拷锟斤拷捉锟竭筹拷
	Runnable bzzl = new Runnable() {
		public void run() {
			ReadOver = true;
			int ret = -1;
			while (Read) {
				ret = scl.read_standard(data);
				if (ret == 0) {
					Log.i("ss-3", scl.ASCII2HexString(data, 20));
					Message message = new Message();
					message.what = 1;
					mHandler.sendMessage(message);
				} else if (ret == 1) {
					Log.i("ss-1", scl.ASCII2HexString(data, 20));
					Message message = new Message();
					message.what = 6;
					mHandler.sendMessage(message);

				} else if (ret == 2) {
					Message message = new Message();
					message.what = 7;
					mHandler.sendMessage(message);
				}
			}
			ReadOver = false;
			// comio.readFinish();

		}
	};
	// 锟斤拷时锟竭筹拷
	Runnable wd = new Runnable() {
		public void run() {
			wdOver = true;
			while (wdzt) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				time++;
			}
			wdOver = false;
			// comio.readFinish();
		}
	};
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			// super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				// 锟斤拷锟斤拷锟侥诧拷锟斤拷
				try{
					int a = (Integer.parseInt(scl.ASCII2HexString(data, 20)
							.substring(0, 1)));
					String b = scl.ASCII2HexString(data, 20).substring(1, 7);
					String c = scl.ASCII2HexString(data, 20).substring(8, 14);
					double suttle = Double.parseDouble(b);
					double tare = Double.parseDouble(c);
					TxTKG.setText("Weight锟斤拷" + suttle + "KG    Tare锟斤拷" + tare + "KG");
					TxTReturn.setText(scl.ASCII2HexString(data, 20));
					if ((a & 1) != 1 && !wdbz) {
						wdbz = true;
						time = 0;
					} else if ((a & 1) == 1 && wdbz) {
						wdbz = false;
						TxTWD.setText(time * 50 + "ms     ");
					}
				}catch(NumberFormatException e){
					
				}
				
				
				// txt.setText(suttle+"");
				break;
			case 2:
				// 锟斤拷锟斤拷锟侥诧拷锟斤拷
				Calibration = true;
				scl.calibration_range(calibrationnum);
				break;
			case 3:
				// 锟斤拷锟斤拷锟侥诧拷锟斤拷
				Settare = true;
				scl.send_ytare(tarenum);
				break;
			case 4:
				// 锟斤拷锟斤拷锟侥诧拷锟斤拷
				Gravity = true;
				scl.calibration_gravity(gravitynum);
				break;
			case 5:
				// 锟斤拷锟斤拷锟侥诧拷锟斤拷
				mSharedPreferences.edit().putInt("x1", x1).commit();
				mSharedPreferences.edit().putInt("x2", x2).commit();
				mSharedPreferences.edit().putInt("x3", x3).commit();
				mSharedPreferences.edit().putInt("x4", x4).commit();
				Setother = true;
				scl.send_other(x1, x2, x3, x4);
				break;
			case 6:
				if (Version) {
					mDataField.append("AD Ver锟斤拷" + scl.ASCII2HexString(data, 20)
							+ "\n");
				} else if (scl.ASCII2HexString(data, 20).contains("OK")) {
					if (Set15KG) {
						mDataField.append("Parameter setting Successed\n");
					} else if (Calibration) {
						mDataField.append("Calibration Successed\n");
					} else if (Librationzero) {
						mDataField.append("Zero Calibration Successed\n");
					} else if (Setother) {
						mDataField.append("Other Parameters Successed\n");
					} else if (Settare) {
						mDataField.append("Set Tare Successed\n");
					} else if (Tare) {
						mDataField.append("Tare Successed\n");
					} else if (Zero) {
						mDataField.append("Zero Successed\n");
					} else if (Gravity) {
						mDataField.append("Set Gravity Successed\n");
					}
				} else if (scl.ASCII2HexString(data, 20).contains("NG")) {
					if (Set15KG) {
						mDataField.append("Parameter setting Failed\n");
					} else if (Calibration) {
						mDataField.append("Calibration Failed\n");
					} else if (Librationzero) {
						mDataField.append("Zero Calibration Failed\n");
					} else if (Setother) {
						mDataField.append("Other Parameters Failed\n");
					} else if (Settare) {
						mDataField.append("Set Tare Failed\n");
					} else if (Tare) {
						mDataField.append("Tare Failed\n");
					} else if (Zero) {
						mDataField.append("Zero Failed\n");
					} else if (Gravity) {
						mDataField.append("Set Gravity Failed\n");
					}
				}
				SETFALSE();
				break;
			case 7:

				break;
			case 8:
				mSharedPreferences.edit().putString("devicePath", COM_Port).commit();
				mSharedPreferences.edit().putLong("COM_num", COM_num).commit();
				scl= new SCL(COM_Port);
				Read = true;
				wdzt = true;
				scl.RESUME();
				new Thread(bzzl).start();
				new Thread(wd).start();
				openState(true);
				btnopen.setVisibility(View.GONE);
				btnclose.setVisibility(View.VISIBLE);
				break;
			case 9:

				break;
			}

		}
	}; // 锟斤拷锟斤拷一锟斤拷锟斤拷锟竭筹拷

	private void SETFALSE() {
		Set15KG = false;
		Calibration = false;
		Librationzero = false;
		Setother = false;
		Settare = false;
		Tare = false;
		Version = false;
		Zero = false;
		Gravity = false;

	}

	private void openState(boolean state) {
		btnset15KG.setEnabled(state);
		btncalibration.setEnabled(state);
		btncalibrationzero.setEnabled(state);
		btnsetother.setEnabled(state);
		btnsettare.setEnabled(state);
		btntare.setEnabled(state);
		btnversion.setEnabled(state);
		btnzero.setEnabled(state);
		btngravity.setEnabled(state);
	}

	private String getcom(int com) {
		String ADCom=null;
		switch (com) {
		case 0:
			ADCom="/dev/ttyS0";
			break;
		case 1:
			ADCom="/dev/ttyS1";
			break;
		case 2:
			ADCom="/dev/ttySAC0";
			break;
		case 3:
			ADCom="/dev/ttySAC1";
			break;
		case 4:
			ADCom="/dev/ttySAC2";
			break;
		case 5:
			ADCom="/dev/ttySAC3";
			break;
		case 6:
			ADCom="/dev/ttySAC4";
			break;
		
		
		}
		return ADCom;
	}
}
