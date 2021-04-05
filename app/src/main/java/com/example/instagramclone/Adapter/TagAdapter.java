package com.example.instagramclone.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.R;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder>{

    private final Context context;
    private List<String> mTags;
    private List<String> mTagsCount;

    public TagAdapter(Context mContext, List<String> mTags, List<String> mTagsCount) {
        this.context = mContext;
        this.mTags = mTags;
        this.mTagsCount = mTagsCount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tag_items, parent, false);

        return new TagAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tag.setText("#" +mTags.get(position));
        holder.numberOfPosts.setText(mTagsCount.get(position) + " posts");
    }

    @Override
    public int getItemCount() {
        return mTags.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tag;
        public TextView numberOfPosts;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tag = itemView.findViewById(R.id.tag_id);
            numberOfPosts = itemView.findViewById(R.id.numberOfTagPost_id);
        }
    }

    public void filter(List<String> filterTags, List<String> filterTagsCount) {
        this.mTags = filterTags;
        this.mTagsCount = filterTagsCount;

        notifyDataSetChanged();
    }
}
