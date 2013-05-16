// This file is part of Nanobrowser.
// Copyright 2012, Tobias Kuhn, http://www.tkuhn.ch
//
// Nanobrowser is free software: you can redistribute it and/or modify it under the terms of the
// GNU Lesser General Public License as published by the Free Software Foundation, either version
// 3 of the License, or (at your option) any later version.
//
// Nanobrowser is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
// even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License along with Nanobrowser.
// If not, see http://www.gnu.org/licenses/.

package ch.tkuhn.nanobrowser;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.openrdf.rio.RDFFormat;

public class NanobrowserApplication extends WebApplication {
	
	private static Properties properties = new Properties();
	
	static {
		try {
			InputStream in = NanobrowserApplication.class.getResourceAsStream("/nanobrowser.properties");
			properties.load(in);
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private AgentElement user;

	public NanobrowserApplication() {
		String baseUri = getProperty("nanopub-server-baseuri");
		user = new AgentElement(baseUri + "user/anonymous");
	}
	
	public static String getProperty(String propertyName) {
		return properties.getProperty(propertyName);
	}
	
	public AgentElement getUser() {
		return user;
	}
	
	public void setUser(AgentElement user) {
		this.user = user;
	}
	
	protected void internalInit() {
		super.internalInit();
		mountPage("/nanopub", NanopubPage.class);
		mountPage("/sentence", SentencePage.class);
		mountPage("/agent", AgentPage.class);
		mountPage("/thing", ThingPage.class);
		mountPage("/search", SearchPage.class);
		getSharedResources().add("/trig", new RawNanopubPage(RDFFormat.TRIG));
	    mountResource("/trig", new SharedResourceReference("/trig"));
		getSharedResources().add("/xml", new RawNanopubPage(RDFFormat.TRIX));
	    mountResource("/xml", new SharedResourceReference("/xml"));
		getSharedResources().add("/nq", new RawNanopubPage(RDFFormat.NQUADS));
	    mountResource("/nq", new SharedResourceReference("/nq"));
	}

	public Class<? extends Page> getHomePage() {
		return MainPage.class;
	}
	
	public static String getTimestamp() {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date()).replaceFirst("(..)$", ":$1");
	}

}
