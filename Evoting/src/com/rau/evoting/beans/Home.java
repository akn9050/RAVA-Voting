package com.rau.evoting.beans;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import com.rau.evoting.data.SqlDataProvider;
import com.rau.evoting.models.User;

public class Home {
	private String appId = "515272745187738";
	private String appSecret = "e37f4bd94fc533c364ad291a2ecbba09";
	private String username;
	private String password;
	
	public Home(){
		
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String login(){
		return "login";
//		SqlDataProvider dataprovider = SqlDataProvider.getInstance(); 
//		User user = dataprovider.getUser(username);
//		if(user != null){
//			if(user.getPassword().equals(password)){;
//				return "create";
//			}
//		}
//		return "error";
	}
	
	public void fbLogin(ActionEvent event) {
		String fUrl = "https://www.facebook.com/dialog/oauth?"
			      + "client_id=" + appId + "&"
			      + "redirect_uri=" + "http://localhost:8080/Evoting/Elections.xhtml" +"&"  
			      + "scope=publish_stream,user_groups,status_update&"
			      + "response_type=code";
		
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		
		try {
			response.sendRedirect(fUrl);
			System.out.println(fUrl);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error");
		}
	}
}
