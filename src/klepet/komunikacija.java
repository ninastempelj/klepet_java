package klepet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;

public class komunikacija {

	public static void main(String[] args) {
		String ime = "Nina";
		//logirajSe(ime);
		System.out.println(sporocila(ime));
		poslji(ime, "Oj!");
		//odjaviSe(ime);
		System.out.println(vpisaniUporabniki());
	}


	
	public static String vpisaniUporabniki (){
		try {
            String uporabniki = Request.Get("http://chitchat.andrej.com/users")
                                  .execute()
                                  .returnContent().asString();
            return uporabniki;
        } catch (IOException e) {
        		e.printStackTrace();
        		return new String();
        }		
	}
	
	public static void logirajSe(String ime) throws ClientProtocolException, IOException{
	  try{
		  URI uri = new URIBuilder("http://chitchat.andrej.com/users")
	          .addParameter("username", ime)
	          .build();
		  String responseBody = Request.Post(uri)
                  .execute()
                  .returnContent()
                  .asString();
		  System.out.println(responseBody);
	  } catch (URISyntaxException e) {
		e.printStackTrace();
	  } 
	}
	
	public static void odjaviSe(String ime){
	URI uri;
	try {
		uri = new URIBuilder("http://chitchat.andrej.com/users")
		          .addParameter("username", ime)
		          .build();
		 String responseBody = Request.Delete(uri)
			         .execute()
			         .returnContent()
			         .asString();
			 System.out.println(responseBody);
		
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
		e.printStackTrace();
		}
	}
	
	public static String sporocila(String ime){
		URI uri;
		try {
			uri = new URIBuilder("http://chitchat.andrej.com/messages")
			          .addParameter("username", ime)
			          .build();
		  String responseBody;
			responseBody = Request.Get(uri)
			                               .execute()
			                               .returnContent()
			                               .asString();
			  return(responseBody);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new String();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new String();
		}		
	}
	
	public static void poslji(String ime, String tekst){
			URI uri;
			try {
				uri = new URIBuilder("http://chitchat.andrej.com/messages")
				  .addParameter("username", ime)
				  .build();
				  String message = "{ \"global\" : true, \"text\" :  \""+ tekst + "\"}";

				  String responseBody = Request.Post(uri)
					          .bodyString(message, ContentType.APPLICATION_JSON)
					          .execute()
					          .returnContent()
					          .asString();
			
				  System.out.println(responseBody);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



	}
}
	

