package com.example.mobile_project_hza2m;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder> {

    private List<Company> companies;

    public CompanyAdapter(List<Company> companies) {
        this.companies = companies;
    }

    @NonNull
    @Override
    public CompanyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_company_icon, parent, false);
        return new CompanyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyViewHolder holder, int position) {
        Company company = companies.get(position);
        holder.companyName.setText(company.getName());
        holder.companyIcon.setImageResource(company.getIconResId());
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    static class CompanyViewHolder extends RecyclerView.ViewHolder {
        ImageView companyIcon;
        TextView companyName;

        public CompanyViewHolder(@NonNull View itemView) {
            super(itemView);
            companyIcon = itemView.findViewById(R.id.companyIcon);
            companyName = itemView.findViewById(R.id.companyName);
        }
    }
}