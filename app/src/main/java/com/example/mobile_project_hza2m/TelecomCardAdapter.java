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

public class TelecomCardAdapter extends RecyclerView.Adapter<TelecomCardAdapter.CardViewHolder> {

    private Context context;
    private List<TelecomCard> telecomCardList;
    private OnCardClickListener listener;

    public interface OnCardClickListener {
        void onCardClick(TelecomCard plan);
    }

    public TelecomCardAdapter(Context context, List<TelecomCard> telecomCardList, OnCardClickListener listener) {
        this.context = context;
        this.telecomCardList = telecomCardList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_telecom_plan, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        TelecomCard card = telecomCardList.get(position);

        holder.textViewTitle.setText(card.getTitle());
        holder.textViewDescription.setText(card.getDescription());
        holder.textViewPrice.setText(card.getPrice());
        Glide.with(context)
                .load(card.getImageUrl())
                .placeholder(R.drawable.khadmatiico)
                .error(R.drawable.khadmatiico)
                .into(holder.imageViewIcon);

        holder.buttonRequest.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCardClick(card);
            }
        });
    }

    @Override
    public int getItemCount() {
        return telecomCardList.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewIcon;
        TextView textViewTitle, textViewDescription, textViewPrice;
        Button buttonRequest;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewPlanTitle);
            textViewDescription = itemView.findViewById(R.id.textViewPlanDescription);
            textViewPrice = itemView.findViewById(R.id.textViewPlanPrice);
            imageViewIcon = itemView.findViewById(R.id.imageViewTelecomIcon);
            buttonRequest = itemView.findViewById(R.id.buttonRequestTelecom);
        }
    }
}
