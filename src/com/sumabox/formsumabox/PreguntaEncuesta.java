package com.sumabox.formsumabox;

import java.util.List;

public class PreguntaEncuesta {

	int _id_pregunta;
	int _total;
	int _id_encuesta;
	boolean _zero;
	boolean _escala;
	String _tipo;
	String _orientation;
	String _label;
	String _before_label;
	String _after_label;
	List<String> _options;
	
	
	public PreguntaEncuesta() {
		// TODO Auto-generated constructor stub
	}
	
	public PreguntaEncuesta(int id_pregunta, String tipo, boolean escala, String orientation, 
			int total, boolean zero, String label, String before_label, String after_label) {
		
		this._id_pregunta = id_pregunta;
		this._tipo = tipo;
		this._escala = escala;
		this._orientation = orientation;
		this._total = total;
		this._zero = zero;
		this._label = label;
		this._before_label = before_label;
		this._after_label = after_label;
		//this._options = options;
		
	}


	public int get_id_pregunta() {
		return _id_pregunta;
	}


	public void set_id_pregunta(int _id_pregunta) {
		this._id_pregunta = _id_pregunta;
	}


	public String get_tipo() {
		return _tipo;
	}


	public void set_tipo(String _tipo) {
		this._tipo = _tipo;
	}


	public boolean is_escala() {
		return _escala;
	}


	public void set_escala(boolean _escala) {
		this._escala = _escala;
	}


	public String get_orientation() {
		return _orientation;
	}


	public void set_orientation(String _orientation) {
		this._orientation = _orientation;
	}


	public int get_total() {
		return _total;
	}


	public void set_total(int _total) {
		this._total = _total;
	}


	public boolean is_zero() {
		return _zero;
	}


	public void set_zero(boolean _zero) {
		this._zero = _zero;
	}


	public String get_label() {
		return _label;
	}


	public void set_label(String _label) {
		this._label = _label;
	}


	public String get_before_label() {
		return _before_label;
	}


	public void set_before_label(String _before_label) {
		this._before_label = _before_label;
	}


	public String get_after_label() {
		return _after_label;
	}


	public void set_after_label(String _after_label) {
		this._after_label = _after_label;
	}


	public List<String> get_options() {
		return _options;
	}


	public void set_options(List<String> _options) {
		this._options = _options;
	}
	
	public int get_id_encuesta() {
		return _id_encuesta;
	}


	public void set_id_encuesta(int _id_encuesta) {
		this._id_encuesta = _id_encuesta;
	}
	

}
