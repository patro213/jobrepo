'use strict';

/**
 * Login configuration
 * @namespace Configurations
 */
angular
    .module('app.login')
    .config(loginRoutes);

/**
 * @desc router configuration for login component
 * @param $stateProvider injected state provider object
 */
function loginRoutes($stateProvider) {
    $stateProvider
        .state('login', {
            url: '/login',
            templateUrl: 'js/components/user/login/loginView.html',
            controller: 'loginController',
            controllerAs: 'vm'
        })
}