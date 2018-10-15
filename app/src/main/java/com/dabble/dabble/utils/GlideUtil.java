package com.dabble.dabble.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

public class GlideUtil {
    public static void loadImage(String url, final ImageView imageView) {
        Context context = imageView.getContext();
        Glide.with(context)
                .load(url)
                .bitmapTransform(new CenterCrop(context))
                .into(new GlideDrawableImageViewTarget(imageView) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);

                        imageView.setClipToOutline(true);
                    }
                });
    }
}