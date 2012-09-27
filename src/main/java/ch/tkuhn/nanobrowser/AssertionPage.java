package ch.tkuhn.nanobrowser;

import static ch.tkuhn.nanobrowser.NanopubAccess.getNanopubs;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class AssertionPage extends WebPage {

	private static final long serialVersionUID = -4673886567380719848L;

	public AssertionPage(final PageParameters parameters) {
		
		String uri = parameters.get("uri").toString();
		
		if (uri.startsWith("http://krauthammerlab.med.yale.edu/nanopub/claims/")) {
			add(new Label("title", Utils.getSentenceFromURI(uri)));
		} else {
			add(new Label("title", Utils.getGraphSummary(TripleStoreAccess.getGraph(uri))));
		}
		
		//add(new Label("uri", uri));
		
		add(new ListView<String>("nanopubs", getNanopubs(uri)) {
			
			private static final long serialVersionUID = 3911519757128281636L;

			protected void populateItem(ListItem<String> item) {
				String uri = item.getModelObject();
				String s = Utils.getLastPartOfURI(uri);
				PageParameters params = new PageParameters();
				params.add("uri", uri);
				BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("nanopublink", PubPage.class, params);
				item.add(link);
				link.add(new Label("nanopub", s));
			}
			
		});
		
	}

}