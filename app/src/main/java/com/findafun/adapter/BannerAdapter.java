package com.findafun.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.findafun.app.AppController;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by DataCrawl on 5/3/2016.
 */

public class BannerAdapter extends BaseAdapter {

    ImageLoader uImageLoader = AppController.getInstance().getUniversalImageLoader();
    private Context context;
    private ArrayList<String> imgList;
    public BannerAdapter(Context context, ArrayList<String> imgList){
        this.context = context;
        this.imgList = imgList;
    }

    @Override
    public int getCount() {
        return
                imgList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

          /*  LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.image_banner, viewGroup, false);


            ImageView imageView = (ImageView) convertView.findViewById(R.id.banner);
            String thumbnailUrl = getThumbnailImageUrl(imgList.get(position), 0, 0);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);*/

        ImageView image = new ImageView(context);

        String thumbnailUrl = getThumbnailImageUrl(imgList.get(position),40,40);
          image.setScaleType(ImageView.ScaleType.FIT_XY);
        image.setLayoutParams(new Gallery.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        //  uImageLoader.destroy();
        uImageLoader.displayImage(thumbnailUrl, image,
                new DisplayImageOptions.Builder()
                        .showImageOnLoading(android.R.color.white)
                        .cacheInMemory(true).cacheOnDisk(true).build(), loadingListener);

        image.setAdjustViewBounds(true);


        return image;






           /* View view = convertView;







            PhotoView p = new PhotoView(EventDetailActivity.this);

           p.setLayoutParams(new AbsListView.LayoutParams((int) (getResources().getDisplayMetrics().density * 400), (int) (getResources().getDisplayMetrics().density * 400)));
           p.setScaleType(ImageView.ScaleType.FIT_XY);
            p.setEnabled(false);
try {
    //get thumbnailurl to save user data...like WeChat does
    String thumbnailUrl = getThumbnailImageUrl(imgList.get(i), 0, 0);

    uImageLoader.displayImage(thumbnailUrl, p,
            new DisplayImageOptions.Builder()
                    .showImageOnLoading(android.R.color.darker_gray)
                    .cacheInMemory(true).cacheOnDisk(true).build(), loadingListener);

   *//* ImageLoader.getInstance().displayImage(thumbnailUrl, p,
            new DisplayImageOptions.Builder()
                    .showImageOnLoading(android.R.color.darker_gray)
                    .cacheInMemory(true).cacheOnDisk(true).build(), loadingListener);*//*
   p.setAdjustViewBounds(true);
    p.touchEnable(false);//disable touch
} catch (Exception e){
    e.printStackTrace();
}
            return p;*/
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