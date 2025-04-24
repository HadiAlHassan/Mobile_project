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
import java.util.List;

public class TelecomPlanAdapter extends RecyclerView.Adapter<TelecomPlanAdapter.PlanViewHolder> {

    private Context context;
    private List<TelecomPlan> planList;
    private OnPlanClickListener listener;

    public interface OnPlanClickListener {
        void onPlanClick(TelecomPlan plan);
    }

    public TelecomPlanAdapter(Context context, List<TelecomPlan> planList, OnPlanClickListener listener) {
        this.context = context;
        this.planList = planList;
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
        TelecomPlan plan = planList.get(position);
        holder.textViewTitle.setText(plan.getTitle());
        holder.textViewDescription.setText(plan.getDescription());
        holder.textViewPrice.setText(plan.getPrice());
        holder.imageViewIcon.setImageResource(plan.getImageResId());

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
            imageViewIcon = itemView.findViewById(R.id.imageViewTelecomIcon);
            buttonRequest = itemView.findViewById(R.id.buttonRequestTelecom);
        }
    }
}
