<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "tracker";

// Create connection
$conn1 = new mysqli($servername, $username, $password);
// Check connection
if ($conn1->connect_error) {
    die("Connection failed: " . $conn1->connect_error);
} 

// Create database
$sql = "CREATE DATABASE tracker";
if ($conn1->query($sql) === TRUE) {
    echo "Database created successfully";
} else {
    echo "Error creating database: " . $conn1->error;
}
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

// sql to create table
$sql1 = "CREATE TABLE IF NOT EXISTS boy (
name TEXT, 
latitude INT(11),
longitude INT(11),
duration INT(11),
points TEXT
)";

if ($conn->query($sql1) === TRUE) {
    echo "Table boy created successfully";
} else {
    echo "Error creating table: " . $conn->error;
}
$conn1->close();
$conn->close();
?>