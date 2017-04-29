package com.findafun.activity;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ViewFlipper;

import com.findafun.R;
import com.findafun.app.AppController;
import com.findafun.bean.events.Event;
import com.findafun.helper.AViewFlipper;
import com.findafun.helper.TouchImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

public class BannerListNew extends AppCompatActivity {
    private Animator mCurrentAnimator;
    ImageLoader uImageLoader = AppController.getInstance().getUniversalImageLoader();
    private Event event;
    private GestureDetector mGestureDetector;
    private static final String TAG = BannerListNew.class.getName();
    private ArrayList<String> imgList = new ArrayList<>();
    private int mShortAnimationDuration = 500;
    TouchImageView imageView, imageView1, imageView2, imageView3, imageView4;
    TouchImageView imageView_zoom;
    private ViewFlipper imgbanner;
    AViewFlipper aViewFlipper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_list);
        event = (Event) getIntent().getSerializableExtra("eventObj");


//        imageView = (TouchImageView) findViewById(R.id.banner_list);
//        imageView1 = (TouchImageView) findViewById(R.id.banner_list1);
//        imageView2= (TouchImageView) findViewById(R.id.banner_list2);
//        imageView3 = (TouchImageView) findViewById(R.id.banner_list3);
//        imageView4 = (TouchImageView) findViewById(R.id.banner_list4);
//        imgbanner = (ViewFlipper) findViewById(R.id.banner_new);
//        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
//        mGestureDetector = new GestureDetector(this, customGestureDetector);


        aViewFlipper = new AViewFlipper(this, null);
        LinearLayout linearlayout = (LinearLayout) findViewById(R.id.banner_container);
        AViewFlipper.LayoutParams params2 = new AViewFlipper.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        linearlayout.addView(aViewFlipper, params2);



        Log.d(TAG, "Image uri is" + event.getEventBanner());
        if (event.getEventLogo().contains(".")) {
//            uImageLoader.displayImage(event.getEventLogo(), imageview);
            imgList.add(0, event.getEventLogo());

        }
        if (event.getEventLogo_1().contains(".")) {
//           uImageLoader.displayImage(event.getEventLogo_1(), imageview1);
            imgList.add(1, event.getEventLogo_1());
        }
        if (event.getEventLogo_2().contains(".")) {
//            uImageLoader.displayImage(event.getEventLogo_2(), imageview2);
            imgList.add(2, event.getEventLogo_2());
        }
        if (event.getEventLogo_3().contains(".")) {
//            uImageLoader.displayImage(event.getEventLogo_3(), imageview3);
            imgList.add(3, event.getEventLogo_3());
        }
        if (event.getEventLogo_4().contains(".")) {
//            uImageLoader.displayImage(event.getEventLogo_4(), imageview4);
            imgList.add(4, event.getEventLogo_4());
        }

        TouchImageView imageview = new TouchImageView(BannerListNew.this);
        TouchImageView imageview1 = new TouchImageView(BannerListNew.this);
        TouchImageView imageview2 = new TouchImageView(BannerListNew.this);
        TouchImageView imageview3 = new TouchImageView(BannerListNew.this);
        TouchImageView imageview4 = new TouchImageView(BannerListNew.this);


        switch (imgList.size()) {
            case 1:
                aViewFlipper.addView(imageview);
                uImageLoader.displayImage(event.getEventLogo(), imageview);
                break;
            case 2:
                aViewFlipper.addView(imageview);
                aViewFlipper.addView(imageview1);
                uImageLoader.displayImage(event.getEventLogo(), imageview);
                uImageLoader.displayImage(event.getEventLogo_1(), imageview1);
                break;
            case 3:
                aViewFlipper.addView(imageview);
                aViewFlipper.addView(imageview1);
                aViewFlipper.addView(imageview2);
                uImageLoader.displayImage(event.getEventLogo(), imageview);
                uImageLoader.displayImage(event.getEventLogo_1(), imageview1);
                uImageLoader.displayImage(event.getEventLogo_2(), imageview2);
                break;
            case 4:
                aViewFlipper.addView(imageview);
                aViewFlipper.addView(imageview1);
                aViewFlipper.addView(imageview2);
                aViewFlipper.addView(imageview3);
                uImageLoader.displayImage(event.getEventLogo(), imageview);
                uImageLoader.displayImage(event.getEventLogo_1(), imageview1);
                uImageLoader.displayImage(event.getEventLogo_2(), imageview2);
                uImageLoader.displayImage(event.getEventLogo_3(), imageview3);
                break;
            case 5:
                aViewFlipper.addView(imageview);
                aViewFlipper.addView(imageview1);
                aViewFlipper.addView(imageview2);
                aViewFlipper.addView(imageview3);
                aViewFlipper.addView(imageview4);
                uImageLoader.displayImage(event.getEventLogo(), imageview);
                uImageLoader.displayImage(event.getEventLogo_1(), imageview1);
                uImageLoader.displayImage(event.getEventLogo_2(), imageview2);
                uImageLoader.displayImage(event.getEventLogo_3(), imageview3);
                uImageLoader.displayImage(event.getEventLogo_4(), imageview4);
                break;
            default:
                break;
        }






    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        return gestureDetector.onTouchEvent(event);
    }

    GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {

            float sensitvity = 350;
            if ((e1.getX() - e2.getX()) > sensitvity) {
                SwipeLeft();
            } else if ((e2.getX() - e1.getX()) > sensitvity) {
                SwipeRight();
            }

            return true;
        }

    };

    GestureDetector gestureDetector = new GestureDetector(simpleOnGestureListener);



    private void SwipeLeft() {
        aViewFlipper.setInAnimation(this, R.anim.left_in);
        aViewFlipper.showNext();

    }


    private void SwipeRight() {
        aViewFlipper.setInAnimation(this, R.anim.left_out);
        aViewFlipper.showPrevious();

    }


//    public class ImageAdapter extends BaseAdapter {
//        private Context context;
//        private int itemBackground;
//
//        public ImageAdapter(Context c) {
//            context = c;
//            // sets a grey background; wraps around the images
//          /*  TypedArray a =obtainStyledAttributes(R.styleable.MyGallery);
//            itemBackground = a.getResourceId(R.styleable.MyGallery_android_galleryItemBackground, 0);
//            a.recycle();*/
//        }
//
//        // returns the number of images
//        public int getCount() {
//            return imgList.size();
//        }
//
//        // returns the ID of an item
//        public Object getItem(int position) {
//            return position;
//        }
//
//        // returns the ID of an item
//        public long getItemId(int position) {
//            return position;
//        }
//
//        // returns an ImageView view
//        public View getView(int position, View convertView, ViewGroup parent) {
//          /*  ImageView imageView = new ImageView(context);
//           // imageView.setImageResource(imgList[position]);
//            imageView.setLayoutParams(new Gallery.LayoutParams(100, 100));
//            imageView.setBackgroundResource(R.color.bg_gray);
//            imageView.setPadding(5,2,5,2);*/
//            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
//            convertView = inflater.inflate(R.layout.image_banner, null);
//            ImageView imageView = (ImageView) convertView.findViewById(R.id.banner);
//            String thumbnailUrl = getThumbnailImageUrl(imgList.get(position), 0, 0);
//            uImageLoader.displayImage(thumbnailUrl, imageView,
//                    new DisplayImageOptions.Builder()
//                            .showImageOnLoading(android.R.color.darker_gray)
//                            .cacheInMemory(true).cacheOnDisk(true).build(), loadingListener);
//            imageView.setAdjustViewBounds(true);
//
//            return convertView;
//        }
//    }

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
