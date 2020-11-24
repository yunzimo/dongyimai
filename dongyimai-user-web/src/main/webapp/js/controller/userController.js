 //用户表控制层 
app.controller('userController' ,function($scope,userService){


	$scope.sendCode=function(){

		//检查手机号格式
		var reg = /(1[3-9]\d{9}$)/;
		if($scope.entity.phone!=""&&$scope.entity.phone!=null){
			if(!reg.test($scope.entity.phone)){
				alert("请输入正确格式的手机号码！");
				return;
			}else {
				//发送验证码
				userService.sendSms($scope.entity.phone).success(function (response) {
					if(response.success){
						alert("验证码以发送成功，请注意接收");
					}
				})
			}
		}else {
			alert("请输入手机号！");
			return;
		}
	};

	$scope.reg=function(){

		//验证密码
		if($scope.entity.password==null||$scope.entity.password==""){
			alert("请输入密码");
		}else {
			if($scope.password!=$scope.entity.password){
				alert("两次密码不一致");
			}else {
				if($scope.entity.phone==null||$scope.entity.phone==""||$scope.smscode==null||$scope.smscode==""){
					alert("请输入完整的信息");
				}else {
					userService.add($scope.smscode,$scope.entity).success(function (response) {
						if(response.success){
							alert("注册成功");
						}else{
							alert(response.message);
						}
					})
				}
			}
		}
	};


    
});	