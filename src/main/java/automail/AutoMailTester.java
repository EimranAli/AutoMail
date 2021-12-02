package automail;

import java.util.Scanner;

import org.json.JSONObject;

public class AutoMailTester {
	public static void main(String[] args) {
		// key present in url while making call to lambda function using API-Gateway to extract parameters
		final String queryParamsKey = "q";

		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the receiver mail id");
		String receiverMailId = sc.next();

		System.out.println("Enter the Subject");
		String subject = sc.next();

		System.out.println("Enter the Message to Send");
		String message = sc.next();
		sc.close();

		JSONObject innerJson = new JSONObject();
		innerJson.append("receiver", receiverMailId);
		innerJson.append("subject", subject);
		innerJson.append("message", message);
		JSONObject json = new JSONObject();

		json.append(queryParamsKey, innerJson);

		/*
		 * json structure will be  :         { 
		 * 										"q" : {
		 * 												"receiver":receiverMailId, 
		 * 												"subject":subject, 
		 * 												"message":message
		 * 											  } 
		 * 									 }
		 * 
		 */

		AutoMail auto = new AutoMail();
		// call lambda function
		auto.handleRequest(json, null); 
	}
}
