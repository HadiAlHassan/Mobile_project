package com.example.mobile_project_hza2m;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    private Context context;
    private List<SubscriptionPlan> planList;
    private OnSubscribeClickListener listener;

    public interface OnSubscribeClickListener {
        void onSubscribeClick(SubscriptionPlan plan);
    }

    public PlanAdapter(Context context, List<SubscriptionPlan> planList, OnSubscribeClickListener listener) {
        this.context = context;
        this.planList = planList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_subscription_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        SubscriptionPlan plan = planList.get(position);
        holder.textViewTitle.setText(plan.getTitle());
        holder.textViewPrice.setText(plan.getPrice());
        holder.textViewMaxUsers.setText("Up to " + plan.getMaxUsers() + " users");

        holder.buttonSubscribe.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSubscribeClick(plan);
            }
        });
    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewPrice, textViewMaxUsers;
        Button buttonSubscribe;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewPlanTitle);
            textViewPrice = itemView.findViewById(R.id.textViewPlanPrice);
            textViewMaxUsers = itemView.findViewById(R.id.textViewMaxUsers);
            buttonSubscribe = itemView.findViewById(R.id.buttonSubscribe);
        }
    }
}
