//用户表服务层
app.service('userService',function($http){

	//发送验证码
	this.sendSms=function (mobile) {
		return $http.post('../user/sendSms.do?mobile='+mobile);
	};
	
	//增加
	this.add=function(code,entity){
		return $http.post('../user/add.do?smscode='+code,entity);
	}	

});