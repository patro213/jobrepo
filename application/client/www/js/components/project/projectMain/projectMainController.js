'use strict';

/**
 * Project controller
 * @namespace Controllers
 */
angular
    .module('app.project')
    .controller('projectMainController', projectMainController);

/**
 * @desc Controller configuration
 * @param $rootScope rootScope object
 * @param $q promise resolver object
 * @param $state state provider object
 * @param project specific project resolved at state switch
 * @param Auth authentication service
 * @memberof Controllers
 */
function projectMainController($rootScope, $location, $q, $state, project, Auth) {

    var vm = this;
    vm.projectId = $state.params.projectId;
    vm.project = project;
	vm.project.testsFailed = 5;
	vm.project.testsSuccessful = 4;
	vm.curUser = null;

    vm.isActive = isActive;
    vm.goHome = goHome;
    vm.goDrawings = goDrawings;
    vm.goTests = goTests;
    vm.goUsers = goUsers;
    vm.goSettings = goSettings;
    vm.goOverview = goOverview;
    vm.getProjectOwnerName = getProjectOwnerName;

    getCurrentUser();

    $rootScope.$watch(userObserve, updateUserName);

    /**
     * @desc observing user profile changes
     */
    function userObserve() {
        return Auth.getUser();
    }

    /**
     * @desc updates user name at projects
     */
    function updateUserName(data) {
        if(vm.curUser != null && vm.project.creator != null) {
            if (vm.curUser.email == vm.project.creator.email) {
                vm.project.creator = data;
            }
        }
    }

    /**
     * @desc Retrieves current authenticated user
     */
    function getCurrentUser() {
        Auth.getCurrentUser()
            .then(function(user) {
                    vm.curUser = user;
                }
            );
    }

    function isActive(viewLocation) {
        return viewLocation === $location.path();
    }

    function goHome() {
      $state.go('project', {}, { reload: true });
    }

    function goDrawings() {
      $state.go('project.drawings', {}, { reload: true });
    }

    function goTests() {
      $state.go('project.tests', {}, { reload: true });
    }

    function goUsers() {
      $state.go('project.users', {}, { reload: true });
    }

    function goSettings() {
      $state.go('project.settings', {}, { reload: true });
    }

    function goOverview() {
      $state.go('main', {}, { reload: true });
    }

    /**
     * @desc Sets user name based on user profile
     */
    function getProjectOwnerName() {
        if(vm.project.creator != null) {
            if (isNullOrEmpty(vm.project.creator.name) && isNullOrEmpty(vm.project.creator.surname)) {
                return vm.project.creator.email;
            } else {
                if (isNullOrEmpty(vm.project.creator.name)) {
                    return vm.project.creator.surname;
                } else if (isNullOrEmpty(vm.project.creator.surname)) {
                    return vm.project.creator.name;
                } else {
                    return vm.project.creator.name + " " + vm.project.creator.surname;
                }
            }
        }
    }

    /**
     * checks for null or empty variable
     * @param variable to check
     */
    function isNullOrEmpty(variable) {
        if(variable == null || variable == "") {
            return true;
        } else {
            return false;
        }
    }
}
