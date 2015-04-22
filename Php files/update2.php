<?php
$username ='u967317085_root';
$password ='yogesh';
$hostname ='mysql.hostinger.in';
$database ='u967317085_track';


$localhost = mysqli_connect($hostname,$username,$password,$database); if (!$localhost) {
    die("Connection failed: " . mysqli_connect_error());
} 
	 
	$name=$_REQUEST['name'];
	$latitude=$_REQUEST['latitude'];
	$longitude=$_REQUEST['longitude'];
       $points=$_REQUEST['points'];

	$flag['code']=0;

	if(mysqli_query($localhost, "UPDATE boy SET latitude='$latitude', longitude='$longitude', points = '$points' WHERE name ='$name'"))
	{
		$flag['code']=1;
	}

	print(json_encode($flag));
	mysqli_close($localhost);
?>