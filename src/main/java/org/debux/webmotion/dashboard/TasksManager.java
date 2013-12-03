package org.debux.webmotion.dashboard;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.debux.webmotion.dashboard.Task.SortedTasks;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.render.Render;
import org.debux.webmotion.server.render.RenderWebSocket;
import org.debux.webmotion.server.websocket.WebMotionWebSocketJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TasksManager extends WebMotionController {

    private static final Logger log = LoggerFactory.getLogger(TasksManager.class);
    
    public Render createWebsocket() {
        TasksManagerWebSocket socket = new TasksManagerWebSocket();
        return new RenderWebSocket(socket);
    }
 
    public class TasksManagerWebSocket extends WebMotionWebSocketJson {
 
        @Override
        public void onOpen() {
            // Store all connections
            ServerContext serverContext = getServerContext();
            List<TasksManagerWebSocket> connections = (List<TasksManagerWebSocket>) serverContext.getAttribute("connections");
            if (connections == null) {
                connections = new ArrayList<TasksManagerWebSocket>();
                serverContext.setAttribute("connections", connections);
            }
            connections.add(this);
        }

        @Override
        public void onClose() {
            ServerContext serverContext = getServerContext();
            List<TasksManagerWebSocket> connections = (List<TasksManagerWebSocket>) serverContext.getAttribute("connections");
            connections.remove(this);
        }
        
        @Override
        public void sendObjectMessage(String methodName, Object message) {
            ServerContext serverContext = getServerContext();
            List<TasksManagerWebSocket> connections = (List<TasksManagerWebSocket>) serverContext.getAttribute("connections");
            for (TasksManagerWebSocket socket : connections) {
                socket.superSendObjectMessage(methodName, message);
            }
        }
        
        public void superSendObjectMessage(String methodName, Object message) {
            super.sendObjectMessage(methodName, message);
        }
        
        public SortedTasks getTasks() {
            ServerContext serverContext = getServerContext();
            List<Task> tasks = (List<Task>) serverContext.getAttribute("tasks");
            SortedTasks sortedTasks = Task.getSortedTasks(tasks);
            return sortedTasks;
        }

        public SortedTasks addTask(String name) {
            ServerContext serverContext = getServerContext();
            
            Task task = new Task(name);
            List<Task> tasks = (List<Task>) serverContext.getAttribute("tasks");
            tasks.add(task);
            
            return getTasks();
        }

        public SortedTasks updateTask(final String id, String newStatus) {
            ServerContext serverContext = getServerContext();
            
            List<Task> tasks = (List<Task>) serverContext.getAttribute("tasks");
            Task task = (Task) CollectionUtils.find(tasks, new Predicate() {
                public boolean evaluate(Object object) {
                    Task other = (Task) object;
                    return other.getId().equals(id);
                }
            });

            task.setStatus(newStatus);
            tasks.remove(task);
            tasks.add(task);
            
            return getTasks();
        }

        public SortedTasks delTask(final String id) {
            ServerContext serverContext = getServerContext();
            
            List<Task> tasks = (List<Task>) serverContext.getAttribute("tasks");
            CollectionUtils.filter(tasks, new Predicate() {
                public boolean evaluate(Object object) {
                    Task other = (Task) object;
                    return !other.getId().equals(id);
                }
            });
            
            return getTasks();
        }
    }
}
