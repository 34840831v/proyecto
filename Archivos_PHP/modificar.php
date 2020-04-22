<?php 
    //modificar.php
    
	// 000webhost usa una versión de php más avanzada => Extensión MySQL mejorada
	// han quedado algunas funciones obsoletas o deprecated => mysql_connect => obsoleta
	// http://php.net/manual/es/book.mysqli.php

    $hostname ="localhost";  //nuestro servidor de BD
	$database ="id9564461_proyecto"; //Nombre de nuestra base de datos
	$username ="id9564461_javi";       //Nombre de usuario de nuestra base de datos 
	$password ="123456";   //Contraseña de nuestra base de datos	
	
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
	// observar que se llaman igual en nuestra app y aquí:
	// el índice de $_POST['indice'] con la clave en App: parametros_POST = new ArrayList<NameValuePair>(5))
	$id_ubicacion=$_POST['id_ubicacion'];
	$nombre=$_POST['nombre'];
	$direccion=$_POST['direccion'];
	$ubicacion=$_POST['ubicacion'];
	$web=$_POST['web'];
	$tipo=$_POST['tipo'];

	// realizar la modificación de los datos del alumno en la tabla ubicaciones
	$modifica = "UPDATE tabla_ubicacion SET nombre = '$nombre', direccion = '$direccion', ubicacion = '$ubicacion', web = '$web', tipo = '$tipo' WHERE id_ubicacion = $id_ubicacion;";

	// lanzar la consulta
	$query_exec = $mysqli->query($modifica);
	if (!$query_exec) {
		echo "Falló al modificar: (" . $mysqli->errno . ") " . $mysqli->error;
	}
?>