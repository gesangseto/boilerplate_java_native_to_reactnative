package com.rnurovoscanner; // replace your-apps-package-name with your app’s package name

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;
//UROVO LIBRARY

public class UrovoScannerBackup extends ReactContextBaseJavaModule {
    private String barcodeResult;
    private int count = 0;

    UrovoScannerBackup(ReactApplicationContext context) {
        super(context);
    }

    // add to CalendarModule.java
    @Override
    public String getName() {
        return "UrovoScanner";
    }

    @ReactMethod
    public void createUrovoScanner(Callback callback) {
        callback.invoke("Data dari module");
    }

    @ReactMethod
    public void createUrovoScannerPromise(Promise promise) {
        try {
            promise.resolve("Test Success data urovo scanner");
            sendEvent(getReactApplicationContext(), "BarcodeStremerUrovo", count += 1);
        } catch (Exception e) {
            promise.reject("Test data urovo scanner", e);
        }
    }

    @ReactMethod
    private void sendEvent(ReactContext reactContext, String eventName, int params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }
}

