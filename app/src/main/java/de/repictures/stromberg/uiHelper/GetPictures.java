package de.repictures.stromberg.uiHelper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.util.Arrays;
import java.util.List;

public class GetPictures implements Runnable {

    private static final String TAG = "GetPictures";
    String url;
    int id;
    Uri uri;
    ImageView imageView;
    private Drawable drawable;
    Activity activity;
    Boolean transfrom;
    private boolean transition;
    private boolean blur;

    public GetPictures(String url, ImageView imageView, Activity activity,
                       boolean circle, boolean transition, boolean blur) {
        this.activity = activity;
        this.imageView = imageView;
        this.url = url;
        this.transfrom = circle;
        this.transition = transition;
        this.blur = blur;
        //Log.i(TAG, "GetPictures: started with: " + url);
    }

    public GetPictures(int id, ImageView imageView, Activity activity,
                       boolean circle, boolean transition, boolean blur) {
        this.activity = activity;
        this.imageView = imageView;
        this.id = id;
        this.transfrom = circle;
        this.transition = transition;
        this.blur = blur;
    }

    public GetPictures(int id, Drawable drawable, Activity activity,
                       boolean circle, boolean transition, boolean blur) {
        this.drawable = drawable;
        this.activity = activity;
        this.id = id;
        this.transfrom = circle;
        this.transition = transition;
        this.blur = blur;
    }

    public GetPictures(String url, Drawable drawable, Activity activity,
                       boolean circle, boolean transition, boolean blur) {
        this.drawable = drawable;
        this.activity = activity;
        this.url = url;
        this.transfrom = circle;
        this.transition = transition;
        this.blur = blur;
    }

    public GetPictures(Uri uri, ImageView imageView, Activity activity,
                       boolean circle, boolean transition, boolean blur) {
        this.activity = activity;
        this.imageView = imageView;
        this.uri = uri;
        this.transfrom = circle;
        this.transition = transition;
        this.blur = blur;
    }

    public GetPictures(Uri uri, Drawable drawable, Activity activity,
                       boolean circle, boolean transition, boolean blur) {
        this.drawable = drawable;
        this.activity = activity;
        this.uri = uri;
        this.transfrom = circle;
        this.transition = transition;
        this.blur = blur;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        Target target = null;
        if (imageView != null) {
            target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Drawable d = new BitmapDrawable(activity.getResources(), bitmap);
                    if (transition) {
                        TransitionDrawable td = new TransitionDrawable(new Drawable[]{
                                new ColorDrawable(activity.getResources().getColor(android.R.color.transparent)),
                                d
                        });
                        imageView.setImageDrawable(td);
                        td.startTransition(110);
                    } else {
                        imageView.setImageDrawable(d);
                    }
                    Log.d(TAG, "ImageView success");
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    imageView.setImageDrawable(errorDrawable);
                    Log.e(TAG, "ImageView onBitmapFailed: drawingImageFailed");
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    Log.i(TAG, "ImageView onPrepareLoad: prepared");
                }
            };
            imageView.setTag(target);
        } else if (drawable != null){
            target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    drawable = new BitmapDrawable(activity.getResources(), bitmap);
                    Log.d(TAG, "Drawable success");
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    drawable = errorDrawable;
                    Log.e(TAG, "Drawable onBitmapFailed: drawingImageFailed");
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    Log.i(TAG, "Drawable onPrepareLoad: prepared");
                }
            };
        }
        final Target finalTarget = target;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (finalTarget != null) {
                    Log.d(TAG, "run: supiii... fertig ausgeführt!!");
                    if (transfrom && !blur) {
                        if (url != null) Picasso.with(activity).load(url).transform(new CircleTransform()).into(finalTarget);
                        else if (id != 0) Picasso.with(activity).load(id).transform(new CircleTransform()).into(finalTarget);
                        else if (uri != null) Picasso.with(activity).load(uri).transform(new CircleTransform()).into(finalTarget);
                    } else if (blur && !transfrom) {
                        if (url != null) Picasso.with(activity).load(url).transform(new BlurTransform(activity, 25, 1)).into(finalTarget);
                        else if (id != 0) Picasso.with(activity).load(id).transform(new BlurTransform(activity, 25, 1)).into(finalTarget);
                        else if (uri != null) Picasso.with(activity).load(uri).transform(new BlurTransform(activity, 25, 1)).into(finalTarget);
                    } else if (blur && transfrom) {
                        List<Transformation> transformations = Arrays.asList(new CircleTransform(), new BlurTransform(activity, 25, 1));

                        if (url != null) Picasso.with(activity).load(url).transform(transformations).into(finalTarget);
                        else if (id != 0) Picasso.with(activity).load(id).transform(transformations).into(finalTarget);
                        else if (uri != null) Picasso.with(activity).load(uri).transform(transformations).into(finalTarget);
                    } else {
                        if (url != null) Picasso.with(activity).load(url).into(finalTarget);
                        else if (id != 0) Picasso.with(activity).load(id).into(finalTarget);
                        else if (uri != null) Picasso.with(activity).load(uri).into(finalTarget);
                    }
                }
            }
        });

    }
}
