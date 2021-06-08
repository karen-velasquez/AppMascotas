package com.example.mascotasproject;

import androidx.recyclerview.widget.RecyclerView;

public interface CallBackItemTouch {
    void onSwiped(RecyclerView.ViewHolder viewHolder,int position);
}
