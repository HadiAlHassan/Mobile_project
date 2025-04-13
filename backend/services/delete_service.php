<?php
// delete_service.php
require '../configuration/db.php';
header('Content-Type: application/json');

$provider_id = $_POST['provider_id'];
$service_id  = $_POST['service_id'];

if (!$provider_id || !$service_id) {
    echo json_encode(["success" => false, "message" => "Provider ID and Service ID are required"]);
    exit;
}

// Only allow the provider to delete their own service
$sql = "DELETE FROM services WHERE service_id = ? AND provider_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ii", $service_id, $provider_id);
$stmt->execute();

echo json_encode([
    "success" => $stmt->affected_rows > 0,
    "message" => $stmt->affected_rows > 0 ? "✅ Service deleted" : "❌ No service deleted or unauthorized"
]);
?>
