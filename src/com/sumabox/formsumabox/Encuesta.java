package com.sumabox.formsumabox;

import java.util.List;

public class Encuesta {

	int _id;
	String _logoUrl;
	String _sucursal;
	List<PreguntaEncuesta> _preguntas;
	
	public Encuesta() {
		
	}
	
	public Encuesta(int id, String logoUrl, String sucursal) {
		this._id = id;
		this._logoUrl = logoUrl;
		this._sucursal = sucursal;
	}
	
	public int getId() {
		return _id;
	}

	public void setId(int _id) {
		this._id = _id;
	}

	public String getLogoUrl() {
		return _logoUrl;
	}

	public void setLogoUrl(String _logoUrl) {
		this._logoUrl = _logoUrl;
	}

	public String getSucursal() {
		return _sucursal;
	}

	public void setSucursal(String _sucursal) {
		this._sucursal = _sucursal;
	}

	public List<PreguntaEncuesta> getPreguntas() {
		return _preguntas;
	}

	public void setPreguntas(List<PreguntaEncuesta> _preguntas) {
		this._preguntas = _preguntas;
	}
	
	
}
