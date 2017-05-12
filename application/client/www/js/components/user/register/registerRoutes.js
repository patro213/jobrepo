'use strict';

/**
 * Register configuration
 * @namespace Configurations
 */
angular
    .module('app.register')
    .config(registerRoutes);

/**
 * @desc router configuration for register component
 * @param $stateProvider injected state provider object
 */
function registerRoutes($stateProvider) {
    $stateProvider
        .state('register', {
            url: '/register',
            templateUrl: 'js/components/user/register/registerView.html',
            controller: 'registerController',
            controllerAs: 'vm'
        }).state('accountActivation', {
            url: '/activate/{activationToken}',
            controller: 'accountActivationController',
            controllerAs: 'vm'
        })
}
