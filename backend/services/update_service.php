<?php
// update_service.php
require '../configuration/db.php';
header('Content-Type: application/json');

$provider_id   = $_POST['provider_id'];
$service_id    = $_POST['service_id'];
$title         = $_POST['title'] ?? '';
$details       = $_POST['details'] ?? '';
$price         = $_POST['price'] ?? 0;
$status        = $_POST['status'] ?? 'available'; // ENUM: available, paused, discontinued
$category      = $_POST['category'] ?? '';
$location_name = $_POST['location_name'] ?? '';
$latitude      = $_POST['latitude'] ?? 0.0;
$longitude     = $_POST['longitude'] ?? 0.0;
$address       = $_POST['address'] ?? '';

if (!$provider_id || !$service_id) {
    echo json_encode(["success" => false, "message" => "Provider ID and Service ID are required"]);
    exit;
}

// Ensure the service belongs to the provider
$check = $conn->prepare("SELECT service_id FROM services WHERE service_id = ? AND provider_id = ?");
$check->bind_param("ii", $service_id, $provider_id);
$check->execute();
if ($check->get_result()->num_rows === 0) {
    echo json_encode(["success" => false, "message" => "Unauthorized or non-existent service"]);
    exit;
}

// Update the service
$sql = "UPDATE services 
        SET title = ?, details = ?, price = ?, status = ?, category = ?, 
            location_name = ?, latitude = ?, longitude = ?, address = ?
        WHERE service_id = ? AND provider_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ssdsssddsi", 
    $title, $details, $price, $status, $category,
    $location_name, $latitude, $longitude, $address,
    $service_id, $provider_id
);
$stmt->execute();

echo json_encode([
    "success" => $stmt->affected_rows > 0,
    "message" => $stmt->affected_rows > 0 ? "✅ Service updated" : "⚠️ No changes made"
]);
?>
