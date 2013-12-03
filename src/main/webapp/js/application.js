angular.module('components', [])
    .directive('dragEvent', ['$parse', function($parse) {
        return function(scope, element, attrs) {
            element.bind("dragstart", function (evt) {
                var id = element.attr("id");
                evt.dataTransfer.setData("drag-id", id);

                var fn = $parse(attrs.dragEvent);
                fn(scope, {$element : element});
            });
            element.attr("draggable", true);
        }
    }])
    .directive('dropEvent', ['$parse', function($parse) {
        return function(scope, element, attrs) {
            element.bind("dragover dragenter", function (evt) {
                evt.stopPropagation();
                evt.preventDefault();

                return false;
            });

            element.bind("drop", function (evt) {
                var id = evt.dataTransfer.getData("drag-id");
                var elementTransfer = angular.element(document.getElementById(id));
                element.append(elementTransfer);

                evt.stopPropagation();
                evt.preventDefault();

                var fn = $parse(attrs.dropEvent);
                fn(scope, {$element : elementTransfer, $to : element});
            });
        }
    }])
    .factory('WebSocket', function() {
        return {
            connect : function(url) {
                var self = this;
                this.connection = new WebSocket(url);

                this.connection.onopen = function() {
                    if (this.onopen) {
                        self.onopen();
                    }
                }
                this.connection.onclose = function() {
                    if (this.onclose) {
                        self.onclose();
                    }
                }
                this.connection.onerror = function (error) {
                    if (this.onerror) {
                        self.onerror(error);
                    }
                }
                this.connection.onmessage = function(event) {
                    if (this.onmessage) {
                        self.onmessage(event);
                    }
                }
            },
            
            send : function(message) {
                this.connection.send(message);
            },
            
            close : function() {
                this.connection.onclose = function () {}; // disable onclose handler first
                this.connection.close()
            }
        }
    })
    .factory('TasksManager', function() {
        var url = "ws://localhost:8080/dashboard/tasksManager";
        return {
            init : function() {
                var self = this;
                this.connection = new WebSocket(url);

                this.connection.onopen = function() {
                    console.log("connected");
                    self.getTasks();
                }
                this.connection.onclose = function() {
                    console.log("onclose");
                }
                this.connection.onerror = function (error) {
                    console.log(error);
                }
                this.connection.onmessage = function(event) {
                    console.log("refresh");
                    var data = angular.fromJson(event.data);
                    self.refresh(data.result);
                }
            },
            
            getTasks : function() {
                this.sendMessage({
                    method : "getTasks",
                    params : {}
                });
            },
            
            addTask : function(name) {
                this.sendMessage({
                    method : "addTask",
                    params : {
                        name : name
                    }
                });
            },
            
            updateTask : function(id, status) {
                this.sendMessage({
                    method : "updateTask",
                    params : {
                        id : id, 
                        newStatus : status
                    }
                });
            },
            
            delTask : function(id) {
                this.sendMessage({
                    method : "delTask",
                    params : {
                        id : id
                    }
                });
            },
            
            sendMessage : function(event) {
                this.connection.send(JSON.stringify(event));
            }
        }
    });

angular.module('DashboardApp', ['components']);

function MainCtrl($scope, TasksManager) {
    
    $scope.addTask = function() {
        TasksManager.addTask($scope.taskName);
        $scope.taskName = "";
    }
    
    $scope.updateTask = function(element, status) {
        var id = element.attr("id");
        TasksManager.updateTask(id, status);
    }
    
    $scope.delTask = function(task) {
        TasksManager.delTask(task.id);
    }
    
    TasksManager.refresh = function(tasks) {
        $scope.tasks = tasks;
        $scope.$digest();
    }
    
    TasksManager.init();
}
