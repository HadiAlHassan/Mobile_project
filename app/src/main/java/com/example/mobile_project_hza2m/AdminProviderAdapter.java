package com.example.mobile_project_hza2m;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminProviderAdapter extends RecyclerView.Adapter<AdminProviderAdapter.ProviderViewHolder> {

    private final Context context;
    private final List<Provider> providerList;
    private final String deleteUrl = "https://yourdomain.com/api/admin_delete_provider.php"; // 🔄 Change to your actual URL

    public AdminProviderAdapter(Context context, List<Provider> providerList) {
        this.context = context;
        this.providerList = providerList;
    }

    @NonNull
    @Override
    public ProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_provider, parent, false);
        return new ProviderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProviderViewHolder holder, int position) {
        Provider provider = providerList.get(position);
        holder.textViewProviderName.setText(provider.getBusinessName());

        holder.buttonDeleteProvider.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirm Delete")
                    .setMessage("Delete provider " + provider.getBusinessName() + "?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteProvider(provider.getProviderId(), position))
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void deleteProvider(int providerId, int position) {
        StringRequest request = new StringRequest(Request.Method.POST, deleteUrl,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            providerList.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Provider deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("provider_id", String.valueOf(providerId));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    @Override
    public int getItemCount() {
        return providerList.size();
    }

    static class ProviderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewProviderName;
        Button buttonDeleteProvider;

        public ProviderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewProviderName = itemView.findViewById(R.id.textViewProviderName);
            buttonDeleteProvider = itemView.findViewById(R.id.buttonDeleteProvider);
        }
    }
}
