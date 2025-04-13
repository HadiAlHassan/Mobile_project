<?php
// get_all_services_by_category.php
require '../configuration/db.php';

header('Content-Type: application/json');

$category = $_GET['category'] ?? $_POST['category'] ?? null;

if (!$category) {
    echo json_encode([
        "success" => false,
        "message" => "Category is required"
    ]);
    exit;
}

// Fetch services that are marked as 'available'
$sql = "SELECT 
            s.service_id,
            s.title,
            s.details,
            s.price,
            s.location_name,
            s.latitude,
            s.longitude,
            s.address,
            sp.business_name AS provider_name
        FROM services s
        JOIN service_providers sp ON s.provider_id = sp.provider_id
        WHERE s.category = ? AND s.status = 'available'";

$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $category);
$stmt->execute();
$result = $stmt->get_result();

$services = [];
while ($row = $result->fetch_assoc()) {
    $services[] = $row;
}

if (count($services) > 0) {
    echo json_encode([
        "success" => true,
        "category" => $category,
        "services" => $services
    ]);
} else {
    echo json_encode([
        "success" => true,
        "category" => $category,
        "services" => [],
        "message" => "No available services in this category"
    ]);
}
?>
