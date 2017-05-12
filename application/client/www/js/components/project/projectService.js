'use strict';

/**
 * Project Factory
 * @namespace Factories
 */
angular
    .module('app.project')
    .factory('Project', projectService);

/**
 * @namespace Project
 * @desc Service used for CRUD REST operations for project entity
 * @memberOf Factories
 */
function projectService($resource, API) {
    return $resource(API + 'projects/:id/:projectId', {projectId: '@id'}, {
        update: {
            method: 'PUT'
        }
    });
}
