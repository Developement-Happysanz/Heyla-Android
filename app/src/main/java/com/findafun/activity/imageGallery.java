package com.findafun.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.findafun.R;
import com.findafun.app.AppController;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

public class imageGallery extends AppCompatActivity {

    ImageLoader uImageLoader = AppController.getInstance().getUniversalImageLoader();
    Gallery galleryView;
    ImageView imgView;
    ArrayList<String> imgList = new ArrayList<>();

 /*   private int[] imageResource = {
            R.drawable.bookings, R.drawable.background_landing_pager_tab, R.drawable.amateur_trophy, R.drawable.add_event_spinner };
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);
        Intent i = getIntent();
        imgList = i.getStringArrayListExtra("image_list");
        imgView = (ImageView) findViewById(R.id.imageView);
        galleryView = (Gallery) findViewById(R.id.gallery);
        imgList.size();
        imgView.setImageURI(Uri.parse(imgList.get(0)));
        galleryView.setAdapter(new myImageAdapter(this));

        galleryView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                  uImageLoader.displayImage(imgList.get(position), imgView,
                        new DisplayImageOptions.Builder()
                                .showImageOnLoading(android.R.color.darker_gray)
                                .cacheInMemory(true).cacheOnDisk(true).build(), loadingListener);

                int imagePosition = position + 1;
           //     Toast.makeText(getApplicationContext(), "You have selected image = " + imagePosition, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //gallery image onclick event
    /*    galleryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int i, long id) {
            //    imgView.setImageURI(Uri.parse(imgList.get(i)));

                uImageLoader.displayImage(imgList.get(i), imgView,
                        new DisplayImageOptions.Builder()
                                .showImageOnLoading(android.R.color.darker_gray)
                                .cacheInMemory(true).cacheOnDisk(true).build(), loadingListener);


                int imagePosition = i + 1;
              //  Toast.makeText(getApplicationContext(), "You have selected image = " + imagePosition, Toast.LENGTH_LONG).show();
            }
        });*/

    }

    public class myImageAdapter extends BaseAdapter {
        private Context mcontext;

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView mgalleryView = new ImageView(mcontext);
            String thumbnailUrl = getThumbnailImageUrl(imgList.get(position),300,300);
            mgalleryView.setImageURI(Uri.parse(imgList.get(position)));
            mgalleryView.setLayoutParams(new Gallery.LayoutParams(100, 100));
            mgalleryView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mgalleryView.setLayoutParams(new Gallery.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            //imgView.setImageResource(R.drawable.image_border);
           // mgalleryView.setPadding(10, 5,10, 5);

            uImageLoader.displayImage(thumbnailUrl, mgalleryView,
                    new DisplayImageOptions.Builder()
                            .showImageOnLoading(android.R.color.darker_gray)
                            .cacheInMemory(true).cacheOnDisk(true).build(), loadingListener);

            uImageLoader.displayImage(imgList.get(position), imgView,
                    new DisplayImageOptions.Builder()
                            .showImageOnLoading(android.R.color.darker_gray)
                            .cacheInMemory(true).cacheOnDisk(true).build(), loadingListener);

            return mgalleryView;
        }

        public myImageAdapter(Context context) {
            mcontext = context;
        }

        public int getCount() {
            return imgList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }
    }

    private ImageLoadingListener loadingListener = new SimpleImageLoadingListener() {
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            view.setEnabled(true);//only loadedImage is available we can click item
        }
    };

    public String getThumbnailImageUrl(String imgUrl,int width,int height){
        String url="http://imgsize.ph.126.net/?imgurl=data1_data2xdata3x0x85.jpg&enlarge=true";
        //   width = (int) (getResources().getDisplayMetrics().density * 100);
        //    height = (int) (getResources().getDisplayMetrics().density * 100); //just for convenient
        url=url.replaceAll("data1", imgUrl).replaceAll("data2", width+"").replaceAll("data3", height+"");
        return url;
    }

}