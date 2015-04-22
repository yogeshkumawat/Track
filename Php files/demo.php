<?php
$username ='u967317085_root';
$password ='yogesh';
$hostname ='mysql.hostinger.in';
$database ='u967317085_track';

$localhost = mysqli_connect($hostname, $username, $password, $database);
if (!$localhost) {
    die("Connection failed: " . mysqli_connect_error());
} 
$sql = "select * from boy";
$result = mysqli_query($localhost, $sql);

if(mysqli_num_rows($result) > 0) {
while( $row = mysqli_fetch_assoc($result)){
print(json_encode($row['name']));
print(json_encode($row['latitude']));
print(json_encode($row['longitude']));
print(json_encode($row['duration']));
echo "\n";
}
}
mysqli_close($localhost);
?>