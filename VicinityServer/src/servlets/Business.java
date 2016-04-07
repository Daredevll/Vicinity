package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import db.DBManager;
import model.ProcessedOrder;
import model.WaitingOrder;

/**
 * Servlet implementation class Business
 */
@WebServlet("/Business")
public class Business extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Business() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String restaurantId = request.getParameter("id");
		DBManager dbManager = DBManager.getInstance();
		ArrayList<WaitingOrder> orders = dbManager.getWaitingOrder(restaurantId);
		
		JsonArray datasets = new JsonArray();        
        
        for(WaitingOrder order : orders){
        	JsonObject dataset = new JsonObject();

	        dataset.addProperty("customer_id", order.getCutumerId());
	        dataset.addProperty("restaurant_id", order.getRestaurantId());
	        dataset.addProperty("people_count", order.getPeopleCount());
	        dataset.addProperty("comment", order.getComment());
	        dataset.addProperty("time", order.getHour());
	        dataset.addProperty("date", order.getDate());
	        dataset.addProperty("restaurant_name", order.getRestaurantName());
	        
	        datasets.add(dataset);
        }

        response.getWriter().append(datasets.toString());
        
        if(orders.size() == 0){
        	response.setStatus(404);
        }else {
        	response.setStatus(200);
        }
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StringBuilder builder = new StringBuilder();
		
		BufferedReader reader = request.getReader();
		String line = reader.readLine();
		
		while (line != null) {
			builder.append(line);
			line = reader.readLine();
		}
		
		JsonParser parser = new JsonParser();
		JsonObject jObject = parser.parse(builder.toString()).getAsJsonObject();
		
		
		ProcessedOrder order = new ProcessedOrder(
				jObject.get("customer_id").getAsString(),
				jObject.get("restaurant_id").getAsString(),
				jObject.get("comment").getAsString(),
				jObject.get("people_count").getAsInt(),
				jObject.get("time").getAsString(),
				jObject.get("date").getAsString(),
				jObject.get("isConfirmed").getAsBoolean(),
				jObject.get("restaurant_name").getAsString());

		DBManager dbManager = DBManager.getInstance();
		
		dbManager.addProcessedOrder(order);
	}

}
