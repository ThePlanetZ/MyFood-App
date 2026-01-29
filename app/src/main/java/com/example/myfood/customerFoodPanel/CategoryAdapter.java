package com.example.myfood.customerFoodPanel;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myfood.R;

import java.util.ArrayList;
import java.util.List;
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context mContext;
    private List<Category> mCategoryList;
    private OnCategoryClickListener mListener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(Context context, List<Category> categoryList, OnCategoryClickListener listener) {
        this.mContext = context;
        this.mCategoryList = categoryList != null ? categoryList : new ArrayList<>();
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.categorie_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = mCategoryList.get(position);

        holder.textViewCategoryName.setText(category.getName());
        Glide.with(mContext).load(category.getImageURL()).into(holder.imageViewCategory);

        // Set click listener on itemView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CategoryAdapter", "set click listener work");

                if (mListener != null) {
                    Log.d("CategoryAdapter", "set click listener khdaam");

                    mListener.onCategoryClick(category);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCategoryList != null ? mCategoryList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewCategory;
        TextView textViewCategoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCategory = itemView.findViewById(R.id.imageViewCategory);
            textViewCategoryName = itemView.findViewById(R.id.textViewCategoryName);
        }
    }
}
