'use strict';

/**
 * Login controller
 * @namespace Controllers
 */
angular
    .module('app.login')
    .controller('loginController', loginController);

/**
 * @desc Controller configuration
 * @param $rootScope rootScope object
 * @param $scope scope object
 * @param $q promise resolver object
 * @param $state state provider object
 * @param Auth authentication service
 * @param LOGGER logger service
 * @memberof Controllers
 */
function loginController($rootScope, $scope, $q, $state, Auth, LOGGER) {

    var vm = this;
    vm.login = login;
    vm.toRegistration = toRegistration;

    vm.errorLogin = null;
    vm.loggedUser = null;

    $scope.$watch(authObserve, userContext);

    // FUNCTIONS
    /**
     * @desc performs login request to API with form data
     */
    function login() {
        vm.login.email = $rootScope.email;

        Auth.login({
            email: vm.login.email,
            password: vm.login.password
        }).then(onSuccess)
            .catch(onError);

        /**
         * @desc switches state to myprofile if login was successful
         */
        function onSuccess() {
            $rootScope.switchLocalization();
            $rootScope.registrationEmailSended = null;
            vm.errorLogin = null;
            $rootScope.passwordRecoveryEmailSended = null;
            $rootScope.accountActivated = null;
            $rootScope.invalidActivationLink = null;

            var invitation = $rootScope.transferToInvitation;

            if (typeof invitation === 'undefined' || invitation === null) {
                $state.go('main');
            } else {
                $state.go('invitations', { invitationToken: invitation });
            }
        }

        function onError(response) {
            vm.errorLogin = 1;
            $rootScope.passwordRecoveryEmailSended = null;
            $rootScope.accountActivated = null;
            $rootScope.invalidActivationLink = null;
            return response;
        }
    }

    /**
     * @desc switches to register state
     */
	function toRegistration() {
	    $state.go('register');
	    vm.login.password = null;
	}

	/**
	 * @desc Checks if user is authenticated
	 */
	function authObserve() {
		return Auth.isAuthenticated();
	}

	/**
	 * @desc set user context for authenticated user
	 * @param user new user object
	 */
	function userContext(user) {
		if (user) {
			LOGGER.setUserContext(user);
		}
	}
}
