package me.sweetll.pm25demo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

public class HttpClient implements Runnable {

	private String urlPath;
	private String result;
	
	public HttpClient(String urlPath) {
		this.urlPath = urlPath;
		result = "";
	}
	
	public String getResult() {
		return result;
	}
	
	public void getCrawlResult() {
		URL url = null;
		PrintWriter out = null;
		try {
			Log.e("url", urlPath);
			url = new URL(urlPath);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("GET");  
			httpConn.connect();
			/*httpConn.setDoOutput(true);  
			httpConn.setDoInput(true);  
			httpConn.setRequestProperty("Content-Type", "application/json");			
			httpConn.connect();
			
			out = new PrintWriter(httpConn.getOutputStream());  
            // �����������  
            out.print(param);  
            // flush������Ļ���  
            out.flush(); */ 
			Log.e("responsecode",httpConn.getResponseCode()+"");
            if (httpConn.getResponseCode() == 200) {
				InputStreamReader input = new InputStreamReader(httpConn.getInputStream(), "utf-8");
				BufferedReader bufReader = new BufferedReader(input);
				String line = "";
				StringBuilder contentBuf = new StringBuilder();
				while ((line = bufReader.readLine()) != null) {
					contentBuf.append(line);
					contentBuf.append("\n");
				}
				bufReader.close();
				httpConn.disconnect(); 
				
				String buf = contentBuf.toString();
				Log.e("result", buf);
				result =  buf;
            }
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out!=null) {
				out.close();
			}
		}
	}
	
	public static String getCrawlResult(String urlPath) {
		//System.out.println(urlPath);
		Log.e("url", urlPath);
		URL url = null;
		PrintWriter out = null;
		try {
			url = new URL(urlPath);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("GET");  
			httpConn.connect();
			/*httpConn.setDoOutput(true);  
			httpConn.setDoInput(true);  
			httpConn.setRequestProperty("Content-Type", "application/json");			
			httpConn.connect();
			
			out = new PrintWriter(httpConn.getOutputStream());  
            // �����������  
            out.print(param);  
            // flush������Ļ���  
            out.flush(); */ 
            if (httpConn.getResponseCode() == 200) {
				InputStreamReader input = new InputStreamReader(httpConn.getInputStream(), "utf-8");
				BufferedReader bufReader = new BufferedReader(input);
				String line = "";
				StringBuilder contentBuf = new StringBuilder();
				while ((line = bufReader.readLine()) != null) {
					contentBuf.append(line);
					contentBuf.append("\n");
				}
				bufReader.close();
				httpConn.disconnect(); 
				
				String buf = contentBuf.toString();
				
				return buf;
            }
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out!=null) {
				out.close();
			}
		}
		return null;
	}

	@Override
	public void run() {
		Log.e("thread", "thread start");
		getCrawlResult();
		Log.e("thread", "thread end");
	}
	
}

