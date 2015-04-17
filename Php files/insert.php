<?php
$username ='root';
$password ='';
$hostname ='localhost';
$database ='tracker';

$localhost = mysql_connect($hostname,$username,$password) or trigger_error(mysql_error(),E_USER_ERROR);
mysql_select_db($database,$localhost);
	 
	$name=$_REQUEST['name'];
	$latitude=$_REQUEST['latitude'];
	$longitude=$_REQUEST['longitude'];
  $duration="0";
  $points=" ";
	$flag['code']=0;

	if($r=mysql_query("insert into boy values('$name','$latitude','$longitude','$duration','$points') ",$localhost))
	{
		$flag['code']=1;
	}

	print(json_encode($flag));
	mysql_close($localhost);
?>