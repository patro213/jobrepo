'use strict';

/**
 * Registration controller
 * @namespace Controllers
 */
angular
    .module('app.register')
    .controller('registerController', registerController);

/**
 * @desc Controller configuration
 * @param $rootScope rootScope object
 * @param $state state provider object
 * @param Auth authentication service
 * @param LOGGER logger service
 * @memberof Controllers
 */
function registerController($rootScope, $state, Auth, LOGGER) {
    var vm = this;

    vm.register = register;
    vm.errorPasswordMatch = null;
    vm.errorInUse = null;
    vm.passwordLength = null;

    vm.registerForm = 1;
    vm.registrationEmailSended = null;

    // FUNCTIONS
    /**
     * @desc registration method which sends user information to defined API
     */
    function register() {
        vm.register.email = $rootScope.email;
        $rootScope.accountDeactivated = null;

        if (vm.register.password !== vm.register.confirmPassword) {
            vm.errorPasswordMatch  = 1;
            vm.passwordLength = null;
        }
        else {
            vm.errorPasswordMatch = null;
            vm.passwordLength = null;

            if (vm.register.password.length > 5) {
                var user = {
                    email: vm.register.email,
                    password: vm.register.password
                };

                Auth.register(user)
                    .then(onSuccess)
                    .catch(onError);
            }
            else {
                vm.passwordLength = 1;
            }
        }

        /**
         * @desc switches state to login state, after successful registration
         */
        function onSuccess() {
            $rootScope.registrationEmailSended = 1;
            $state.go('login');
        }

        /**
         * @desc sets error indicator if email is already registered
         * @param response response sent from API
         */
        function onError(response) {
            if (response.status == 500) {
                LOGGER.error('User creation failed: Failed to generate hashed activation token for new user',
                    new Error('Failed to persist new user ' + vm.register.email, response));
            }
            vm.errorInUse = 1;
            $rootScope.accountDeactivated = null;
        }
    }
}
