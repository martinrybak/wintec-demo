package cn.wintec.wtandroiddemo;


import cn.wintec.wtandroidjar.ComIO;
import cn.wintec.wtandroidjar.ComIO.Baudrate;
import cn.wintec.wtandroidjar.ComIO.DataBit;
import cn.wintec.wtandroidjar.ComIO.FlowControl;
import cn.wintec.wtandroidjar.ComIO.Param;
import cn.wintec.wtandroidjar.ComIO.StopBit;
import cn.wintec.wtandroidjar.ComIO.VerifyBit;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class SetSerialPort extends Activity{
	String devicePath;
	

    private void comParamToGUI(ComIO.Param param) {
        Spinner spinnerBaudrate     = (Spinner)findViewById(R.id.spinner_baudrate);
        Spinner spinnerVerifyBit    = (Spinner)findViewById(R.id.spinner_verify_bit);
        Spinner spinnerDataBit      = (Spinner)findViewById(R.id.spinner_data_bit);
        Spinner spinnerStopBit      = (Spinner)findViewById(R.id.spinner_stop_bit);
        Spinner spinnerFlowControl  = (Spinner)findViewById(R.id.spinner_flow_control);

        try{
            spinnerBaudrate.setSelection(param.baudrate.ordinal());
            }
            finally{};
        spinnerVerifyBit.setSelection(param.verifyBit.ordinal());
        spinnerDataBit.setSelection(param.dataBit.ordinal());
        spinnerStopBit.setSelection(param.stopBit.ordinal());
        spinnerFlowControl.setSelection(param.flowControl.ordinal());
    }
    private void baudrateToGUI(ComIO.Baudrate baudrate) {
        Spinner spinnerBaudrate     = (Spinner)findViewById(R.id.spinner_baudrate);     
        try{
            spinnerBaudrate.setSelection(baudrate.ordinal());
            }
            finally{};
    }
    private ComIO.Param comGUI2Param() {
    	ComIO.Param param = new ComIO(devicePath).new Param();
        Spinner spinnerBaudrate     = (Spinner)findViewById(R.id.spinner_baudrate);
        Spinner spinnerVerifyBit    = (Spinner)findViewById(R.id.spinner_verify_bit);
        Spinner spinnerDataBit      = (Spinner)findViewById(R.id.spinner_data_bit);
        Spinner spinnerStopBit      = (Spinner)findViewById(R.id.spinner_stop_bit);
        Spinner spinnerFlowControl  = (Spinner)findViewById(R.id.spinner_flow_control);
        
        param.baudrate      = (ComIO.Baudrate.values())[spinnerBaudrate.getSelectedItemPosition()];
        param.verifyBit     = (ComIO.VerifyBit.values())[spinnerVerifyBit.getSelectedItemPosition()];
        param.dataBit       = (ComIO.DataBit.values())[spinnerDataBit.getSelectedItemPosition()];
        param.stopBit       = (ComIO.StopBit.values())[spinnerStopBit.getSelectedItemPosition()];
        param.flowControl   = (ComIO.FlowControl.values())[spinnerFlowControl.getSelectedItemPosition()];
        return param;
    }
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_serial_port);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        devicePath=bundle.getString("device_path");
        ComIO.Baudrate baudrate=(ComIO.Baudrate)bundle.getSerializable("baudrate");
        baudrateToGUI(baudrate);
   //     ComIO.Param param = new ComIO(devicePath).getSerialParam();
    //    comParamToGUI(param);
       
        Button buttonCancel = (Button)findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	setResult(RESULT_CANCELED);
                SetSerialPort.this.finish();
            }
        });

        Button buttonOK = (Button)findViewById(R.id.button_ok);
        buttonOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	ComIO.Param param = comGUI2Param();
                new ComIO(devicePath).setSerialParam(
                        param.baudrate,
                        param.verifyBit,
                        param.dataBit,
                        param.stopBit,
                        param.flowControl);
                new ComIO(devicePath).setSerialBaudrate(param.baudrate);
                Intent intent=new Intent();
                intent.putExtra("baudrate", param.baudrate.toString());
                setResult(RESULT_OK,intent);
                SetSerialPort.this.finish();
            }
        });
    }
}
