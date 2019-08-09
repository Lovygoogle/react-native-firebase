package io.invertase.firebase.admob;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.google.ads.consent.AdProvider;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.annotation.Nullable;

import io.invertase.firebase.common.ReactNativeFirebaseModule;

public class ReactNativeFirebaseAdmobConsentModule extends ReactNativeFirebaseModule {
  private static final String TAG = "AdmobConsent";
  private ConsentInformation consentInformation;
  private ConsentForm consentForm;

  public ReactNativeFirebaseAdmobConsentModule(ReactApplicationContext reactContext) {
    super(reactContext, TAG);
    consentInformation = ConsentInformation.getInstance(reactContext);
  }

  private int getConsentStatusInt(ConsentStatus consentStatus) {
    switch (consentStatus) {
      case PERSONALIZED:
        return 1;
      case NON_PERSONALIZED:
        return 2;
      case UNKNOWN:
      default:
        return 0;
    }
  }

  @ReactMethod
  public void requestInfoUpdate(ReadableArray publisherIds, Promise promise) {
    @SuppressWarnings("SuspiciousToArrayCall")
    String[] publisherIdsArray = publisherIds.toArrayList().toArray(new String[0]);

    consentInformation.requestConsentInfoUpdate(publisherIdsArray, new ConsentInfoUpdateListener() {
      @Override
      public void onConsentInfoUpdated(ConsentStatus consentStatus) {
        WritableMap requestInfoMap = Arguments.createMap();
        requestInfoMap.putInt("status", getConsentStatusInt(consentStatus));
        requestInfoMap.putBoolean("isRequestLocationInEeaOrUnknown", consentInformation.isRequestLocationInEeaOrUnknown());
        promise.resolve(requestInfoMap);
      }

      @Override
      public void onFailedToUpdateConsentInfo(String reason) {
        rejectPromiseWithCodeAndMessage(promise, "consent-update-failed", reason);
      }
    });
  }

  @ReactMethod
  public void showForm(ReadableMap options, Promise promise) {
    getCurrentActivity().runOnUiThread(() -> {
      URL privacyUrl = null;

      try {
        privacyUrl = new URL(options.getString("privacyPolicy"));
      } catch (MalformedURLException e) {
        // Validated in JS land
      }

      ConsentFormListener listener = new ConsentFormListener() {
        @Override
        public void onConsentFormLoaded() {
          consentForm.show();
        }

        @Override
        public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
          WritableMap consentFormMap = Arguments.createMap();
          consentFormMap.putInt("status", getConsentStatusInt(consentStatus));
          consentFormMap.putBoolean("userPrefersAdFree", userPrefersAdFree);
          promise.resolve(consentFormMap);
        }

        @Override
        public void onConsentFormError(String reason) {
          rejectPromiseWithCodeAndMessage(promise, "consent-form-error", reason);
        }
      };

      ConsentForm.Builder builder = new ConsentForm.Builder(getCurrentActivity(), privacyUrl)
        .withListener(listener);

      if (options.hasKey("withPersonalizedAds") && options.getBoolean("withPersonalizedAds")) {
        builder = builder.withPersonalizedAdsOption();
      }

      if (options.hasKey("withNonPersonalizedAds") && options.getBoolean("withNonPersonalizedAds")) {
        builder = builder.withNonPersonalizedAdsOption();
      }

      if (options.hasKey("withAdFree") && options.getBoolean("withAdFree")) {
        builder = builder.withAdFreeOption();
      }

      consentForm = builder.build();
      consentForm.load();
    });
  }

  @ReactMethod
  public void getAdProviders(Promise promise) {
    List<AdProvider> providers = consentInformation.getAdProviders();

    WritableArray formattedAdProviders = Arguments.createArray();

    for (AdProvider provider : providers) {
      WritableMap formattedProvider = Arguments.createMap();
      formattedProvider.putString("companyName", provider.getName());
      formattedProvider.putString("companyId", provider.getId());
      formattedProvider.putString("privacyPolicyUrl", provider.getPrivacyPolicyUrlString());
      formattedAdProviders.pushMap(formattedProvider);
    }

    promise.resolve(formattedAdProviders);
  }

  @ReactMethod
  public void setDebugGeography(@Nullable int geography, Promise promise) {
    if (geography == 0) {
      consentInformation.setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_DISABLED);
    } else if (geography == 1) {
      consentInformation.setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
    } else if (geography == 2) {
      consentInformation.setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_NOT_EEA);
    }

    promise.resolve(null);
  }

}
