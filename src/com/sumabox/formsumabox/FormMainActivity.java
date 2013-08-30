package com.sumabox.formsumabox;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
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

public class FormMainActivity extends Activity implements OnClickListener {
	
	private TextView[] labels;
	private EditText[] editText;
	private RadioButton[] radioButton;
	private static LinearLayout relativeLayout;
	static String url = "http://moveapps.cl/json_android.php";
	static String id_pregunta, label, before_label, after_label, tipo, orientation, url_logo_encuesta;
	static Boolean escala;
	static ImageView imageView;
	private static Integer total, id_encuesta;
	static JSONArray opciones;
	PreguntasConnect dbConnect = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ConnectivityManager conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo wifi = conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		if(wifi.isConnected()) {
			loadActivity();
		} else {
			dialog("Debe estar conectado a Wifi para cargar el formulario.");
		}
		
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
		
		if(wifi.isConnected()) {
			LinearLayout rl = (LinearLayout) findViewById(1001);
			if(rl != null) {
				loadActivity();
			} else {
				dialog("Debe estar conectado a Wifi para cargar el formulario.");
			}
		}
	}
	
	private void dialog(String message) {
		
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
				ConnectivityManager conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
				NetworkInfo wifi = conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				
				if(wifi.isConnected()) {
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
			JSONObject jsonData = task.execute(url).get();
			
			ScrollView scrollView;
			LinearLayout relativeLayout;
			
			//Variables para el manejo de distancias entre objetos
			int row; // Indica el objeto que creamos
			int numeroPreguntas; // Escala para los radio button
			
			JSONObject encuesta = jsonData.getJSONObject("encuesta");
			id_encuesta = encuesta.getInt("id");
			url_logo_encuesta = encuesta.getString("logo_url");
			
			JSONArray preguntas = encuesta.getJSONArray("preguntas");
			
			numeroPreguntas = preguntas.length();
			
			labels = new TextView[numeroPreguntas];  
			editText = new EditText[numeroPreguntas];  
			
			scrollView = new ScrollView(this);
			relativeLayout = new LinearLayout(this);
			relativeLayout.setId(1001);
			
			//Logo de la aplicacion
			if(!url_logo_encuesta.equals("")) {
				
				imageView = new ImageView(this);
				LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 
						LinearLayout.LayoutParams.WRAP_CONTENT);
				imageView.setLayoutParams(imgParams);
				
				GetPngImageTask imageTask = new GetPngImageTask();
				imageTask.execute(url_logo_encuesta);
				
				relativeLayout.addView(imageView);
			}
			
			for(row = 0; row < numeroPreguntas; row++) {
				
				JSONObject c = preguntas.getJSONObject(row);
				
				Integer id = c.getInt("id_pregunta");
				tipo = c.getString("tipo");
				
				if(tipo.toString().equals("radio")) {
					escala = c.getBoolean("escala");
					orientation = c.getString("orientation");
					if(escala) {
						before_label = c.getString("before_label");
						after_label = c.getString("after_label");
						total = c.getInt("total");
						radioButton = new RadioButton[total];
					} else {
						opciones = c.getJSONArray("options");
						radioButton = new RadioButton[opciones.length()];
					}
				}
				
				label = c.getString("label");
				
				if(!tipo.equals("radio")) {
					
					LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
					
					labels[row] = new TextView(this);
					labels[row].setText(label);
					labelParams.setMargins(50, 10, 50, 0);
					labels[row].setTextSize(22);
					labels[row].setLayoutParams(labelParams);
					relativeLayout.addView(labels[row]);
					
					LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
					inputParams.setMargins(50, 20, 50, 0);
					editText[row] = new EditText(this);
					editText[row].setHint("Inserte su respuesta aqui...");
					editText[row].setId(id);
					editText[row].setLayoutParams(inputParams);
					relativeLayout.addView(editText[row]);
					
				} else {
					
					LinearLayout.LayoutParams labelRadioParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
					
					labels[row] = new TextView(this);
					labels[row].setText(label);
					labelRadioParams.setMargins(50, 10, 50, 0);
					labels[row].setTextSize(22);
					labels[row].setLayoutParams(labelRadioParams);
					relativeLayout.addView(labels[row]);
					
					LinearLayout.LayoutParams leftLabelRadioParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					
					if(escala) {
						//Before Label
						labels[row] = new TextView(this);
						labels[row].setText("Debe seleccionar entre " + before_label + " y " + after_label + ".");
						labels[row].setTextSize(18);
						labels[row].setTextColor(Color.GRAY);
						leftLabelRadioParams.setMargins(50, 15, 50, 0);
						labels[row].setLayoutParams(leftLabelRadioParams);
						relativeLayout.addView(labels[row]);
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
					relativeLayout.addView(radioGroup);
					
					if(escala) {
						for (int r = 0; r < total; r++) {
							radioButton[r] = new RadioButton(this);
							radioButton[r].setText("" + r);
							radioGroup.addView(radioButton[r]);
						}
					} else {
						for (int r = 0; r < opciones.length() ; r++) {
							JSONObject option = opciones.getJSONObject(r);
							radioButton[r] = new RadioButton(this);
							radioButton[r].setText(option.getString("texto"));
							radioGroup.addView(radioButton[r]);
						}
					}
				}
			}
			
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
			relativeLayout.addView(button);
			
			//Params para el layout Relative
			relativeLayout.setOrientation(LinearLayout.VERTICAL);
			scrollView.addView(relativeLayout);
			
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
			JSONObject jObj = jparser.getJSONFromUrl(urls[0]);
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
			
			String urlPostData = "http://moveapps.cl/json.php";
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
	
	@Override
	protected void onStop() {
		super.onStop();
		//System.out.println("STOP");
	}
	
	@Override
	public void onClick(View view) {
		
		JSONObject jPost = new JSONObject();
		JSONArray jArray = new JSONArray();
		
		Boolean error = false;
		
		String encuestado = UUID.randomUUID().toString();
		
		ArrayList<EditText> editTextList = new ArrayList<EditText>();
		ArrayList<RadioGroup> radioGroupList = new ArrayList<RadioGroup>();
		relativeLayout = (LinearLayout) findViewById(1001);
				
		for(int i = 0; i < relativeLayout.getChildCount(); i++) {
			
			if(relativeLayout.getChildAt(i) instanceof EditText) {
				editTextList.add((EditText) relativeLayout.getChildAt(i) );
			}
			
			if(relativeLayout.getChildAt(i) instanceof RadioGroup) {
				radioGroupList.add((RadioGroup) relativeLayout.getChildAt(i));
			}
		}
		
		//Recorremos el arrayList para buscar errores.
		for(int e = 0; e < editTextList.size(); e++) {
			EditText texto = (EditText) findViewById(editTextList.get(e).getId());
			
			String respuesta = (String) texto.getText().toString();
			
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
			dbConnect = new PreguntasConnect(this);
			
			for (int i = 0; i < editTextList.size(); i++) {
				
				EditText texto = (EditText) findViewById(editTextList.get(i).getId());
				String respuesta = (String) texto.getText().toString();
				
				dbConnect.addPregunta(encuestado, id_encuesta, texto.getId(), respuesta);
				
				JSONObject reqObj = new JSONObject();
				
				try {
					reqObj.put("encuestado", encuestado);
					reqObj.put("id_encuesta", id_encuesta);
					reqObj.put("id_respuesta", texto.getId());
					reqObj.put("valor_respuesta", respuesta);
					jArray.put(reqObj);
					jPost.put("preguntas", jArray);
					
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
						
						dbConnect.addPregunta(encuestado, id_encuesta, radioButtonGroup.getId(), String.valueOf(ra));
						
						JSONObject reqObj = new JSONObject();
						
						try {
							reqObj.put("encuestado", encuestado);
							reqObj.put("id_encuesta", id_encuesta);
							reqObj.put("id_respuesta", radioButtonGroup.getId());
							reqObj.put("valor_respuesta", String.valueOf(ra));
							jArray.put(reqObj);
							jPost.put("preguntas", jArray);
							
						} catch (JSONException e2) {
							e2.printStackTrace();
						}
						radioButtonGroup.clearCheck();
					}
				}
			}
		}
		
		
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		if(wifi.isConnected() && !error) {
			JsonPostObjectTask task = new JsonPostObjectTask();
			task.execute(jPost);
			
			Toast mToast = Toast.makeText(this, "Formulario guardado correctamente.", Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,0, 0);
			mToast.show();
		} else if(!wifi.isConnected()){
			Toast mToast = Toast.makeText(this, "Sin conexion wifi", Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,0, 0);
			mToast.show();
		}
	}
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.form_main, menu);
		return true;
	}
	*/
}
