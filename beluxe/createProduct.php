    <?php
    include 'koneksi.php';
    
    $product_image    = $_POST['product_image'];
    $product_name     = $_POST['product_name'];
    $product_supplier = $_POST['product_supplier'];
    $supplier_price   = $_POST['supplier_price'];
    $product_price    = $_POST['product_price'];
    
    date_default_timezone_set('Asia/Jakarta');
    $path = 'images/' . date("d-m-Y-His") . '-' . rand(100, 10000) . '.jpg';
    
    $query = "INSERT INTO barang (product_image, product_name, product_supplier, supplier_price, product_price) VALUES (?, ?, ?, ?, ?)";
    $stmt = mysqli_prepare($conn, $query);
    
    // Sesuaikan jumlah parameter dan tipe data
    mysqli_stmt_bind_param($stmt, 'sssss', $path, $product_name, $product_supplier, $supplier_price, $product_price);
    
    $result = mysqli_stmt_execute($stmt);

    if ($result) {
        // Ganti variabel $img dengan $product_image
        file_put_contents($path, base64_decode($product_image));
        $response["message"] = "Success Insert Image";
    } else {
        $response["message"] = "Failed To Insert Image";
    }

    echo json_encode($response);
    
    mysqli_stmt_close($stmt);
    mysqli_close($conn);
?>
