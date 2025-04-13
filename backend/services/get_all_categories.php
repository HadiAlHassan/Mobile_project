<?php
// get_all_categories.php
require '../configuration/db.php';
header('Content-Type: application/json');

// Fetch distinct categories where services are available
$sql = "SELECT DISTINCT category 
        FROM services 
        WHERE status = 'available' AND category IS NOT NULL AND category != '' 
        ORDER BY category ASC";

$result = $conn->query($sql);

$categories = [];
while ($row = $result->fetch_assoc()) {
    $categories[] = $row['category'];
}

echo json_encode([
    "success" => true,
    "categories" => $categories
]);
?>
