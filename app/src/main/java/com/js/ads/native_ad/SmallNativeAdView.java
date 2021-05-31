package com.js.ads.native_ad;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.js.ads.R;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

public class SmallNativeAdView extends ConstraintLayout {

    private NativeAdView nativeAdView;
    private ImageView imgIcon;
    private TextView tvAppName, tvDes;
    private Button btnAction;


    public SmallNativeAdView(Context context) {
        super(context);
        init();
    }

    public SmallNativeAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SmallNativeAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setVisibility(GONE);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.native_ad_small, this, true);

        nativeAdView = rootView.findViewById(R.id.nativeAdView);
        imgIcon = rootView.findViewById(R.id.imgIcon);
        tvAppName = rootView.findViewById(R.id.tvAppName);
        tvDes = rootView.findViewById(R.id.tvDescription);
        btnAction = rootView.findViewById(R.id.btnAction);
        int color = Color.parseColor("#2196F3");
        btnAction.getBackground().mutate().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC));
        loadAd();
    }

    public void loadAd() {
        AdLoader.Builder builder = new AdLoader.Builder(getContext(), getResources().getString(R.string.admob_native_id));
        builder.forNativeAd(this::populateNativeAd);
        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
        NativeAdOptions nativeAdOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
        builder.withNativeAdOptions(nativeAdOptions);
        AdLoader adLoader = builder.withAdListener(null).build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void populateNativeAd(NativeAd nativeAd) {
        nativeAdView.setHeadlineView(tvAppName);
        nativeAdView.setBodyView(tvDes);
        nativeAdView.setCallToActionView(btnAction);
        nativeAdView.setIconView(imgIcon);

        if (nativeAd.getHeadline() != null) {
            ((TextView) nativeAdView.getHeadlineView()).setText(nativeAd.getHeadline());
            nativeAdView.getHeadlineView().setVisibility(VISIBLE);
        } else {
            nativeAdView.getHeadlineView().setVisibility(GONE);
        }

        if (nativeAd.getBody() != null) {
            ((TextView) nativeAdView.getBodyView()).setText(nativeAd.getBody());
            nativeAdView.getBodyView().setVisibility(View.VISIBLE);
        } else {
            nativeAdView.getBodyView().setVisibility(View.GONE);
        }

        if (nativeAd.getCallToAction() != null) {
            ((Button) nativeAdView.getCallToActionView()).setText(nativeAd.getCallToAction());
            nativeAdView.getCallToActionView().setVisibility(View.VISIBLE);
        } else {
            nativeAdView.getCallToActionView().setVisibility(View.GONE);
        }

        if (nativeAd.getIcon() != null) {
            ((ImageView) nativeAdView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            nativeAdView.getIconView().setVisibility(View.VISIBLE);
        } else {
            nativeAdView.getIconView().setVisibility(View.GONE);
        }

        nativeAdView.setNativeAd(nativeAd);
        setVisibility(VISIBLE);
    }
}
