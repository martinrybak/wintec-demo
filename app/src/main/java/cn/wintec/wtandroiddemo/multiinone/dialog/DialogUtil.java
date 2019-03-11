package cn.wintec.wtandroiddemo.multiinone.dialog;

import java.util.List;

import android.view.View;

import cn.wintec.wtandroiddemo.multiinone.MsrActivity;
//import com.gary.msr.R;
import cn.wintec.wtandroiddemo.R;

public class DialogUtil {
	public static void showCodeDialog(MsrActivity activity,
			 View fromView) {
		CodeDialog dialog = new CodeDialog(activity, R.style.codeDialogStyle);
		dialog.setCustomerInfo(fromView, 
				activity.codeDialogListener);
		dialog.show();
	}

	public static void showAsciiDialog(MsrActivity activity,
			 View fromView) {
		AsciiDialog dialog = new AsciiDialog(activity);
		dialog.setCustomerInfo(fromView,
				activity.asciiDialogListener);
		dialog.show();
	}

	public static void showStoreFileDialog(MsrActivity activity) {
		StoreFileDialog dialog = new StoreFileDialog(activity);
		dialog.show();
	}

	public static void showLoadFileDialog(MsrActivity activity) {
		LoadFileDialog dialog = new LoadFileDialog(activity);
		dialog.show();
	}

}
