package com.example.mobileapp;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class HorizontalSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final int space;

    public HorizontalSpacingItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.right = space; // Khoảng cách giữa các item
    }
}
