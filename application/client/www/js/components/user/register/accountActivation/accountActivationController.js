'use strict';

/**
 * Account activation controller
 * @namespace Controllers
 */
angular
    .module('app.register')
    .controller('accountActivationController', accountActivationController);

/**
 * @desc Controller configuration
 * @param $rootScope rootScope object
 * @param $state state provider object
 * @param $http http request provider object
 * @param API path to remote API
 * @memberof Controllers
 */
function accountActivationController($rootScope, $state, $http, API) {

    var vm = this;
    vm.activate = activate;
    vm.activationToken = $state.params.activationToken;

    activate();

    /**
     * Sends activation token to REST endpoint to activate user account
     */
    function activate() {
        return $http.get(API + 'activate/' + vm.activationToken, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        }).then(onSuccess)
            .catch(onError);

        function onSuccess(response) {
            $rootScope.accountActivated = 1;
            $rootScope.invalidActivationLink = null;
            $rootScope.accountDeactivated = null;
            $rootScope.email = response.data;
            $state.go('login');
        }

        function onError(response) {
            if (response.status == 400) {
                $rootScope.invalidActivationLink = 1;
                $rootScope.accountActivated = null;
                $rootScope.accountDeactivated = null;
                $rootScope.email = null;
                $state.go('login');
            } else {
                $rootScope.accountDeactivated = 1;
                $rootScope.invalidActivationLink = null;
                $rootScope.accountActivated = null;
                $rootScope.email = null;
                $state.go('register');
            }
        }
    }
}
