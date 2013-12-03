package org.debux.webmotion.dashboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.debux.webmotion.server.WebMotionServerListener;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.mapping.Mapping;

public class StartupListenner implements WebMotionServerListener {

    public void onStart(Mapping mapping, ServerContext serverContext) {
        List<Task> tasks = Arrays.asList(
            new Task("Task 0"),
            new Task("Task 1"),
            new Task("Task 10"),
            new Task("Task 11")
        );
        serverContext.setAttribute("tasks", new ArrayList<Task>(tasks));
    }

    public void onStop(ServerContext serverContext) { }
}
