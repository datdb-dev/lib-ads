package com.js.ads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

@SuppressLint("StaticFieldLeak")
public class AdMobAdsManager {
    private static AdMobAdsManager instance;
    private static Context context;
    private boolean notShowAd = false;
    private boolean isLoadingInterstitialAd;

    private InterstitialAd interstitialAd;
    private int frequency = 4;
    private int count = 0;
    private final int LIMIT_REQUEST_FAILED = 2;
    private int countRequestFailed = 0;

    private AdMobAdsManager() {
    }

    public static void init(Context ctx) {
        if (instance == null) {
            instance = new AdMobAdsManager();
            context = ctx;
        }

        MobileAds.initialize(context);
    }

    public static AdMobAdsManager getInstance() {
        return instance;
    }

    public void setNotShowAd(boolean notShowAd) {
        this.notShowAd = notShowAd;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void loadInterstitialAd() {
        if (interstitialAd == null && !isLoadingInterstitialAd && !notShowAd && countRequestFailed < LIMIT_REQUEST_FAILED) {
            isLoadingInterstitialAd = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(context, context.getString(R.string.admob_interstitial_id), adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd ad) {
                    super.onAdLoaded(ad);
                    interstitialAd = ad;
                    isLoadingInterstitialAd = false;
                    countRequestFailed = 0;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    isLoadingInterstitialAd = false;
                    interstitialAd = null;
                    countRequestFailed++;
                }
            });
        }
    }

    public void showInterstitialAd(Activity activity, boolean alwaysShow, OnInterstitialAdListener onInterstitialAdListener) {
        if (interstitialAd != null && !notShowAd && (count % frequency == 0 || alwaysShow)) {
            interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    if (onInterstitialAdListener != null) {
                        onInterstitialAdListener.onAdClose();
                    }
                    interstitialAd = null;
                    loadInterstitialAd();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    if (onInterstitialAdListener != null) {
                        onInterstitialAdListener.onAdClose();
                    }
                    interstitialAd = null;
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    interstitialAd = null;
                }
            });
            interstitialAd.show(activity);
        } else {
            if (onInterstitialAdListener != null) {
                onInterstitialAdListener.onAdClose();
            }
            if (interstitialAd == null) {
                loadInterstitialAd();
            }
        }
        if (!alwaysShow) {
            count++;
        }
    }

    public void loadBanner(Activity activity, BannerAdView adView) {
        if (!notShowAd) {
            adView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().build();
            AdSize adSize = getAdSize(activity);
            adView.loadAd(adRequest, adSize);
        } else {
            adView.setVisibility(View.GONE);
        }
    }

    private AdSize getAdSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

    public void showRewardAd(Activity activity, OnRewardAdListener onRewardAdListener) {
        if (onRewardAdListener != null) {
            onRewardAdListener.onAdLoading();
        }
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(activity, activity.getString(R.string.admob_rewarded_id), adRequest, new RewardedAdLoadCallback() {

            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                super.onAdLoaded(rewardedAd);
                if (onRewardAdListener != null) {
                    onRewardAdListener.onAdLoaded();
                }
                showRewardAd(activity, rewardedAd, onRewardAdListener);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Toast.makeText(activity, R.string.load_ad_failed, Toast.LENGTH_SHORT).show();
                if (onRewardAdListener != null) {
                    onRewardAdListener.onAdFailedToLoad();
                }
            }
        });
    }

    private void showRewardAd(Activity activity, RewardedAd rewardedAd, OnRewardAdListener onRewardAdListener) {
        rewardedAd.show(activity, new OnUserEarnedRewardListener() {
            @Override
            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                if (onRewardAdListener != null) {
                    onRewardAdListener.onUserEarnedReward(rewardItem.getType(), rewardItem.getAmount());
                }
            }
        });
    }
}
