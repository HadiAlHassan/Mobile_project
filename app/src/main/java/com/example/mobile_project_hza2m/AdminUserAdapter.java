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

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {

    private final Context context;
    private final List<UserAdmin> userList;
    private final String deleteUrl = Config.BASE_URL+"admin/admin_delete_user.php";

    public AdminUserAdapter(Context context, List<UserAdmin> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_admin, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserAdmin user = userList.get(position);
        holder.textViewUserName.setText(user.getFullName());

        holder.buttonDeleteUser.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete this user?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteUser(user.getUserId(), position))
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void deleteUser(int userId, int position) {
        StringRequest request = new StringRequest(Request.Method.POST, deleteUrl,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            userList.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show();
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
                params.put("user_id", String.valueOf(userId));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserName;
        Button buttonDeleteUser;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            buttonDeleteUser = itemView.findViewById(R.id.buttonDeleteUser);
        }
    }
}
