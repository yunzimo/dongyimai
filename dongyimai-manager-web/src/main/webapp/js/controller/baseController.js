app.controller('baseController',function ($scope) {
    $scope.paginationConf={
        currentPage:1,
        totalItems:10,
        itemsPerPage:10,
        perPageOptions:[10,20,30,40],
        onChange:function () {
            $scope.reloadList();
        }
    }

    $scope.reloadList=function(){
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    };

    $scope.selectIds=[];
    $scope.addToIds=function ($event,id) {
        if($event.target.checked==true){
            $scope.selectIds.push(id);
        }else{
            var number = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(number,1);
        }
    }
})