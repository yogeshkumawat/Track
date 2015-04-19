#Track
Project of Feet On Street
Main requirement for this project is setting up a server with Database name "tracker", a Table name "boy", with have five column name as following:
Column Name      

	Name             Type	

1	name             (text)

2	latitude         (int)	

3	longitude        (int)

4	duration          (int)

5	points            (text)


After setting up the mysql server with Table name "tracker". Just need to copy the "demo.php","insert.php","update.php","update2.php" into htdoc folder of xampp.

There are two project for Tracker:
1. Tracker(Admin): It will track all the client who has registered under this server.It will show the list of clients
2. TrackMe(Client): It will send location to server and will update its location when location will change.

In this project we are connecting to server from the IP address of system on which we have set the xampp server.so it is necessary enter system's IP address itno code to run these apks.

There are three major part in this project:
1. To show current location of client
2. Show historic route of client: To show route there should be location change of client so due to this location movement limitation i have shown a route between its current location to a fixed point.
3. To show total duation of time spent on agent's location.
