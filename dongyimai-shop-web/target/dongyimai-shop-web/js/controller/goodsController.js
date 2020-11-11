 //商品控制层 
app.controller('goodsController' ,function($scope,goodsService,uploadService,typeTemplateService){

    //定义数据结构
/*
$scope.entity={
        goods:{},
        goodsDesc:{
            itemImages:[{
                    color:{},
                    url:{}
                }]
        }
    };
*/

    $scope.entity={
        goods:{
            category1Id:{},
            category2Id:{},
            category3Id:{}

        },
        goodsDesc:{
            itemImages:[]
        }
    };

	$scope.image_entity={};
	$scope.upload=function(){
		uploadService.upload().success(function (response) {
			if(response.message){
				console.log(response.message);
				$scope.image_entity.url=response.message;
				console.log("imageurl="+$scope.image_entity.url);
			}else {
				alert(response.message);
			}
		})
	};

	$scope.addToImageList=function(){
	    $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	    console.log($scope.image_entity.url);
    };

	$scope.removeImage=function(index){
	    $scope.entity.goodsDesc.itemImages.splice(index,1);
    };

	//初始化商品分类列表
    $scope.initTypeList=function(){
        goodsService.findByParentId(0).success(function (response) {
            $scope.typeList_1=response;

            //console.log($scope.typeList_1);
        })
    };

    $scope.$watch('entity.goods.category1Id',function (newValue,oldValue) {
       goodsService.findByParentId(newValue).success(function (response) {
           $scope.typeList_2=response;
           $scope.typeTempId=response[0].typeId;
           console.log($scope.typeTempId);
           $scope.typeList_3={};
       }) 
    });
    $scope.$watch('entity.goods.category2Id',function (newValue,oldValue) {
        goodsService.findByParentId(newValue).success(function (response) {
            $scope.typeList_3=response;
        })
    });
    showBrandList=function(id){
        typeTemplateService.findOne(id).success(function (response) {
            $scope.brandList=JSON.parse(response.brandIds);
        })
    };
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	};
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	};
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	};
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象
		$scope.entity.goodsDesc.introduction=editor.html();
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	alert(response.message);

		        	//清空对象和富文本
		        	$scope.entity={};
		        	editor.html("");
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
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
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	$scope.findByParentId=function () {
		
	}
    
});	