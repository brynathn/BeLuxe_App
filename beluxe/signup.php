<?php
	include 'koneksi.php';

	// Ambil data dari POST request
	$full_name = $_POST['full_name'];
	$email     = $_POST['email'];
	$user_pass  = $_POST['user_pass'];
	$action    = $_POST['action'];

	function isAccessIdUnique($conn, $accessId) {
		$query = "SELECT COUNT(*) as count FROM user WHERE access_id = '$accessId'";
		$result = $conn->query($query);
	
		if ($result) {
			$row = $result->fetch_assoc();
			return $row['count'] == 0;
		}
	
		return false;
	}
	
	// Menghasilkan Access ID yang unik
	function generateUniqueAccessId($conn) {
		$maxAttempts = 5; // Jumlah maksimal percobaan untuk menghasilkan Access ID unik
	
		for ($i = 0; $i < $maxAttempts; $i++) {
			$randomLetters = chr(rand(65, 90)) . chr(rand(65, 90));
			$randomNumbers = str_pad(mt_rand(1, 999), 3, '0', STR_PAD_LEFT);
			$accessId = $randomLetters . $randomNumbers;
	
			// Mengecek uniknya Access ID
			if (isAccessIdUnique($conn, $accessId)) {
				return $accessId;
			}
		}
	
		return ""; // Jika gagal menghasilkan Access ID unik setelah sejumlah percobaan
	}


	switch($action){
		case "add":
			$access_id = generateUniqueAccessId($conn);
			if (!empty($access_id)) {
				$query = "INSERT INTO user (access_id, full_name, email, user_pass) VALUES ('$access_id', '$full_name', '$email', '$user_pass')";
				$result = mysqli_query($conn, $query);
	
				if ($result == 1) {
					$response["error_text"] = "Success";
					$response["access_id"] = $access_id;
				} else {
					$response["error_text"] = "Fail";
				}
			} else {
				// Gagal menghasilkan Access ID unik
				$response["error_text"] = "Failed to generate unique Access ID";
			}
	
			echo json_encode($response);
			mysqli_close($conn);
			break;
	}
?>
