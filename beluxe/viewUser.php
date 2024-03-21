<?php
	include 'koneksi.php';
	
	$result = mysqli_query($conn, "SELECT * FROM user");
	if (mysqli_num_rows($result) > 0) {
		$items = array();
		while($row = mysqli_fetch_object($result)){
			array_push($items, $row);
		}
		
		$response['error_text'] = "Berhasil";
		$response['data'] = $items;
	} 
	else {
		$response['error_text'] = "No products found";
	}
	 
	echo json_encode($response);
?>