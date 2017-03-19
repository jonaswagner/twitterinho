/**
 * Created by flaviokeller on 19.03.17.
 */


angular
    .module('twitterinho', ['ngResource'])
    .service('TwitterService', function ($log, $resource) {
        return {
            getTweet: function () {
                $http.get('/tweet').success(function (data) {
                    $scope.tweet = data;
                })

            }
        }
    })
    .controller('TweetController', function ($scope, $log, TwitterService) {
        $scope.tweet = TwitterService.getTweet();
    });