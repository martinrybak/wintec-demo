package cn.wintec.wtandroiddemo.multiinone;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.TabActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import cn.wintec.wtandroiddemo.R;
import cn.wintec.wtandroiddemo.multiinone.dialog.AsciiDialogListener;
import cn.wintec.wtandroiddemo.multiinone.dialog.CodeDialogListener;
import cn.wintec.wtandroiddemo.multiinone.dialog.DialogUtil;
import cn.wintec.wtandroidjar.UsbIO;
import cn.wintec.wtandroidjar.multiinone.MSRConstants;
import cn.wintec.wtandroiddemo.multiinone.MSRFileUtil;
import cn.wintec.wtandroidjar.multiinone.DataChangeListener;
import cn.wintec.wtandroidjar.multiinone.MSRLayerParams.MSRLayer1Params;
import cn.wintec.wtandroidjar.multiinone.MSRLayerParams.MSRLayer2Params;
import cn.wintec.wtandroidjar.multiinone.MSRLayerParams.MSRLayerNParam;
import cn.wintec.wtandroidjar.multiinone.Msr;
import cn.wintec.wtandroidjar.multiinone.MSRProto;

public class MsrActivity extends TabActivity implements OnClickListener,
		OnCheckedChangeListener {
	private TabHost tabhost;
	private TabWidget tabWidget;
	Msr msr=new Msr(0x10C4,0x8499);

	/*********************************** 控件定义 *******************************************************/
	Button loadBtn, readBtn, writeBtn, normalBtn, testBtn, storeBtn,
			restoreBtn, exitBtn, iccardPowerupBtn, iccardPowerdownBtn,
			iccardApduSendBtn;

	CheckBox isoEnable1CBox, isoEnable2CBox, isoEnable3CBox, isoEnter1CBox,
			isoEnter2CBox, isoEnter3CBox, isoExpansionsBeepCBox,
			isoExpansionsIBMCBox, isoExpansionsDataCBox, iButtonAttachBeepCBox,
			iButtonRemoveBeepCBox;
	EditText isoSS1Edit, isoSS2Edit, isoSS3Edit, isoES1Edit, isoES2Edit,
			isoES3Edit, jis2SSEdit, jis2ESEdit, iButtonSSEdit, iButtonESEdit,
			iButtonAttachFromEdit, iccardApduEdit, iccardReceiveEdit,
			iccardMessageEdit, iButtonAttachLengthEdit, iButtonRemoveFromEdit,
			iButtonRemoveLengthEdit;

	RadioButton isoHeaderAsciiRBtn, isoHeaderCodeRBtn, isoSeparatorAsciiRBtn,
			isoSeparatorCodeRBtn, isoSuffixAsciiRBtn, isoSuffixCodeRBtn,
			jis2PrefixAsciiRBtn, jis2PrefixCodeRBtn, jis2SuffixAsciiRBtn,
			jis2SuffixCodeRBtn, iButtonAttachPrefixAsciiRBtn,
			iButtonAttachPrefixCodeRBtn, iButtonAttachSuffixAsciiRBtn,
			iButtonAttachSuffixCodeRBtn, iButtonRemovePrefixAsciiRBtn,
			iButtonRemoveSuffixAsciiRBtn, iButtonRemoveSuffixCodeRBtn,
			iButtonRemovePrefixCodeRBtn;

	TextView isoHeaderResult, jis2PrefixResult, jis2SuffixResult,
			isoSeparatorResult, isoSuffixResult, iButtonAttachPrefixResult,
			iButtonAttachSuffixResult, iButtonRemovePrefixResult,
			iButtonRemoveSuffixResult;
	Spinner isoExpansionsLangSpinner, isoExpansionsSeqSpinner,
			iButtonAttachSpinner, iButtonRemoveSpinner;

	RadioGroup iButtonRemoveSuffixRGroup, iButtonRemovePrefixRGroup,
			iButtonAttachSuffixRGroup, iButtonAttachPrefixRGroup,
			jis2SuffixRGroup, jis2PrefixRGroup, isoSuffixRGroup,
			isoSeparatorRGroup, isoHeaderRGroup;

	Button isoClearBtn, iButtonClearBtn, versionBtn;
	EditText isoTestEdit, iButtonTestEdit;

	/***********************************  *******************************************************/

	private void fillAllSpinner() {
		ArrayAdapter isoLanguageAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, MSRConstants.COUNTRYNAME);
		isoLanguageAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		isoExpansionsLangSpinner.setAdapter(isoLanguageAdapter);
		isoExpansionsLangSpinner.setVisibility(View.VISIBLE);
		ArrayAdapter isoSequenceAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, MSRConstants.SEQUNCE);
		isoSequenceAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		isoExpansionsSeqSpinner.setAdapter(isoSequenceAdapter);
		isoExpansionsSeqSpinner.setVisibility(View.VISIBLE);

		ArrayAdapter methodAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, MSRConstants.METHOD);
		methodAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		iButtonAttachSpinner.setAdapter(methodAdapter);
		iButtonRemoveSpinner.setAdapter(methodAdapter);
	}

	@SuppressWarnings("deprecation")
	private void initTabs() {
		tabhost = getTabHost();

		LayoutInflater.from(this).inflate(R.layout.activity_multiinone_msr,
				tabhost.getTabContentView(), true);

		tabhost.addTab(tabhost.newTabSpec("tab1").setIndicator("ISO")
				.setContent(R.id.isollayout));
		/*tabhost.addTab(tabhost.newTabSpec("tab2").setIndicator("JIS2")
				.setContent(R.id.jis2llayout));
		tabhost.addTab(tabhost.newTabSpec("tab3").setIndicator("iButton")
				.setContent(R.id.iButtonllayout));
		tabhost.addTab(tabhost.newTabSpec("tab4")
				.setIndicator(getString(R.string.ic_card))
				.setContent(R.id.iccardllayout));*/////20150427为了屏蔽后面的jis2 ic ibutton的选项卡

		tabhost.setCurrentTab(0);
		tabWidget = tabhost.getTabWidget();
		for (int i = 0; i < tabWidget.getChildCount(); i++) {
			tabWidget.getChildAt(i).getLayoutParams().height = 45;
			TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(
					android.R.id.title);
			tv.setTextColor(0xffff0000);
		}
	}

	private void initAllViews() {
		/**
		 * 底下按钮
		 */
		loadBtn = (Button) findViewById(R.id.loadBtn);
		readBtn = (Button) findViewById(R.id.readBtn);
		writeBtn = (Button) findViewById(R.id.writeBtn);
		normalBtn = (Button) findViewById(R.id.normalBtn);
		testBtn = (Button) findViewById(R.id.testBtn);
		storeBtn = (Button) findViewById(R.id.storeBtn);
		restoreBtn = (Button) findViewById(R.id.restoreBtn);
		exitBtn = (Button) findViewById(R.id.exitBtn);
		/**
		 * ISO页面
		 */
		isoEnable1CBox = (CheckBox) findViewById(R.id.isoEnable1CBox);
		isoEnable2CBox = (CheckBox) findViewById(R.id.isoEnable2CBox);
		isoEnable3CBox = (CheckBox) findViewById(R.id.isoEnable3CBox);
		isoEnter1CBox = (CheckBox) findViewById(R.id.isoEnter1CBox);
		isoEnter2CBox = (CheckBox) findViewById(R.id.isoEnter2CBox);
		isoEnter3CBox = (CheckBox) findViewById(R.id.isoEnter3CBox);

		isoSS1Edit = (EditText) findViewById(R.id.isoSS1Edit);
		isoSS2Edit = (EditText) findViewById(R.id.isoSS2Edit);
		isoSS3Edit = (EditText) findViewById(R.id.isoSS3Edit);

		isoES1Edit = (EditText) findViewById(R.id.isoES1Edit);
		isoES2Edit = (EditText) findViewById(R.id.isoES2Edit);
		isoES3Edit = (EditText) findViewById(R.id.isoES3Edit);

		isoHeaderAsciiRBtn = (RadioButton) findViewById(R.id.isoHeaderAsciiRBtn);
		isoHeaderCodeRBtn = (RadioButton) findViewById(R.id.isoHeaderCodeRBtn);
		isoHeaderResult = (TextView) findViewById(R.id.isoHeaderResult);
		isoHeaderRGroup = (RadioGroup) findViewById(R.id.isoHeaderRGroup);

		isoSeparatorAsciiRBtn = (RadioButton) findViewById(R.id.isoSeparatorAsciiRBtn);
		isoSeparatorCodeRBtn = (RadioButton) findViewById(R.id.isoSeparatorCodeRBtn);
		isoSeparatorResult = (TextView) findViewById(R.id.isoSeparatorResult);
		isoSeparatorRGroup = (RadioGroup) findViewById(R.id.isoSeparatorRGroup);

		isoSuffixAsciiRBtn = (RadioButton) findViewById(R.id.isoSuffixAsciiRBtn);
		isoSuffixCodeRBtn = (RadioButton) findViewById(R.id.isoSuffixCodeRBtn);
		isoSuffixResult = (TextView) findViewById(R.id.isoSuffixResult);
		isoSuffixRGroup = (RadioGroup) findViewById(R.id.isoSuffixRGroup);

		isoExpansionsBeepCBox = (CheckBox) findViewById(R.id.isoExpansionsBeepCBox);
		isoExpansionsIBMCBox = (CheckBox) findViewById(R.id.isoExpansionsIBMCBox);
		isoExpansionsDataCBox = (CheckBox) findViewById(R.id.isoExpansionsDataCBox);

		isoExpansionsLangSpinner = (Spinner) findViewById(R.id.isoExpansionsLangSpinner);
		isoExpansionsSeqSpinner = (Spinner) findViewById(R.id.isoExpansionsSeqSpinner);

		/**
		 * JIS2页面
		 */
		jis2SSEdit = (EditText) findViewById(R.id.jis2SSEdit);
		jis2ESEdit = (EditText) findViewById(R.id.jis2ESEdit);

		jis2PrefixAsciiRBtn = (RadioButton) findViewById(R.id.jis2PrefixAsciiRBtn);
		jis2PrefixCodeRBtn = (RadioButton) findViewById(R.id.jis2PrefixCodeRBtn);
		jis2PrefixResult = (TextView) findViewById(R.id.jis2PrefixResult);
		jis2PrefixRGroup = (RadioGroup) findViewById(R.id.jis2PrefixRGroup);

		jis2SuffixAsciiRBtn = (RadioButton) findViewById(R.id.jis2SuffixAsciiRBtn);
		jis2SuffixCodeRBtn = (RadioButton) findViewById(R.id.jis2SuffixCodeRBtn);
		jis2SuffixResult = (TextView) findViewById(R.id.jis2SuffixResult);
		jis2SuffixRGroup = (RadioGroup) findViewById(R.id.jis2SuffixRGroup);

		/**
		 * IButton页面
		 */
		iButtonSSEdit = (EditText) findViewById(R.id.iButtonSSEdit);
		iButtonESEdit = (EditText) findViewById(R.id.iButtonESEdit);

		iButtonAttachSpinner = (Spinner) findViewById(R.id.iButtonAttachSpinner);
		iButtonRemoveSpinner = (Spinner) findViewById(R.id.iButtonRemoveSpinner);

		iButtonAttachPrefixAsciiRBtn = (RadioButton) findViewById(R.id.iButtonAttachPrefixAsciiRBtn);
		iButtonAttachPrefixCodeRBtn = (RadioButton) findViewById(R.id.iButtonAttachPrefixCodeRBtn);
		iButtonAttachPrefixResult = (TextView) findViewById(R.id.iButtonAttachPrefixResult);
		iButtonAttachPrefixRGroup = (RadioGroup) findViewById(R.id.iButtonAttachPrefixRGroup);

		iButtonAttachSuffixAsciiRBtn = (RadioButton) findViewById(R.id.iButtonAttachSuffixAsciiRBtn);
		iButtonAttachSuffixCodeRBtn = (RadioButton) findViewById(R.id.iButtonAttachSuffixCodeRBtn);
		iButtonAttachSuffixResult = (TextView) findViewById(R.id.iButtonAttachSuffixResult);
		iButtonAttachSuffixRGroup = (RadioGroup) findViewById(R.id.iButtonAttachSuffixRGroup);

		iButtonAttachFromEdit = (EditText) findViewById(R.id.iButtonAttachFromEdit);
		iButtonAttachLengthEdit = (EditText) findViewById(R.id.iButtonAttachLengthEdit);
		iButtonAttachBeepCBox = (CheckBox) findViewById(R.id.iButtonAttachBeepCBox);

		iButtonRemovePrefixAsciiRBtn = (RadioButton) findViewById(R.id.iButtonRemovePrefixAsciiRBtn);
		iButtonRemovePrefixCodeRBtn = (RadioButton) findViewById(R.id.iButtonRemovePrefixCodeRBtn);
		iButtonRemovePrefixResult = (TextView) findViewById(R.id.iButtonRemovePrefixResult);
		iButtonRemovePrefixRGroup = (RadioGroup) findViewById(R.id.iButtonRemovePrefixRGroup);

		iButtonRemoveSuffixAsciiRBtn = (RadioButton) findViewById(R.id.iButtonRemoveSuffixAsciiRBtn);
		iButtonRemoveSuffixCodeRBtn = (RadioButton) findViewById(R.id.iButtonRemoveSuffixCodeRBtn);
		iButtonRemoveSuffixResult = (TextView) findViewById(R.id.iButtonRemoveSuffixResult);
		iButtonRemoveSuffixRGroup = (RadioGroup) findViewById(R.id.iButtonRemoveSuffixRGroup);

		iButtonRemoveFromEdit = (EditText) findViewById(R.id.iButtonRemoveFromEdit);
		iButtonRemoveLengthEdit = (EditText) findViewById(R.id.iButtonRemoveLengthEdit);
		iButtonRemoveBeepCBox = (CheckBox) findViewById(R.id.iButtonRemoveBeepCBox);

		/**
		 * ic card页面
		 */
		iccardPowerupBtn = (Button) findViewById(R.id.iccardPowerupBtn);
		iccardPowerdownBtn = (Button) findViewById(R.id.iccardPowerdownBtn);

		iccardApduEdit = (EditText) findViewById(R.id.iccardApduEdit);
		iccardApduSendBtn = (Button) findViewById(R.id.iccardApduSendBtn);

		iccardReceiveEdit = (EditText) findViewById(R.id.iccardReceiveEdit);
		iccardMessageEdit = (EditText) findViewById(R.id.iccardMessageEdit);

		isoHeaderAsciiRBtn.setOnClickListener(this);
		isoHeaderCodeRBtn.setOnClickListener(this);
		isoSeparatorAsciiRBtn.setOnClickListener(this);
		isoSeparatorCodeRBtn.setOnClickListener(this);
		isoSuffixAsciiRBtn.setOnClickListener(this);
		isoSuffixCodeRBtn.setOnClickListener(this);
		jis2PrefixAsciiRBtn.setOnClickListener(this);
		jis2PrefixCodeRBtn.setOnClickListener(this);
		jis2SuffixAsciiRBtn.setOnClickListener(this);
		jis2SuffixCodeRBtn.setOnClickListener(this);
		iButtonAttachPrefixAsciiRBtn.setOnClickListener(this);
		iButtonAttachPrefixCodeRBtn.setOnClickListener(this);
		iButtonAttachSuffixAsciiRBtn.setOnClickListener(this);
		iButtonAttachSuffixCodeRBtn.setOnClickListener(this);
		iButtonRemovePrefixAsciiRBtn.setOnClickListener(this);
		iButtonRemoveSuffixAsciiRBtn.setOnClickListener(this);
		iButtonRemoveSuffixCodeRBtn.setOnClickListener(this);
		iButtonRemovePrefixCodeRBtn.setOnClickListener(this);

		isoEnable1CBox.setOnCheckedChangeListener(this);
		isoEnable2CBox.setOnCheckedChangeListener(this);
		isoEnable3CBox.setOnCheckedChangeListener(this);

		iButtonAttachSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long idl) {
						int id = (int) idl;
						Map<Integer, Integer> map = MSRConstants.METHOD_OFFSETS
								.get(id);
						if (map == null || map.size() < 1)
							return;
						Iterator<Entry<Integer, Integer>> iter = map.entrySet()
								.iterator();
						int key = -1, val = -1;
						while (iter.hasNext()) {
							Entry<Integer, Integer> entry = iter.next();
							key = entry.getKey();
							val = entry.getValue();
						}
						iButtonAttachFromEdit.setText(key + "");
						iButtonAttachLengthEdit.setText(val + "");
						if (id == 4) {
							iButtonAttachFromEdit.setEnabled(true);
							iButtonAttachLengthEdit.setEnabled(true);
						} else {
							iButtonAttachFromEdit.setEnabled(false);
							iButtonAttachLengthEdit.setEnabled(false);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});
		iButtonRemoveSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long idl) {
						int id = (int) idl;
						Map<Integer, Integer> map = MSRConstants.METHOD_OFFSETS
								.get(id);
						if (map == null || map.size() < 1)
							return;
						Iterator<Entry<Integer, Integer>> iter = map.entrySet()
								.iterator();
						int key = -1, val = -1;
						while (iter.hasNext()) {
							Entry<Integer, Integer> entry = iter.next();
							key = entry.getKey();
							val = entry.getValue();
						}
						iButtonRemoveFromEdit.setText(key + "");
						iButtonRemoveLengthEdit.setText(val + "");
						if (id == 4) {
							iButtonRemoveFromEdit.setEnabled(true);
							iButtonRemoveLengthEdit.setEnabled(true);
						} else {
							iButtonRemoveFromEdit.setEnabled(false);
							iButtonRemoveLengthEdit.setEnabled(false);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});

		loadBtn.setOnClickListener(this);
		readBtn.setOnClickListener(this);
		writeBtn.setOnClickListener(this);
		normalBtn.setOnClickListener(this);
		testBtn.setOnClickListener(this);
		storeBtn.setOnClickListener(this);
		restoreBtn.setOnClickListener(this);
		exitBtn.setOnClickListener(this);

		iccardApduSendBtn.setOnClickListener(this);
		iccardPowerupBtn.setOnClickListener(this);
		iccardPowerdownBtn.setOnClickListener(this);

		isoClearBtn = (Button) findViewById(R.id.isoClearBtn);
		isoClearBtn.setOnClickListener(this);
		isoTestEdit = (EditText) findViewById(R.id.isoTestEdit);

		iButtonClearBtn = (Button) findViewById(R.id.iButtonClearBtn);
		iButtonClearBtn.setOnClickListener(this);
		iButtonTestEdit = (EditText) findViewById(R.id.iButtonTestEdit);

		versionBtn = (Button) findViewById(R.id.versionBtn);
		versionBtn.setOnClickListener(this);
	}

	public static final int GROUP_UNCHECKED = 0;
	public static final int GROUP_ASCII_CHECKED = 1;
	public static final int GROUP_CODE_CHECKED = 2;

	private void initViewTags() {
		// 第三层
		isoHeaderAsciiRBtn.setTag(R.id.tag_layernum, 3);
		isoHeaderCodeRBtn.setTag(R.id.tag_layernum, 3);
		isoHeaderAsciiRBtn.setTag(R.id.tag_asciitype, true);
		isoHeaderCodeRBtn.setTag(R.id.tag_asciitype, false);
		isoHeaderResult.setTag(R.id.tag_layernum, 3);
		isoHeaderRGroup.setTag(R.id.tag_layernum, 3);
		isoHeaderRGroup.setTag(R.id.tag_rbtnstatus, GROUP_UNCHECKED);
		// 第四层
		isoSeparatorAsciiRBtn.setTag(R.id.tag_layernum, 4);
		isoSeparatorCodeRBtn.setTag(R.id.tag_layernum, 4);
		isoSeparatorAsciiRBtn.setTag(R.id.tag_asciitype, true);
		isoSeparatorCodeRBtn.setTag(R.id.tag_asciitype, false);
		isoSeparatorResult.setTag(R.id.tag_layernum, 4);
		isoSeparatorRGroup.setTag(R.id.tag_layernum, 4);
		isoSeparatorRGroup.setTag(R.id.tag_rbtnstatus, GROUP_UNCHECKED);
		// 第五层
		isoSuffixAsciiRBtn.setTag(R.id.tag_layernum, 5);
		isoSuffixCodeRBtn.setTag(R.id.tag_layernum, 5);
		isoSuffixAsciiRBtn.setTag(R.id.tag_asciitype, true);
		isoSuffixCodeRBtn.setTag(R.id.tag_asciitype, false);
		isoSuffixResult.setTag(R.id.tag_layernum, 5);
		isoSuffixRGroup.setTag(R.id.tag_layernum, 5);
		isoSuffixRGroup.setTag(R.id.tag_rbtnstatus, GROUP_UNCHECKED);
		// 第六层
		iButtonAttachPrefixAsciiRBtn.setTag(R.id.tag_layernum, 6);
		iButtonAttachPrefixCodeRBtn.setTag(R.id.tag_layernum, 6);
		iButtonAttachPrefixAsciiRBtn.setTag(R.id.tag_asciitype, true);
		iButtonAttachPrefixCodeRBtn.setTag(R.id.tag_asciitype, false);
		iButtonAttachPrefixResult.setTag(R.id.tag_layernum, 6);
		iButtonAttachPrefixRGroup.setTag(R.id.tag_layernum, 6);
		iButtonAttachPrefixRGroup.setTag(R.id.tag_rbtnstatus, GROUP_UNCHECKED);
		// 第七层
		iButtonAttachSuffixAsciiRBtn.setTag(R.id.tag_layernum, 7);
		iButtonAttachSuffixCodeRBtn.setTag(R.id.tag_layernum, 7);
		iButtonAttachSuffixAsciiRBtn.setTag(R.id.tag_asciitype, true);
		iButtonAttachSuffixCodeRBtn.setTag(R.id.tag_asciitype, false);
		iButtonAttachSuffixResult.setTag(R.id.tag_layernum, 7);
		iButtonAttachSuffixRGroup.setTag(R.id.tag_layernum, 7);
		iButtonAttachSuffixRGroup.setTag(R.id.tag_rbtnstatus, GROUP_UNCHECKED);
		// 第八层
		iButtonRemovePrefixAsciiRBtn.setTag(R.id.tag_layernum, 8);
		iButtonRemovePrefixCodeRBtn.setTag(R.id.tag_layernum, 8);
		iButtonRemovePrefixAsciiRBtn.setTag(R.id.tag_asciitype, true);
		iButtonRemovePrefixCodeRBtn.setTag(R.id.tag_asciitype, false);
		iButtonRemovePrefixResult.setTag(R.id.tag_layernum, 8);
		iButtonRemovePrefixRGroup.setTag(R.id.tag_layernum, 8);
		iButtonRemovePrefixRGroup.setTag(R.id.tag_rbtnstatus, GROUP_UNCHECKED);
		// 第九层
		iButtonRemoveSuffixAsciiRBtn.setTag(R.id.tag_layernum, 9);
		iButtonRemoveSuffixCodeRBtn.setTag(R.id.tag_layernum, 9);
		iButtonRemoveSuffixAsciiRBtn.setTag(R.id.tag_asciitype, true);
		iButtonRemoveSuffixCodeRBtn.setTag(R.id.tag_asciitype, false);
		iButtonRemoveSuffixResult.setTag(R.id.tag_layernum, 9);
		iButtonRemoveSuffixRGroup.setTag(R.id.tag_layernum, 9);
		iButtonRemoveSuffixRGroup.setTag(R.id.tag_rbtnstatus, GROUP_UNCHECKED);
		// 第十层
		jis2PrefixAsciiRBtn.setTag(R.id.tag_layernum, 10);
		jis2PrefixCodeRBtn.setTag(R.id.tag_layernum, 10);
		jis2PrefixAsciiRBtn.setTag(R.id.tag_asciitype, true);
		jis2PrefixCodeRBtn.setTag(R.id.tag_asciitype, false);
		jis2PrefixResult.setTag(R.id.tag_layernum, 10);
		jis2PrefixRGroup.setTag(R.id.tag_layernum, 10);
		jis2PrefixRGroup.setTag(R.id.tag_rbtnstatus, GROUP_UNCHECKED);
		// 第十一层
		jis2SuffixAsciiRBtn.setTag(R.id.tag_layernum, 11);
		jis2SuffixCodeRBtn.setTag(R.id.tag_layernum, 11);
		jis2SuffixAsciiRBtn.setTag(R.id.tag_asciitype, true);
		jis2SuffixCodeRBtn.setTag(R.id.tag_asciitype, false);
		jis2SuffixResult.setTag(R.id.tag_layernum, 11);
		jis2SuffixRGroup.setTag(R.id.tag_layernum, 11);
		jis2SuffixRGroup.setTag(R.id.tag_rbtnstatus, GROUP_UNCHECKED);

	}

	private static MsrActivity instance;

	public static MsrActivity getInstance() {
		return instance;
	}

	public static final int REFRESH_MAIN_UI = 0x1;
	public static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REFRESH_MAIN_UI:
				MsrActivity.getInstance().updateMSRLayer1UI(
						MSRFileUtil.getParam1());
				MsrActivity.getInstance().updateMSRLayer2UI(
						MSRFileUtil.getParam2());
				MsrActivity.getInstance().updateMSRLayerNUI(
						MSRFileUtil.getParamns());
				break;
			}
		}

	};

	private MSRLayer1Params defaultLayer1Params;
	private int defaultLayer2Index = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/////////////////////////////////////加入设置VIDPID
		//USBOp.setVidAndPid(MSRConstants.HID_VID, MSRConstants.HID_PID);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		instance = this;
		initTabs();
		initAllViews();
		fillAllSpinner();
		initViewTags();
		defaultLayer1Params = buildMSRLayer1Params();
		defaultLayer2Index = (int) isoExpansionsLangSpinner.getSelectedItemId();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public AsciiDialogListener asciiDialogListener = new AsciiDialogListener() {

		@Override
		public void refreshActivity(View fromView, String chars) {
			Integer nObj = (Integer) fromView.getTag(R.id.tag_layernum);
			LayerViews layerViews = findLayerViewsByTag(nObj.intValue());
			if (layerViews == null || !layerViews.isValidViews())
				return;
			if (TextUtils.isEmpty(chars)) {
//				Integer checkState = (Integer) layerViews.rgroup
//						.getTag(R.id.tag_rbtnstatus);
//				int checkStateVal = checkState.intValue();
//				if (checkStateVal == GROUP_UNCHECKED)
//					layerViews.rgroup.clearCheck();
//				else if (checkStateVal == GROUP_CODE_CHECKED)
//					layerViews.rgroup.check(layerViews.codeRBtn.getId());
				layerViews.rgroup.clearCheck();
				layerViews.resultTv.setText("");
				MSRProto.invalidateMSRLayerNData(nObj.intValue());
				return;
			}
			layerViews.rgroup.setTag(R.id.tag_rbtnstatus, GROUP_ASCII_CHECKED);
			MSRLayerNParam paramn = new MSRLayerNParam(nObj.intValue(), true,
					parseAsciiData(chars));
			MSRProto.buildMSRLayerNData(paramn);
			fromView.setTag(R.id.tag_defaultval, chars);
			layerViews.codeRBtn.setTag(R.id.tag_defaultval, null);
			switch (fromView.getId()) {
			case R.id.isoHeaderAsciiRBtn:
			case R.id.isoSeparatorAsciiRBtn:
			case R.id.isoSuffixAsciiRBtn:
			case R.id.jis2PrefixAsciiRBtn:
			case R.id.jis2SuffixAsciiRBtn:
			case R.id.iButtonAttachPrefixAsciiRBtn:
			case R.id.iButtonAttachSuffixAsciiRBtn:
			case R.id.iButtonRemovePrefixAsciiRBtn:
			case R.id.iButtonRemoveSuffixAsciiRBtn:
				layerViews.resultTv.setText(chars);
				break;
			default:
				break;
			}

		}
	};

	public CodeDialogListener codeDialogListener = new CodeDialogListener() {

		@Override
		public void refreshActivity(View fromView, List<String> codes) {
			Integer nObj = (Integer) fromView.getTag(R.id.tag_layernum);
			LayerViews layerViews = findLayerViewsByTag(nObj.intValue());
			if (layerViews == null || !layerViews.isValidViews())
				return;
			if (codes == null || codes.size() < 1) {
//				Integer checkState = (Integer) layerViews.rgroup
//						.getTag(R.id.tag_rbtnstatus);
//				int checkStateVal = checkState.intValue();
//				if (checkStateVal == GROUP_UNCHECKED)
//				{
//					layerViews.rgroup.clearCheck();
//				}
//				else if (checkStateVal == GROUP_ASCII_CHECKED)
//					layerViews.rgroup.check(layerViews.asciiRBtn.getId());
				layerViews.resultTv.setText("");
				layerViews.rgroup.clearCheck();
				MSRProto.invalidateMSRLayerNData(nObj.intValue());
				return;
			}
			layerViews.rgroup.setTag(R.id.tag_rbtnstatus, GROUP_CODE_CHECKED);
			String showTxt = "";
			for (String code : codes) {
				showTxt += "<" + code + ">";
			}
			MSRProto.buildMSRLayerNData(new MSRLayerNParam(nObj.intValue(),
					false, parseCodeData(codes)));
			fromView.setTag(R.id.tag_defaultval, codes);
			layerViews.asciiRBtn.setTag(R.id.tag_defaultval, null);
			switch (fromView.getId()) {
			case R.id.isoHeaderCodeRBtn:
				isoHeaderResult.setText(showTxt);
				break;
			case R.id.isoSeparatorCodeRBtn:
				isoSeparatorResult.setText(showTxt);
				break;
			case R.id.isoSuffixCodeRBtn:
				isoSuffixResult.setText(showTxt);
				break;
			case R.id.jis2PrefixCodeRBtn:
				jis2PrefixResult.setText(showTxt);
				break;
			case R.id.jis2SuffixCodeRBtn:
				jis2SuffixResult.setText(showTxt);
				break;
			case R.id.iButtonAttachPrefixCodeRBtn:
				iButtonAttachPrefixResult.setText(showTxt);
				break;
			case R.id.iButtonAttachSuffixCodeRBtn:
				iButtonAttachSuffixResult.setText(showTxt);
				break;
			case R.id.iButtonRemoveSuffixCodeRBtn:
				iButtonRemoveSuffixResult.setText(showTxt);
				break;
			case R.id.iButtonRemovePrefixCodeRBtn:
				iButtonRemovePrefixResult.setText(showTxt);
				break;
			default:
				break;
			}
		}
	};

	private void restoreAllViews() {
		// RadioGroup清空
		LayerViews layerViews;
		for (int i = 3; i < 12; i++) {
			layerViews = findLayerViewsByTag(i);
			layerViews.rgroup.clearCheck();
			layerViews.asciiRBtn.setTag(R.id.tag_defaultval, null);
			layerViews.codeRBtn.setTag(R.id.tag_defaultval, null);
			layerViews.resultTv.setText("");
		}

		iccardApduEdit.setText("");
		iccardReceiveEdit.setText("");
		iccardMessageEdit.setText("");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.isoHeaderAsciiRBtn:
		case R.id.isoSeparatorAsciiRBtn:
		case R.id.isoSuffixAsciiRBtn:
		case R.id.jis2PrefixAsciiRBtn:
		case R.id.jis2SuffixAsciiRBtn:
		case R.id.iButtonAttachPrefixAsciiRBtn:
		case R.id.iButtonAttachSuffixAsciiRBtn:
		case R.id.iButtonRemovePrefixAsciiRBtn:
		case R.id.iButtonRemoveSuffixAsciiRBtn:
			DialogUtil.showAsciiDialog(MsrActivity.this, v);
			break;
		case R.id.isoHeaderCodeRBtn:
		case R.id.isoSeparatorCodeRBtn:
		case R.id.isoSuffixCodeRBtn:
		case R.id.jis2PrefixCodeRBtn:
		case R.id.jis2SuffixCodeRBtn:
		case R.id.iButtonAttachPrefixCodeRBtn:
		case R.id.iButtonAttachSuffixCodeRBtn:
		case R.id.iButtonRemoveSuffixCodeRBtn:
		case R.id.iButtonRemovePrefixCodeRBtn:

			DialogUtil.showCodeDialog(MsrActivity.this, v);
			break;

		case R.id.loadBtn: {
			String[] fs = MSRFileUtil.getFilenames();
			if (fs == null || fs.length < 1) {
				Toast.makeText(MsrActivity.this,
						getString(R.string.file_not_found), Toast.LENGTH_SHORT)
						.show();
				return;
			}
			DialogUtil.showLoadFileDialog(MsrActivity.this);
			break;
		}
		case R.id.readBtn: {
			int[] configData = new int[9 * 1024];
			boolean flag = msr.protoReadConfig(configData);
			if (!flag) {
				Toast.makeText(MsrActivity.this,
						getString(R.string.read_fail), Toast.LENGTH_SHORT)
						.show();
				return;
			}
			Toast.makeText(MsrActivity.this, getString(R.string.read_success),
					Toast.LENGTH_SHORT).show();
			MSRProto.deconstructMSRConfigData(configData);
			MSRLayer1Params param1 = MSRProto.resolveMSRLayer1();
			updateMSRLayer1UI(param1);
			MSRLayer2Params param2 = MSRProto.resolveMSRLayer2();
			updateMSRLayer2UI(param2);
			List<MSRLayerNParam> paramns = MSRProto.resolveMSRLayerNs();
			updateMSRLayerNUI(paramns);
			break;
		}
		case R.id.writeBtn: {
			MSRLayer1Params params = buildMSRLayer1Params();
			buildMSRLayer1Data(params);
			int id = (int) isoExpansionsLangSpinner.getSelectedItemId();
			buildMSRLayer2Data(id);
			boolean flag = msr.protoWriteConfig();
			if (flag)
				Toast.makeText(MsrActivity.this,
						getString(R.string.write_success), Toast.LENGTH_SHORT)
						.show();
			else
				Toast.makeText(MsrActivity.this,
						getString(R.string.write_fail), Toast.LENGTH_SHORT)
						.show();

			break;
		}
		case R.id.normalBtn: {
			boolean flag = msr.protoTestMode(true);
			if (flag)
				Toast.makeText(MsrActivity.this,
						getString(R.string.normal_mode_success),
						Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(MsrActivity.this,
						getString(R.string.normal_mode_fail),
						Toast.LENGTH_SHORT).show();

			break;
		}
		case R.id.testBtn: {
			boolean flag = msr.protoTestMode(false);
			if (flag)
				Toast.makeText(MsrActivity.this,
						getString(R.string.test_mode_success),
						Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(MsrActivity.this,
						getString(R.string.test_mode_fail), Toast.LENGTH_SHORT)
						.show();

			break;
		}
		case R.id.storeBtn: {
			MSRFileUtil.setParam1(buildMSRLayer1Params());
			MSRFileUtil.setParam2(new MSRLayer2Params(
					(int) isoExpansionsLangSpinner.getSelectedItemId()));
			MSRFileUtil.setParamns(MSRProto.resolveMSRLayerNs());
			DialogUtil.showStoreFileDialog(MsrActivity.this);
			break;
		}
		case R.id.restoreBtn: {
			buildMSRLayer1Data(defaultLayer1Params);
			int id = defaultLayer2Index;
			buildMSRLayer2Data(id);
			MSRProto.clearLayerNDatas();

			boolean flag = msr.protoWriteConfig();
			if (flag)
				Toast.makeText(MsrActivity.this,
						getString(R.string.write_success), Toast.LENGTH_SHORT)
						.show();
			else {
				Toast.makeText(MsrActivity.this,
						getString(R.string.write_fail), Toast.LENGTH_SHORT)
						.show();
				return;
			}
			this.onClick(readBtn);
			restoreAllViews();
			break;
		}
		case R.id.exitBtn:
			MsrActivity.this.finish();
			break;

		case R.id.iccardApduSendBtn: {
			String str = iccardApduEdit.getText().toString().trim();
			msr.protoAPDU(str, dataChangeListener);
			break;
		}
		case R.id.iccardPowerupBtn: {
			msr.protoICPowerup(dataChangeListener);
			break;
		}
		case R.id.iccardPowerdownBtn: {
			msr.protoICPowerdown(dataChangeListener);
			break;
		}
		case R.id.isoClearBtn: {
			isoTestEdit.setText("");
			break;
		}
		case R.id.iButtonClearBtn: {
			iButtonTestEdit.setText("");
			break;
		}
		case R.id.versionBtn: {
			int[] configData = new int[512];
			int len = msr.protoVersion(configData);
			if (len < 0) {
				Toast.makeText(MsrActivity.this,
						getString(R.string.read_fail), Toast.LENGTH_SHORT)
						.show();
				return;
			}
			String ver = parseAsciiData(configData, len);
			Toast.makeText(MsrActivity.this, ver, Toast.LENGTH_LONG).show();
			break;
		}
		default:
			break;
		}
	}

	DataChangeListener dataChangeListener = new DataChangeListener() {

		@Override
		public void dataSndChanged(String str) {
			iccardReceiveEdit.setText("");
			iccardMessageEdit.setText(getString(R.string.send_data) + " \n"
					+ str);
		}

		@Override
		public void dataRcvChanged(String str) {
			String tmpStr = iccardMessageEdit.getText().toString();
			iccardMessageEdit.setText(tmpStr + " \n"
					+ getString(R.string.received_data) + " \n" + str);

		}

		@Override
		public void dataFlagChanged(String str) {
			iccardReceiveEdit.setText(getString(R.string.response_code) + " \n"
					+ str);

		}
	};

	private int[] parseAsciiData(String chars) {
		if (TextUtils.isEmpty(chars))
			return null;
		byte[] bs = null;
		try {
			bs = chars.getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (bs == null)
			return null;
		// bs = EncodingUtils.getAsciiBytes(chars);
		return byteArr2IntArr(bs);
	}

	public static char[] intArr2CharArr(int[] intArr, int len) {
		if (intArr == null || intArr.length < 1 || len < 1)
			return null;
		char[] charArr = new char[len];
		for (int i = 0; i < len; i++)
			charArr[i] = (char) intArr[i];
		return charArr;
	}

	public static int[] byteArr2IntArr(byte[] byteArr) {
		if (byteArr == null || byteArr.length < 1)
			return null;
		int[] intArr = new int[byteArr.length];
		for (int i = 0; i < byteArr.length; i++)
			intArr[i] = (byte) byteArr[i];
		return intArr;
	}

	private String parseAsciiData(int[] datas, int len) {
		char[] cs = intArr2CharArr(datas, len);
		if (cs == null)
			return null;
		return String.valueOf(cs);
	}

	private int[] parseCodeData(List<String> codes) {
		if (codes == null || codes.size() < 1)
			return null;
		int[] ns = new int[codes.size()];
		for (int n = 0; n < codes.size(); n++)
			ns[n] = MSRConstants.KeyCode.get(codes.get(n));
		return ns;
	}

	private List<String> parseCodeData(int[] data) {
		List<String> lst = new ArrayList<String>();
		for (int n = 0; n < data.length; n++) {
			for (Map.Entry<String, Integer> entry : MSRConstants.KeyCode
					.entrySet()) {
				System.out.println(entry.getKey() + "--->" + entry.getValue());
				if (entry.getValue() == data[n]) {
					lst.add(entry.getKey());
					break;
				}
			}

		}
		return lst;
	}

	private void buildMSRLayer2Data(int id) {
		// int id = (int) isoExpansionsLangSpinner.getSelectedItemId();
		MSRProto.buildMSRLayer2Data(id);
	}

	private void updateMSRLayer2UI(MSRLayer2Params param2) {
		isoExpansionsLangSpinner.setSelection(param2.index);
	}

	public class LayerViews {
		public RadioButton asciiRBtn;
		public RadioButton codeRBtn;
		public TextView resultTv;
		public RadioGroup rgroup;

		public boolean isValidViews() {
			return ((asciiRBtn != null) && (codeRBtn != null)
					&& (resultTv != null) && (rgroup != null));
		}
	}

	public void updateMSRLayerNUI(List<MSRLayerNParam> paramns) {
		// List<MSRLayerNParam> paramns = MSRProto.resolveMSRLayerNs();
		LayerViews layerViews;
		for (MSRLayerNParam paramn : paramns) {
			layerViews = findLayerViewsByTag(paramn.n);
			// if(layerViews == null || !layerViews.isValidViews())
			// {
			// continue;
			// }
			if (paramn.asciiMode) {
				String str = parseAsciiData(paramn.data, paramn.data.length);
				layerViews.asciiRBtn.setTag(R.id.tag_defaultval, str);
				layerViews.codeRBtn.setTag(R.id.tag_defaultval, null);
				layerViews.resultTv.setText(str);
				layerViews.rgroup.check(layerViews.asciiRBtn.getId());
			} else {
				List<String> strLst = parseCodeData(paramn.data);
				layerViews.asciiRBtn.setTag(R.id.tag_defaultval, null);
				layerViews.codeRBtn.setTag(R.id.tag_defaultval, strLst);
				String content = "";
				for (String code : strLst) {
					content += "<" + code + ">";
				}
				layerViews.resultTv.setText(content);
				layerViews.rgroup.check(layerViews.codeRBtn.getId());
			}
		}
	}

	private LayerViews findLayerViewsByTag(int layern) {
		List<View> allViews = getAllChildViews();
		// int viewCount = globalLLayout.getChildCount();
		// View v = null;
		Object tagObject = null;
		int tagVal = 0;
		LayerViews layerViews = new LayerViews();
		// for (int n = 0; n < viewCount; n++) {
		for (View v : allViews) {
			// v = globalLLayout.getChildAt(n);
			if (v == null)
				continue;
			tagObject = v.getTag(R.id.tag_layernum);
			if (tagObject == null && !(tagObject instanceof Integer))
				continue;
			tagVal = (Integer) tagObject;
			if (tagVal != layern)
				continue;
			if (TextView.class == v.getClass()) {
				layerViews.resultTv = (TextView) v;
				continue;
			} else if (RadioButton.class == v.getClass()) {
				Boolean asciiType = (Boolean) v.getTag(R.id.tag_asciitype);
				if (asciiType)
					layerViews.asciiRBtn = (RadioButton) v;
				else
					layerViews.codeRBtn = (RadioButton) v;

			} else if (RadioGroup.class == v.getClass()) {
				layerViews.rgroup = (RadioGroup) v;
				continue;
			}

		}
		return layerViews;
	}

	public List<View> getAllChildViews() {
		View view = this.getWindow().getDecorView();
		return getAllChildViews(view);
	}

	private List<View> getAllChildViews(View view) {
		List<View> allchildren = new ArrayList<View>();
		if (view instanceof ViewGroup) {
			ViewGroup vp = (ViewGroup) view;
			for (int i = 0; i < vp.getChildCount(); i++) {
				View viewchild = vp.getChildAt(i);
				allchildren.add(viewchild);
				allchildren.addAll(getAllChildViews(viewchild));
			}
		}
		return allchildren;

	}

	private MSRLayer1Params buildMSRLayer1Params() {

		int id = (int) isoExpansionsSeqSpinner.getSelectedItemId();
		int card1 = 0, card2 = 0, card3 = 0;

		String seqStr = MSRConstants.SEQUNCE[id];
		String strs[] = seqStr.split("-");
		card1 = Integer.parseInt(strs[0]);
		card2 = Integer.parseInt(strs[1]);
		card3 = Integer.parseInt(strs[2]);
		if (!isoEnable1CBox.isChecked())
			card1 = 0;
		if (!isoEnable2CBox.isChecked())
			card2 = 0;
		if (!isoEnable3CBox.isChecked())
			card3 = 0;
		char ch1ss = isoSS1Edit.getText().toString().toCharArray()[0];
		char ch1es = isoES1Edit.getText().toString().toCharArray()[0];
		char ch2ss = isoSS2Edit.getText().toString().toCharArray()[0];
		char ch2es = isoES2Edit.getText().toString().toCharArray()[0];
		char ch3ss = isoSS3Edit.getText().toString().toCharArray()[0];
		char ch3es = isoES3Edit.getText().toString().toCharArray()[0];
		boolean beepFlag = isoExpansionsBeepCBox.isChecked();
		boolean ibmFlag = isoExpansionsIBMCBox.isChecked();
		boolean checkFormatFlag = !isoExpansionsDataCBox.isChecked();
		boolean usbModeFlag = true;
		boolean newFlag = false;

		boolean chard1EnterFlag = isoEnter1CBox.isChecked();
		boolean chard2EnterFlag = isoEnter2CBox.isChecked();
		boolean chard3EnterFlag = isoEnter3CBox.isChecked();

		int iButtonOnOffsetStart = Integer.parseInt(iButtonAttachFromEdit
				.getText().toString()) - 1;
		int iButtonOnOffsetType = (int) iButtonAttachSpinner
				.getSelectedItemId();
		int iButtonOnOffsetLen = Integer.parseInt(iButtonAttachLengthEdit
				.getText().toString()) - 1;
		boolean iButtonOnBeepFlag = iButtonAttachBeepCBox.isChecked();

		int iButtonOffOffsetStart = Integer.parseInt(iButtonRemoveFromEdit
				.getText().toString()) - 1;
		int iButtonOffOffsetType = (int) iButtonRemoveSpinner
				.getSelectedItemId();
		int iButtonOffOffsetLen = Integer.parseInt(iButtonRemoveLengthEdit
				.getText().toString()) - 1;
		boolean iButtonOffBeepFlag = iButtonRemoveBeepCBox.isChecked();
		char iButtonss = iButtonSSEdit.getText().toString().toCharArray()[0];
		char iButtones = iButtonESEdit.getText().toString().toCharArray()[0];
		char jis2ss = jis2SSEdit.getText().toString().toCharArray()[0];
		char jis2es = jis2ESEdit.getText().toString().toCharArray()[0];

		MSRLayer1Params params = new MSRLayer1Params(ch1ss, ch1es, ch2ss,
				ch2es, ch3ss, ch3es, beepFlag, ibmFlag, checkFormatFlag,
				usbModeFlag, newFlag, card1, chard1EnterFlag, card2,
				chard2EnterFlag, card3, chard3EnterFlag, iButtonOnOffsetStart,
				iButtonOnOffsetType, iButtonOnOffsetLen, iButtonOnBeepFlag,
				iButtonOffOffsetStart, iButtonOffOffsetType,
				iButtonOffOffsetLen, iButtonOffBeepFlag, iButtonss, iButtones,
				jis2ss, jis2es);

		return params;
	}

	private void buildMSRLayer1Data(MSRLayer1Params params) {
		// MSRLayer1Params params = buildMSRLayer1Params();

		MSRProto.buildMSRLayer1Data(params);
	}

	private int getSeqIndex(String str) {
		for (int n = 0; n < MSRConstants.SEQUNCE.length; n++) {
			if (str.equals(MSRConstants.SEQUNCE[n]))
				return n;
		}
		return -1;
	}

	private void updateMSRLayer1UI(MSRLayer1Params param1) {

		String seqStr = (param1.card1 & 0x3) + "-" + (param1.card2 & 0x3) + "-"
				+ (param1.card3 & 0x3);
		int seqIndex = getSeqIndex(seqStr);
		isoExpansionsSeqSpinner.setSelection(seqIndex);

		isoEnable1CBox.setChecked(true);
		isoEnable2CBox.setChecked(true);
		isoEnable3CBox.setChecked(true);

		isoSS1Edit.setText(String.valueOf(param1.ch1ss));
		isoES1Edit.setText(String.valueOf(param1.ch1es));
		isoSS2Edit.setText(String.valueOf(param1.ch2ss));
		isoES2Edit.setText(String.valueOf(param1.ch2es));
		isoSS3Edit.setText(String.valueOf(param1.ch3ss));

		isoES3Edit.setText(String.valueOf(param1.ch3es));

		isoExpansionsBeepCBox.setChecked(param1.beepFlag);
		isoExpansionsIBMCBox.setChecked(param1.ibmFlag);
		isoExpansionsDataCBox.setChecked(!param1.checkFormatFlag);

		isoEnter1CBox.setChecked(param1.chard1EnterFlag);
		isoEnter2CBox.setChecked(param1.chard2EnterFlag);
		isoEnter3CBox.setChecked(param1.chard3EnterFlag);

		iButtonAttachFromEdit.setText(String
				.valueOf(param1.iButtonOnOffsetStart + 1));
		iButtonAttachSpinner.setSelection(param1.iButtonOnOffsetType);
		iButtonAttachLengthEdit.setText(String
				.valueOf(param1.iButtonOnOffsetLen + 1));
		iButtonAttachBeepCBox.setChecked(param1.iButtonOnBeepFlag);

		iButtonRemoveFromEdit.setText(String
				.valueOf(param1.iButtonOffOffsetStart + 1));
		iButtonRemoveSpinner.setSelection(param1.iButtonOffOffsetType);
		iButtonRemoveLengthEdit.setText(String
				.valueOf(param1.iButtonOffOffsetLen + 1));
		iButtonRemoveBeepCBox.setChecked(param1.iButtonOffBeepFlag);

		iButtonSSEdit.setText(String.valueOf(param1.iButtonss));
		iButtonESEdit.setText(String.valueOf(param1.iButtones));

		jis2SSEdit.setText(String.valueOf(param1.jis2ss));
		jis2ESEdit.setText(String.valueOf(param1.jis2es));

	}

	@Override
	public void onCheckedChanged(CompoundButton view, boolean isChecked) {
		switch (view.getId()) {
		case R.id.isoEnable1CBox:
			isoSS1Edit.setEnabled(isChecked);
			isoES1Edit.setEnabled(isChecked);
			isoEnter1CBox.setChecked(isChecked);
			isoEnter1CBox.setEnabled(isChecked);

			break;
		case R.id.isoEnable2CBox:
			isoSS2Edit.setEnabled(isChecked);
			isoES2Edit.setEnabled(isChecked);
			isoEnter2CBox.setChecked(isChecked);
			isoEnter2CBox.setEnabled(isChecked);
			break;
		case R.id.isoEnable3CBox:
			isoSS3Edit.setEnabled(isChecked);
			isoES3Edit.setEnabled(isChecked);
			isoEnter3CBox.setChecked(isChecked);
			isoEnter3CBox.setEnabled(isChecked);
			break;
		default:
			break;
		}
	}

}