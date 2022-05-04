package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection dbConnection = DBConnectionFactory.getConnection();
		try {
			// Input parameter true: create a new session if none exists.
			// 				   false: do not create a new session.
			HttpSession session = request.getSession(false);  
			JSONObject obj = new JSONObject();
			if (session != null) {  // Session exists.
				String userId = session.getAttribute("user_id").toString();
				obj.put("status", "OK").put("user_id", userId).put("name", dbConnection.getFullname(userId));
			} else {
				obj.put("status", "Invalid Session");
				response.setStatus(403);  // 403: forbidden, server refuses the request
			}
			RpcHelper.writeJSONObject(response, obj);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnection.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection dbConnection = DBConnectionFactory.getConnection();
		try {
			JSONObject input = RpcHelper.readJSONObject(request);
			String userId = input.getString("user_id");
			String password = input.getString("password");

			JSONObject obj = new JSONObject();
			if (dbConnection.verifyLogin(userId, password)) {
				// Create the new HttpSession object and also add a Cookie to the response object with name JSESSIONID and value as session id. 
				// This cookie is used to identify the HttpSession object in further requests from client.
				HttpSession session = request.getSession();  // No input parameter: create a new session if none exists.
				session.setAttribute("user_id", userId);
				//Specify the time, in seconds, between client requests before the servlet container will invalidate this session.
				session.setMaxInactiveInterval(600);
				obj.put("status", "OK").put("user_id", userId).put("name", dbConnection.getFullname(userId));
			} else {
				obj.put("status", "User Doesn't Exist");
				response.setStatus(401);  // 401: unauthorized
			}
			RpcHelper.writeJSONObject(response, obj);		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnection.close();
		}
	}

}
