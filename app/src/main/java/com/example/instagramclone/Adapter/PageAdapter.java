package com.example.instagramclone.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.instagramclone.FF_fragments.FollowerFragment;
import com.example.instagramclone.FF_fragments.FollowingFragment;

public class PageAdapter extends FragmentStateAdapter {

    final int numberOfTabs;

    public PageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, int numberOfTabs) {
        super(fragmentManager, lifecycle);
        this.numberOfTabs = numberOfTabs;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0) {
            return new FollowerFragment();
        } else {
            return new FollowingFragment();
        }
    }

    @Override
    public int getItemCount() {
        return numberOfTabs;
    }
}
