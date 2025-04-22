package com.example.mobile_project_hza2m;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<Category> categoryList;
    private final Context context;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_with_services, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryName.setText(category.getName());

        ServiceAdapter serviceAdapter = new ServiceAdapter(context, category.getServices());
        holder.serviceRecyclerView.setLayoutManager(
                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        );
        holder.serviceRecyclerView.setAdapter(serviceAdapter);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        RecyclerView serviceRecyclerView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.textCategoryName);
            serviceRecyclerView = itemView.findViewById(R.id.companyRecyclerView);
        }
    }
}
