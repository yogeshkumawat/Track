<?php
$username ='root';
$password ='';
$hostname ='localhost';
$database ='tracker';

$localhost = mysql_connect($hostname,$username,$password) or trigger_error(mysql_error(),E_USER_ERROR);
mysql_select_db($database,$localhost);
	 
	$name=$_REQUEST['name'];
  $duration=$_REQUEST['duration'];

	$flag['code']=0;

	if($r=mysql_query("UPDATE boy SET duration = '$duration' WHERE name ='$name'",$localhost))
	{
		$flag['code']=1;
	}

	print(json_encode($flag));
	mysql_close($localhost);
?>