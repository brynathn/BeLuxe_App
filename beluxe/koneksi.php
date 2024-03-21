<?php
	$server = "localhost";
	$user 	= "root";
	$pass 	= "";
	$db 	= "beluxe_uas";
	$conn	= mysqli_connect($server,$user,$pass,$db);
	
	if (!$conn) {
		die("Connection failed: " . mysqli_connect_error());
	}
?>