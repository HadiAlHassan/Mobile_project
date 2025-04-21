package com.example.mobile_project_hza2m;

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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.ViewHolder> {

    private final Context context;
    private final List<CategoryModel> categoryList;
    private final OnCategoryDeletedListener deleteListener;

    public interface OnCategoryDeletedListener {
        void onCategoryDeleted();
    }

    public AdminCategoryAdapter(Context context, List<CategoryModel> categoryList, OnCategoryDeletedListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public AdminCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminCategoryAdapter.ViewHolder holder, int position) {
        CategoryModel category = categoryList.get(position);
        holder.textViewCategoryName.setText(category.getName());

        holder.buttonDelete.setOnClickListener(v -> {
            deleteCategory(category.getId());
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCategoryName;
        Button buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategoryName = itemView.findViewById(R.id.textViewCategoryName);
            buttonDelete = itemView.findViewById(R.id.buttonDeleteCategory);
        }
    }

    private void deleteCategory(int categoryId) {
        String url = "http:// 192.168.0.74/Mobile_submodule_backend/PHP/admin/admin_delete_category.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            Toast.makeText(context, "Category deleted", Toast.LENGTH_SHORT).show();
                            deleteListener.onCategoryDeleted();
                        } else {
                            Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(context, "Request failed", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("category_id", String.valueOf(categoryId));
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

}
