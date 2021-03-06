var sqlApp=angular.module('sqlApp', []);
sqlApp.controller(
		'serverCtrl',
		function($scope, $http,$interval,$location) {
			//$scope.url='http://'+$location.host()+':'+$location.port()+'/master/';
			$scope.responseBody='';
			$scope.requestBody={maxSize:'100',dbid:'defJt',sql:''};
			$scope.queryType='query';
			$scope.qts=['query','update'];
			$scope.table='';
			$scope.tables=[];
			$scope.reinit=function(){
				$scope.data={};
				$scope.datas=[{}];
				$scope.responseBody='';
			};
			$scope.dss=[];
			$http.post("../sml/cmd","#{sqlMarkupAbstractTemplate.dss.keySet()}",{'Content-Type':'application/json'}).then(function(response){$scope.dss=response.data;});
			$scope.dbchange=function(){
				$http.get("../jdbc/metadata/table?dbid="+$scope.requestBody.dbid).then(function(response){$scope.tables=response.data;});
			};
			$scope.reinit();
			$scope.dbchange();
			$scope.execute=function(){
				$scope.reinit();
				$http.post("../jdbc/"+$scope.queryType,angular.toJson($scope.requestBody),{'Content-Type':'application/json'})
				.then(function(response){
					if($scope.queryType=='update'){
						$scope.datas=[{result:angular.toJson(response.data)}];
					}else{
						$scope.datas=response.data;
					}
					$scope.data=$scope.datas[0];
				},function(data){
					$scope.responseBody=data.data;
				});
			}
		});