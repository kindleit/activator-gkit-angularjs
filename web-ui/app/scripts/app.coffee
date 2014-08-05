'use strict'

angular
  .module('app', [
    'ngResource',
    'ngRoute',
    'ui.bootstrap',
    'xeditable',
    'angular-loading-bar'
  ])
  .config ($routeProvider, cfpLoadingBarProvider) ->
    $routeProvider
      .when '/',
        templateUrl: 'views/todo.html'
        controller: 'TodoCtrl'
      .otherwise
        redirectTo: '/'
  .run (editableOptions) ->
    editableOptions.theme = 'bs3'
        