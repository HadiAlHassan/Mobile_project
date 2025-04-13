<?php
// get_wallet_balance.php
require '../configuration/db.php';

header('Content-Type: application/json');

$user_id = $_GET['user_id'] ?? null;

if (!$user_id || !is_numeric($user_id)) {
    echo json_encode([
        "success" => false,
        "message" => "Invalid or missing user ID."
    ]);
    exit;
}

$sql = "SELECT balance FROM wallets WHERE user_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();
$row = $result->fetch_assoc();

if ($row) {
    echo json_encode([
        "success" => true,
        "balance" => number_format((float)$row['balance'], 2, '.', '')
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Wallet not found"
    ]);
}
?>
