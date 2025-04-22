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

public class ServiceCategoryAdapter extends RecyclerView.Adapter<ServiceCategoryAdapter.CategoryViewHolder> {

    private final List<ServiceCategory> categoryList;
    private final Context context;

    public ServiceCategoryAdapter(Context context, List<ServiceCategory> categoryList) {
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
        ServiceCategory category = categoryList.get(position);
        holder.categoryTitle.setText(category.getCategoryName());

        CompanyAdapter companyAdapter = new CompanyAdapter(
                category.getCompanies(),
                category.getServiceType() // Assuming ServiceCategory has this method
        );

        holder.companyRecyclerView.setLayoutManager(
                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        );
        holder.companyRecyclerView.setAdapter(companyAdapter);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle;
        RecyclerView companyRecyclerView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.categoryTitle);
            companyRecyclerView = itemView.findViewById(R.id.companyRecyclerView);
        }
    }
}
