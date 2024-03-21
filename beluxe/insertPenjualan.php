<?php
include 'koneksi.php';

$reciepent_name = isset($_POST['reciepent_name']) ? $_POST['reciepent_name'] : null;
$qty = isset($_POST['qty']) ? $_POST['qty'] : null;
$grand_total = isset($_POST['grand_total']) ? $_POST['grand_total'] : null;
$cash = isset($_POST['cash']) ? $_POST['cash'] : null;

if ($qty !== null && $grand_total !== null) {
    $query = "INSERT INTO penjualan (reciepent_name, qty, grand_total, cash) VALUES ('$reciepent_name', '$qty', '$grand_total', '$cash')";
    $result = mysqli_query($conn, $query);

    if ($result) {
        $response['error'] = false;
        $response['message'] = "Data saved successfully";
        $response['order_id'] = $conn->insert_id;
    } else {
        $response['error'] = true;
        $response['message'] = "Invalid request method";
    }
} else {
    $response['error'] = true;
    $response['message'] = "Missing parameters (qty or grandTotal or cash)";
}

echo json_encode($response);

mysqli_close($conn);
?>
