<?php
$username ='root';
$password ='';
$hostname ='localhost';
$database ='tracker';

$localhost = mysql_connect($hostname,$username,$password) or trigger_error(mysql_error(),E_USER_ERROR);
mysql_select_db($database,$localhost);
$i=mysql_query("select * from boy");
 
$num_rows = mysql_num_rows($i);

#print(json_encode($num_rows));
#while($row = mysql_fetch_array($i))
#{
#$r[]=$row;
#$check=$row['_id'];
#}
if($num_rows > 0) {
while( $row = mysql_fetch_assoc( $i ) ){
print(json_encode($row['name']));
print(json_encode($row['latitude']));
print(json_encode($row['longitude']));
print(json_encode($row['duration']));
echo "\n";
}
}
mysql_close($localhost);
?>
