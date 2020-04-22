<?php
	// REST CON JSON 000webhost --- selectAll.php

	// alojamiento en 000webhost => 

	// 000webhost usa una versión de php más avanzada => Extensión MySQL mejorada
	// han quedado algunas funciones obsoletas o deprecated => mysql_connect => obsoleta
	// http://php.net/manual/es/book.mysqli.php

	$hostname ="localhost";  //nuestro servidor de BD
	$database ="id9564461_proyecto"; //Nombre de nuestra base de datos
	$username ="id9564461_javi";       //Nombre de usuario de nuestra base de datos 
	$password ="guiaTuristica@2020";   //Contraseña de nuestra base de datos	
	
	// intentar conectar al servidor con el usuario y contraseña anteriores
	$mysqli = new mysqli($hostname,$username,$password, $database);

	/*
	 * This is the "official" OO way to do it,
	 * BUT $connect_error was broken until PHP 5.2.9 and 5.3.0.
	 */
	if ($mysqli->connect_error) {
		die('Connect Error (' . $mysqli->connect_errno . ') '
			    . $mysqli->connect_error);
	}	
	
 	
	// realizar una consulta que selecciona todas las ubicaciones ordenadas por id
	$consulta = "select * from tabla_ubicacion order by id_ubicacion";

	// crear un array para almacenar el json
	$json = array();

	// lanzar la consulta
	$query_exec = $mysqli->query($consulta);
	if (!$query_exec) {
		echo "Falló la consulta de la tabla_ubicación: (" . $mysqli->errno . ") " . $mysqli->error;
	}
	else {		
		while($row = $query_exec->fetch_array(MYSQLI_ASSOC)){
			// almacena cada fila de la consulta en el array $json
			$json['tabla_ubicacion'][]=$row;
		}		
		
	}		

	// cerrar la conexión con la BD mysql
	$mysqli->close();	

	// mostrar por pantalla y codificar el vector $json como JSON
	echo json_encode($json);	
	
?>


