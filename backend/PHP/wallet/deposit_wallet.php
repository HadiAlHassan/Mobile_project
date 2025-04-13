<?php
// deposit_wallet.php
require '../configuration/db.php';
header('Content-Type: application/json');

$user_id = $_POST['user_id'] ?? null;
$amount = $_POST['amount'] ?? 0;
$admin_id = $_POST['admin_id'] ?? null; // Optional admin crediting

if (!$user_id || $amount <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "Invalid input. User ID and positive amount are required."
    ]);
    exit;
}

// 1. Check if wallet exists
$check = $conn->prepare("SELECT wallet_id FROM wallets WHERE user_id = ?");
$check->bind_param("i", $user_id);
$check->execute();
$result = $check->get_result();

if ($result->num_rows === 0) {
    echo json_encode([
        "success" => false,
        "message" => "Wallet not found."
    ]);
    exit;
}

$row = $result->fetch_assoc();
$wallet_id = $row['wallet_id'];

try {
    $conn->begin_transaction();

    // 2. Update wallet balance
    $sql = "UPDATE wallets SET balance = balance + ? WHERE user_id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("di", $amount, $user_id);
    $stmt->execute();

    // 3. Log transaction
    $log = $conn->prepare("INSERT INTO wallet_transactions 
        (wallet_id, type, amount, performed_by_admin_id, description)
        VALUES (?, 'deposit', ?, ?, 'Wallet deposit')");
    $log->bind_param("ddi", $wallet_id, $amount, $admin_id);
    $log->execute();

    $conn->commit();
    echo json_encode([
        "success" => true,
        "message" => "Deposit successful"
    ]);

} catch (Exception $e) {
    $conn->rollback();
    echo json_encode([
        "success" => false,
        "message" => "Transaction failed: " . $e->getMessage()
    ]);
}
?>
