package rpc;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.*;

public class RpcHelper {
    public static boolean verifySession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			JSONObject obj = new JSONObject();
			try {
				obj.put("status", "Login Required");
				RpcHelper.writeJSONObject(response, obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return false;
		}
		return true;
    }
	
	public static void writeJSONArray(HttpServletResponse response, JSONArray array) 
			throws IOException {
		response.setContentType("application/json");
		response.getWriter().print(array);	
	}
	
	public static void writeJSONObject(HttpServletResponse response, JSONObject obj) 
			throws IOException {
		response.setContentType("application/json");
		response.getWriter().print(obj);	
	}
	
	// Parses a JSONObject from http request.
	public static JSONObject readJSONObject(HttpServletRequest request) {
		StringBuilder builder = new StringBuilder();
		try (BufferedReader reader = request.getReader()) {
			String line = null;
			while((line = reader.readLine()) != null) {
				builder.append(line);
			}
			return new JSONObject(builder.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONObject();
	}


}
