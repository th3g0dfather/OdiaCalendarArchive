package com.mohit.odiacalendararchive.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohit.odiacalendararchive.R;
import com.mohit.odiacalendararchive.model.LoadImage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context context;
    private List<LoadImage> loadImages;
    private Map<String, Bitmap> mBitmaps = new HashMap<>();

    public ImageAdapter(Context context, List<LoadImage> loadImages) {
        this.context = context;
        this.loadImages = loadImages;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_view, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        LoadImage currentImage = loadImages.get(position);

        /*
        File file = CacheImageManager.getImage(context, currentImage);
        if (file.exists()) {
            Picasso.get()
                    .load(file)
                    .fit()
                    .into(holder.imageViewDownload);
        } else {
            Picasso.get().load(currentImage.getImageUrl())
                    .networkPolicy(NetworkPolicy.NO_CACHE,NetworkPolicy.NO_STORE)
                    .fit()
                    .into(holder.imageViewDownload, new Callback() {
                        @Override
                        public void onSuccess() {
                            MyImageTask task = new MyImageTask();
                            task.execute(currentImage);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }
         */

        Picasso.get()
                .load(currentImage.getImageUrl())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.imageViewDownload, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Picasso", "Successful");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("Picasso", "not stored offline");
                        Picasso.get()
                                .load(currentImage.getImageUrl())
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                .into(holder.imageViewDownload, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d("Picasso", "onSuccess: fetch image success");
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Log.d("Picasso", "onError: fetch image failed");
                                    }
                                });
                    }
                });
    }

    @Override
    public int getItemCount() {
        return loadImages.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewDownload;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewDownload = itemView.findViewById(R.id.image_view_download);
        }
    }

    /*
    class MyImageTask extends AsyncTask<LoadImage, Void, Bitmap> {

        private LoadImage loadImage;
        private ImageViewHolder imageViewHolder;

        public void setViewHolder(ImageViewHolder imageViewHolder) {
            this.imageViewHolder = imageViewHolder;
        }

        @Override
        protected Bitmap doInBackground(LoadImage... loadImages) {
            Bitmap bitmap = null;
            loadImage = loadImages[0];

            InputStream inputStream = null;

            try {
                URL imageUrl = new URL(loadImage.getImageUrl());
                inputStream = (InputStream) imageUrl.getContent();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            CacheImageManager.putImage(context, loadImage, bitmap);
            //mBitmaps.put(loadImage.getMonth(), bitmap);
        }
    }
     */
}
