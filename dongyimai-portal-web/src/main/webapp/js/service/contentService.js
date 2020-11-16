app.service('contentService', function ($http) {

    //根据分类编号找广告
    this.findByCategoryId=function (categoryId) {
        return $http.post('../content/findByCategoryId.do?categoryId='+categoryId);
    }
});