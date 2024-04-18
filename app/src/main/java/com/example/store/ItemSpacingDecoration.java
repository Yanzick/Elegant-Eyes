package com.example.store;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemSpacingDecoration extends RecyclerView.ItemDecoration {
    private final int itemSpacing;

    public ItemSpacingDecoration(Context context, int itemSpacing) {
        this.itemSpacing = context.getResources().getDimensionPixelSize(itemSpacing);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = itemSpacing;
        outRect.bottom = itemSpacing;
        outRect.left = itemSpacing;
        outRect.right = itemSpacing;
    }
}
