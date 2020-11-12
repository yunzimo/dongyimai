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
            itemImages:[],
            customAttributeItems:[],
            specificationItems:[]  //规格
        },
		itemList:[
				{
					spec:{},
					/*{
						"机身内存":"16G",
						"网络":"联通3G"
					}*/

					price:0,
					num:99999,
					status:'0',
					isDefault:'0'
				}
			]
    };


/*	[{
		"attributeName": "网络制式",
		"attributeValue": ["移动3G", "移动4G"]
	}, {
		"attributeName": "屏幕尺寸",
		"attributeValue": ["6寸", "5寸"]
	}]
	*/

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

    //监控第一个下拉框的改变
    $scope.$watch('entity.goods.category1Id',function (newValue,oldValue) {
       goodsService.findByParentId(newValue).success(function (response) {
           $scope.typeList_2=response;
           //获得模板ID
           $scope.entity.goods.typeTemplateId=response[0].typeId;

           //初始化需要模板的数据
           InitTemp($scope.entity.goods.typeTemplateId);

           //处理规格选项
           GetSpecList($scope.entity.goods.typeTemplateId);

           //console.log($scope.entity.goods.typeTemplateId);
           $scope.typeList_3={};
       }) 
    });

    //更新specificationItems
	$scope.updateSpecItems=function($event,name,value){
		var object=searchAttrName($scope.entity.goodsDesc.specificationItems,name);
		if(object!=null){
			if($event.target.checked){
				object.attributeValue.push(value);
			}else {
				 object.attributeValue.splice(object.attributeValue.indexOf(value),1);
				 if(object.attributeValue.length==0){
				 	$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				 }
			}
		}else{
			$scope.entity.goodsDesc.specificationItems.push({'attributeName':name,'attributeValue':[value]});
		}
		console.log($scope.entity.goodsDesc.specificationItems);
	}
	searchAttrName=function(list,name){
		for(var i=0;i<list.length;i++){
			if(list[i]['attributeName']==name){
				return list[i];
			}
		}
		return null;
	};

	//更新列表
	$scope.createList=function(){
		$scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0'}];//初始

		var object=$scope.entity.goodsDesc.specificationItems;
		for(var i=0;i<object.length;i++){
			$scope.entity.itemList=$scope.deepClone($scope.entity.itemList,object[i].attributeName,object[i].attributeValue);
		}
	};

	$scope.deepClone=function(list,name,value){
		var newList=[];
		for(var i=0;i<list.length;i++){
			var oldRow=list[i];
			for(var j=0;j<value.length;j++){
				var newRow=JSON.parse(JSON.stringify(oldRow));
				newRow.spec[name]=value[j];
				newList.push(newRow);
			}
		}
		return newList;
	};






    //监控第二个下拉框的改变
    $scope.$watch('entity.goods.category2Id',function (newValue,oldValue) {
        goodsService.findByParentId(newValue).success(function (response) {
            $scope.typeList_3=response;
        })
    });

    GetSpecList=function(id){
        console.log("id="+id);
        typeTemplateService.getSpecList(id).success(function (response) {
            $scope.specIds=response;
        })
    };



    //关于模板信息的初始化
    InitTemp=function(id){
        typeTemplateService.findOne(id).success(function (response) {
            $scope.brandList=JSON.parse(response.brandIds);
            $scope.entity.goodsDesc.customAttributeItems=JSON.parse(response.customAttributeItems);

/*            $scope.specIds=JSON.parse(response.specIds);
            console.log($scope.specIds);
            for(var i=0;i<$scope.specIds.length;i++){

                $scope.specIds[i].specOptionlist=specificationOptionService.findBySpecId($scope.specIds[i].id);
            }*/
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