package cn.wintec.wtandroiddemo.multiinone.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.wintec.wtandroiddemo.R;
import cn.wintec.wtandroidjar.multiinone.Msr;
//import com.andorid.RM2091demo.R;;
import cn.wintec.wtandroiddemo.multiinone.MSRFileUtil;

public class StoreFileDialog extends Dialog {
	Context context;

	public StoreFileDialog(Context context) {
		super(context);
		this.context = context;
	}

	public StoreFileDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	private EditText fileEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_file_dialog);
		setTitle(context.getString(R.string.store_configuration));
		setCanceledOnTouchOutside(false);
		fileEdit = (EditText) findViewById(R.id.storeFileNameEdit);
		Button okBtn = (Button) findViewById(R.id.storeFileOkBtn);
		okBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String strs = fileEdit.getText().toString().trim();
				if (MSRFileUtil.isFileExist(strs)) {
					Toast.makeText(context,
							context.getString(R.string.file_exist),
							Toast.LENGTH_LONG).show();
					return;
				}
				MSRFileUtil.writeParams(strs);
				dismiss();
			}
		});
		Button cancelBtn = (Button) findViewById(R.id.storeFileCancelBtn);
		cancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}

		});
	}

}
