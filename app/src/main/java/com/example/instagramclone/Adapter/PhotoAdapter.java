package com.example.instagramclone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.Models.Post;
import com.example.instagramclone.R;
import com.example.instagramclone.fragments.PostDetailFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private final Context context;
    private final List<Post> postList;
    private final String from;

    public PhotoAdapter(Context context, List<Post> postList, String from) {
        this.context = context;
        this.postList = postList;
        this.from = from;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_item, parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);
        Picasso.get().load(post.getImageUrl()).placeholder(R.mipmap.ic_default_image).into(holder.postImage);

        holder.postImage.setOnClickListener(v -> {

            if(from.equals("profile")) {
                context.getSharedPreferences("POST", Context.MODE_PRIVATE).edit().putString("postId",post.getPostId()).apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_id, new PostDetailFragment()).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView postImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.photo_id);
        }
    }

}
