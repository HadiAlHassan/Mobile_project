<?php
require '../configuration/db.php';

$provider_id = $_POST['provider_id']; // now passed directly
$category = $_POST['category'];
$title = $_POST['title'];
$details = $_POST['details'];
$price = $_POST['price'];
$location_name = $_POST['location_name'];
$latitude = $_POST['latitude'];
$longitude = $_POST['longitude'];
$address = $_POST['address'];

// Check that provider exists
$query = "SELECT provider_id FROM service_providers WHERE provider_id = ?";
$stmt = $conn->prepare($query);
$stmt->bind_param("i", $provider_id);
$stmt->execute();
$result = $stmt->get_result();
$row = $result->fetch_assoc();

if ($row) {
    $sql = "INSERT INTO services (provider_id, category, title, details, price, location_name, latitude, longitude, address)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("isssdsdds", $provider_id, $category, $title, $details, $price, $location_name, $latitude, $longitude, $address);
    $stmt->execute();

    echo $stmt->affected_rows > 0 ? "✅ Service added" : "❌ Failed to add service";
} else {
    echo "❌ Invalid provider ID.";
}
?>
