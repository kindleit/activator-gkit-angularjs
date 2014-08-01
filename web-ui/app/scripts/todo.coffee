'use strict'

angular
  .module('app')
  .service 'Todo', ($resource) ->
    $resource '/api/todos/:_id', _id: '@_id', { update: method: 'PUT' }

angular
  .module('app')
  .controller 'TodoCtrl', ($scope, Todo) ->

    $scope.todo = {}

    $scope.todos = Todo.query sortby: 'description', asc: true

    $scope.save = (todo) ->
      Todo.save todo, (res) ->
        todo = angular.extend todo, res
        i = _.sortedIndex $scope.todos, todo, 'description'
        $scope.todos.splice i, 0, todo
        $scope.todo = {}

    $scope.delete = (todo) ->
      Todo.delete _id: todo._id, -> _.pull $scope.todos, todo
      
    $scope.update = (todo, ss) -> 
      Todo.update todo