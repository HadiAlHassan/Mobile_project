package com.example.mobile_project_hza2m;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TelecomCardAdapter extends RecyclerView.Adapter<TelecomCardAdapter.CardViewHolder> {

    private Context context;
    private List<TelecomCard> telecomCardList;
    private OnCardClickListener listener;

    public interface OnCardClickListener {
        void onCardClick(TelecomCard card);
    }

    public TelecomCardAdapter(Context context, List<TelecomCard> telecomCardList, OnCardClickListener listener) {
        this.context = context;
        this.telecomCardList = telecomCardList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_telecom_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        TelecomCard card = telecomCardList.get(position);
        holder.textViewValue.setText(card.getValue());
        holder.imageViewCard.setImageResource(card.getImageResId());

        holder.itemView.setOnClickListener(v -> {
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
        ImageView imageViewCard;
        TextView textViewValue;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCard = itemView.findViewById(R.id.imageViewRechargeCard);
            textViewValue = itemView.findViewById(R.id.textViewCardValue);
        }
    }
}

