<?php
// login.php
require '../configuration/db.php';
require '../configuration/session.php';

header('Content-Type: application/json');

$email = trim($_POST['email']);
$password = trim($_POST['password']);

$sql = "SELECT user_id, password, is_verified FROM users WHERE email = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();
$user = $result->fetch_assoc();

if (!$user) {
    echo json_encode([
        "success" => false,
        "message" => "No account found with that email"
    ]);
    exit;
}

if (!$user['is_verified']) {
    echo json_encode([
        "success" => false,
        "message" => "Email not verified. Please check your inbox."
    ]);
    exit;
}

if (password_verify($password, $user['password'])) {
    // Start session and set user ID
    $_SESSION['user_id'] = $user['user_id'];

    echo json_encode([
        "success" => true,
        "user_id" => $user['user_id']
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Invalid password"
    ]);
}
?>
