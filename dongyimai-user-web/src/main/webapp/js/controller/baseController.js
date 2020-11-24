app.controller("baseController",function($scope){
	//从集合中按照key查询对象
	$scope.searchObjectByKey=function(list,key,keyValue){
		//console.log(list);
		for(var i=0;i<list.length;i++){
			if(list[i][key]==keyValue){
				return list[i];
			}
		}
		return null;
	}
	
	$scope.reloadList=function(){
		$scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
	}
	//分页控件配置
	$scope.paginationConf={
			currentPage:1,
			totalItems:10,
			itemsPerPage:10,
			perPageOptions:[10,20,30,40,50],
			onChange:function(){
				$scope.reloadList();//重新加载
			}
	}
	
	$scope.selectIds=[];//选中的ID集合
	
	//更新复选
	$scope.updateSelection=function($event,id){
		if($event.target.checked){//如果是被选中，则增加到数组
			$scope.selectIds.push(id);
		}else{
			var idx=$scope.selectIds.indexOf(id);
			$scope.selectIds.splice(idx,1);//删除
		}
	}
});