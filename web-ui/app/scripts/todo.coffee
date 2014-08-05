'use strict'

angular
  .module('app')
  .service 'Todo', ($resource) ->
    $resource '/api/todos/:_id', _id: '@_id', { update: method: 'PUT' }

angular
  .module('app')
  .controller 'TodoCtrl', ($scope, Todo, cfpLoadingBar) ->

    $scope.todo = {}

    $scope.todos = Todo.query sortby: 'description', asc: true

    $scope.save = (todo) ->
      cfpLoadingBar.start()
      Todo.save todo, (res) ->
        todo = angular.extend todo, res
        i = _.sortedIndex $scope.todos, todo, 'description'
        $scope.todos.splice i, 0, todo
        $scope.todo = {}

    $scope.delete = (todo) ->
      cfpLoadingBar.start()
      Todo.delete _id: todo._id, -> _.pull $scope.todos, todo
      
    $scope.update = (todo, att) ->
      cfpLoadingBar.start() 
      Todo.update _.pick(todo, '_id'), _.pick(todo, att)
      
    $scope.creationAt = (todo) -> 
      moment(todo.creationAt).calendar()
    
    $scope.checkDescription = (data) ->
       if _.isEmpty data
         return "description can't be empty"
