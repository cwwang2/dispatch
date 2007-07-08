package net.databinder.auth.components;

import net.databinder.auth.IAuthSession;
import net.databinder.auth.IAuthSettings;
import net.databinder.auth.data.IUser;
import net.databinder.components.DataStyleLink;
import net.databinder.components.SourceList;
import net.databinder.models.HibernateObjectModel;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;

/**
 * Serves as both a sign in and simple registration page. 
 */
public class DataSignInPage extends WebPage {
	private SourceList sourceList;
	
	private Component profileSocket, signinSocket;
	
	private SourceList.SourceLink profileLink, signinLink;

	/**
	 * Displays sign in page.
	 */
	public DataSignInPage(PageParameters params) {
		String username = params.getString("username");
		String token = params.getString("token");
		if (username != null && token != null) {
			IAuthSettings settings = ((IAuthSettings)Application.get());
			HibernateObjectModel userModel = new HibernateObjectModel(settings.getUserClass(),
					settings.getUserCriteriaBuilder(username));  
			IUser.CookieAuth user = (IUser.CookieAuth) userModel.getObject();
			if (user != null && user.getToken().equals(token))
				getAuthSession().signIn(user, true);
			setResponsePage(((Application)settings).getHomePage());
			setRedirect(true);
			return;
		}
		add(new DataStyleLink("dataStylesheet"));
		
		sourceList = new SourceList();
		
		add(profileSocket = profileSocket("profileSocket"));
		add(new WebMarkupContainer("profileLinkWrapper") {
			public boolean isVisible() {
				return profileLink.isEnabled();
			}
		}.add(profileLink = sourceList.new SourceLink("profileLink", profileSocket)));
		
		add(signinSocket = signinSocket("signinSocket"));
		add(new WebMarkupContainer("signinLinkWrapper") {
			@Override
			public boolean isVisible() {
				return signinLink.isEnabled();
			}
		}.add(signinLink = sourceList.new SourceLink("signinLink", signinSocket)));
		signinLink.onClick();	// show sign in first
	}
	
	protected Component signinSocket(String id) {
		return new DataSignInPanel(id);
	}
	
	protected Component profileSocket(String id) {
		return new DataProfilePanel(id);
	}
	
	protected static  IAuthSession getAuthSession() {
		return (IAuthSession) Session.get();
	}
}
