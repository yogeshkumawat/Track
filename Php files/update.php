<?php
$username ='u967317085_root';
$password ='yogesh';
$hostname ='mysql.hostinger.in';
$database ='u967317085_track';

$localhost = mysqli_connect($hostname,$username,$password,$database); if (!$localhost) {
    die("Connection failed: " . mysqli_connect_error());
} 

	 
	$name=$_REQUEST['name'];
  $duration=$_REQUEST['duration'];

	$flag['code']=0;

	if(mysqli_query($localhost, "UPDATE boy SET duration = '$duration' WHERE name ='$name'"))
	{
		$flag['code']=1;
	}

	print(json_encode($flag));
	mysqli_close($localhost);
?>