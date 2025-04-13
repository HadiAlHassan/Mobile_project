<?php
// make_payment.php
require '../configuration/db.php';

header('Content-Type: application/json');

$user_id = $_POST['user_id'];
$service_id = $_POST['service_id'];
$amount = $_POST['amount'];
$performed_by_admin_id = $_POST['admin_id'] ?? null; // Optional, if admin is triggering

try {
    $conn->begin_transaction();

    // 1. Check wallet balance
    $check = $conn->prepare("SELECT wallet_id, balance FROM wallets WHERE user_id = ?");
    $check->bind_param("i", $user_id);
    $check->execute();
    $wallet = $check->get_result()->fetch_assoc();

    if (!$wallet || $wallet['balance'] < $amount) {
        throw new Exception("Insufficient wallet balance.");
    }

    $wallet_id = $wallet['wallet_id'];

    // 2. Deduct from wallet
    $update = $conn->prepare("UPDATE wallets SET balance = balance - ? WHERE user_id = ?");
    $update->bind_param("di", $amount, $user_id);
    $update->execute();

    // 3. Record payment
    $insert_payment = $conn->prepare("INSERT INTO payments (user_id, service_id, amount, status, payment_method) 
                                      VALUES (?, ?, ?, 'completed', 'wallet')");
    $insert_payment->bind_param("iid", $user_id, $service_id, $amount);
    $insert_payment->execute();
    $payment_id = $conn->insert_id;

    // 4. Log wallet transaction
    $log = $conn->prepare("INSERT INTO wallet_transactions 
        (wallet_id, type, amount, payment_id, performed_by_admin_id, description)
        VALUES (?, 'payment', ?, ?, ?, 'Service payment')");
    $log->bind_param("iddi", $wallet_id, $amount, $payment_id, $performed_by_admin_id);
    $log->execute();

    // 5. Subscribe or update user-service link
    $renewal_date = date('Y-m-d', strtotime('+30 days'));
    $subscribe = $conn->prepare("
        INSERT INTO user_services (user_id, service_id, payment_id, renewal_date, last_renewed_at, status)
        VALUES (?, ?, ?, ?, NOW(), 'active')
        ON DUPLICATE KEY UPDATE 
            status = 'active',
            renewal_date = VALUES(renewal_date),
            last_renewed_at = NOW(),
            payment_id = VALUES(payment_id),
            subscribed_at = CURRENT_TIMESTAMP
    ");
    $subscribe->bind_param("iiis", $user_id, $service_id, $payment_id, $renewal_date);
    $subscribe->execute();

    $conn->commit();

    echo json_encode([
        "success" => true,
        "message" => "✅ Payment and subscription successful",
        "payment_id" => $payment_id
    ]);

} catch (Exception $e) {
    $conn->rollback();
    echo json_encode([
        "success" => false,
        "message" => "❌ Failed: " . $e->getMessage()
    ]);
}
?>
