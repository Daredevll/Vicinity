package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import db.DBManager;
import model.SMS;

/**
 * Servlet implementation class SMSServlet
 */
@WebServlet("/SMSServlet")
public class SMSServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SMSServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		DBManager dbManager = DBManager.getInstance();
		SMS sms = dbManager.getSMS();
		
		JsonObject dataset = new JsonObject();

        dataset.addProperty("place_phone_loc", sms.getPhone());
        dataset.addProperty("generated_code", sms.getCode());
		
		response.getWriter().append(dataset.toString());
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
		
		DBManager dbManager = DBManager.getInstance();
		dbManager.updatePlace(jObject.get("user_id").getAsString(), jObject.get("generated_code").getAsString());
	}

}
