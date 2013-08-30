package com.sumabox.formsumabox;

public class Pregunta {

	String _encuestado; 
	int _id_encuesta; 
	int _id_respuesta; 
	String _valor_respuesta;
	
	public Pregunta() {
	}

	public Pregunta(String encuestado, int id_encuesta, int id_respuesta, String valor_respuesta) {
		this._encuestado = encuestado;
		this._id_encuesta = id_encuesta;
		this._id_respuesta = id_respuesta;
		this._valor_respuesta = valor_respuesta;
	}

	public String getEncuestado() {
		return _encuestado;
	}

	public void setEncuestado(String _encuestado) {
		this._encuestado = _encuestado;
	}

	public int getIdEncuesta() {
		return _id_encuesta;
	}

	public void setIdEncuesta(int _id_encuesta) {
		this._id_encuesta = _id_encuesta;
	}

	public int get_id_respuesta() {
		return _id_respuesta;
	}

	public void set_id_respuesta(int _id_respuesta) {
		this._id_respuesta = _id_respuesta;
	}

	public String get_valor_respuesta() {
		return _valor_respuesta;
	}

	public void set_valor_respuesta(String _valor_respuesta) {
		this._valor_respuesta = _valor_respuesta;
	}
	
	

}
