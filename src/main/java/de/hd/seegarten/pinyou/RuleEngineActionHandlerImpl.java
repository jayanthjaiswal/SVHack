/**
 * (c) 2014 SAP AG or an SAP affiliate company.  All rights reserved.
 *
 * No part of this publication may be reproduced or transmitted in any form or for any purpose
 * without the express permission of SAP AG.  The information contained herein may be changed
 * without prior notice.
 */

package de.hd.seegarten.pinyou;

import org.codehaus.jettison.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public class RuleEngineActionHandlerImpl {
	private static final Logger logger = LoggerFactory.getLogger(RuleEngineActionHandlerImpl.class);
	private KeywordExtraction keywordExtractor;

	public RuleEngineActionHandlerImpl(KeywordExtraction extractor) {
		this.keywordExtractor = extractor;

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response handleAction(@QueryParam("url") String url) throws IOException, JSONException {
		List<String> response = keywordExtractor.fetchKeywords(url);

//		List<String> str = new ArrayList<String>();
//		str.add("25");
//		str.add("forrest");
//		str.add("gump");
//		str.add("quotes");
		List<String> subList= response.subList(0, Math.min(response.size()/2,5) );
		String outStr = PinterestLogic.convertToJSON(PinterestLogic.getListPins(subList));
		return Response.status(200).type(APPLICATION_JSON).entity(outStr).build();
	}

}
