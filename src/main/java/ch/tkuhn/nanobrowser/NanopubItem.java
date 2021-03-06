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
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class NanopubItem extends ThingItem {
	
	private static final long serialVersionUID = -5109507637942030910L;

	public NanopubItem(String id, NanopubElement n, int guiItemStyle) {
		super(id);
		
		PageParameters params = new PageParameters();
		params.add("uri", n.getURI());
		BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("nanopublink", NanopubPage.class, params);
		add(link);
		WebMarkupContainer icon = new WebMarkupContainer("icon");
		if (n.isValid()) {
			icon.add(new AttributeModifier("src", new Model<String>("icons/nanopubv.svg")));
		}
		link.add(icon);

		String date = n.getCreateDateString();
		Label dateLabel;
		if (date == null) {
			dateLabel = new Label("nanopubdate", "");
		} else {
			dateLabel = new Label("nanopubdate", n.getCreateDateString());
		}
		dateLabel.setVisible(guiItemStyle == ThingElement.LONG_GUI_ITEM);
		add(dateLabel);
		Label nameLabel = new Label("nanopubname", " " + n.getShortName() + " ");
		nameLabel.setVisible(guiItemStyle != ThingElement.TINY_GUI_ITEM);
		link.add(nameLabel);
	}

	public NanopubItem(String id, NanopubElement n) {
		this(id, n, ThingElement.MEDIUM_GUI_ITEM);
	}

}
