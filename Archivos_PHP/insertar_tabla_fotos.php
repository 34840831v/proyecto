<?php

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

	// variables que almacenan los valores que enviamos por nuestra app por POST o a través de un formulario
	//el índice de $_POST['indice'] coincide con el 'name' de los input de insert.html 
	// observar que se llaman igual en nuestra app y aquí, esto es:
	// el índice de $_POST['indice'] con la clave en App: parametros_POST = new ArrayList<NameValuePair>(4))
	$id_ubicacion=$_POST['id_ubicacion'];
	$foto=$_POST['foto'];

	// realizar la inserción de los datos de la ubicación en tabla_ubicacion
	$insertar = "insert into tabla_fotos(id_ubicacion, foto) values ('".$id_ubicacion."','".$foto."')";
	
	// lanzar la consulta
	$query_exec = $mysqli->query($insertar);
	if (!$query_exec) {
		echo "Falló la inserción de en tabla_fotos: (" . $mysqli->errno . ") " . $mysqli->error;
	}	 

?>
