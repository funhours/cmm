$(function() {
    $('#example').DataTable({
        //searching: false, //禁用搜索
        //ordering:  false, //禁用排序
        //"processing": true, //进度,开启后会闪动
        "serverSide": true, //启用服务模式
        "ajax": {
            "url": getCtxPath() + "/admin/ajax_data_tables",
            //当处理大数据时，延迟渲染数据，有效提高Datatables处理能力
            "deferRender": true
        },
        "columns": [
            { "data": "id" },
            { "data": "title" },
            { "data": "content" },
            { "data": null }
        ],
        columnDefs: [
            {
                 targets: 3, //第四列，从0开始
                 render: function (a, b, c, d) {
                     // 这块可以采用我之前讲到的js模板处理：http://laytpl.layui.com/
                     var html = '<button data-id="#tpl_edit" data-action="' + getCtxPath() + '/admin/blog_edit/' + c.id + '" type="button" class="js_edit am-btn am-btn-xs am-btn-primary">修改</button>'
                              + '<button data-action="' + getCtxPath() + '/admin/blog_del/' + c.id + '" type="button" class="js_del am-btn am-btn-xs am-btn-danger">删除</button>';
                      return html;
                 }
            }
         ]
    });
    $('#example').on("click", ".js_edit", function(e){
        var action = $(this).data("action");
        var $view  = $($(this).data("id"));
        var width  = $view.data("width");
        var height = $view.data("height");
        $.getJSON(action, function(data){
            laytpl($view.html()).render(data, function(html){
                layer.open({
                    type: 1,
                    title: '<span class="am-icon-file-text-o"></span> 编辑',
                    scrollbar: false,
                    skin: 'layui-layer-rim',
                    area: [width == null ? '680px' : width, height == null ? '420px' : height],
                    content: html
                });
            });
        });
    });
    $('#example').on("click", ".js_del", function(e){
        var action = $(this).data("action");
        alert(action);
//        $.getJSON(action, function(data) {
//            
//        });
    });
});