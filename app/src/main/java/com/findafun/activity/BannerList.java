package com.findafun.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import com.findafun.R;
import com.findafun.app.AppController;
import com.findafun.bean.events.Event;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import java.util.ArrayList;

public class BannerList extends AppCompatActivity {
    private Animator mCurrentAnimator;
    ImageLoader uImageLoader = AppController.getInstance().getUniversalImageLoader();
    private Event event;
    private ArrayList<String> imgList = new ArrayList<>();
    private int mShortAnimationDuration = 500;
    static int zoomVal = 1;
    Gallery gallery;
    ImageView imageView;
    View imageView_zoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_list);
        event = (Event) getIntent().getSerializableExtra("eventObj");

        imageView = (ImageView) findViewById(R.id.banner_list);
        imageView_zoom = (ImageView) findViewById(R.id.banner_list);
        gallery = (Gallery) findViewById(R.id.gallery_list);
        if (event.getEventLogo().contains(".")) {
            imgList.add(0, event.getEventLogo());
        }
        if (event.getEventLogo_1().contains(".")) {
            imgList.add(1, event.getEventLogo_1());
        }
        if (event.getEventLogo_2().contains(".")) {
            imgList.add(2, event.getEventLogo_2());
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomImageFromThumb(imageView_zoom, zoomVal);
            }
        });


        gallery.setAdapter(new ImageAdapter(this));
        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //  imageView.setImageResource(imgList[position]);
                String thumbnailUrl = getThumbnailImageUrl(imgList.get(position), 0, 0);
                zoomVal = position;
                uImageLoader.displayImage(thumbnailUrl, imageView,
                        new DisplayImageOptions.Builder()
                                .showImageOnLoading(android.R.color.darker_gray)
                                .cacheInMemory(true).cacheOnDisk(true).build(), loadingListener);

                imageView.setAdjustViewBounds(true);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void zoomImageFromThumb(final View thumbView, int zoomVal) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.banner_list_zoom);
      /*  final LinearLayout container_expandedImageView = (LinearLayout) findViewById(
                R.id.container_expanded_image);*/
        //  expandedImageView.setImageResource(imageResId);
        if (zoomVal == 0) {
            uImageLoader.displayImage(event.getEventLogo(), expandedImageView);
        } else if (zoomVal == 1) {
            uImageLoader.displayImage(event.getEventLogo_1(), expandedImageView);
        }
        if (zoomVal == 2) {
            uImageLoader.displayImage(event.getEventLogo_2(), expandedImageView);
        }
        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.banner_container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }
        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);
      /*  container_expandedImageView.setVisibility(View.VISIBLE);*/

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        //    container_expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }


    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private int itemBackground;

        public ImageAdapter(Context c) {
            context = c;
            // sets a grey background; wraps around the images
          /*  TypedArray a =obtainStyledAttributes(R.styleable.MyGallery);
            itemBackground = a.getResourceId(R.styleable.MyGallery_android_galleryItemBackground, 0);
            a.recycle();*/
        }

        // returns the number of images
        public int getCount() {
            return imgList.size();
        }

        // returns the ID of an item
        public Object getItem(int position) {
            return position;
        }

        // returns the ID of an item
        public long getItemId(int position) {
            return position;
        }

        // returns an ImageView view
        public View getView(int position, View convertView, ViewGroup parent) {
          /*  ImageView imageView = new ImageView(context);
           // imageView.setImageResource(imgList[position]);
            imageView.setLayoutParams(new Gallery.LayoutParams(100, 100));
            imageView.setBackgroundResource(R.color.bg_gray);
            imageView.setPadding(5,2,5,2);*/
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.image_banner, parent, false);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.banner);
            String thumbnailUrl = getThumbnailImageUrl(imgList.get(position), 0, 0);
            uImageLoader.displayImage(thumbnailUrl, imageView,
                    new DisplayImageOptions.Builder()
                            .showImageOnLoading(android.R.color.darker_gray)
                            .cacheInMemory(true).cacheOnDisk(true).build(), loadingListener);
            imageView.setAdjustViewBounds(true);

            return convertView;
        }
    }

    public String getThumbnailImageUrl(String imgUrl, int width, int height) {
        String url = "http://imgsize.ph.126.net/?imgurl=data1_data2xdata3x0x85.jpg&enlarge=true";
        width = (int) (getResources().getDisplayMetrics().density * 100);
        height = (int) (getResources().getDisplayMetrics().density * 100); //just for convenient
        url = url.replaceAll("data1", imgUrl).replaceAll("data2", width + "").replaceAll("data3", height + "");
        return url;
    }

    private ImageLoadingListener loadingListener = new SimpleImageLoadingListener() {
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            view.setEnabled(true);//only loadedImage is available we can click item
        }
    };

}
