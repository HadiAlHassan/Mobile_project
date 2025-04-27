package com.example.mobile_project_hza2m;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class InsurancePlanAdapter extends RecyclerView.Adapter<InsurancePlanAdapter.PlanViewHolder> {

    private Context context;
    private List<InsurancePlan> planList;
    private OnPlanClickListener listener;

    public interface OnPlanClickListener {
        void onPlanClick(InsurancePlan plan);
    }

    public InsurancePlanAdapter(Context context, List<InsurancePlan> planList, OnPlanClickListener listener) {
        this.context = context;
        this.planList = planList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_insurance_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        InsurancePlan plan = planList.get(position);
        holder.textViewTitle.setText(plan.getTitle());
        holder.textViewDescription.setText(plan.getDescription());
        holder.textViewPrice.setText(plan.getPrice());

        // ✅ Load Firebase image with Glide
        Glide.with(context)
                .load(plan.getImageUrl()) // make sure plan.getImageUrl() returns full Firebase URL
                .placeholder(R.drawable.khadmatiico)
                .error(R.drawable.khadmatiico)
                .into(holder.imageViewIcon);

        holder.buttonRequest.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlanClick(plan);
            }
        });
    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDescription, textViewPrice;
        ImageView imageViewIcon;
        Button buttonRequest;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewPlanTitle);
            textViewDescription = itemView.findViewById(R.id.textViewPlanDescription);
            textViewPrice = itemView.findViewById(R.id.textViewPlanPrice);
            imageViewIcon = itemView.findViewById(R.id.imageViewInsuranceIcon);
            buttonRequest = itemView.findViewById(R.id.buttonRequestInsurance);
        }
    }
}
