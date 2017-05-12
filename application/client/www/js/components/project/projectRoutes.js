'use strict';

/**
 * Project configuration
 * @namespace Configurations
 */
angular
    .module('app.project')
    .config(projectRoutes);

/**
 * @desc router configuration for project component
 * @param $stateProvider injected state provider object
 */
function projectRoutes($stateProvider) {
    $stateProvider
        .state('project', {
            url: '/project/{projectId}',
            templateUrl: 'js/components/project/projectMain/projectMainView.html',
            controller: 'projectMainController',
            controllerAs: 'vm',
            cache : false,
			resolve: {
                project: function (Project, $stateParams) {
                    return Project.get({ projectId: $stateParams.projectId });
                }
            }
        }).state('project.drawings', {
            url: '/drawings',
            templateUrl: 'js/components/project/projectDrawings/projectDrawingsView.html',
            controller: 'projectDrawingsController',
            controllerAs: 'vmDrawings'
        }).state('project.tests', {
            url: '/tests',
            templateUrl: 'js/components/project/projectTests/projectTestsView.html',
            controller: 'projectTestsController',
            controllerAs: 'vmTests',
            resolve: {
                sketches: function ($http, $stateParams, API) {
                    return $http.get(API + 'projects/' + $stateParams.projectId + '/sketches/singleTestResult');
                },
                browsers: function ($http, API) {
                    return $http.get(API + 'tests/browsers');
                },
                resolutions: function ($http, API) {
                    return $http.get(API + 'tests/resolutions');
                }
            }
        }).state('project.users', {
            url: '/users',
            templateUrl: 'js/components/project/projectUsers/projectUsersView.html',
            controller: 'projectUsersController',
            controllerAs: 'vmUsers'
        }).state('project.settings', {
            url: '/settings',
            templateUrl: 'js/components/project/projectSettings/projectSettingsView.html',
            controller: 'projectSettingsController',
            controllerAs: 'vmSettings',
            resolve: {
                project: function (Project, $stateParams) {
                    return Project.get({ projectId: $stateParams.projectId });
                }
            }
        }).state('invitations', {
            url: '/invitations/{invitationToken}',
            templateUrl: 'js/components/project/projectInvitations/projectInvitationsView.html',
            controller: 'projectInvitationsController',
            controllerAs: 'vmInvitations',
            resolve: {
                invitation: function ($http, $stateParams, API) {
                    return $http.get(API + 'projects/invitation/' + $stateParams.invitationToken);
                }
            }
        })
}
