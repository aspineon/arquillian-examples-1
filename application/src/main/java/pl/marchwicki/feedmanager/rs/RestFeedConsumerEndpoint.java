package pl.marchwicki.feedmanager.rs;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pl.marchwicki.feedmanager.FeedsService;
import pl.marchwicki.feedmanager.model.Feed;

@Path("/rs")
@Stateless
public class RestFeedConsumerEndpoint {

	@Inject
	FeedsService service;

	@Path("/feeds")
	@GET
	@Produces(value = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Feed[] returnAllFeeds() {
		return service.getAll();
	}
	
	@Path("/feed/{feedname}")
	@POST
	public Response consume(@PathParam("feedname") String feedname,
			String messageBody) {
		service.addNewItems(feedname, messageBody);
		return Response.status(Status.CREATED).build();
	}

	@Path("/feed/{feedname}")
	@GET
	@Produces(value = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response retrieve(@PathParam("feedname") String feedname) {
		Feed feed = service.getFeed(feedname);

		if (feed == null) {
			return Response.status(Status.NOT_FOUND).build();
		} else {
			return Response.status(Status.OK).entity(feed).build();
		}
	}

}
