app.controller('LoginController',function ($scope,loginService) {

    $scope.showName=function () {
        loginService.showName().success(function (response) {
            $scope.username=JSON.parse(response);
        })
    }
});