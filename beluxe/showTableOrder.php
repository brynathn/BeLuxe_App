<?php
include 'koneksi.php';

$response = array();

$result = mysqli_query($conn, "SELECT * FROM penjualan");

if (mysqli_num_rows($result) > 0) {
    $items = array();
    while ($row = mysqli_fetch_assoc($result)) {
        $items[] = $row;
    }

    $response['error'] = false;
    $response['error_text'] = "Berhasil";
    $response['data'] = $items;
} else {
    $response['error'] = true;
    $response['error_text'] = "No users found";
}

echo json_encode($response);
?>
