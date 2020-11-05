
app.controller('brandController',function ($scope,brandService,$controller) {

    $controller('baseController',{$scope:$scope});


    $scope.key={};
    $scope.findPage=function (pageNum,pageSize) {
        brandService.findPage(pageNum,pageSize,$scope.key).success(function (response) {
            $scope.list=response.rows;
            $scope.paginationConf.totalItems=response.total;
        })
    }

    $scope.save=function () {
        if($scope.brand.id==null){
            method="addBrand";
        }else{
            method="updateBrand";
        }
        brandService.save(method,$scope.brand).success(function (response) {
            if(response.success){
                alert(response.msg);
                $scope.reloadList();
            }else{
                alert(response.msg);
            }
        })
    }
    $scope.findOne=function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.brand=response;
        })
    }


    $scope.delete=function () {
        brandService.delete($scope.selectIds).success(function (response) {
            if(response.success){
                alert(response.msg);
                $scope.reloadList();
            }else{
                alert(response.msg);
            }
        })
    }


})