app.service('brandService',function ($http) {
    this.findOne=function(id){
        return  $http.post("../brand/findOne.do?id="+id);
    }

    this.findPage=function (pageNum,pageSize,key) {
        return $http.post("../brand/findPage.do?pageNum="+pageNum+"&pageSize="+pageSize,key);
    }
    this.delete=function (ids) {
        return  $http.post("../brand/delete.do?ids="+ids);
    }

    this.save=function (method,brand) {
        return  $http.post("../brand/"+method+".do",brand);
    }

    this.findBrandOption=function () {
        return $http.get("../brand/findBrandOptions.do");
    }

})