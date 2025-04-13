<?php
// signup.php
require '../configuration/db.php';

header('Content-Type: application/json');

$full_name = trim($_POST['full_name']);
$email = trim($_POST['email']);
$password = password_hash($_POST['password'], PASSWORD_BCRYPT);

// 0. Check for existing email
$check = $conn->prepare("SELECT user_id FROM users WHERE email = ?");
$check->bind_param("s", $email);
$check->execute();
$check->store_result();

if ($check->num_rows > 0) {
    echo json_encode([
        "success" => false,
        "message" => "Email is already registered"
    ]);
    exit;
}

// 1. Insert user with is_verified = false
$sql = "INSERT INTO users (full_name, email, password, is_verified) VALUES (?, ?, ?, 0)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("sss", $full_name, $email, $password);
$stmt->execute();

if ($stmt->affected_rows > 0) {
    $user_id = $stmt->insert_id;

    // 2. Generate email verification token
    $token = bin2hex(random_bytes(32));
    $expires = date('Y-m-d H:i:s', strtotime('+30 minutes'));

    $sql_token = "INSERT INTO email_verification_tokens (user_id, token, expires_at) VALUES (?, ?, ?)";
    $stmt_token = $conn->prepare($sql_token);
    $stmt_token->bind_param("iss", $user_id, $token, $expires);
    $stmt_token->execute();

    // 3. Send verification link (placeholder logic)
    $verification_link = "https://yourdomain.com/verify_email.php?token=" . $token;

    echo json_encode([
        "success" => true,
        "message" => "Verification email sent",
        "link" => $verification_link  // For testing
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Signup failed. Please try again."
    ]);
}
?>
