package com.rau.evoting.beans;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.mail.MessagingException;

import com.rau.evoting.data.SqlDataProvider;
import com.rau.evoting.models.*;
import com.rau.evoting.utils.MailService;
import com.rau.evoting.utils.Util;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.Group;
import com.restfb.types.User;

public class OpenElection {
	private Election election;
	private ArrayList<Answer> answers;
	private ArrayList<Trustee> trustees;
	private String trusteeName;
	private String trusteeEmail;
	private String answer;
	private int maxId;
	private boolean disabled;
	private boolean showRemove;
	private boolean canOpen;
	private String openningMessage;
	private String selectedGroup;
	private String selectedVoteMode;
	private List<Group> groups;
	
	private String accessToken;
	
	public OpenElection() {
		trustees = new ArrayList<Trustee>();
		disabled = false;
		selectedVoteMode = "all";
		accessToken = (String)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("accessToken");
	}
	
	public Election getElection() {
		return election;
	}

	public void setElection(Election election) {
		this.election = election;
	}
	
	public ArrayList<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(ArrayList<Answer> answers) {
		this.answers = answers;
	}
	
	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	public ArrayList<Trustee> getTrustees() {
		trustees = SqlDataProvider.getInstance().getElectionTrustees(election.getId()); //fix
		return trustees;
	}

	public void setTrustees(ArrayList<Trustee> trustees) {
		this.trustees = trustees; 
	}

	public String getTrusteeName() {
		return trusteeName;
	}

	public void setTrusteeName(String trusteeName) {
		this.trusteeName = trusteeName;
	}

	public String getTrusteeEmail() {
		return trusteeEmail;
	}

	public void setTrusteeEmail(String trusteeEmail) {
		this.trusteeEmail = trusteeEmail;
	}

	public int getMaxId() {
		maxId = answers.size()+1;
		return maxId;
	}

	public void setMaxId(int maxId) {
		this.maxId = maxId;
	}
	
	public String getSelectedGroup() {
		return selectedGroup;
	}

	public void setSelectedGroup(String selectedGroup) {
		this.selectedGroup = selectedGroup;
	}

	public String getSelectedVoteMode() {
		return selectedVoteMode;
	}

	public void setSelectedVoteMode(String selectedVoteMode) {
		this.selectedVoteMode = selectedVoteMode;
	}

	public List<Group> getGroups() {
		FacebookClient fbClient = new DefaultFacebookClient(accessToken);
		Connection<Group> gr = fbClient.fetchConnection("me/groups", Group.class);
		groups = gr.getData();
		return groups;
	}

	public void setGroups(ArrayList<Group> groups) {
		this.groups = groups;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	public boolean isShowRemove() {
		return answers.size() > 0 ;
	}

	public void setShowRemove(boolean showRemove) {
		this.showRemove = showRemove;
	}

	
	public boolean isCanOpen() {
		boolean allGenerated = true;
		for(Trustee tr: trustees){
			allGenerated &= tr.isGenerated();
			if(!allGenerated)
				break;
		}
		canOpen = allGenerated & (answers.size() > 0) ;
		return canOpen;
	}

	public void setCanOpen(boolean canOpen) {
		this.canOpen = canOpen;
	}

	public String getOpenningMessage() {
		boolean allGenerated = true;
		for(Trustee tr: trustees){
			allGenerated &= tr.isGenerated();
			if(!allGenerated)
				break;
		}
		if(answers.size() == 0) {
			openningMessage = "You have no answers ";
			if(!allGenerated) {
				openningMessage += "and not all trustees generate their keys.";
			}
		}
		else if(!allGenerated) {
			openningMessage = "Not all trustees generated their keys.";
		}
		return openningMessage;
	}

	public void setOpenningMessage(String openningMessage) {
		this.openningMessage = openningMessage;
	}

	public String navigateAnswers() {
		answers = SqlDataProvider.getInstance().getElectionAnswers(election.getId());
		return "Answers";
	}
	
	public String navigateTrustees() {
		return "Trustees";
	}
		
	public String createElection(String name, String description) {
		FacebookClient fbClient = new DefaultFacebookClient(accessToken);
		User user = fbClient.fetchObject("me", User.class); 
		String userId = user.getId();
		int elId = SqlDataProvider.getInstance().insertElecttion(new Election(0, name, description),userId);
		election = new Election(elId, name, description);
		return "next";
	}
	
	public String addAnswer() {
		answers.add(new Answer(maxId, answer));
		answer = "";
		return "";
	}
	
	public String removeAnswer() {
		if (answers.size() > 0) {
			answers.remove(answers.size() - 1);
		}
		answer = "";
		return "";
	}
	
	public String cancelAnswers() {
		answer = "";
		return "OpenElection";
	}
	
	public String addAnswers() {
		if(!answer.equals("")) {
			answers.add(new Answer(maxId, answer));
			answer = "";
		}
		SqlDataProvider.getInstance().insertAnswers(election.getId(), answers);
		return "OpenElection";
	}
	
	public String addTrustee() {
		String message = "Hello, you are chosen trustee for  " + election.getName() + " election\n Please, generate your key: \n";
		String token = Util.generateRandomToken();
		int trId = SqlDataProvider.getInstance().insertTrustee(election.getId(), new Trustee(null, trusteeEmail, false,token));
		String url = "http://localhost:8080/Evoting/TrusteeHome.xhtml?trId=" + trId + "&token=" + token;
		String encodedUrl = url;
		try {
			encodedUrl = URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		message += url;  // or encoded
		try {
			MailService.sendMessage(trusteeEmail, "Trustee for " + election.getName() +" evoting", message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}		
		trusteeName = "";
		trusteeEmail = "";
		
		return "Trustees";
	}
	
	public String setElection(int id) {
		election = SqlDataProvider.getInstance().getElection(id);
		answers = SqlDataProvider.getInstance().getElectionAnswers(election.getId());
		return "OpenElection";
	}
	
	public String open() {
		SqlDataProvider.getInstance().openElection(election.getId());
		return "Elections";
	}
	
	public void ajaxListener(AjaxBehaviorEvent event) {
		System.out.println("event: " +  event.getSource().toString());
		disabled = !disabled; 
	}

	public String fromVoters() {
		if(selectedVoteMode == "all") {
		}
		else {
		}
		return "OpenElection";
	}
	
}
