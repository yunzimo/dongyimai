app.controller('indexController',function ($scope,indexService) {
    $scope.showName=function () {
        indexService.showName().success(function (response) {
            $scope.loginName=JSON.parse(response);
        })
    }
});