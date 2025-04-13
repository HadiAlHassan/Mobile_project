<?php
session_start();

// Optional: Set custom session timeout (e.g., 30 mins)
ini_set('session.gc_maxlifetime', 1800);
setcookie(session_name(), session_id(), time() + 1800, "/");

// Check if user is logged in
function is_logged_in() {
    return isset($_SESSION['user_id']);
}

// Get user ID
function get_user_id() {
    return $_SESSION['user_id'] ?? null;
}

// Get user role (if stored)
function get_user_role() {
    return $_SESSION['role'] ?? 'user';
}

// Require login
function ensure_logged_in() {
    if (!is_logged_in()) {
        http_response_code(401);
        die("Unauthorized. Please log in.");
    }
}
?>
