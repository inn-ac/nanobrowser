package ch.tkuhn.nanobrowser;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class PersonPage extends WebPage {

	private static final long serialVersionUID = -4673886567380719848L;

	public PersonPage(final PageParameters parameters) {
		
		Person p = new Person(parameters.get("uri").toString());
		
		add(new Label("title", p.getName()));

		add(new ExternalLink("uri", p.getURI(), p.getURI()));
		
		add(new ListView<Nanopub>("nanopubs", p.getAuthoredNanopubs()) {
			
			private static final long serialVersionUID = 3911519757128281636L;

			protected void populateItem(ListItem<Nanopub> item) {
				item.add(new NanopubItem("nanopub", item.getModelObject()));
			}
			
		});
		
	}

}
