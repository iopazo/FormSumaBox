package com.sumabox.formsumabox;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseConnect extends SQLiteOpenHelper {
		
	private static final String DATABASE = "db_suma";
	private static final int DATABASE_VERSION = 1;
	static final String TABLE = "encuestas";
	
	private static String ENCUESTADO = "encuestado";
	private static String ID_ENCUESTA = "id_encuesta";
	private static String ID_RESPUESTA = "id_respuesta";
	private static String RESPUESTA = "valor_respuesta";
	
	DatabaseConnect(Context context) {
		super(context, DATABASE, null, DATABASE_VERSION);
	}
	
	public void insert(String encuestado, Integer id_encuesta, Integer id_respuesta, String valor_respuesta) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(ENCUESTADO, encuestado);
		values.put(ID_ENCUESTA, id_encuesta);
		values.put(ID_RESPUESTA, id_respuesta);
		values.put(RESPUESTA, valor_respuesta);
		db.insert(TABLE, null, values);
		db.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, encuestado VARCHAR (250), "
				+ "id_encuesta INTEGER, id_respuesta INTEGER, valor_respuesta TEXT, sync TINYINT(1))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE);
		onCreate(db);
		
	}
}
