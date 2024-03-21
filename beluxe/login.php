<?php
include 'koneksi.php';

// Ambil data dari POST request
$access_id = $_POST['access_id'];
$user_pass = $_POST['user_pass'];

// Cari pengguna berdasarkan email
$query = "SELECT * FROM user WHERE access_id = '$access_id'";
$result = $conn->query($query);

if ($result->num_rows > 0) {
    // Pengguna ditemukan, periksa kata sandi
    $row = $result->fetch_assoc();
    if ($user_pass == $row['user_pass']) {
        // Kata sandi cocok, login berhasil
        $response = array(
            "error" => false,
            "message" => "Login berhasil",
            "access_id" => $row['access_id'],
            "full_name" => $row['full_name']
        );
    } else {
        // Kata sandi tidak cocok
        $response = array(
            "error" => true,
            "message" => "Kata sandi salah. Silakan coba lagi."
        );
    }
} else {
    // Pengguna tidak ditemukan
    $response = array(
        "error" => true,
        "message" => "Akun tidak ditemukan. Silakan daftar terlebih dahulu."
    );
}

echo json_encode($response);

$conn->close();
?>
