<%@ page language="java"  pageEncoding="utf-8"%>
<script type="text/javascript" src="${jslib}/requireJs/require.js" data-main=""></script>
<script type="text/javascript">
	require.config({
		baseUrl : CTX + '/jssrc',
		paths : {
			'jquery' : JSLIB + '/jquery-1.12.2/jquery-1.12.2.min',
			'echarts' : JSLIB + '/echarts/echarts-all',
			'text' : JSLIB + '/requireJs/plugins/text'//text plugin
		},
		shim : {
			echarts : {
				exports : 'exports'//echarts.echarts and echarts.zrender
			}
		}
	});
</script>