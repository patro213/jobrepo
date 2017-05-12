'use strict';

/**
 * New project configuration
 * @namespace Configurations
 */
angular
    .module('app.newProject')
    .config(projectCreateRoutes);

/**
 * @desc router configuration for project component
 * @param $stateProvider injected state provider object
 */
function projectCreateRoutes($stateProvider) {
    $stateProvider
        .state('createProject', {
            url: '/create',
            templateUrl: 'js/components/project/projectCreate/projectCreateView.html',
            controller: 'projectCreateController',
            controllerAs: 'vmCreate'
        })
}
