<?php
    include 'koneksi.php';
    
    $product_id         = $_POST['product_id'];
    $product_image      = $_POST['product_image'];
    $productName		= $_POST['product_name'];
    $productSupplier	= $_POST['product_supplier'];
    $supplierPrice		= $_POST['supplier_price'];
    $productPrice		= $_POST['product_price'];

    date_default_timezone_set('Asia/Jakarta');
    $path = 'images/'.date("d-m-Y-his").'-'.rand(100, 10000).'.jpg';
    
    // Hapus gambar lama
    $data = mysqli_query($conn,"SELECT * FROM barang WHERE product_id='$product_id'");
    $d = mysqli_fetch_array($data);
    unlink($d['product_image']);
    
    // Update data dengan gambar baru
    $query = "UPDATE barang SET product_image='".$path."', product_name='".$productName."', product_supplier='".$productSupplier."', supplier_price='".$supplierPrice."', product_price='".$productPrice."' WHERE product_id='".$product_id."'";
    $result = mysqli_query($conn, $query) or die('Error query:  '.$query);
    
    if ($result == 1){
        // Simpan gambar baru
        file_put_contents($path, base64_decode($img));
        $response['message'] = "Success Update";
    } else {
        $response['message'] = "Failed Update";
    }
    
    echo json_encode($response);
    mysqli_close($conn);
?>
