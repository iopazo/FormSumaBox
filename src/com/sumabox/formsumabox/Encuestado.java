package com.sumabox.formsumabox;

import java.util.List;

public class Encuestado {
	
	String _mail;
	String _nombre;
	int _id;
	int _async;
	List<Pregunta> _preguntas;
	
	public Encuestado() {
	}
	
	public Encuestado(String mail, String nombre) {
		this._mail = mail;
		this._nombre = nombre;
	}
	
	public Encuestado(int id, String nombre, String mail, List<Pregunta> preguntas) {
		this._id = id;
		this._mail = mail;
		this._nombre = nombre;
		this._preguntas = preguntas;
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
	
	

}
