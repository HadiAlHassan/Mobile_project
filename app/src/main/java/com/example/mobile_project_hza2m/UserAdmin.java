package com.example.mobile_project_hza2m;

public class UserAdmin {

        private int userId;
        private String fullName;

        public UserAdmin(int userId, String fullName) {
            this.userId = userId;
            this.fullName = fullName;
        }

        public int getUserId() { return userId; }
        public String getFullName() { return fullName; }
    }
