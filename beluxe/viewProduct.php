<?php
	include 'koneksi.php';
	
	$result = mysqli_query($conn, "SELECT * FROM barang");
	if (mysqli_num_rows($result) > 0) {
		$items = array();
		while($row = mysqli_fetch_object($result)){
			array_push($items, $row);
		}
		$response['message'] = "Terdapat Barang";
		$response['data'] = $items;
	} 
	else {
		$response['message'] = "Tidak Ada Barang";
	}
	 
	echo json_encode($response);
?>