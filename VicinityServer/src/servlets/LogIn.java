package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import db.DBManager;
import model.Place;
import model.SMS;

/**
 * Servlet implementation class LogIn
 */
@WebServlet("/LogIn")
public class LogIn extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LogIn() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getParameter("id");
		
		DBManager dbManager = DBManager.getInstance();
		String id = dbManager.getUser(userId);
		
		if(id == null || id.equals("null")){
			dbManager.addUser(userId);
			response.setStatus(404);
		} else {
			boolean isBusiness = false;
			Place place = dbManager.getPlaceBuUser(userId);
			
			if(place != null && place.isConfirmed()){
				isBusiness = true;
			}			
			JsonObject dataset = new JsonObject();

	        dataset.addProperty("is_business", isBusiness);
	        
	        response.getWriter().append(dataset.toString());
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
		
		String phone = jObject.get("place_phone_loc").getAsString();
		String code = this.getRndString(6);
		
		Place place = new Place(
				jObject.get("user_id_goog").getAsString(),
				jObject.get("place_id_goog").getAsString(),
				phone,
				code,
				false);
		
		DBManager dbManager = DBManager.getInstance();
		dbManager.addPlace(place);
		dbManager.addSMS(new SMS(phone, code));
	}
	
	private String getRndString(int length){
		StringBuilder builder = new StringBuilder();
		Random rnd = new Random();
			
		for(int i = 0; i < length; i++){
			if(rnd.nextBoolean()){
				char c = (char) ((rnd.nextInt(91 -65) + 65));
				
				if(rnd.nextBoolean()){
					c = Character.toLowerCase(c);
				}
				
				builder.append(c);
			} else {
				builder.append(rnd.nextInt(10));
			}
		}
		
		return builder.toString();
	}

}
