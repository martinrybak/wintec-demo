package cn.wintec.wtandroiddemo.multiinone.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

//import com.gary.msr.R;
import cn.wintec.wtandroiddemo.R;
public class AsciiDialog extends Dialog {
	Context context;
	private AsciiDialogListener listener = null;
	private View fromView;

	public AsciiDialog(Context context) {
		super(context);
		this.context = context;
	}

	public AsciiDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	public void setCustomerInfo(View fromView, AsciiDialogListener listener) {
		this.listener = listener;
		this.fromView = fromView;
	}

	private EditText msrEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ascii_dialog);
		setTitle(context.getString(R.string.input_ascii));
		setCanceledOnTouchOutside(false);
		msrEdit = (EditText) findViewById(R.id.asciiMsrEdit);
		Button okBtn = (Button) findViewById(R.id.asciiOkBtn);
		okBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String strs = msrEdit.getText().toString().trim();
				if (TextUtils.isEmpty(strs))
					return;
				if (listener != null)
					listener.refreshActivity(fromView, strs);
				dismiss();
			}
		});
		Button cancelBtn = (Button) findViewById(R.id.asciiCancelBtn);
		cancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listener != null)
					listener.refreshActivity(fromView, null);
				dismiss();
			}

		});
		if (!TextUtils.isEmpty((String) fromView.getTag(R.id.tag_defaultval)))
			msrEdit.setText((String) fromView.getTag(R.id.tag_defaultval));
//		else
//			msrEdit.setText("gary");
	}

}
