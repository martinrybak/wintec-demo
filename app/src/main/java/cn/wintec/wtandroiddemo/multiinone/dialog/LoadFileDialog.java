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

import cn.wintec.wtandroiddemo.multiinone.MsrActivity;
//import com.gary.msr.R;
import cn.wintec.wtandroiddemo.R;
import cn.wintec.wtandroidjar.multiinone.MSRConstants;
import cn.wintec.wtandroiddemo.multiinone.MSRFileUtil;

public class LoadFileDialog extends Dialog implements
		android.view.View.OnClickListener {

	Context context;

	public LoadFileDialog(Context context) {
		super(context);
		this.context = context;
	}

	public LoadFileDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	private String fnames[];
	private ArrayAdapter<String> adapter;
	private ListView listview;
	private int selectedId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.load_file_dialog);
		setTitle(context.getString(R.string.load_configuration));
		setCanceledOnTouchOutside(false);

		fnames = MSRFileUtil.getFilenames();

		listview = (ListView) findViewById(R.id.loadFileListview);
		adapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_list_item_single_choice, fnames);

		listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				selectedId = arg2;
			}
		});

		Button okBtn = (Button) findViewById(R.id.loadFileOkBtn);
		okBtn.setOnClickListener(this);
		Button cancelBtn = (Button) findViewById(R.id.loadFileCancelBtn);
		cancelBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loadFileCancelBtn:
			dismiss();
			break;
		case R.id.loadFileOkBtn: {
			int id = selectedId;
			if (id < 0)
				return;

			MSRFileUtil.readParams(fnames[id]);
			MsrActivity.handler.sendEmptyMessage(MsrActivity.REFRESH_MAIN_UI);
			dismiss();
			break;
		}
		}
	}

}
