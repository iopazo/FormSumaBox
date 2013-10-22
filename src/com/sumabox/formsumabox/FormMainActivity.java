package com.sumabox.formsumabox;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class FormMainActivity extends Activity implements OnClickListener {
	
	private TextView[] labels;
	private EditText[] editText;
	private RadioButton[] radioButton;
	static SharedPreferences prefs;
	static String url;
	static String sucursal;
	static String id_pregunta, label, before_label, after_label, tipo, orientation, url_logo_encuesta;
	static Boolean escala, zero, sync;
	static ImageView imageView;
	private static Integer total, id_encuesta;
	static String[] opciones;
	DatabaseConnect dbConnect = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		url = prefs.getString("example_text", "");
		sucursal = prefs.getString("id_sucursal", "");
		sync = prefs.getBoolean("sync", false);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		ConnectivityManager conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo wifi = conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobile = conManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		
		dbConnect = new DatabaseConnect(this);
		LinearLayout ll = (LinearLayout) findViewById(1001);
		
		if(dbConnect.havingEncuesta(sync)) {
			if(ll == null) {
				loadActivity();
			}
		} else {
			
			if((wifi.isConnected() || (mobile != null && mobile.isConnected())) && URLUtil.isValidUrl(url)) {
				if(ll == null) {
					loadActivity();
				}
			} else if(!wifi.isConnected() && (mobile != null && mobile.isConnected())){
				dialog("Debe estar conectado a Wifi para cargar el formulario.");
			} else {
				Intent intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
			}
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		
		if(wifi.isConnected() || (mobile != null && mobile.isConnected())) {
			sinchronizeData();
		}
	}
	
	private void dialog(String message) {
		
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
				ConnectivityManager conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
				NetworkInfo wifi = conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				NetworkInfo mobile = conManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				
				if(wifi.isConnected() || mobile.isConnected()) {
					loadActivity();
				} else {
					dialog("Debe estar conectado a Wifi para cargar el formulario.");
				}
			}
		});
		dialogBuilder.setTitle("Formulario");
		dialogBuilder.setMessage(message);
		dialogBuilder.show();
		
	}
	
	private void loadActivity() {
		
		try {
			//Aca se conecta con el objeto json...
			JSONObjectTask task = new JSONObjectTask();
			
			//Obtenemos el objeto json desde la tarea asincrona
			JSONObject jsonData = null;
			dbConnect = new DatabaseConnect(this);
			
			if(!dbConnect.havingEncuesta(sync)) {
				jsonData = task.execute(url).get();
				
				if(jsonData == null){
					Intent intent = new Intent(this, SettingsActivity.class);
					startActivity(intent);
				}
			}
			
			Encuesta encuestaObj = dbConnect.saveEncuesta(jsonData);
			
			if(sync) {
				 SharedPreferences settings = 
				        PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				    SharedPreferences.Editor editor = settings.edit();
				    editor.putBoolean("sync", false);
				    editor.commit();
			}
			
			ScrollView scrollView;
			LinearLayout linearLayout;
			
			//Variables para el manejo de distancias entre objetos
			int row; // Indica el objeto que creamos
			int numeroPreguntas; // Escala para los radio button
			
			//JSONObject encuesta = jsonData.getJSONObject("encuesta");
			id_encuesta = encuestaObj.getId();
			url_logo_encuesta = encuestaObj.getLogoUrl();
			
			//JSONArray preguntas = encuesta.getJSONArray("preguntas");
			List<PreguntaEncuesta> preguntas = encuestaObj.getPreguntas();
			
			numeroPreguntas = preguntas.size();
			
			labels = new TextView[numeroPreguntas];  
			editText = new EditText[numeroPreguntas];  
			
			scrollView = new ScrollView(this);
			linearLayout = new LinearLayout(this);
			linearLayout.setId(1001);
			
			//Logo de la aplicacion
			if(!url_logo_encuesta.equals("")) {
				
				imageView = new ImageView(this);
				LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 
						LinearLayout.LayoutParams.WRAP_CONTENT);
				imageView.setLayoutParams(imgParams);
				
				GetPngImageTask imageTask = new GetPngImageTask();
				imageTask.execute(url_logo_encuesta);
				
				linearLayout.addView(imageView);
			}
			
			for(row = 0; row < numeroPreguntas; row++) {
				
				PreguntaEncuesta c = preguntas.get(row);
				//JSONObject c = preguntas.getJSONObject(row);
				
				Integer id = c.get_id_pregunta();
				tipo = c.get_tipo();
				
				if(tipo.equals("radio")) {
					escala = c.is_escala();
					orientation = c.get_orientation();
					if(escala) {
						before_label = c.get_before_label();
						after_label = c.get_after_label();
						total = c.get_total();
						radioButton = new RadioButton[total];
						zero = c.is_zero();
					} else {
						opciones = TextUtils.split(c.get_options(), ",");
						radioButton = new RadioButton[opciones.length];
					}
				}
				
				label = c.get_label();
				
				if(!tipo.equals("radio")) {
					
					LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					
					labels[row] = new TextView(this);
					labels[row].setText(label);
					labelParams.setMargins(50, 10, 50, 0);
					labels[row].setTextSize(22);
					labels[row].setLayoutParams(labelParams);
					linearLayout.addView(labels[row]);
					
					LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					inputParams.setMargins(50, 20, 50, 0);
					editText[row] = new EditText(this);
					editText[row].setHint("Inserte su respuesta aqui...");
					editText[row].setId(id);
					editText[row].setLayoutParams(inputParams);
					linearLayout.addView(editText[row]);
					
				} else {
					
					LinearLayout.LayoutParams labelRadioParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					
					labels[row] = new TextView(this);
					labels[row].setText(label);
					labelRadioParams.setMargins(50, 10, 50, 0);
					labels[row].setTextSize(22);
					labels[row].setLayoutParams(labelRadioParams);
					linearLayout.addView(labels[row]);
					
					LinearLayout.LayoutParams leftLabelRadioParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					
					if(escala && !before_label.equals("") && !after_label.equals("")) {
						//Before Label
						labels[row] = new TextView(this);
						labels[row].setText("Debe seleccionar entre " + before_label + " y " + after_label + ".");
						labels[row].setTextSize(18);
						labels[row].setTextColor(Color.GRAY);
						leftLabelRadioParams.setMargins(50, 15, 50, 0);
						labels[row].setLayoutParams(leftLabelRadioParams);
						linearLayout.addView(labels[row]);
					}
					
					LinearLayout.LayoutParams radioGParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					radioGParams.setMargins(50, 20, 0, 0);
					RadioGroup radioGroup = new RadioGroup(this);
					
					if(orientation.equals("horizontal")) {
						radioGroup.setGravity(Gravity.CENTER_HORIZONTAL);
						radioGroup.setOrientation(RadioGroup.HORIZONTAL);
					}
					
					radioGroup.setLayoutParams(radioGParams);
					radioGroup.setId(id);
					linearLayout.addView(radioGroup);
					
					if(escala) {
						for (int r = ((!zero) ? 0 : 1); r < total; r++) {
							radioButton[r] = new RadioButton(this);
							radioButton[r].setText("" + r);
							radioGroup.addView(radioButton[r]);
						}
					} else {
						for (int r = 0; r < opciones.length ; r++) {
							radioButton[r] = new RadioButton(this);
							radioButton[r].setText(opciones[r]);
							radioGroup.addView(radioButton[r]);
						}
					}
				}
			}
			
			LinearLayout.LayoutParams usuariosInputParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			usuariosInputParam.setMargins(50, 10, 50, 0);
			
			TextView nombreLabel = new TextView(this);
			nombreLabel.setTextSize(22);
			nombreLabel.setText(getResources().getString(R.string.encuestado_label));
			EditText nombre = new EditText(this);
			nombre.setTag("nombre");
			nombre.setHint("Ingrese su nombre aqui");
			nombre.setLayoutParams(usuariosInputParam);
			nombreLabel.setLayoutParams(usuariosInputParam);
			
			TextView emailLabel = new TextView(this);
			emailLabel.setTextSize(22);
			emailLabel.setText(getResources().getString(R.string.mail_label));
			EditText email = new EditText(this);
			email.setTag("email");
			email.setHint("Ingrese su email");
			email.setLayoutParams(usuariosInputParam);
			emailLabel.setLayoutParams(usuariosInputParam);
			
			linearLayout.addView(nombreLabel);
			linearLayout.addView(nombre);
			linearLayout.addView(emailLabel);
			linearLayout.addView(email);
			
			/*
			 * Boton Guardar
			 */
			Button button = new Button(this);
			button.setText("Guardar Formulario");
			LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			buttonParams.setMargins(50, 40, 50, 0);
			button.setLayoutParams(buttonParams);
			button.setOnClickListener(this);
			linearLayout.addView(button);
			
			//Params para el layout Linear
			linearLayout.setOrientation(LinearLayout.VERTICAL);
			scrollView.addView(linearLayout);
			
			//Params para el scroll View.
			LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
			scrollView.setLayoutParams(scrollParams);
			scrollView.setBackgroundColor(Color.parseColor("#e9e9e9"));
			setContentView(scrollView);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	 * AsyncTask HERE
	 */
	private class JSONObjectTask extends AsyncTask<String, Void, JSONObject>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected JSONObject doInBackground(String... urls) {
			JSONParser jparser = new JSONParser();
			JSONObject jObj = null;
			try {
				jObj = jparser.getJSONFromUrl(urls[0]);
			} catch (JSONException e) {
				Log.e("JSON Parser", "Error parsing data " + e.toString());
			}
			return jObj;
		}
		
		@Override
	    protected void onPostExecute(JSONObject json) {
	    	super.onPostExecute(json);
	    }
	}
	
	private class JsonPostObjectTask extends AsyncTask<JSONObject, Void, Boolean> {
		
		@Override
		protected Boolean doInBackground(JSONObject... json) {
			
			Boolean exito = false;
			
			String urlPostData = "http://lavozdelservicio.cl/encuestas/formsumabox.php";
			JSONParser jsonParser = new JSONParser();
			try {
				exito = jsonParser.postJsonFromUrl(json[0], urlPostData);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return exito;
		}
		
	}
	
	private class GetPngImageTask extends AsyncTask<String, Void, Bitmap>{
		
		@Override
		protected Bitmap doInBackground(String... url) {
			Bitmap map = null;
			map = downloadImage(url[0]);
			return map;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			imageView.setImageBitmap(result);
		}
		
		private Bitmap downloadImage(String url) {
			Bitmap map = null;
			InputStream is = null;
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			
			try {
				is = getHttpConnection(url);
				map = BitmapFactory.decodeStream(is, null, options);
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return map;
		}
		
		private InputStream getHttpConnection(String urlString) throws IOException {
			InputStream is = null;
			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();
			
			try {
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				httpConnection.setRequestMethod("GET");
				httpConnection.connect();
				
				if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					is = httpConnection.getInputStream();
				}
			} catch (Exception e) {
				throw new IOException("Error connecting");
			}
			return is;
		}
	}
	
	public boolean validateEmail(String email) {
		
		boolean validEmail = true;
		if(!email.toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
			validEmail = false;
		}
		return validEmail;
	}
	
	private void sinchronizeData() {
		
		dbConnect = new DatabaseConnect(this);
		
		List<Encuestado> contactosArray = dbConnect.getEncuestados();
		
		if(contactosArray.size() > 0) {
			for (int i = 0; i < contactosArray.size(); i++) {
				JSONObject jPost = new JSONObject();
				JSONArray preguntasArray = new JSONArray();
				JSONArray encuestadoArray = new JSONArray();
				Encuestado encuestado = contactosArray.get(i);
				
				JSONObject encuestadoObj = new JSONObject();
				
				try {
					encuestadoObj.put("nombre", encuestado.getNombre());
					encuestadoObj.put("email", encuestado.getMail());
					encuestadoObj.put("id_encuesta", encuestado.getIdEncuesta());
					encuestadoObj.put("sucursal", encuestado.getSucursal());
					encuestadoObj.put("fecha", encuestado.getFecha());
					encuestadoArray.put(encuestadoObj);
					jPost.put("encuestado", encuestadoArray);
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				
				if(encuestado.getPreguntas().size() > 0) {
					for (int j = 0; j < encuestado.getPreguntas().size(); j++) {
						Pregunta pregunta = encuestado.getPreguntas().get(j);
						
						JSONObject preguntaObject = new JSONObject();
						try {
							preguntaObject.put("encuestado", pregunta.getEncuestado());
							preguntaObject.put("id_encuesta", pregunta.getIdEncuesta());
							preguntaObject.put("id_respuesta", pregunta.getIdRespuesta());
							preguntaObject.put("valor_respuesta", pregunta.getValorRespuesta());
							preguntasArray.put(preguntaObject);
							jPost.put("preguntas", preguntasArray);
							
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
					}
				}
				JsonPostObjectTask task = new JsonPostObjectTask();
				task.execute(jPost);
				dbConnect.updateEncuestado(encuestado.getId());
			}
		}
	}
	
	@Override
	public void onClick(View view) {
			
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		
		JSONObject jPost = new JSONObject();
		JSONArray preguntasArray = new JSONArray();
		JSONArray encuestadoArray = new JSONArray();
		Boolean error = false;
		int encuestado;
		ArrayList<EditText> userTextList = new ArrayList<EditText>();
		ArrayList<EditText> editTextList = new ArrayList<EditText>();
		ArrayList<RadioGroup> radioGroupList = new ArrayList<RadioGroup>();
		
		LinearLayout linearLayout = (LinearLayout) findViewById(1001);
		
		for(int i = 0; i < linearLayout.getChildCount(); i++) {
			
			if(linearLayout.getChildAt(i) instanceof EditText) {
				EditText texto = (EditText) linearLayout.getChildAt(i);
				if(texto.getTag() == null) {
					editTextList.add(texto);
				} else {
					userTextList.add(texto);
				}
			}
			
			if(linearLayout.getChildAt(i) instanceof RadioGroup) {
				radioGroupList.add((RadioGroup) linearLayout.getChildAt(i));
			}
		}
		
		// Recorremos primero los datos del usuario.
		for (int u = 0; u < userTextList.size(); u++) {
			EditText texto = (EditText) userTextList.get(u);
			
			if(texto.getText().toString().length() == 0) {
				if(texto.getTag().equals("nombre")) {
					error = true;
					texto.setError("Debe ingresar su nombre");
				}
				if(texto.getTag().equals("email")) {
					error = true;
					texto.setError("Debe ingresar su email");
				}
			} else if(texto.getTag().equals("email") && !validateEmail(texto.getText().toString())){
				error = true;
				texto.setError("Debe ingresar un email valido");
			}
		}
		
		
		//Recorremos el arrayList para buscar errores.
		for(int e = 0; e < editTextList.size(); e++) {
			
			EditText texto = (EditText) findViewById(editTextList.get(e).getId());
			String respuesta = texto.getText().toString();
			if(respuesta.length() == 0) {
				error = true;
				texto.setError("Debe ingresar su respuesta aqui");
			}
		}
		
		/*
		 * Recorremos los radioGroup
		 */
		for(int rg = 0; rg < radioGroupList.size(); rg++) {
			Boolean radioChecked = false;
			RadioGroup radioButtonGroup = (RadioGroup) findViewById(radioGroupList.get(rg).getId());
			
			int radioChilds = radioButtonGroup.getChildCount();
			for(int ra = 0; ra < radioChilds; ra++) {
				RadioButton radioButton = (RadioButton)radioButtonGroup.getChildAt(ra);
				
				if(radioButton.isChecked()) {
					radioChecked = true;
				}
			}
			
			if(!radioChecked) {
				RadioButton radioButton = (RadioButton)radioButtonGroup.getChildAt(0);
				radioButton.setError("Debe seleccionar una opcion");
				error = true;
			}
		}
		
		//No hay errores asi que se continua
		if(!error) {
			dbConnect = new DatabaseConnect(this);
			Encuestado encuestadoObj = new Encuestado();
			
			for (int u = 0; u < userTextList.size(); u++) {
				EditText texto = (EditText) userTextList.get(u);
				if(texto.getTag().equals("email")) {
					encuestadoObj.setMail(texto.getText().toString());
				}
				if(texto.getTag().equals("nombre")) {
					encuestadoObj.setNombre(texto.getText().toString());
				}
				texto.setText("");
			}
			if(wifi.isConnected()) {
				encuestadoObj.setAsync(1);
			} else {
				encuestadoObj.setAsync(0);
			}
			encuestadoObj.setIdEncuesta(id_encuesta);
			encuestadoObj.setSucursal(sucursal);
			encuestado = (int) dbConnect.addEncuestado(encuestadoObj);
			
			for (int i = 0; i < editTextList.size(); i++) {
				
				EditText texto = (EditText) findViewById(editTextList.get(i).getId());
				String respuesta = (String) texto.getText().toString();
				
				dbConnect.addPregunta(new Pregunta(encuestado, id_encuesta, texto.getId(), respuesta));
				
				JSONObject reqObj = new JSONObject();
				
				try {
					reqObj.put("encuestado", encuestado);
					reqObj.put("id_encuesta", id_encuesta);
					reqObj.put("id_respuesta", texto.getId());
					reqObj.put("valor_respuesta", respuesta);
					preguntasArray.put(reqObj);
					jPost.put("preguntas", preguntasArray);
					
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				texto.setText("");
			}
			
			for(int rg = 0; rg < radioGroupList.size(); rg++) {
				RadioGroup radioButtonGroup = (RadioGroup) findViewById(radioGroupList.get(rg).getId());
				
				int radioChilds = radioButtonGroup.getChildCount();
				for(int ra = 0; ra < radioChilds; ra++) {
					RadioButton radioButton = (RadioButton)radioButtonGroup.getChildAt(ra);
					
					if(radioButton.isChecked()) {
						
						dbConnect.addPregunta(new Pregunta(encuestado, id_encuesta, radioButtonGroup.getId(), String.valueOf(ra)));
						
						JSONObject reqObj = new JSONObject();
						
						try {
							reqObj.put("encuestado", encuestado);
							reqObj.put("id_encuesta", id_encuesta);
							reqObj.put("id_respuesta", radioButtonGroup.getId());
							reqObj.put("valor_respuesta", String.valueOf(ra));
							preguntasArray.put(reqObj);
							jPost.put("preguntas", preguntasArray);
							
						} catch (JSONException e2) {
							e2.printStackTrace();
						}
						radioButtonGroup.clearCheck();
					}
				}
			}
			
			JSONObject reqObj = new JSONObject();
			try {
				reqObj.put("nombre", encuestadoObj.getNombre());
				reqObj.put("email", encuestadoObj.getMail());
				reqObj.put("id_encuesta", encuestadoObj.getIdEncuesta());
				reqObj.put("sucursal", encuestadoObj.getSucursal());
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date date = new Date();
				reqObj.put("fecha", dateFormat.format(date));
				encuestadoArray.put(reqObj);
				jPost.put("encuestado", encuestadoArray);
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
		}
		
		if(wifi.isConnected() 
				|| (mobile != null && mobile.isConnected()) 
				&& !error) {
			JsonPostObjectTask task = new JsonPostObjectTask();
			task.execute(jPost);
			
			Toast mToast = Toast.makeText(this, "Formulario guardado correctamente.", Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,0, 0);
			mToast.show();
		} else if(!wifi.isConnected() && (mobile != null && !mobile.isConnected())){
			Toast mToast = Toast.makeText(this, "Sin conexion wifi", Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,0, 0);
			mToast.show();
		}
	}
	
	//Menu
	public void configApp() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.form_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
	    switch (item.getItemId()) {
	    case R.id.item1:
	    	configApp();
	        return true;

	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}
