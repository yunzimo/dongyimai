 //商品类目控制层 
app.controller('itemCatController' ,function($scope,$controller,itemCatService){
	
	$controller('baseController',{$scope:$scope});//继承

	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	};
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	};
	$scope.entity={
		list:[]
	}
	$scope.findByParentId=function (parentId) {
		itemCatService.findByParentId(parentId).success(
			function (response) {
				$scope.entity.list=response;
			}
		)
	};


	//面包屑代码
	$scope.level=1;

	$scope.setLevel=function (level) {
		$scope.level=level;
	};

	$scope.selectNative=function (p_entity) {
		if($scope.level==1){
			p_entity={id:0};
			$scope.entity_1=null;
			$scope.entity_2=null;
		}else if($scope.level==2){
			$scope.entity_1=p_entity;
			$scope.entity_2=null;
		}else if($scope.level==3){
			$scope.entity_2=p_entity;
		}
		$scope.findByParentId(p_entity.id);
	}
    
});	