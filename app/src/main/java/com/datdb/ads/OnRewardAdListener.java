package com.datdb.ads;

public interface OnRewardAdListener {
    void onAdLoading();

    void onAdFailedToLoad();

    void onAdLoaded();

    void onUserEarnedReward(String type, int aMount);
}
