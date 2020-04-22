<?php

	$hostname ="localhost";  //nuestro servidor de BD
	$database ="id9564461_proyecto"; //Nombre de nuestra base de datos
	$username ="id9564461_javi"; //Nombre de usuario de nuestra base de datos 
	$password ="123456";   //Contraseña de nuestra base de datos
	
	$id_ubicacion=$_REQUEST['id_ubicacion'];

	// intentar conectar al servidor con el usuario y contraseña anteriores
	$cnx = new mysqli($hostname,$username,$password, $database);

	//Creamos la sentencia SQL y la ejecutamos
	$cnx->query("Delete from tabla_ubicacion where id_ubicacion='$id_ubicacion'");
	echo "datos eliminados";

?>

