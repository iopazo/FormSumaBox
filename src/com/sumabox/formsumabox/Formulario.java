package com.sumabox.formsumabox;

import java.util.List;

public class Formulario {
	
	String _mail;
	String _nombre;
	int _id;
	int _id_encuesta;
	int _async;
	String _fecha;
	List<Pregunta> _preguntas;
	String _sucursal;
	
	public Formulario() {
	}
	
	public Formulario(String mail, String nombre) {
		this._mail = mail;
		this._nombre = nombre;
	}
	
	public Formulario(int id, String nombre, String mail, List<Pregunta> preguntas, int id_encuesta, String fecha, String sucursal) {
		this._id = id;
		this._mail = mail;
		this._nombre = nombre;
		this._preguntas = preguntas;
		this._id_encuesta = id_encuesta;
		this._fecha = fecha;
		this._sucursal = sucursal;
	}

	public String getMail() {
		return _mail;
	}

	public void setMail(String _mail) {
		this._mail = _mail;
	}

	public String getNombre() {
		return _nombre;
	}

	public void setNombre(String _nombre) {
		this._nombre = _nombre;
	}

	public int getId() {
		return _id;
	}

	public void setId(int _id) {
		this._id = _id;
	}

	public List<Pregunta> getPreguntas() {
		return _preguntas;
	}

	public void setPreguntas(List<Pregunta> _preguntas) {
		this._preguntas = _preguntas;
	}

	public int getAsync() {
		return _async;
	}

	public void setAsync(int _async) {
		this._async = _async;
	}

	public int getIdEncuesta() {
		return _id_encuesta;
	}

	public void setIdEncuesta(int _id_encuesta) {
		this._id_encuesta = _id_encuesta;
	}

	public String getFecha() {
		return _fecha;
	}

	public void setFecha(String _fecha) {
		this._fecha = _fecha;
	}

	public String getSucursal() {
		return _sucursal;
	}

	public void setSucursal(String _sucursal) {
		this._sucursal = _sucursal;
	}
	
	
}
