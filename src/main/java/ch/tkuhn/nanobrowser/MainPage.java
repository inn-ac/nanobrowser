package ch.tkuhn.nanobrowser;

import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.openrdf.query.BindingSet;

public class MainPage extends WebPage {

	private static final long serialVersionUID = 6634220350799250923L;

	public MainPage(final PageParameters parameters) {
		
		String nanopubsQuery = "select distinct ?p where {?p a <http://www.nanopub.org/nschema#Nanopublication>}";
		List<BindingSet> nanopubs = TripleStoreAccess.getTuples(nanopubsQuery);
		
		ListView<BindingSet> nanopubList = new ListView<BindingSet>("nanopubs", nanopubs) {
			
			private static final long serialVersionUID = 1587686459411075758L;
			
			protected void populateItem(ListItem<BindingSet> item) {
				BindingSet b = item.getModelObject();
				String uri = b.getValue("p").stringValue();
				String s = Utils.getLastPartOfURI(uri);
				PageParameters params = new PageParameters();
				params.add("uri", uri);
				BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("nanopublink", PubPage.class, params);
				item.add(link);
				link.add(new Label("nanopubname", s));
			}
			
		};
		add(nanopubList);
		
		String claimQuery = "select distinct ?c where {?s <http://krauthammerlab.med.yale.edu/nanopub/extensions/asSentence> ?c}";
		List<BindingSet> claims = TripleStoreAccess.getTuples(claimQuery);
		
		ListView<BindingSet> claimList = new ListView<BindingSet>("claims", claims) {
			
			private static final long serialVersionUID = 3911519757128281636L;

			protected void populateItem(ListItem<BindingSet> item) {
				BindingSet b = item.getModelObject();
				String uri = b.getValue("c").stringValue();
				String s = Utils.getSentenceFromURI(uri);
				PageParameters params = new PageParameters();
				params.add("uri", uri);
				BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("claimlink", ClaimPage.class, params);
				item.add(link);
				link.add(new Label("claim", s));
			}
			
		};
		add(claimList);
		
	}

}