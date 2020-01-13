package si.RSOteam8;


import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.logs.cdi.LogParams;


import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("comments/{imageId}")
@Log
public class CommentResource {

    @Inject
    @RestClient
    private LeaderboardService leaderboardService;

    @Inject
    private ConfigProperties cfg;

    @GET

    public Response getAllComments(@PathParam("imageId") int imageid) {
        List<Comment> comments = new LinkedList<Comment>();

        try (
                Connection conn = DriverManager.getConnection(cfg.getDburl(), cfg.getDbuser(), cfg.getDbpass());
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM comments.comments WHERE \"imageid\" = "+"'"+imageid+"'");
        ) {
            while (rs.next()) {
                Comment comment = new Comment();
                comment.setId(rs.getString(1));
                comment.setComment(rs.getString(2));
                comments.add(comment);
            }
        }
        catch (SQLException e) {
            System.err.println(e);
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.ok(comments).build();
    }
    /*@Counted(name = "getAllComments-count")
    @GET
    public Response getAllComments() {
        Logger.getLogger(CommentHealthCheck.class.getSimpleName()).info("just testing");
        List<Comment> comments = new LinkedList<Comment>();
        Comment comment = new Comment();
        comment.setId("1");
        comment.setCommentname(cfg.getTest());
        comments.add(comment);
        comment = new Comment();
        comment.setId("2");
        comment.setCommentname("peterklepec");
        comments.add(comment);
        return Response.ok(comments).build();
    }*/

    @GET
    @Path("{userId}/{commentId}")
    public Response getComment(@PathParam("commentId") String commentid) {

        try (
                Connection conn = DriverManager.getConnection(cfg.getDburl(), cfg.getDbuser(), cfg.getDbpass());
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM comments.comments WHERE \"id\" = "+"'"+commentid+"'");
        ) {
            if (rs.next()){
                Comment comment = new Comment();
                comment.setId(rs.getString(1));
                comment.setComment(rs.getString(2));
                return Response.ok(comment).build();

            }
        }
        catch (SQLException e) {
            System.err.println(e);
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

   /* @POST
    public Response addNewComment(Comment comment) {
        //Database.addCustomer(customer);
        return Response.noContent().build();
    }*/
    @POST
    @Path("{userId}")
    public Response addNewComment(@PathParam("userId") String username,
                                  @PathParam("imageId") int imagename,
                                  Comment comment) {
        try (
                Connection conn = DriverManager.getConnection(cfg.getDburl(), cfg.getDbuser(), cfg.getDbpass());
                Statement stmt = conn.createStatement();
        ) {
            stmt.executeUpdate("INSERT INTO comments.comments (comment, userid, imageid) VALUES ('"
                    + comment.getComment() + "', '" + username + "', '"+ imagename + "')",
            Statement.RETURN_GENERATED_KEYS);
            leaderboardService.updateLeaderboard(username);
        }
        catch (SQLException e) {
            System.err.println(e);
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.noContent().build();
    }
    @DELETE
    @Path("{commentId}")
    public Response deleteComment(@PathParam("commentId") int commentid) {
        try (
                Connection conn = DriverManager.getConnection(cfg.getDburl(), cfg.getDbuser(), cfg.getDbpass());
                Statement stmt = conn.createStatement();
        ) {
            stmt.executeUpdate("DELETE FROM comments.comments WHERE \"id\" = " + "'"+commentid+"'");
        }
        catch (SQLException e) {
            System.err.println(e);
            return Response.status(Response.Status.FORBIDDEN).build();
        }

            return Response.noContent().build();
    }
    /*@DELETE
    @Path("{commentId}")
    public Response deleteComment(@PathParam("commentId") String commentId) {
        //Database.deleteCustomer(customerId);
        return Response.noContent().build();
    }*/
}
