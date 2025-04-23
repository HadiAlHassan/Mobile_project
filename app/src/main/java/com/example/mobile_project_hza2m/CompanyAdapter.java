package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder> {

    private final List<Company> companies;

    public CompanyAdapter(List<Company> companies, String serviceType) {
        this.companies = companies;
    }

    @NonNull
    @Override
    public CompanyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service_icon, parent, false);
        return new CompanyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyViewHolder holder, int position) {
        Company company = companies.get(position);
        holder.companyName.setText(company.getName());
        holder.companyIcon.setImageResource(company.getDrawableId());

        holder.itemView.setOnClickListener(v -> {
            Class<?> targetActivity;
            String serviceType = company.getCategory();

            switch (serviceType) {
                case "Ogero":
                    targetActivity = OgeroServiceUserActivity.class;
                    break;
                case "Insurance":
                    targetActivity = InsuranceServiceUserActivity.class;
                    break;
                case "Tuition Fees":
                    targetActivity = TuitionServiceUserActivity.class;
                    break;
                case "Streaming Services":
                    targetActivity = StreamingServiceUserActivity.class;
                    break;
                case "Telecommunication Services":
                default:
                    targetActivity = TelecomServiceUserActivity.class;
                    break;
            }

            Intent intent = new Intent(holder.itemView.getContext(), targetActivity);
            intent.putExtra("service_id", company.getServiceId()); // ✅ KEY FIX
            intent.putExtra("company_name", company.getName());
            holder.itemView.getContext().startActivity(intent);
        });
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

