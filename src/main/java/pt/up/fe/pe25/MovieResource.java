package pt.up.fe.pe25;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/movies")
public class MovieResource {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello RESTEasy";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovies() {
    	List<Movie> movies = Movie.listAll();
        return Response.ok(movies).build();
    }

    // @GET
    // @Path("{id}")
    // @Produces(MediaType.APPLICATION_JSON)
    // public Response getMovieById(@PathParam("id") Long id
    // 	return Movie.findByIdOptional(id).map(movie -> Response.ok(movie).build())
    //         .orElse(Response.status(Response.Status.NOT_FOUND).build());
    // }

    @GET
    @Path("/movies/{country}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByCountry(@PathParam("country") String country) {
    	List<Movie> movies = Movie.list("SELECT m FROM Movie WHERE m.country = ? ", country);
    	return Response.ok(movies).build();
    }

    @GET
    @Path("/movies/{title}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByTitle(@PathParam("title") String title) {
    	return Movie.find("title", title).singleResultOptional().map(movie -> Response.ok(movie).build())
        .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createMovie(Movie movie) {
    	Movie.persist(movie);
        if (movie.isPersistent()) {
            return Response.created(URI.create("/movies" + movie.id)).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response deleteById(@PathParam("id") Long id) {
        boolean deleted = Movie.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
