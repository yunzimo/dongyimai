app.service('uploadService',function ($http) {
    this.upload=function () {
        var formDate=new FormData();
        formDate.append("file",file.files[0]);

        return $http({
            method:"post",
            url:"../upload.do",
            data:formDate,
            headers:{"Content-Type":undefined},
            transformRequest:angular.identity
        });
    }

    // $http({
    //         method:"post",                      表单提交方式 post方式上传图片
    //         url:"../upload.do",                 表单提交方向action ../upload.do
    //         data:formData,                      表单正文内容 formData
    //         headers:{"Content-Type":undefined}, angularjs默认提交请求是以application/json格式提交,需要设置为undefinded
    //         transformRequest:angular.identity   表单数据序列化
    //     });
})