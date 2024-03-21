<?php
include 'koneksi.php';

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Pemeriksaan untuk memastikan kunci tersedia
    if (isset($_POST['order_id']) && isset($_POST['product_id'])) {
        $orderId = $_POST['order_id'];
        $productId = $_POST['product_id'];
        $qty = $_POST['qty'];

        $stmt = $conn->prepare("INSERT INTO penjualan_detail (order_id, product_id, qty) VALUES (?, ?, ?)");
        $stmt->bind_param("iid", $orderId, $productId, $qty);

        if ($stmt->execute()) {
            $response['error'] = false;
            $response['message'] = "Penjualan detail data inserted successfully";
        } else {
            $response['error'] = true;
            $response['message'] = "Failed to insert penjualan detail data";
        }

        $stmt->close();
    } else {
        $response['error'] = true;
        $response['message'] = "Missing parameters (orderId, productId, or recipientName)";
    }
} else {
    $response['error'] = true;
    $response['message'] = "Invalid request method";
}

echo json_encode($response);
?>
