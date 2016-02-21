/**
 * (c) 2014 SAP AG or an SAP affiliate company.  All rights reserved.
 *
 * No part of this publication may be reproduced or transmitted in any form or for any purpose
 * without the express permission of SAP AG.  The information contained herein may be changed
 * without prior notice.
 */

package de.hd.seegarten.pinyou;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api")
public class RuleEngineActionHandlerImpl {

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response handleAction(@FormParam("url") String url) throws IOException {
		List<String> response = new ArrayList<String>();
		response.add(url);
		GenericEntity<List<String>> entity = new GenericEntity<List<String>>(response) {
		};
		List<String> str = new ArrayList<String>();
		str.add("cricket");
		str.add("test");
		PinterestLogic.getListPins(str);
		return Response.status(200).entity(entity).build();
	}

}
