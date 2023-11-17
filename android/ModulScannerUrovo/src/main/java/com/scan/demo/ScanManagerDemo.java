package com.scan.demo;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.scanner.configuration.PropertyID;
import android.device.scanner.configuration.Symbology;
import android.os.Handler;
import android.os.Message;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.device.ScanManager;
import android.preference.CheckBoxPreference;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;


public class ScanManagerDemo extends AppCompatActivity {

    private static final String TAG = "ScanManagerDemo";
    private static final boolean DEBUG = true;

    private static final String ACTION_DECODE =  "android.intent.ACTION_DECODE_DATA";
    private static final String BARCODE_STRING_TAG ="barcode_string";
    private static final String BARCODE_TYPE_TAG = "barcodeType";
    private static final String BARCODE_LENGTH_TAG = "length";
    private static final String DECODE_DATA_TAG ="barcode";


    private EditText showScanResult = null;

    private ScanManager mScanManager = null;

    private static Map<String, BarcodeHolder> mBarcodeMap = new HashMap<String, BarcodeHolder>();

    private static final int MSG_SHOW_SCAN_RESULT = 1;

    private static final int[] SCAN_KEYCODE = {520, 521, 522, 523};

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                byte[] barcode = intent.getByteArrayExtra(DECODE_DATA_TAG);
                int barcodeLen = intent.getIntExtra(BARCODE_LENGTH_TAG, 0);
                byte temp = intent.getByteExtra(BARCODE_TYPE_TAG, (byte) 0);
                String barcodeStr = intent.getStringExtra(BARCODE_STRING_TAG);
                String scanResult = new String(barcode, 0, barcodeLen);

                String jsonString =  "{'length':'" + barcodeLen + "','barcode':'" + scanResult + "','barcode_string':'" + barcodeStr + "'}";
                Message msg = mHandler.obtainMessage(MSG_SHOW_SCAN_RESULT);
                msg.obj = jsonString;
                mHandler.sendMessage(msg);

                Log.d(TAG, "ScannerBroadcastReceiver: " + jsonString);
//                try {
//                    getReactInstanceManager().getCurrentReactContext()
//                            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
//                            .emit("BarcodeReceiverUrovo", jsonString);
//
//                } catch (Exception e){
//                    Log.e("ReactNative", "Caught Exception: " + e.getMessage());
//                }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SHOW_SCAN_RESULT:
                    String scanResult = (String) msg.obj;
                    printScanResult(scanResult);
                    break;
            }
        }
    };

    private void registerReceiver(boolean register) {
        Log.d(TAG, "ScannerBroadcastReceiver (private void registerReceiver)" );
        if (register && mScanManager != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_DECODE);
            registerReceiver(mReceiver, filter);
        } else if (mScanManager != null) {
            mScanManager.stopDecode();
            unregisterReceiver(mReceiver);
        }
    }

    private void printScanResult(String msg) {
        if (msg == null || showScanResult == null) {
            LogI("printScanResult , ignore to show msg:" + msg + ",showScanResult" + showScanResult);
            return;
        }
        showScanResult.setText(msg);
    }

    private void initView() {
        Log.d(TAG, "ScannerBroadcastReceiver (private void initView)" );
        showScanResult = (EditText) findViewById(R.id.scan_result);
    }



    private void initScan() {
        Log.d(TAG, "ScannerBroadcastReceiver (private void initScan)" );
        mScanManager = new ScanManager();
        mScanManager.switchOutputMode(0);
        boolean powerOn = mScanManager.getScannerState();
        if (!powerOn) {
            powerOn = mScanManager.openScanner();
            if (!powerOn) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Scanner cannot be turned on!");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog mAlertDialog = builder.create();
                mAlertDialog.show();
            }
        }
        initBarcodeParameters();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_scan_manager_demo);
        initView();
    }


    @Override
    protected void onPause() {
        super.onPause();
        registerReceiver(false);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "ScannerBroadcastReceiver (private void onResume)" );
        super.onResume();
        initScan();
        registerReceiver(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        LogD("onKeyUp, keyCode:" + keyCode);
        if (keyCode >= SCAN_KEYCODE[0] && keyCode <= SCAN_KEYCODE[SCAN_KEYCODE.length - 1]) {
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogD("onKeyDown, keyCode:" + keyCode);
        if (keyCode >= SCAN_KEYCODE[0] && keyCode <= SCAN_KEYCODE[SCAN_KEYCODE.length - 1]) {
        }
        return super.onKeyDown(keyCode, event);
    }

    private void LogD(String msg) {
        if (DEBUG) {
            android.util.Log.d(TAG, msg);
        }
    }

    private void LogI(String msg) {
        android.util.Log.i(TAG, msg);
    }

    private void initBarcodeParameters() {
        Log.d(TAG, "ScannerBroadcastReceiver (private void initBarcodeParameters)" );
        mBarcodeMap.clear();
        BarcodeHolder holder = new BarcodeHolder();
        // Symbology.AZTEC
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.AZTEC_ENABLE};
        holder.mParaKeys = new String[]{"AZTEC_ENABLE"};
        mBarcodeMap.put(Symbology.AZTEC + "", holder);
        // Symbology.CHINESE25
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.C25_ENABLE};
        holder.mParaKeys = new String[]{"C25_ENABLE"};
        mBarcodeMap.put(Symbology.CHINESE25 + "", holder);
        // Symbology.CODABAR
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mBarcodeLength1 = new EditTextPreference(this);
        holder.mBarcodeLength2 = new EditTextPreference(this);
        holder.mBarcodeNOTIS = new CheckBoxPreference(this);
        holder.mBarcodeCLSI = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.CODABAR_ENABLE, PropertyID.CODABAR_LENGTH1, PropertyID.CODABAR_LENGTH2, PropertyID.CODABAR_NOTIS, PropertyID.CODABAR_CLSI};
        holder.mParaKeys = new String[]{"CODABAR_ENABLE", "CODABAR_LENGTH1", "CODABAR_LENGTH2", "CODABAR_NOTIS", "CODABAR_CLSI"};
        mBarcodeMap.put(Symbology.CODABAR + "", holder);
        // Symbology.CODE11
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mBarcodeLength1 = new EditTextPreference(this);
        holder.mBarcodeLength2 = new EditTextPreference(this);
        holder.mBarcodeCheckDigit = new ListPreference(this);
        holder.mParaIds = new int[]{PropertyID.CODE11_ENABLE, PropertyID.CODE11_LENGTH1, PropertyID.CODE11_LENGTH2, PropertyID.CODE11_SEND_CHECK};
        holder.mParaKeys = new String[]{"CODE11_ENABLE", "CODE11_LENGTH1", "CODE11_LENGTH2", "CODE11_SEND_CHECK"};
        mBarcodeMap.put(Symbology.CODE11 + "", holder);
        // Symbology.CODE32
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.CODE32_ENABLE};
        holder.mParaKeys = new String[]{"CODE32_ENABLE"};
        mBarcodeMap.put(Symbology.CODE32 + "", holder);
        // Symbology.CODE39
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mBarcodeLength1 = new EditTextPreference(this);
        holder.mBarcodeLength2 = new EditTextPreference(this);
        holder.mBarcodeChecksum = new CheckBoxPreference(this);
        holder.mBarcodeSendCheck = new CheckBoxPreference(this);
        holder.mBarcodeFullASCII = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.CODE39_ENABLE, PropertyID.CODE39_LENGTH1, PropertyID.CODE39_LENGTH2, PropertyID.CODE39_ENABLE_CHECK, PropertyID.CODE39_SEND_CHECK, PropertyID.CODE39_FULL_ASCII};
        holder.mParaKeys = new String[]{"CODE39_ENABLE", "CODE39_LENGTH1", "CODE39_LENGTH2", "CODE39_ENABLE_CHECK", "CODE39_SEND_CHECK", "CODE39_FULL_ASCII"};
        mBarcodeMap.put(Symbology.CODE39 + "", holder);
        // Symbology.CODE93
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mBarcodeLength1 = new EditTextPreference(this);
        holder.mBarcodeLength2 = new EditTextPreference(this);
        holder.mParaIds = new int[]{PropertyID.CODE93_ENABLE, PropertyID.CODE93_LENGTH1, PropertyID.CODE93_LENGTH2};
        holder.mParaKeys = new String[]{"CODE93_ENABLE", "CODE93_LENGTH1", "CODE93_LENGTH2"};
        mBarcodeMap.put(Symbology.CODE93 + "", holder);
        // Symbology.CODE128
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mBarcodeLength1 = new EditTextPreference(this);
        holder.mBarcodeLength2 = new EditTextPreference(this);
        holder.mBarcodeISBT = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.CODE128_ENABLE, PropertyID.CODE128_LENGTH1, PropertyID.CODE128_LENGTH2, PropertyID.CODE128_CHECK_ISBT_TABLE};
        holder.mParaKeys = new String[]{"CODE128_ENABLE", "CODE128_LENGTH1", "CODE128_LENGTH2", "CODE128_CHECK_ISBT_TABLE"};
        mBarcodeMap.put(Symbology.CODE128 + "", holder);
        // Symbology.COMPOSITE_CC_AB
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.COMPOSITE_CC_AB_ENABLE};
        holder.mParaKeys = new String[]{"COMPOSITE_CC_AB_ENABLE"};
        mBarcodeMap.put(Symbology.COMPOSITE_CC_AB + "", holder);
        // Symbology.COMPOSITE_CC_C
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.COMPOSITE_CC_C_ENABLE};
        holder.mParaKeys = new String[]{"COMPOSITE_CC_C_ENABLE"};
        mBarcodeMap.put(Symbology.COMPOSITE_CC_C + "", holder);
        // Symbology.DATAMATRIX
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.DATAMATRIX_ENABLE};
        holder.mParaKeys = new String[]{"DATAMATRIX_ENABLE"};
        mBarcodeMap.put(Symbology.DATAMATRIX + "", holder);
        // Symbology.DISCRETE25
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.D25_ENABLE};
        holder.mParaKeys = new String[]{"D25_ENABLE"};
        mBarcodeMap.put(Symbology.DISCRETE25 + "", holder);
        // Symbology.EAN8
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.EAN8_ENABLE};
        holder.mParaKeys = new String[]{"EAN8_ENABLE"};
        mBarcodeMap.put(Symbology.EAN8 + "", holder);
        // Symbology.EAN13
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mBarcodeBookland = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.EAN13_ENABLE, PropertyID.EAN13_BOOKLANDEAN};
        holder.mParaKeys = new String[]{"EAN13_ENABLE", "EAN13_BOOKLANDEAN"};
        mBarcodeMap.put(Symbology.EAN13 + "", holder);
        // Symbology.GS1_14
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.GS1_14_ENABLE};
        holder.mParaKeys = new String[]{"GS1_14_ENABLE"};
        mBarcodeMap.put(Symbology.GS1_14 + "", holder);
        // Symbology.GS1_128
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.CODE128_GS1_ENABLE};
        holder.mParaKeys = new String[]{"CODE128_GS1_ENABLE"};
        mBarcodeMap.put(Symbology.GS1_128 + "", holder);
        // Symbology.GS1_EXP
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mBarcodeLength1 = new EditTextPreference(this);
        holder.mBarcodeLength2 = new EditTextPreference(this);
        holder.mParaIds = new int[]{PropertyID.GS1_EXP_ENABLE, PropertyID.GS1_EXP_LENGTH1, PropertyID.GS1_EXP_LENGTH2};
        holder.mParaKeys = new String[]{"GS1_EXP_ENABLE", "GS1_EXP_LENGTH1", "GS1_EXP_LENGTH2"};
        mBarcodeMap.put(Symbology.GS1_EXP + "", holder);
        // Symbology.GS1_LIMIT
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.GS1_LIMIT_ENABLE};
        holder.mParaKeys = new String[]{"GS1_LIMIT_ENABLE"};
        mBarcodeMap.put(Symbology.GS1_LIMIT + "", holder);
        // Symbology.INTERLEAVED25
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mBarcodeLength1 = new EditTextPreference(this);
        holder.mBarcodeLength2 = new EditTextPreference(this);
        holder.mBarcodeChecksum = new CheckBoxPreference(this);
        holder.mBarcodeSendCheck = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.I25_ENABLE, PropertyID.I25_LENGTH1, PropertyID.I25_LENGTH2, PropertyID.I25_ENABLE_CHECK, PropertyID.I25_SEND_CHECK};
        holder.mParaKeys = new String[]{"I25_ENABLE", "I25_LENGTH1", "I25_LENGTH2", "I25_ENABLE_CHECK", "I25_SEND_CHECK"};
        mBarcodeMap.put(Symbology.INTERLEAVED25 + "", holder);
        // Symbology.MATRIX25
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.M25_ENABLE};
        holder.mParaKeys = new String[]{"M25_ENABLE"};
        mBarcodeMap.put(Symbology.MATRIX25 + "", holder);
        // Symbology.MAXICODE
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.MAXICODE_ENABLE};
        holder.mParaKeys = new String[]{"MAXICODE_ENABLE"};
        mBarcodeMap.put(Symbology.MAXICODE + "", holder);
        // Symbology.MICROPDF417
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.MICROPDF417_ENABLE};
        holder.mParaKeys = new String[]{"MICROPDF417_ENABLE"};
        mBarcodeMap.put(Symbology.MICROPDF417 + "", holder);
        // Symbology.MSI
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mBarcodeLength1 = new EditTextPreference(this);
        holder.mBarcodeLength2 = new EditTextPreference(this);
        holder.mBarcodeSecondChecksum = new CheckBoxPreference(this);
        holder.mBarcodeSendCheck = new CheckBoxPreference(this);
        holder.mBarcodeSecondChecksumMode = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.MSI_ENABLE, PropertyID.MSI_LENGTH1, PropertyID.MSI_LENGTH2, PropertyID.MSI_REQUIRE_2_CHECK, PropertyID.MSI_SEND_CHECK, PropertyID.MSI_CHECK_2_MOD_11};
        holder.mParaKeys = new String[]{"MSI_ENABLE", "MSI_LENGTH1", "MSI_LENGTH2", "MSI_REQUIRE_2_CHECK", "MSI_SEND_CHECK", "MSI_CHECK_2_MOD_11"};
        mBarcodeMap.put(Symbology.MSI + "", holder);
        // Symbology.PDF417
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.PDF417_ENABLE};
        holder.mParaKeys = new String[]{"PDF417_ENABLE"};
        mBarcodeMap.put(Symbology.PDF417 + "", holder);
        // Symbology.QRCODE
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.QRCODE_ENABLE};
        holder.mParaKeys = new String[]{"QRCODE_ENABLE"};
        mBarcodeMap.put(Symbology.QRCODE + "", holder);
        // Symbology.TRIOPTIC
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.TRIOPTIC_ENABLE};
        holder.mParaKeys = new String[]{"TRIOPTIC_ENABLE"};
        mBarcodeMap.put(Symbology.TRIOPTIC + "", holder);
        // Symbology.UPCA
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mBarcodeChecksum = new CheckBoxPreference(this);
        holder.mBarcodeSystemDigit = new CheckBoxPreference(this);
        holder.mBarcodeConvertEAN13 = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.UPCA_ENABLE, PropertyID.UPCA_SEND_CHECK, PropertyID.UPCA_SEND_SYS, PropertyID.UPCA_TO_EAN13};
        holder.mParaKeys = new String[]{"UPCA_ENABLE", "UPCA_SEND_CHECK", "UPCA_SEND_SYS", "UPCA_TO_EAN13"};
        mBarcodeMap.put(Symbology.UPCA + "", holder);
        // Symbology.UPCE
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mBarcodeChecksum = new CheckBoxPreference(this);
        holder.mBarcodeSystemDigit = new CheckBoxPreference(this);
        holder.mBarcodeConvertUPCA = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.UPCE_ENABLE, PropertyID.UPCE_SEND_CHECK, PropertyID.UPCE_SEND_SYS, PropertyID.UPCE_TO_UPCA};
        holder.mParaKeys = new String[]{"UPCE_ENABLE", "UPCE_SEND_CHECK", "UPCE_SEND_SYS", "UPCE_TO_UPCA"};
        mBarcodeMap.put(Symbology.UPCE + "", holder);
        // Symbology.UPCE1
        holder = new BarcodeHolder();
        holder.mBarcodeEnable = new CheckBoxPreference(this);
        holder.mParaIds = new int[]{PropertyID.UPCE1_ENABLE};
        holder.mParaKeys = new String[]{"UPCE1_ENABLE"};
        mBarcodeMap.put(Symbology.UPCE1 + "", holder);
    }

    static class BarcodeHolder {
        CheckBoxPreference mBarcodeEnable = null;
        EditTextPreference mBarcodeLength1 = null;
        EditTextPreference mBarcodeLength2 = null;

        CheckBoxPreference mBarcodeNOTIS = null;
        CheckBoxPreference mBarcodeCLSI = null;

        CheckBoxPreference mBarcodeISBT = null;
        CheckBoxPreference mBarcodeChecksum = null;
        CheckBoxPreference mBarcodeSendCheck = null;
        CheckBoxPreference mBarcodeFullASCII = null;
        ListPreference mBarcodeCheckDigit = null;
        CheckBoxPreference mBarcodeBookland = null;
        CheckBoxPreference mBarcodeSecondChecksum = null;
        CheckBoxPreference mBarcodeSecondChecksumMode = null;
        CheckBoxPreference mBarcodeSystemDigit = null;
        CheckBoxPreference mBarcodeConvertEAN13 = null;
        CheckBoxPreference mBarcodeConvertUPCA = null;
        int[] mParaIds = null;
        String[] mParaKeys = null;
    }
}
