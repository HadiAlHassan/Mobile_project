package com.example.mobile_project_hza2m;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void onDelete(int position);
    }

    private List<String> categoryList;
    private OnDeleteClickListener deleteClickListener;

    public CategoryAdapter(List<String> categoryList, OnDeleteClickListener listener) {
        this.categoryList = categoryList;
        this.deleteClickListener = listener;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_category_display, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        String category = categoryList.get(position);
        holder.textCategoryName.setText(category);

        holder.buttonDelete.setOnClickListener(v -> deleteClickListener.onDelete(position));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textCategoryName;
        ImageButton buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCategoryName = itemView.findViewById(R.id.textCategoryName);
            buttonDelete = itemView.findViewById(R.id.buttonDeleteCategory);
        }
    }
}
