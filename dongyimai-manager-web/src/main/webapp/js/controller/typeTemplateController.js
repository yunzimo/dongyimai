 //类型模板控制层 
app.controller('typeTemplateController' ,function($scope,$controller,typeTemplateService,brandService,specificationService){
	
	$controller('baseController',{$scope:$scope}); //继承

/*	private Long id;

	private String name;

	private String specIds;

	private String brandIds;

	private String customAttributeItems;*/

	$scope.entity={
		id:{},
		name:{},
		specIds:{},
		brandIds:{},
		customAttributeItems:[]
	};

	$scope.deleteTableRow=function(index){
		$scope.entity.customAttributeItems.splice(index,1);

	}

	$scope.addTableRow=function(){
		$scope.entity.customAttributeItems.push({}); //添加一个规格选项对象到数组中
	}


    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}

	//json字符串转换为固定格式字符串

	$scope.formatChange=function(list){
		for(var item in list){

		}
	}
	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;
				//this.formatChange(response.rows);
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entity= response;
				$scope.entity.specIds=JSON.parse(response.specIds)
				$scope.entity.brandIds=JSON.parse(response.brandIds)
				$scope.entity.customAttributeItems=JSON.parse(response.customAttributeItems)
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
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
		typeTemplateService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	$scope.findOptionList=function () {
		brandService.findBrandOption().success(function (response) {
			$scope.brandList={data:response};
		})
		this.findSpecificOption();
	}

	$scope.findSpecificOption=function () {
		specificationService.findSpecificOption().success(function (response) {
			$scope.specificOptionList={data:response};
		})
	}

    
});	