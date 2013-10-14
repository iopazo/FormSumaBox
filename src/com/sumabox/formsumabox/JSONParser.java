package com.sumabox.formsumabox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.NetworkOnMainThreadException;
import android.util.Log;

public class JSONParser {
	
	static InputStream is = null;
	static JSONObject jsonObject = null;
	static String json = "";


	public JSONParser() {
		
	}
	
	public JSONObject getJSONFromUrl(String url) throws JSONException{
		
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-type", "application/json");
			
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			is = entity.getContent();
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NetworkOnMainThreadException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
			
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + '\n');
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}
		
		try {
			jsonObject = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}
		
		return jsonObject;
	}
	
	public Boolean postJsonFromUrl(JSONObject json, String url) throws JSONException {

		Boolean exito = false;
		HttpClient httpClient = new DefaultHttpClient();
		//InputStream is = null;
		
		try {
			HttpPost post = new HttpPost(url);
			List<NameValuePair> nvp = new ArrayList<NameValuePair>(2);
			nvp.add(new BasicNameValuePair("json", json.toString()));
			post.setEntity(new UrlEncodedFormEntity(nvp));
			HttpResponse response = httpClient.execute(post);
			
			if(response != null) {
				is = response.getEntity().getContent();
				exito = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return exito; 
	}
	
}
