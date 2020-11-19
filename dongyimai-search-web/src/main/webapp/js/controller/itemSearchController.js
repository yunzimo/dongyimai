app.controller('itemSearchController',function($scope,$location,itemSearchService){

    $scope.search = function(){
        itemSearchService.search($scope.searchMap).success(
            function(response){
                //console.log(response);
                $scope.entity = response;
                $scope.setPageLabel();
            }
        )
    };

    $scope.loadKeywords=function(){
        var keywords=$location.search()['keywords'];
        if(keywords!=null&&keywords!=''){
            $scope.searchMap.keywords=keywords;
            $scope.search();
        }
    };

    $scope.findByPageNo=function(pageNo){
        if (pageNo>$scope.entity.totalPage){
            return;
        }
        if(pageNo<1){
            return;
        }
      $scope.searchMap.pageNo=pageNo;
      $scope.search();
    };

    //排序
    $scope.findBySort=function(sortField,sortValue){
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sortValue=sortValue;
        $scope.search();
    };

    //关键词检查
    $scope.keywordCheck=function(){
        if($scope.entity.brandList!=null){
            for(var i=0;i<$scope.entity.brandList.length;i++){
                if($scope.searchMap.keywords.indexOf($scope.entity.brandList[i].text)>=0){
                    return true;
                }
            }
        }
        return false;
    };



    $scope.searchMap={
        "category":'',
        "brand":'',
        'price':'',
        'pageNo':1,
        'pageSize':10,
        'sortField':'',
        'sortValue':'',
        spec:{}
    };

    //设置分页栏

/*  主要的原理是根据当前页码
    确定第一页和最后一页
    确定分页页码同时根据当前页面判断
    前面的"..."和后面的是否显示
*/
    $scope.setPageLabel=function(){
        $scope.pageLabel=[];
        var firstPage=1;
        var lastPage=$scope.entity.totalPage;
        var maxPage=$scope.entity.totalPage;
        $scope.firstDot=true;
        $scope.lastDot=true;
        if(maxPage>5){
            if($scope.searchMap.pageNo<=3){
                lastPage=5;
                $scope.firstDot=false;
            }else if($scope.searchMap.pageNo>=maxPage-2){
                firstPage=maxPage-4;
                $scope.lastDot=false;
            }else {
                firstPage=$scope.searchMap.pageNo-2;
                lastPage=$scope.searchMap.pageNo+2;
            }
        }else {
            $scope.firstDot=true;
            $scope.lastDot=true;
        }
        for(var i=firstPage;i<=lastPage;i++){
            $scope.pageLabel.push(i);
        }

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