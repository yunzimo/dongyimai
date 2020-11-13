 //商品控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,uploadService,typeTemplateService,itemCatService){


    $controller('baseController',{$scope:$scope});
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

	$scope.checkValue=function(name,value){

		var objet=searchAttrName($scope.entity.goodsDesc.specificationItems,name);

		if(objet!=null){
			if(objet.attributeValue.indexOf(value)>=0){
				return true;
			}else return false;

/*			for(var i=0;i<objet['attributeValue'].length;i++){
				if(objet['attributeValue'][i]==value){
					return true;
				}
			}*/
		}
		return false;
	};

    //上传文件代码
	$scope.image_entity={};
	$scope.upload=function(){
		uploadService.upload().success(function (response) {
			if(response.message){
				//console.log(response.message);
				$scope.image_entity.url=response.message;
				//console.log("imageurl="+$scope.image_entity.url);
			}else {
				alert(response.message);
			}
		})
	};

	$scope.addToImageList=function(){
	    $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	    //console.log($scope.image_entity.url);
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
           $scope.typeList_3={};
       }) 
    });

	//监控第二个下拉框的改变
	$scope.$watch('entity.goods.category2Id',function (newValue,oldValue) {
		goodsService.findByParentId(newValue).success(function (response) {
			$scope.typeList_3=response;
		})
	});
	//监控第三个下拉框的改变，决定typeId
	$scope.$watch('entity.goods.category3Id',function (newValue,oldValue) {
		itemCatService.findOne(newValue).success(function (response) {
			//console.log(response);
			$scope.entity.goods.typeTemplateId=response.typeId;
		})
	});

	//监控typeId的改变，修改关于模板的所有数据
	$scope.$watch('entity.goods.typeTemplateId',function (newValue,oldValue) {

		if(newValue){
			console.log("newValue==="+newValue);
			console.log("id==="+$location.search()['id']);
			typeTemplateService.getSpecList(newValue).success(function (response) {
				$scope.specIds=response;
			});

			typeTemplateService.findOne(newValue).success(function (response) {
				//console.log(response);
				$scope.brandList=JSON.parse(response.brandIds);
				if($location.search()['id']==null){
					$scope.entity.goodsDesc.customAttributeItems=JSON.parse(response.customAttributeItems);
				}
			});

			if ($location.search()["id"] == null){
				$scope.entity.goodsDesc.specificationItems = [];
				$scope.entity.itemList=[];
			}
		}

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
		//console.log($scope.entity.goodsDesc.specificationItems);
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
	//深度克隆
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

	//清空页面中增加图片的的属性
	$scope.clear=function(){
	    $scope.image_entity={};
	    $("#file").val('');
    }




    







	
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
	    var id=$location.search()['id'];
	    if(id==null){
	        return;
        }
	    console.log(id);
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
                editor.html($scope.entity.goodsDesc.introduction);//将详情放入富文本编辑器中
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);//将字符串转换json对象
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                $scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
                //$scope.entity.itemList=JSON.parse($scope.entity.itemList);
				for(var i=0;i<$scope.entity.itemList.length;i++){
					$scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
				}
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
	};

	$scope.searchEntity={};//定义搜索对象

	//搜索
	$scope.search=function(page,rows){
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}
		);
	};

	//初始化状态数据结构
    $scope.status = ["未申请","申请中","审核通过","已驳回"];

    //初始化分类数据结构
    $scope.itemCatList=[];

    $scope.initCategory=function () {
        itemCatService.findAll().success(function (response) {

            for(var i=0;i<response.length;i++){
                $scope.itemCatList[response[i]['id']]=response[i]['name'];
            }

        })
    }

    
});	