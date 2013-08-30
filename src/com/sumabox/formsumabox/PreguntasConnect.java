package com.sumabox.formsumabox;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PreguntasConnect extends SQLiteOpenHelper {
		
	private static final String DATABASE = "db_suma";
	private static final int DATABASE_VERSION = 1;
	static final String TABLE = "encuestas";
	static final String TABLE_ENCUESTADO = "encuestado";
	
	private static String ENCUESTADO = "encuestado";
	private static String ID_ENCUESTA = "id_encuesta";
	private static String ID_RESPUESTA = "id_respuesta";
	private static String RESPUESTA = "valor_respuesta";
	private String MAIL = "email";
	private String NOMBRE = "nombre";
	
	PreguntasConnect(Context context) {
		super(context, DATABASE, null, DATABASE_VERSION);
	}
	
	public long addEncuestado(Encuestado encuestado) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		long lastInsertId = 0;
		
		ContentValues values = new ContentValues();
		values.put(MAIL, encuestado.getMail());
		values.put(NOMBRE, encuestado.getNombre());
		lastInsertId = db.insert(TABLE_ENCUESTADO, null, values);
		db.close();
		
		return lastInsertId;
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

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, encuestado VARCHAR (250), "
				+ "id_encuesta INTEGER, id_respuesta INTEGER, valor_respuesta TEXT, sync TINYINT(1))");
		db.execSQL("CREATE TABLE " + TABLE_ENCUESTADO + " (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre VARCHAR(200),"
				+ " email VARCHAR(200))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENCUESTADO);
		onCreate(db);
	}
}
