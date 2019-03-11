package cn.wintec.wtandroiddemo.multiinone;

import cn.wintec.wtandroidjar.UsbIO;
import  cn.wintec.wtandroidjar.multiinone.MSRConstants;

import android.app.Application;

public class MainApplication extends Application {
	private static MainApplication instance;

	public static MainApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		instance = this; //modifyUSBBusMode
		UsbIO.modifyUSBBusMode();
//		USBOp.setVidAndPid(MSRConstants.HID_VID, MSRConstants.HID_PID);
		super.onCreate();
	}
}
