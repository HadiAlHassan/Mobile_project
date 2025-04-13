<?php
// refund_payment.php

require '../configuration/db.php';

header('Content-Type: application/json');

$payment_id = $_POST['payment_id'];
$performed_by_admin_id = $_POST['admin_id'] ?? null; // Optional

try {
    // 1. Get payment info
    $get = $conn->prepare("SELECT user_id, service_id, amount FROM payments WHERE payment_id = ?");
    $get->bind_param("i", $payment_id);
    $get->execute();
    $info = $get->get_result()->fetch_assoc();

    if (!$info) {
        throw new Exception("Payment not found.");
    }

    $user_id = $info['user_id'];
    $service_id = $info['service_id'];
    $amount = $info['amount'];

    // 2. Get wallet
    $get_wallet = $conn->prepare("SELECT wallet_id FROM wallets WHERE user_id = ?");
    $get_wallet->bind_param("i", $user_id);
    $get_wallet->execute();
    $wallet = $get_wallet->get_result()->fetch_assoc();

    if (!$wallet) {
        throw new Exception("Wallet not found.");
    }

    $wallet_id = $wallet['wallet_id'];

    // 3. Start transaction
    $conn->begin_transaction();

    // 4. Refund balance
    $update = $conn->prepare("UPDATE wallets SET balance = balance + ? WHERE user_id = ?");
    $update->bind_param("di", $amount, $user_id);
    $update->execute();

    // 5. Log transaction
    $log = $conn->prepare("INSERT INTO wallet_transactions 
        (wallet_id, type, amount, payment_id, performed_by_admin_id, description)
        VALUES (?, 'refund', ?, ?, ?, 'Refund issued')");
    $log->bind_param("diii", $wallet_id, $amount, $payment_id, $performed_by_admin_id);
    $log->execute();

    // 6. Cancel subscription (if exists)
    $cancel = $conn->prepare("UPDATE user_services SET status = 'cancelled' WHERE user_id = ? AND service_id = ?");
    $cancel->bind_param("ii", $user_id, $service_id);
    $cancel->execute();

    $conn->commit();

    echo json_encode([
        "success" => true,
        "message" => "✅ Refund processed and subscription cancelled."
    ]);
} catch (Exception $e) {
    $conn->rollback();
    echo json_encode([
        "success" => false,
        "message" => "❌ Error: " . $e->getMessage()
    ]);
}
?>
