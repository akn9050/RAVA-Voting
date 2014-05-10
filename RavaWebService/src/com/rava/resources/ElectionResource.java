package com.rava.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.rava.data.ElectionDP;
import com.rava.model.Election;
import com.sun.jersey.api.NotFoundException;

@Path("/elections")
public class ElectionResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Election> getAllElections() {
		List<Election> elections = ElectionDP.loadElections();
		return elections;
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Election getElection(@PathParam("id") int id) {
		Election election = ElectionDP.getElection(id);
		if (election == null) {
			// Response response = Response.status(Status.NOT_FOUND)
			// .entity("Election, " + id + ", not found").build();
			// throw new WebApplicationException(response);

			throw new NotFoundException("Election, " + id + ", not found");
		}
		return election;
	}
}
