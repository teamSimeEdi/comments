package si.RSOteam8;
import com.kumuluz.ee.discovery.annotations.RegisterService;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@RegisterService
@ApplicationPath("v1")
public class CommentApplication extends Application {

}
