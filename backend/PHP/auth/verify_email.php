<?php
// verify_email.php
require '../configuration/db.php';
require '../configuration/session.php';

header('Content-Type: application/json');

// 1. Get token from URL
$token = $_GET['token'] ?? null;

if (!$token) {
    echo json_encode(["success" => false, "message" => "Verification token is required."]);
    exit;
}

// 2. Fetch token and user data
$sql = "SELECT evt.token_id, evt.user_id, evt.expires_at, evt.is_used, u.full_name
        FROM email_verification_tokens evt
        JOIN users u ON evt.user_id = u.user_id
        WHERE evt.token = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $token);
$stmt->execute();
$result = $stmt->get_result();
$row = $result->fetch_assoc();

if (!$row) {
    echo json_encode(["success" => false, "message" => "Invalid or unknown token."]);
    exit;
}

if ($row['is_used']) {
    echo json_encode(["success" => false, "message" => "This verification link has already been used."]);
    exit;
}

if (strtotime($row['expires_at']) < time()) {
    echo json_encode(["success" => false, "message" => "This verification link has expired."]);
    exit;
}

// 3. Mark token as used
$update = $conn->prepare("UPDATE email_verification_tokens SET is_used = 1 WHERE token_id = ?");
$update->bind_param("i", $row['token_id']);
$update->execute();

// 4. Mark user as verified
$verify = $conn->prepare("UPDATE users SET is_verified = 1 WHERE user_id = ?");
$verify->bind_param("i", $row['user_id']);
$verify->execute();

// 5. Start session
$_SESSION['user_id'] = $row['user_id'];

echo json_encode([
    "success" => true,
    "message" => "✅ Email verified successfully. Welcome, " . $row['full_name'],
    "user_id" => $row['user_id']
]);
?>
