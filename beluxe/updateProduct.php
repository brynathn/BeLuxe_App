<?php
	include 'koneksi.php';
	
	$product_id		= $_POST['product_id'];
	$productName = $_POST['product_name'];
	$productSupplier = $_POST['product_supplier'];
	$supplierPrice = $_POST['supplier_price'];
	$productPrice = $_POST['product_price'];
	
	$query = "UPDATE barang SET 
				product_name='".$productName."', 
				product_supplier='".$productSupplier."', 
				supplier_price='".$supplierPrice."', 
				product_price='".$productPrice."' 
				WHERE product_id='".$product_id."'";
	
	$result = mysqli_query($conn, $query) or die('Error query: '.$query);
	
	if ($result == 1){
		$response['message'] = "Success Update";
	} else {
		$response['message'] = "Failed Update";
	}
	
	echo json_encode($response);
	mysqli_close($conn);
?>
