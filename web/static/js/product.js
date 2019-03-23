$(function () {

    //1.初始化Table
    var oTable = new TableInit();
    oTable.Init();

    //2.初始化Button的点击事件
    var oButtonInit = new ButtonInit();
    oButtonInit.Init();
    /////////////上方不改/////////////////////////
    //判断角色，显示采购入库还是显示销售出库的方法，将其模态框置空，刷入审核员，定义模态框显示的内容
    judgeAuthority();
    //初始化类别选择的ztree
    $.fn.zTree.init($("#treeDemo"), setting);
    zTree = $.fn.zTree.getZTreeObj("treeDemo");
});

/////////////下方是bootstrap-table的内容/////////////////////////////////
var TableInit = function () {
    var oTableInit = new Object();
    //初始化Table
    oTableInit.Init = function () {
        $('#tb_products').bootstrapTable({
            url: '/Product/GetProduct.do',         //请求后台的URL（*）
            method: 'get',                      //请求方式（*）
            contentType: "application/x-www-form-urlencoded",//post 必须制定类型，否则不能正常传值 #[老师]
            toolbar: '#toolbar',                //工具按钮用哪个容器
            striped: true,                      //是否显示行间隔色
            cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
            pagination: true,                   //是否显示分页（*）
            sortName: "id",                     //默认排序列#[老师]
            sortable: false,                     //是否启用排序
            sortOrder: "asc",                   //排序方式
            queryParams: oTableInit.queryParams,//传递参数（*）
            sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
            pageNumber: 1,                       //初始化加载第一页，默认第一页
            pageSize: 10,                       //每页的记录行数（*）
            pageList: [10, 25, 50, 100],        //可供选择的每页的行数（*）
            search: true,                       //是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
            strictSearch: true,
            showColumns: true,                  //是否显示所有的列
            showRefresh: true,                  //是否显示刷新按钮
            minimumCountColumns: 2,             //最少允许的列数
            clickToSelect: true,                //是否启用点击选中行
            height: 500,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
            uniqueId: "id",                     //每一行的唯一标识，一般为主键列
            showToggle: true,                    //是否显示详细视图和列表视图的切换按钮
            cardView: false,                    //是否显示详细视图
            detailView: false,                   //是否显示父子表
            columns: [{
                checkbox: true
            }, {
                field: 'typeName',
                title: '产品'
            }, {
                field: 'total',
                title: '库存'
            }, {
                field: 'price',
                title: '价格'
            }, {
                field: 'createTime',
                title: '类别创建时间'
            },]
        });
    };

    //得到查询的参数
    oTableInit.queryParams = function (params) {
        var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
            limit: params.limit,   //页面大小
            offset: params.offset,  //页码
            typeName: $("#txt_search_typeName").val(),
            // statu: $("#txt_search_statu").val()
        };
        return temp;
    };
    return oTableInit;
};


var ButtonInit = function () {
    var oInit = new Object();
    var postdata = {};

    oInit.Init = function () {
        //初始化页面上面的按钮事件
        $("#btn_query").bind("click", query);//查询按钮
        $("#btn_add").bind("click",productModalShow);//入库
        $("#btn_minus").bind("click",productModalShow);//出库
        $("#btn_save").bind("click",orderSubmit);//提交审核
        $("#btn_choose").bind("click",chooseTypeOpen);//类别选择按钮
        $("#btn_type_save").bind("click",chooseType);//类别选择确定

    };

    return oInit;
};

//查询
function query() {
    $("#tb_products").bootstrapTable('refresh');
}

//控制【采购入库】【销售出库】显示，
var orderType=0;
//既然已经指代要显示什么按钮，就已经指代是出库还是入库，那么就可以再下面方法直接指定这个全局变量是出库还是入库
//roderType用来标识是入库还是出库，这里先给初始值0，后续1表示入库，2表示出库
function judgeAuthority() {//控制两个按钮的显示,不需要传参，登录就已经限定了用户的权限
    $.ajax({
        url: '/Product/GetRoleList.do',
        type: 'post',
        data: {},
        dataType: 'json',
        success: function (roleList) {
            for (var i = 0; i < roleList.length; i++) {
                var flag = roleList[i].flag;
                console.log(flag);
                if (flag == 'sale') {//销售
                    orderType=2;
                    $("#btn_add").css("display", "none");//采购入库按钮不显示
                    $('#productModalLabel').html("销售出库");
                    $('#inputPriceLabel').html("销售单价");
                    $('#inputNumberLabel').html("销售数量");
                } else if (flag == 'purchase') {//采购
                    orderType=1;
                    $("#btn_minus").css("display", "none");//销售按钮不显示
                    $('#productModalLabel').html("采购入库");
                    $('#inputPriceLabel').html("采购单价");
                    $('#inputNumberLabel').html("采购数量");
                } else if(flag == 'admin'){
                    $("#btn_add").css("display", "none");
                    $("#btn_minus").css("display", "none");
                }else {
                    $("#btn_add").css("display", "none");
                    $("#btn_minus").css("display", "none");
                }
            }
            //加载审核员到审核原输入框
            GetAuditor()
        }
    });
}
//加载审核员到审核原输入框
function GetAuditor() {
    $.ajax({
        url:'/Product/GetAuditor.do',
        data:{},
        dataType:'json',
        type:'post',
        success:function (auditors) {
            var options='';
            $(auditors).each(function () {
                var option='';
                //定义自定义属性auditorId=，便于后面获取，value也是id 注释了
                option+='<option auditorId="'+this.id+'">'+this.name+'</option>'
                options+=option;
            })
            $("#selectAuditor").html(options);
        }
    });
}
//模态框显示方法
function productModalShow(){
    $('#productModal').modal('show');
    //清空类别数据
    $('#inputTypeName').val("");
    $('#inputTypeId').val("");
    $('#inputNumber').val("");
    $('#inputPrice').val("");
}
//出入库单据提交审核
var orderSubmit = function(){
    var typeId =  $('#inputTypeId').val();//类别id
    var number = $('#inputNumber').val();//数量
    var price = $('#inputPrice').val();//价格
    var auditorId=$("#selectAuditor option:selected").attr("auditorId");//审核员Id---userId
    console.log(typeId+"------------")
    if(typeId=="" || typeId==null ||price=="" ||price==null||number==""||number==null){
        return ;
    }
    $('#btn_save').attr('disabled',"true");
    $.ajax({
        url:"/jxcProductOrder/creatTask.do",
        type:"post",
        data:{
            typeId:typeId,
            orderType:orderType,
            number:number,
            price:price,
            auditorId:auditorId
        },
        dataType:"json",
        success:function(info){
            $('#btn_save').removeAttr("disabled");
            if(info&&info.flag){
                console.log("Insert and start one procinst");
            }else {
                alert(info.errorMsg)
            }
            $('#productModal').modal('hide');
        }
    })
}

//类别选择窗口打开
var chooseTypeOpen = function(){
    $('#secondmodal').modal("show");
}
var chooseType = function(){
    //如果没选择类别，不让点击确定
    if(gNode == null){
        alert("请选择一个类别")
        return;
    }
    $('#inputTypeName').val(gNode.name);
    $('#inputTypeId').val(gNode.id);
    $('#secondmodal').modal("hide");
}
/////////////////下方模态框中选择产品类别的js/////////////////////////////////////
// 产品类别树
var zTree;
//单选的时候，保存为全局变量
var gNode;
var setting = {
    async: {
        enable: true,
        url: "/type/init.do",
        dataType:"json"
    },
    data: {
        simpleData: {
            enable: true,
            idKey: "id",
            pIdKey: "pId",
        }
    },
    view: {
        dblClickExpand: false
    },
    callback: {
        onAsyncSuccess: zTreeOnAsyncSuccess,
        onClick:zTreeOnClick
    }
}
function zTreeOnAsyncSuccess(event, treeId, msg) {
    /*
      event js event 对象 标准的 js event 对象
      treeId String 对应 zTree 的 treeId，便于用户操控
      treeNode JSON 进行异步加载的父节点 JSON 数据对象 针对根进行异步加载时，treeNode = null
      msg String / Object 异步获取的节点数据字符串，主要便于用户调试使用。
      实际数据类型会受 setting.async.dataType 的设置影响，请参考 JQuery API 文档。
    */
    //异步加载完后展开到第二级节点
    var nodes = zTree.getNodes();
    for (var i = 0; i < nodes.length; i++) { //设置节点展开
        zTree.expandNode(nodes[i], true, false, true);
    }
}
function zTreeOnClick(event, treeId, treeNode) {
    //父级节点不允许选择
    if(!treeNode.isParent){
        gNode = treeNode;
    }else{
        gNode = null;
    }
};
