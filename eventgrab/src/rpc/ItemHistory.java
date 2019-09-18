package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

import java.util.*;
import external.*;

/**
 * Servlet implementation class ItemHistory
 */
@WebServlet("/history")
public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ItemHistory() {
        super();
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		// Allow access only if session exists.
		if (!RpcHelper.verifySession(request, response)) {
			return;
		}
		
		DBConnection dbConnection = DBConnectionFactory.getConnection("mysql");
		try {
			String userId = request.getParameter("user_id");
			Set<Item> items = dbConnection.getFavoriteItems(userId);
			JSONArray array = new JSONArray();	
			for (Item item : items) {
				JSONObject obj = item.toJSONObject();
				obj.append("favorite", true);
				array.put(obj);
			}
			RpcHelper.writeJSONArray(response, array);
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			dbConnection.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Allow access only if session exists.
		if (!RpcHelper.verifySession(request, response)) {
			return;
		}
		
		DBConnection dbConnnection = DBConnectionFactory.getConnection("mysql");
		try {
			JSONObject input = RpcHelper.readJSONObject(request);
			String userId = input.getString("user_id");
			JSONArray array = input.getJSONArray("favorite");
			List<String> itemIds = new ArrayList<>();
			for(int i = 0; i < array.length(); ++i) {
				itemIds.add(array.getString(i));
			}
			dbConnnection.setFavoriteItems(userId, itemIds);
			RpcHelper.writeJSONObject(response, new JSONObject().put("result", "SUCCESS"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnnection.close();
		}	
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Allow access only if session exists.
		if (!RpcHelper.verifySession(request, response)) {
			return;
		}
		
		DBConnection dbConnnection = DBConnectionFactory.getConnection();
		try {
			JSONObject input = RpcHelper.readJSONObject(request);
			String userId = input.getString("user_id");
			JSONArray array = input.getJSONArray("favorite");
			List<String> itemIds = new ArrayList<>();
			for(int i = 0; i < array.length(); ++i) {
				itemIds.add(array.getString(i));
			}
			dbConnnection.unsetFavoriteItems(userId, itemIds);
			RpcHelper.writeJSONObject(response, new JSONObject().put("result", "SUCCESS"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnnection.close();
		}
	}
}
