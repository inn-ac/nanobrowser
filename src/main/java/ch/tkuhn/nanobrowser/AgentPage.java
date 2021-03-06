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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class AgentPage extends NanobrowserWebPage {

	private static final long serialVersionUID = -4673886567380719848L;

	private AgentElement agent;
	private ListModel<NanopubElement> nanopubModel = new ListModel<NanopubElement>();
	private ListModel<Opinion> opinionModel = new ListModel<Opinion>();
	
	public AgentPage(final PageParameters parameters) {
		
		agent = new AgentElement(parameters.get("uri").toString());
		boolean isBot = agent.isBot();
		
		update();
		
		add(new MenuBar("menubar"));
		
		WebMarkupContainer icon = new WebMarkupContainer("icon");
		if (isBot) {
			icon.add(new AttributeModifier("src", new Model<String>("icons/bot.svg")));
		}
		add(icon);
		
		add(new Label("title", agent.getName()));

		add(new ExternalLink("uri", agent.getURI(), agent.getTruncatedURI()));

		add(new HList("typelist", agent.getTypes(), "Types"));
		
		if (isBot) {
			add(new HList("commanderlist", agent.getCommanders(), "Commanders"));
		} else {
			add(new WebMarkupContainer("commanderlist"));
		}

		add(new VList("nanopublist", nanopubModel, "Nanopublications"));

		add(new Label("emptyopinions", opinionModel.getObject().isEmpty() ? "(nothing)" : ""));

		add(new ListView<Opinion>("opinions", opinionModel) {
			
			private static final long serialVersionUID = -4257147575068849793L;

			protected void populateItem(ListItem<Opinion> item) {
				item.add(new Label("opinion", Opinion.getVerbPhrase(item.getModelObject().getOpinionType(), true)));
				item.add(new SentenceItem("opinionsentence", item.getModelObject().getSentence()));
				item.add(new NanopubItem("opinionpub", item.getModelObject().getNanopub(), ThingElement.TINY_GUI_ITEM));
			}
			
		});

		WebMarkupContainer aa = new WebMarkupContainer("adminactions");
		if (NanobrowserApplication.isInDevelopmentMode()) {
			Link<Object> thatsmeButton;
			aa.add(thatsmeButton = new Link<Object>("thatsme") {
				
				private static final long serialVersionUID = 8608371149183694875L;

				public void onClick() {
					NanobrowserSession.get().setUser(agent);
					update();
					setResponsePage(AgentPage.class, getPageParameters());
				}
				
			});
			thatsmeButton.setVisible(!isBot);
		} else {
			aa.add(new AttributeModifier("class", new Model<String>("hidden")));
			aa.add(new Label("thatsme", ""));
		}
		add(aa);
		
	}
	
	private void update() {
		nanopubModel.setObject(agent.getAuthoredNanopubs(20));
		opinionModel.setObject(agent.getOpinions(true));
	}

}
