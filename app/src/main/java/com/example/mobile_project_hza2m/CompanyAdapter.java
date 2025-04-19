package com.example.mobile_project_hza2m;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder> {

    private List<Company> companies;

    public CompanyAdapter(List<Company> companies, String serviceType) {
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

        // ✅ Access the serviceType per company
        String serviceType = company.getServiceType();

        // Example usage: show toast when clicked
        holder.itemView.setOnClickListener(v -> {
            if(Objects.equals(serviceType, "Ogero Phone Bills")){
                Toast.makeText(holder.itemView.getContext(), "Ogero Phone Bills", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(holder.itemView.getContext(), OgeroServiceUserActivity.class);
                holder.itemView.getContext().startActivity(i);

            }
            else if(Objects.equals(serviceType, "Insurance")){
                Toast.makeText(holder.itemView.getContext(), "Insurance", Toast.LENGTH_SHORT).show();
               /* Intent i = new Intent(holder.itemView.getContext(), InsuranceServiceUserActivity.class);
                holder.itemView.getContext().startActivity(i); */
            }
            else if(serviceType == "Tuition Fees"){
                Toast.makeText(holder.itemView.getContext(), "Tuition Fees", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(holder.itemView.getContext(), TuitionServiceUserActivity.class);
                holder.itemView.getContext().startActivity(i);
            }
            else if(serviceType == "Streaming Services"){
                Toast.makeText(holder.itemView.getContext(), "Streaming Services", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(holder.itemView.getContext(), StreamingServiceUserActivity.class);
                holder.itemView.getContext().startActivity(i);
            }


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
