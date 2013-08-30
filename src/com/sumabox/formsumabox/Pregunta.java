package com.sumabox.formsumabox;

public class Pregunta {

	int _encuestado; 
	int _id_encuesta; 
	int _id_respuesta; 
	String _valor_respuesta;
	
	public Pregunta() {
	}

	public Pregunta(int encuestado, int id_encuesta, int id_respuesta, String valor_respuesta) {
		this._encuestado = encuestado;
		this._id_encuesta = id_encuesta;
		this._id_respuesta = id_respuesta;
		this._valor_respuesta = valor_respuesta;
	}

	public int getEncuestado() {
		return _encuestado;
	}

	public void setEncuestado(int _encuestado) {
		this._encuestado = _encuestado;
	}

	public int getIdEncuesta() {
		return _id_encuesta;
	}

	public void setIdEncuesta(int _id_encuesta) {
		this._id_encuesta = _id_encuesta;
	}

	public int getIdRespuesta() {
		return _id_respuesta;
	}

	public void setIdRespuesta(int _id_respuesta) {
		this._id_respuesta = _id_respuesta;
	}

	public String getValorRespuesta() {
		return _valor_respuesta;
	}

	public void setValorRespuesta(String _valor_respuesta) {
		this._valor_respuesta = _valor_respuesta;
	}
	
	

}
