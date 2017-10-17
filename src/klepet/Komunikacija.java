package klepet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Komunikacija {
	
	static String time = Long.toString(new Date().getTime());

	
	public static List<Uporabnik> vpisaniUporabniki (){
		try {
            String suroviUporabniki = Request.Get("http://chitchat.andrej.com/users")
                                  .execute()
                                  .returnContent().asString();
            
			ObjectMapper map = new ObjectMapper();
			List <Uporabnik> prijavljeniUporabniki = map.readValue(suroviUporabniki, 
					new TypeReference<List<Uporabnik>>(){});
			return prijavljeniUporabniki;
            
        } catch (IOException e) {
        		e.printStackTrace();
        		return new ArrayList<Uporabnik>();
        }		
	}
	
	public static void logirajSe(String ime) throws ClientProtocolException, IOException{
	  try{
		  URI uri = new URIBuilder("http://chitchat.andrej.com/users")
	          .addParameter("username", ime)
			  .addParameter("stop_cache", time)
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
				  .addParameter("stop_cache", time)
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
	
	public static List<Sporocilo> novaSporocila(String ime){
		URI uri;
		try {
			uri = new URIBuilder("http://chitchat.andrej.com/messages")
			          .addParameter("username", ime)
					  .addParameter("stop_cache", time)
			          .build();
		  String responseBody;
			responseBody = Request.Get(uri)
			                               .execute()
			                               .returnContent()
			                               .asString();
						
			ObjectMapper map = new ObjectMapper();
			List <Sporocilo> seznamSporocil = map.readValue(responseBody, 
					new TypeReference<List<Sporocilo>>(){});
			
			return seznamSporocil;
			  
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<Sporocilo>() ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<Sporocilo>();
		}		
	}
	
	public static void posljiSporocilo(Sporocilo sporocilo){
			URI uri;
			try {
				uri = new URIBuilder("http://chitchat.andrej.com/messages")
				  .addParameter("username", sporocilo.getSender())
				  .addParameter("stop_cache", time)
				  .build();
				String message = "";
				if (sporocilo.getGlobal()) {
				    message = "{ \"global\" : " + true + ", \"text\" :  \"" + sporocilo.getText() + "\"}";
				}else {
					message = "{ \"global\" : " + false + ", \"recipient\" :  \"" + sporocilo.getRecipient() + "\", \"text\" :  \""+ sporocilo.getText() + "\"}";
				}
				  System.out.println(message);
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
	

