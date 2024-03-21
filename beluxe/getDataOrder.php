<?php
include 'koneksi.php';

// Periksa apakah 'order_id' telah diatur dalam POST
if (isset($_GET['order_id'])) {
    $order_id = mysqli_real_escape_string($conn, $_GET['order_id']);

    $query = "SELECT 
    penjualan.order_id, 
    penjualan.reciepent_name,
    penjualan.date, 
    penjualan.cash, 
    penjualan_detail.product_id, 
    barang.product_name, 
    barang.product_price, 
    barang.product_image, 
    penjualan_detail.qty 
FROM 
    penjualan 
INNER JOIN 
    penjualan_detail ON penjualan.order_id = penjualan_detail.order_id 
INNER JOIN 
    barang ON penjualan_detail.product_id = barang.product_id 
WHERE 
    penjualan.order_id = '$order_id'";

    $result = mysqli_query($conn, $query);

    if ($result) {
        $items = array();
        while ($row = mysqli_fetch_assoc($result)) {
            $items[] = $row;
        }

        $response['error'] = false;
        $response['message'] = "Berhasil";
        $response['data'] = $items;
    } else {
        // Tambahkan pesan kesalahan yang lebih spesifik jika diperlukan
        $response['error'] = true;
        $response['message'] = "Gagal mengambil data order details: " . mysqli_error($conn);
    }
} else {
    $response['error'] = true;
    $response['message'] = "Parameter 'order_id' tidak diatur.";
}

echo json_encode($response);
mysqli_close($conn);
?>
