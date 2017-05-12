'use strict';

/**
 * New project controller
 * @namespace Controllers
 */
angular
    .module('app.newProject')
    .controller('projectCreateController', projectCreateController);

/**
 * @desc Controller configuration
 * @param $rootScope rootScope object
 * @param $state state provider object
 * @param Project Project service
 * @param LOGGER logger service
 * @memberof Controllers
 */
function projectCreateController($rootScope, $state, Sock, Project, LOGGER) {
    var vmCreate = this;

    vmCreate.errorProjectCreation = null;
    vmCreate.createProject = createProject;
    vmCreate.errorName = 0;
    vmCreate.errorDescription = 0;

    function createProject() {
        vmCreate.errorName = 0;
        vmCreate.errorDescription = 0;
        if(vmCreate.project.name == null || vmCreate.project.name == "") {
            vmCreate.errorName = 1;
        } else if(vmCreate.project.description == null || vmCreate.project.description == "") {
            vmCreate.errorDescription = 1;
        } else {
            if (vmCreate.base64Photo != null) {
                vmCreate.project.logo = "data:" + vmCreate.base64Photo.filetype + ";base64, " + vmCreate.base64Photo.base64;
            } else {
                vmCreate.project.logo = null;
            }

            var project = {
                name: vmCreate.project.name,
                description: vmCreate.project.description,
                logo: vmCreate.project.logo
            };

            Project.save(project)
                .$promise
                .then(onSuccess)
                .catch(onError);
        }

        function onSuccess(data) {
            Sock.subscribe('/topic/project/' + data.id, function (data) {
                $rootScope.$broadcast('$newProjectMessage', JSON.parse(data.body));
            });
            $state.go("project", {projectId: data.id});
        }

        function onError() {
            vmCreate.errorProjectCreation = 1;
            LOGGER.error('Project creation failed',
                new Error('Failed to create new project ' + vmCreate.project.name));
        }
    }
}
