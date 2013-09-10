package com.sumabox.formsumabox;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
				+ " sucursal INTEGER, id_encuesta INTEGER)");
		
		db.execSQL("CREATE TABLE " + TBL_PPREGUNTAS_ENCUESTAS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, id_pregunta INTEGER,"
				+ " total INTEGER, zero TINYINT(1) DEFAULT 0, escala TINYINT(1) DEFAULT 0, tipo VARCHAR(20), orientation VARCHAR(20), label VARCHAR(200),"
				+ "before_label VARCHAR(200), after_label VARCHAR(200)");
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
