package com.admob.control;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.admob.control.dialog.PrepareLoadingAdsDialog;
import com.admob.control.funtion.AdCallback;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.subvip.manage.PurchaseManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;

public class AdmobManager {
    private static final String TAG = AdmobManager.class.getName();
    private long timeReloadAds = 0;
    private static AdmobManager instance;
    private final Handler handler = new Handler();
    private PrepareLoadingAdsDialog dialog;
    private boolean isFan;


    public void setTimeReloadAds(long timeReloadAds) {
        this.timeReloadAds = timeReloadAds;
    }

    public static AdmobManager getInstance() {
        if (instance == null) {
            instance = new AdmobManager();
        }
        return instance;
    }

    private AdmobManager() {

    }

    public void init(Context context, String deviceID) {
        try {
            MobileAds.initialize(context, initializationStatus -> {
            });
            MobileAds.setRequestConfiguration(new RequestConfiguration.Builder()
                    .setTestDeviceIds(Collections.singletonList(deviceID)).build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AdRequest getAdRequest() {
        AdRequest.Builder builder = new AdRequest.Builder();
//        if (isFan) {
//            Bundle extras = new FacebookExtras()
//                    .setNativeBanner(true)
//                    .build();
//
//            builder.addNetworkExtrasBundle(FacebookAdapter.class, extras);
//        }
        return builder.build();
    }


    public void loadSplashInterstitialAds(final Activity context, String id, int timeout, final AdCallback adListener) {
        final InterstitialAd[] miInterstitialAd = new InterstitialAd[1];
        if (context.isFinishing()) {
            return;
        }
        if (PurchaseManager.getInstance().isPremiumed(context)) {
            if (adListener != null) {
                adListener.onAdClosed();
            }
            return;
        }
        loadInterAds(context, id, new AdCallback() {
            @Override
            public void interCallback(InterstitialAd interstitialAd) {
                super.interCallback(interstitialAd);
                miInterstitialAd[0] = interstitialAd;
            }
        });
        handler.postDelayed(() -> {
            if (miInterstitialAd[0] != null && !context.isFinishing()) {
                showInterstitial(context, miInterstitialAd[0], adListener, false);
                return;
            }
            if (adListener != null) {
                adListener.onAdClosed();
            }
        }, timeout);
    }

    public void destroyTimeout() {
        handler.removeCallbacksAndMessages(null);
    }

    public void loadInterAds(Activity context, String id, AdCallback callback) {
        if (PurchaseManager.getInstance().isPremiumed(context)) {
            callback.onAdFailedToLoad(null);
            return;
        }
        InterstitialAd.load(context, id, getAdRequest(), new InterstitialAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                callback.onAdFailedToLoad(loadAdError);
                Log.d("TAG2", "onAdFailedToLoad: ");
            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                callback.interCallback(interstitialAd);
                Log.d("TAG2", "onAdLoaded: ");
            }
        });
    }

    public void showInterstitial(final Activity context, final InterstitialAd mInterstitialAd, final AdCallback callback) {
        showInterstitial(context, mInterstitialAd, callback, true);
    }

    private void showInterstitial(final Activity context, final InterstitialAd mInterstitialAd, final AdCallback callback, final boolean shouldReloadAds) {
        if (PurchaseManager.getInstance().isPremiumed(context)) {
            if (callback != null) {
                callback.onAdClosed();
            }
            return;
        }
        if (mInterstitialAd == null) {
            if (callback != null) {
                callback.onAdClosed();
            }
            return;
        }
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when fullscreen content is dismissed.
                Log.d("TAG", "The ad was dismissed.");
                if (AppOpenManager.getInstance().isInitialized()) {
                    AppOpenManager.getInstance().enableAppResume();
                    Log.d(TAG, "enableAppResume: ");

                }

                if (callback != null) {
                    callback.onAdClosed();
                }
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                Log.d("TAG", "The ad failed to show.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                Log.d("TAG", "The ad was shown.");
            }
        });

        showInterstitialAd(context, mInterstitialAd, callback);
    }

    private void showInterstitialAd(Activity context, final InterstitialAd mInterstitialAd, AdCallback callback) {
        if (ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)
                && mInterstitialAd != null) {
//            try {
//                dialog = new PrepareLoadingAdsDialog(context);
//                dialog.show();
//            } catch (Exception e) {
//                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
//                dialog = null;
//                e.printStackTrace();
//            }
            if (AppOpenManager.getInstance().isInitialized()) {
                AppOpenManager.getInstance().disableAppResume();
                Log.d(TAG, "disableAppResume: ");
            }
            mInterstitialAd.show(context);
//            if (dialog != null) {
//                dialog.dismiss();
//            }
        } else {
            if (callback != null) {
                callback.onAdClosed();
            }
        }
    }

    public void loadBanner(final Activity mActivity, String id) {
        final FrameLayout adContainer = mActivity.findViewById(R.id.banner_container);
        final ShimmerFrameLayout containerShimmer = mActivity.findViewById(R.id.shimmer_container);
        loadBanner(mActivity, id, adContainer, containerShimmer);
    }

    @SuppressLint("CutPasteId")
    public void loadBannerFragment(final Activity mActivity, String id, final View rootView) {
        final FrameLayout adContainer = rootView.findViewById(R.id.shimmer_container);
        final ShimmerFrameLayout containerShimmer = rootView.findViewById(R.id.shimmer_container);
        loadBanner(mActivity, id, adContainer, containerShimmer);
    }

    private void loadBanner(final Activity mActivity, String id, final FrameLayout adContainer, final ShimmerFrameLayout containerShimmer) {
        if (PurchaseManager.getInstance().isPremiumed(mActivity)) {
            containerShimmer.setVisibility(View.GONE);
            return;
        }
        containerShimmer.setVisibility(View.VISIBLE);
        containerShimmer.startShimmer();
        try {
            AdView adView = new AdView(mActivity);
            adView.setAdUnitId(id);
            adContainer.addView(adView);
            AdSize adSize = getAdSize(mActivity);
            adView.setAdSize(adSize);
            adView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            adView.loadAd(getAdRequest());
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    containerShimmer.stopShimmer();
                    adContainer.setVisibility(View.GONE);
                    containerShimmer.setVisibility(View.GONE);
                }


                @Override
                public void onAdLoaded() {
                    containerShimmer.stopShimmer();
                    containerShimmer.setVisibility(View.GONE);
                    adContainer.setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AdSize getAdSize(Activity mActivity) {
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(mActivity, adWidth);

    }


    public void loadNative(final Activity mActivity, String id) {
        final FrameLayout frameLayout = mActivity.findViewById(R.id.fl_adplaceholder);
        final ShimmerFrameLayout containerShimmer = mActivity.findViewById(R.id.shimmer_container);
        loadNative(mActivity, containerShimmer, frameLayout, id, R.layout.native_admob_ad);
    }

    public void loadNativeFragment(final Activity mActivity, String id, View parent) {
        final FrameLayout frameLayout = parent.findViewById(R.id.fl_adplaceholder);
        final ShimmerFrameLayout containerShimmer = parent.findViewById(R.id.shimmer_container);
        loadNative(mActivity, containerShimmer, frameLayout, id, R.layout.native_admob_ad);
    }


    public void loadUnifiedNativeAd(Context context, String id, final AdCallback callback) {
        if (PurchaseManager.getInstance().isPremiumed(context)) {
            callback.onAdFailedToLoad(null);
            return;
        }
        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();
        AdLoader adLoader = new AdLoader.Builder(context, id)
                .forNativeAd(callback::onNativeAds)
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        callback.onAdFailedToLoad(loadAdError);
                    }
                })
                .withNativeAdOptions(adOptions)
                .build();
        adLoader.loadAd(getAdRequest());
    }

    private void loadNative(final Context context, final ShimmerFrameLayout containerShimmer, final FrameLayout frameLayout, final String id, final int layout) {
        if (PurchaseManager.getInstance().isPremiumed(context)) {
            containerShimmer.setVisibility(View.GONE);
            return;
        }
        frameLayout.removeAllViews();
        frameLayout.setVisibility(View.GONE);
        containerShimmer.setVisibility(View.VISIBLE);
        containerShimmer.startShimmer();

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();


        AdLoader adLoader = new AdLoader.Builder(context, id)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        @SuppressLint("InflateParams") NativeAdView adView = (NativeAdView) LayoutInflater.from(context)
                                .inflate(layout, null);
                        containerShimmer.stopShimmer();
                        containerShimmer.setVisibility(View.GONE);
                        populateUnifiedNativeAdView(nativeAd, adView);
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        containerShimmer.stopShimmer();
                        containerShimmer.setVisibility(View.GONE);

                    }
                })
                .withNativeAdOptions(adOptions)
                .build();
        adLoader.loadAd(getAdRequest());
    }


    public void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        MediaView mediaView = adView.findViewById(R.id.ad_media);

        adView.setMediaView(mediaView);

        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
//        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
//        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        try {
            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (nativeAd.getBody() == null) {
                adView.getBodyView().setVisibility(View.INVISIBLE);
            } else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (nativeAd.getCallToAction() == null) {
                adView.getCallToActionView().setVisibility(View.INVISIBLE);
            } else {
                adView.getCallToActionView().setVisibility(View.VISIBLE);
                ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (nativeAd.getIcon() == null) {
                adView.getIconView().setVisibility(View.GONE);
            } else {
                ((ImageView) adView.getIconView()).setImageDrawable(
                        nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (nativeAd.getPrice() == null) {
                adView.getPriceView().setVisibility(View.INVISIBLE);
            } else {
                adView.getPriceView().setVisibility(View.VISIBLE);
                ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (nativeAd.getStore() == null) {
                adView.getStoreView().setVisibility(View.INVISIBLE);
            } else {
                adView.getStoreView().setVisibility(View.VISIBLE);
                ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (nativeAd.getStarRating() == null) {
                adView.getStarRatingView().setVisibility(View.INVISIBLE);
            } else {
                ((RatingBar) adView.getStarRatingView())
                        .setRating(nativeAd.getStarRating().floatValue());
                adView.getStarRatingView().setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (nativeAd.getAdvertiser() == null) {
                adView.getAdvertiserView().setVisibility(View.INVISIBLE);
            } else {
                ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        adView.setNativeAd(nativeAd);

    }


    public RewardedAd requestVideoAds(Context context, String id) {
        final RewardedAd[] rewardedAd = {null};
//        if (PurchaseManager.getInstance().isRemovedAds(context)) {
//            return null;
//        }
//        rewardedAd[0] = new RewardedAd(context, id);
//        rewardedAd[0].loadAd(getAdRequest(), new RewardedAdLoadCallback() {
//            @Override
//            public void onRewardedAdLoaded() {
//                super.onRewardedAdLoaded();
//            }
//
//            @Override
//            public void onRewardedAdFailedToLoad(LoadAdError loadAdError) {
//                rewardedAd[0] = null;
//                super.onAdFailedToLoad(loadAdError);
//            }
//        });
        return rewardedAd[0];
    }

//    public void showVideoAds(final Activity context, RewardedAd rewardedAd, final RewardedAdCallback adCallback) {
//        if (rewardedAd == null) {
//            adCallback.onRewardedAdFailedToShow(null);
//            return;
//        }
//        if (PurchaseManager.getInstance().isRemovedAds(context)) {
//            adCallback.onRewardedAdClosed();
//            return;
//        }
//        if (rewardedAd.isLoaded()) {
//            rewardedAd.show(context, new RewardedAdCallback() {
//                @Override
//                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
//                    if (adCallback != null) {
//                        adCallback.onUserEarnedReward(rewardItem);
//                    }
//                }
//
//                @Override
//                public void onRewardedAdClosed() {
//                    if (adCallback != null) {
//                        adCallback.onRewardedAdClosed();
//                    }
//                }
//
//                @Override
//                public void onRewardedAdFailedToShow(int i) {
//                    if (adCallback != null) {
//                        adCallback.onRewardedAdFailedToShow(i);
//                    }
//                }
//
//                @Override
//                public void onRewardedAdFailedToShow(AdError adError) {
//                    super.onRewardedAdFailedToShow(adError);
//                    if (adCallback != null) {
//                        adCallback.onRewardedAdFailedToShow(adError);
//                    }
//                }
//            });
//        } else {
//            adCallback.onRewardedAdFailedToShow(0);
//            adCallback.onRewardedAdFailedToShow(null);
//        }
//    }

    @SuppressLint("HardwareIds")
    public String getDeviceId(Context context) {
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return md5(android_id).toUpperCase();
    }

    private String md5(final String s) {
        try {
            MessageDigest digest = MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();
            // Create Hex String
            StringBuilder hexString;
            hexString = new StringBuilder();
            for (byte b : messageDigest) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & b));
                while (h.length() < 2)
                    h.insert(0, "0");
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException ignored) {
        }
        return "";
    }

    public void isPurchased(boolean purchased) {
        PurchaseManager.getInstance().setPurchased(purchased);
    }
}
