package io.invertase.firebase.instanceid;


import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

public class RNFirebaseInstanceId extends ReactContextBaseJavaModule {

  private static final String TAG = "RNFirebaseInstanceId";

  public RNFirebaseInstanceId(ReactApplicationContext reactContext) {
    super(reactContext);
    Log.d(TAG, "New instance");
  }

  @Override
  public String getName() {
    return TAG;
  }

  @ReactMethod
  public void delete(Promise promise){
    try {
      Log.d(TAG, "Deleting instance id");
      FirebaseInstanceId.getInstance().deleteInstanceId();
      promise.resolve(null);
    } catch (IOException e) {
      Log.e(TAG, e.getMessage());
      promise.reject("instance_id_error", e.getMessage());
    }
  }

  @ReactMethod
  public void get(Promise promise){
    String id = FirebaseInstanceId.getInstance().getId();
    promise.resolve(id);
  }
}
