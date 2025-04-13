<?php
require '../configuration/db.php';
header('Content-Type: application/json');

$user_id = $_POST['user_id'];

// Check if wallet already exists
$check = $conn->prepare("SELECT wallet_id FROM wallets WHERE user_id = ?");
$check->bind_param("i", $user_id);
$check->execute();
$check_result = $check->get_result();

if ($check_result->num_rows > 0) {
    echo json_encode([
        "success" => false,
        "message" => "Wallet already exists"
    ]);
} else {
    $sql = "INSERT INTO wallets (user_id, balance) VALUES (?, 0.00)";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $user_id);
    $stmt->execute();

    echo json_encode([
        "success" => $stmt->affected_rows > 0,
        "message" => $stmt->affected_rows > 0 ? "Wallet created" : "Error creating wallet"
    ]);
}
?>
