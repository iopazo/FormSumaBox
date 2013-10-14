package com.sumabox.formsumabox;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

public class DatabaseConnect extends SQLiteOpenHelper {
		
	private static final String DATABASE = "db_suma";
	private static final int DATABASE_VERSION = 1;
	// TABLAS
	static final String TABLE_ENCUESTADO = "encuestado";
	static final String TABLE = "respuestas";
	static final String TABLE_ENCUESTAS = "encuestas";
	static final String TBL_PPREGUNTAS_ENCUESTAS = "preguntas";
	// COLUMNAS
	private static String ENCUESTADO = "encuestado";
	private static String ID_ENCUESTA = "id_encuesta";
	private static String ID_RESPUESTA = "id_respuesta";
	private static String RESPUESTA = "valor_respuesta";
	private String MAIL = "email";
	private String NOMBRE = "nombre";
	private String ASYNC = "async";
	private String FECHA = "fecha";
	private String SUCURSAL = "sucursal";
	
	
	DatabaseConnect(Context context) {
		super(context, DATABASE, null, DATABASE_VERSION);
	}
	
	public boolean havingEncuesta(boolean sync) {
		
		if(sync) {
			truncateTable(TABLE_ENCUESTAS);
			truncateTable(TBL_PPREGUNTAS_ENCUESTAS);
		}
		
		SQLiteDatabase queryDB = this.getReadableDatabase();
		String query = "SELECT * FROM " + TABLE_ENCUESTAS;
		Cursor cursor = queryDB.rawQuery(query, null);
		
		if(cursor != null && cursor.getCount() == 0) {
			queryDB.close();
			return false;
		}
		queryDB.close();
		return true;
	}
	
	public void truncateTable(String table) {
		
		SQLiteDatabase deleteQuery = this.getWritableDatabase();
		deleteQuery.delete(table, null, null);
		//String query = "DELETE FROM " + table;
		//deleteQuery.rawQuery(query, null);
		deleteQuery.close();
	}
	
	public Encuesta saveEncuesta(JSONObject jsonObject) throws JSONException{
		
		Encuesta enc = new Encuesta();
		
		SQLiteDatabase queryDB = this.getReadableDatabase();
		String query = "SELECT * FROM " + TABLE_ENCUESTAS;
		Cursor cursor = queryDB.rawQuery(query, null);
		
		if(cursor != null) {
			
			if(cursor.getCount() == 0) {
				
				List<PreguntaEncuesta> preguntasList = new ArrayList<PreguntaEncuesta>();
				
				JSONObject encuesta = jsonObject.getJSONObject("encuesta");
				
				enc.setId(encuesta.getInt("id"));
				enc.setLogoUrl(encuesta.getString("logo_url"));
				enc.setSucursal(encuesta.getString("sucursal"));
				
				ContentValues values = new ContentValues();
				values.put("id_encuesta", enc.getId());
				values.put("logo_url", enc.getLogoUrl());
				values.put("sucursal", enc.getSucursal());
				SQLiteDatabase db = this.getWritableDatabase();
				long insertId = db.insert(TABLE_ENCUESTAS, null, values);
				
				JSONArray preguntas = encuesta.getJSONArray("preguntas");
				int numeroPreguntas = preguntas.length();
				
				for (int i = 0; i < numeroPreguntas; i++) {
					
					PreguntaEncuesta prEncuesta = new PreguntaEncuesta();
					JSONObject pr = preguntas.getJSONObject(i);
					
					prEncuesta.set_id_encuesta((int)insertId);
					prEncuesta.set_id_pregunta(pr.getInt("id_pregunta"));
					prEncuesta.set_tipo(pr.getString("tipo"));
					
					if(prEncuesta.get_tipo().equals("radio")) {
						
						prEncuesta.set_escala(pr.getBoolean("escala"));
						prEncuesta.set_orientation(pr.getString("orientation"));
						
						if(prEncuesta.is_escala()) {
							prEncuesta.set_before_label(pr.getString("before_label"));
							prEncuesta.set_after_label(pr.getString("after_label"));
							prEncuesta.set_total(pr.getInt("total"));
							prEncuesta.set_zero(pr.getBoolean("zero"));
						} else {
							
							JSONArray opciones = pr.getJSONArray("options");
							List<String> options = new ArrayList<String>();
							for (int j = 0; j < opciones.length(); j++) {
								JSONObject textJson = opciones.getJSONObject(j);
								options.add(textJson.getString("texto"));
							}
							String unidos = TextUtils.join(",", options);
							prEncuesta.set_options(unidos);
						}
					}
					prEncuesta.set_label(pr.getString("label"));
					savePreguntas(prEncuesta);
					
					preguntasList.add(prEncuesta);
				}
				
				enc.setPreguntas(preguntasList);
				
				db.close();
				cursor.close();
			} else {
				if(cursor.moveToFirst()) {
					do {
						enc.setLogoUrl(cursor.getString(1));
						enc.setSucursal(cursor.getString(2));
						enc.setId(cursor.getInt(3));
						enc.setPreguntas(getPreguntasEncuestasByEncuesta(cursor.getInt(0)));
					} while (cursor.moveToNext());
				}
				cursor.close();
			}
		}
		return enc;
	}
	
	public List<PreguntaEncuesta> getPreguntasEncuestasByEncuesta(int idEncuesta) {
		
		List<PreguntaEncuesta> preguntasArray = new ArrayList<PreguntaEncuesta>();
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT * FROM " + TBL_PPREGUNTAS_ENCUESTAS + " WHERE id_encuesta = " + idEncuesta;
		Cursor cursor = db.rawQuery(query, null);
		
		if(cursor != null) {
			if(cursor.moveToFirst()) {
				do {
					boolean escala = cursor.getInt(5) == 1 ? true : false;
					boolean zero = cursor.getInt(4) == 1 ? true : false;
					
					preguntasArray.add(new PreguntaEncuesta(cursor.getInt(1), cursor.getString(6), escala, 
							cursor.getString(7), cursor.getInt(3), zero, cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11)));
				} while (cursor.moveToNext());
			}
		}
		db.close();
		return preguntasArray;
	}
	
	public void savePreguntas(PreguntaEncuesta preguntaEncuesta) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("id_pregunta", preguntaEncuesta.get_id_pregunta());
		values.put("total", preguntaEncuesta.get_total());
		values.put("zero", preguntaEncuesta.is_zero());
		values.put("escala", preguntaEncuesta.is_escala());
		values.put("tipo", preguntaEncuesta.get_tipo());
		values.put("orientation", preguntaEncuesta.get_orientation());
		values.put("label", preguntaEncuesta.get_label());
		values.put("before_label", preguntaEncuesta.get_before_label());
		values.put("after_label", preguntaEncuesta.get_after_label());
		values.put("id_encuesta", preguntaEncuesta.get_id_encuesta());
		values.put("options", preguntaEncuesta.get_options());
		
		db.insert(TBL_PPREGUNTAS_ENCUESTAS, null, values);
		db.close();
	}
	
	public long addEncuestado(Encuestado encuestado) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		long lastInsertId = 0;
		
		ContentValues values = new ContentValues();
		values.put(MAIL, encuestado.getMail());
		values.put(NOMBRE, encuestado.getNombre());
		values.put(ASYNC, encuestado.getAsync());
		values.put(ID_ENCUESTA, encuestado.getIdEncuesta());
		values.put(SUCURSAL, encuestado.getSucursal());
		
		lastInsertId = db.insert(TABLE_ENCUESTADO, null, values);
		db.close();
		
		return lastInsertId;
	}
	
	public void updateEncuestado(int idEncuestado) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(ASYNC, 1);
		db.update(TABLE_ENCUESTADO, values, "id = ?", new String[] { String.valueOf(idEncuestado) });
		
		db.close();
	}
	
	public void addPregunta(Pregunta pregunta) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(ENCUESTADO, pregunta.getEncuestado());
		values.put(ID_ENCUESTA, pregunta.getIdEncuesta());
		values.put(ID_RESPUESTA, pregunta.getIdRespuesta());
		values.put(RESPUESTA, pregunta.getValorRespuesta());
		db.insert(TABLE, null, values);
		db.close();
	}
	
	/*
	 * Traemos toda la lista de encuestados sin asincronia.
	 */
	public List<Encuestado> getEncuestados() {
		
		List<Encuestado> encuestadosArray = new ArrayList<Encuestado>();
		List<Pregunta> preguntas = new ArrayList<Pregunta>();
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT id," + NOMBRE + ", " + MAIL + ", " + ID_ENCUESTA + ", " + FECHA + ", " + SUCURSAL + " "
				+ "FROM " + TABLE_ENCUESTADO + " WHERE " + ASYNC + " = 0";
		Cursor cursor = db.rawQuery(query, null);
		
		if(cursor != null) {
			if(cursor.moveToFirst()) {
				do {
					preguntas = getPreguntasByEncuestado(cursor.getInt(0));
					encuestadosArray.add(new Encuestado(cursor.getInt(0), cursor.getString(1), 
							cursor.getString(2), preguntas, cursor.getInt(3), cursor.getString(4), cursor.getString(5)));
				} while (cursor.moveToNext());
			}
		}
		
		db.close();
		return encuestadosArray;
	}
	
	/*
	 * Traemos toda la lista de preguntas por encuestado.
	 */
	public List<Pregunta> getPreguntasByEncuestado(int idEncuestado) {
		
		List<Pregunta> preguntasArray = new ArrayList<Pregunta>();
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT * FROM " + TABLE + " WHERE " + ENCUESTADO + " = " + idEncuestado;
		Cursor cursor = db.rawQuery(query, null);
		
		if(cursor != null) {
			cursor.moveToFirst();
			do {
				 preguntasArray.add(new Pregunta(idEncuestado, cursor.getInt(2), cursor.getInt(3), cursor.getString(4)));
			} while (cursor.moveToNext());
		}
		
		db.close();
		return preguntasArray;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, encuestado INTEGER (10), "
				+ "id_encuesta INTEGER, id_respuesta INTEGER, valor_respuesta TEXT)");
		db.execSQL("CREATE TABLE " + TABLE_ENCUESTADO + " (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre VARCHAR(200),"
				+ " email VARCHAR(200), async TINYINT(1) DEFAULT 0, id_encuesta INTEGER, fecha DATETIME DEFAULT CURRENT_TIMESTAMP, sucursal INTEGER)");
		
		db.execSQL("CREATE TABLE " + TABLE_ENCUESTAS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, logo_url VARCHAR(200),"
				+ " sucursal VARCHAR(20), id_encuesta INTEGER)");
		db.execSQL("CREATE TABLE " + TBL_PPREGUNTAS_ENCUESTAS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, id_pregunta INTEGER, id_encuesta INTEGER, "
				+ " total INTEGER, zero TINYINT(1) DEFAULT 0, escala TINYINT(1) DEFAULT 0, tipo VARCHAR(20), orientation VARCHAR(20), label VARCHAR(200),"
				+ "before_label VARCHAR(200), after_label VARCHAR(200), options TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENCUESTADO);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENCUESTAS);
		db.execSQL("DROP TABLE IF EXISTS " + TBL_PPREGUNTAS_ENCUESTAS);
		onCreate(db);
	}
}
