package com.sumabox.formsumabox;

public class Encuestado {
	
	String _mail;
	String _nombre;
	int _id;
	
	public Encuestado() {
	}
	
	public Encuestado(String mail, String nombre) {
		this._mail = mail;
		this._nombre = nombre;
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

}
