package com.debasish.odiacalendararchive.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.debasish.odiacalendararchive.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<LoadImage> loadImages;
    private Map<String, Bitmap> mBitmaps = new HashMap<>();

    public ImageAdapter(List<LoadImage> loadImages) {
        this.loadImages = loadImages;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_view, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        LoadImage currentImage = loadImages.get(position);

        Picasso.get()
                .load(currentImage.getImageUrl())
                .fit()
                .into(holder.imageViewDownload);
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
}
