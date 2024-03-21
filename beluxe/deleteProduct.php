<?php
	include 'koneksi.php';
	$product_id		= $_POST['product_id'];
	
	$data 	= mysqli_query($conn,"SELECT * FROM barang WHERE product_id='$product_id'");
	$d 		= mysqli_fetch_array($data);
	unlink($d['product_image']);
	
	$query = "DELETE FROM barang WHERE (product_id='".$product_id."')";
	$result = mysqli_query($conn, $query) or die('Error query:  '.$query);
	if ($result == 1){	
		$response["message"]="Success Delete";
		echo json_encode($response);
	}
	else{
		$response["message"]="Failed Delete";
		echo json_encode($response);
	}
	mysqli_close($conn);
?>