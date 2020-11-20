app.controller('itemPageController',function ($scope) {

    $scope.num=1;
    $scope.addNum=function (num) {
        $scope.num+=num;
        if($scope.num<1){
            $scope.num=1;
        }
    };

    //判断所选规格是否是已有SKU
    $scope.compareSku=function(map1,map2){
        for(var k in map1){
            if(map1[k]!=map2[k])return false;
        }
        for (var k in map2){
            if(map2[k]!=map1[k])return false;
        }
        return true;
    };

    //加入购物车

    $scope.addtoCart=function(){
      console.log("sku="+$scope.sku.id+"...num="+$scope.num);
    };


    $scope.selectedItem={};

    //点击规格事件
    $scope.selectItem=function (key,value) {
        $scope.selectedItem[key]=value;

        //和sku列表对比
        for(var i=0;i<skuList.length;i++){
            if($scope.compareSku($scope.selectedItem,JSON.parse(skuList[i].spec))){
                $scope.sku=skuList[i];
                return;
            }
        }
        $scope.sku={'id':0,'title':'------','price':0};

    };
    $scope.checkSelect=function (key,value) {
      if($scope.selectedItem[key]==value){
          return true;
      }else {
          return false;
      }
    };

    //加载默认的SKU
    //console.log(skuList);
    $scope.loadDefaultSku=function(){
        $scope.sku=skuList[0];
        $scope.selectedItem=JSON.parse(skuList[0].spec);
    };
});