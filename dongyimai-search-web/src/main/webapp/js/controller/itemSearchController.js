app.controller('itemSearchController',function($scope,itemSearchService){

    $scope.search = function(){
        itemSearchService.search($scope.searchMap).success(
            function(response){
                //console.log(response);
                $scope.entity = response;
            }
        )
    };

    $scope.searchMap={
        "category":'',
        "brand":'',
        'price':'',
        spec:{}
    };

    //添加到过滤栏
    $scope.addSearchMap=function (key,value) {
         if((key=='category')||(key=='brand')||key=='price'){
             $scope.searchMap[key]=value;
         }else {
             $scope.searchMap.spec[key]=value;
         }
         $scope.search();
         //console.log($scope.searchMap.spec);
    };

    //移除过滤栏
    $scope.removeSearchMap=function (key) {
        if((key=='category')||(key=='brand')||key=='price'){
            $scope.searchMap[key]='';
        }else {
            delete $scope.searchMap.spec[key];
        }
        $scope.search();
    };
});