<?php
include 'koneksi.php';

// Periksa apakah product_id dikirimkan melalui POST
if (isset($_POST['product_id'])) {
    $product_id = $_POST['product_id'];

    // Query untuk mengambil data produk berdasarkan product_id
    $query = "SELECT * FROM barang WHERE product_id = '$product_id'";
    $result = mysqli_query($conn, $query);

    // Periksa apakah query berhasil dieksekusi
    if ($result) {
        // Periksa apakah ada data yang ditemukan
        if (mysqli_num_rows($result) > 0) {
            $row = mysqli_fetch_assoc($result);

            // Kirim respons dalam format JSON
            $response['message'] = "Data ditemukan";
            $response['data'] = $row;
        } else {
            $response['message'] = "Data tidak ditemukan";
        }
    } else {
        $response['message'] = "Error dalam eksekusi query: " . mysqli_error($conn);
    }
} else {
    $response['message'] = "Parameter product_id tidak diterima";
}

// Konversi respons ke format JSON
echo json_encode($response);
?>
