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

public class TelecomPlanAdapter extends RecyclerView.Adapter<TelecomPlanAdapter.PlanViewHolder> {

    private Context context;
    private List<TelecomPlan> plans;
    private OnPlanClickListener listener;

    public interface OnPlanClickListener {
        void onClick(TelecomPlan plan);
    }

    public TelecomPlanAdapter(Context context, List<TelecomPlan> plans, OnPlanClickListener listener) {
        this.context = context;
        this.plans = plans;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_telecom_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        TelecomPlan plan = plans.get(position);
        holder.title.setText(plan.getTitle());
        holder.description.setText(plan.getDescription());
        holder.price.setText(plan.getPrice());

        // ✅ Load image from Firebase with Glide
        Glide.with(context)
                .load(plan.getImageUrl())
                .placeholder(R.drawable.khadmatiico)
                .error(R.drawable.khadmatiico)
                .into(holder.icon);

        holder.subscribeBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(plan);
            }
        });
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, price;
        ImageView icon;
        Button subscribeBtn;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewPlanTitle);
            description = itemView.findViewById(R.id.textViewPlanDescription);
            price = itemView.findViewById(R.id.textViewPlanPrice);
            icon = itemView.findViewById(R.id.imageViewTelecomIcon);
            subscribeBtn = itemView.findViewById(R.id.buttonRequestTelecom);
        }
    }
}
