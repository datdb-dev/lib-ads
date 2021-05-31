package com.js.ads;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class BannerAdView extends FrameLayout {

    private AdView adView;
    private boolean loaded;

    public BannerAdView(@NonNull Context context) {
        super(context);
        init();
    }

    public BannerAdView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BannerAdView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        adView = new AdView(getContext());
        adView.setAdUnitId(getResources().getString(R.string.admob_banner_id));
        addView(adView);
    }

    public void loadAd(AdRequest adRequest, AdSize adSize) {
        if (!loaded) {
            adView.setAdSize(adSize);
            adView.loadAd(adRequest);
            loaded = true;
        }
    }

    public void resume() {
        if (loaded) {
            adView.resume();
        }
    }

    public void pause() {
        if (loaded) {
            adView.pause();
        }
    }

    public void destroy() {
        if (loaded) {
            adView.destroy();
            setVisibility(GONE);
        }
    }
}
