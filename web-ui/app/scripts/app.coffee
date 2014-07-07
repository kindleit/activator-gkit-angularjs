'use strict'

angular
  .module('app', [
    'ngResource',
    'ngRoute'
  ])
  .config ($routeProvider) ->
    $routeProvider
      .when '/',
        templateUrl: 'views/todo.html'
        controller: 'TodoCtrl'
      .otherwise
        redirectTo: '/'
