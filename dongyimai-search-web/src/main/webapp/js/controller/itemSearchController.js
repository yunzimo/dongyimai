app.controller('itemSearchController',function($scope,itemSearchService){
    $scope.search = function(){

        itemSearchService.search($scope.searchMap).success(
            function(response){
                //console.log(response);
                $scope.entity = response;
            }
        )
    };

})