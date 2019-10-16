package rpc;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.*;

import db.DBConnection;
import db.DBConnectionFactory;
import external.*;



/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
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
		
		DBConnection conn = DBConnectionFactory.getConnection();				
		Set<String> favoritedItemIds = new HashSet<>();
		HttpSession session = request.getSession(false);
		if (session != null) {
			String userId = session.getAttribute("user_id").toString();
			favoritedItemIds = conn.getFavoriteItemIds(userId);
		}
		
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		String keyword = request.getParameter("keyword");
		try {
			// Get TicketMaster data and save data to MySQL DB.
			List<Item> items = conn.searchItems(lat, lon, keyword);
			// Add the data to HTTP response in JSONArray format.
			JSONArray array = new JSONArray();
			for (Item item : items) {
				JSONObject obj = item.toJSONObject();
				obj.put("favorite", favoritedItemIds.contains(item.getItemId()));	// Mark if the searched item is favored by the user.
				array.put(obj);
			}
			RpcHelper.writeJSONArray(response, array);
		} catch (JSONException e) {
			e.printStackTrace();
		}		
	}
}

