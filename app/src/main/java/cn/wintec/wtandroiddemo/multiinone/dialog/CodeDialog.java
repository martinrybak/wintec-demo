package cn.wintec.wtandroiddemo.multiinone.dialog;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

//import com.gary.msr.R;
import cn.wintec.wtandroiddemo.R;
import cn.wintec.wtandroidjar.multiinone.MSRConstants;

public class CodeDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;
	private CodeDialogListener listener = null;
	private View fromView;

	public CodeDialog(Context context) {
		super(context);
		this.context = context;
	}

	public CodeDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	public void setCustomerInfo(View fromView, CodeDialogListener listener) {
		this.listener = listener;
		this.fromView = fromView;
	}

	private String codesArr[];
	private List<String> selectedCodesLst = new LinkedList<String>();
	private TextView resultView;
	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.code_dialog);
		setCanceledOnTouchOutside(false);
		setTitle(context.getString(R.string.select_keycode));
		Set<String> set = MSRConstants.KeyCode.keySet();
		codesArr = set.toArray(new String[set.size()]);
		selectedCodesLst.clear();

		final ListView lv = (ListView) findViewById(R.id.codeListview);
		adapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_list_item_single_choice, codesArr);// .simple_list_item_multiple_choice,
																			// codesArr);

		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);// .CHOICE_MODE_MULTIPLE);
		lv.setAdapter(adapter);
		resultView = (TextView) findViewById(R.id.selectedResult);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				selectedCodesLst.add(codesArr[arg2]);
				updateSelectedResult();
			}
		});
		Button okBtn = (Button) findViewById(R.id.codeOkBtn);
		okBtn.setOnClickListener(this);
		Button cancelBtn = (Button) findViewById(R.id.codeCancelBtn);
		cancelBtn.setOnClickListener(this);
		Button backDeleteBtn = (Button) findViewById(R.id.backDeleteBtn);
		backDeleteBtn.setOnClickListener(this);

		List<String> defaultCodes = (List<String>) fromView
				.getTag(R.id.tag_defaultval);
		if (defaultCodes != null && defaultCodes.size() > 0) {
			selectedCodesLst.addAll(defaultCodes);
			updateSelectedResult();
		}
	}

	private void updateSelectedResult() {
		if (selectedCodesLst.size() == 0) {
			resultView.setVisibility(View.GONE);
			return;
		}
		String content = "";
		for (String code : selectedCodesLst) {
			content += "<" + code + ">";
		}
		resultView.setText(content);
		resultView.setVisibility(View.VISIBLE);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.codeCancelBtn:
			if (listener != null)
				listener.refreshActivity(fromView, null);
			dismiss();
			break;
		case R.id.codeOkBtn:
			if (selectedCodesLst == null || selectedCodesLst.size() < 1)
				return;
			if (listener != null)
				listener.refreshActivity(fromView, selectedCodesLst);
			dismiss();
			break;
		case R.id.backDeleteBtn:
			if (selectedCodesLst.size() < 1)
				return;
			int size = selectedCodesLst.size();
			selectedCodesLst.remove(size - 1);
			updateSelectedResult();
			break;
		}
	}

}
