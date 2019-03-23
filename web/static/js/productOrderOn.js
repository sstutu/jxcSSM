$(function () {

    //1.初始化Table
    var oTable = new TableInit();
    oTable.Init();

    //2.初始化Button的点击事件
    var oButtonInit = new ButtonInit();
    oButtonInit.Init();


});

var TableInit = function () {
    var oTableInit = new Object();
    //初始化Table
    oTableInit.Init = function () {
        $('#tb_productOrdersOn').bootstrapTable({
            url: '/jxcProductOrder/GetProductOrderOn.do',         //请求后台的URL（*）
            method: 'post', // 请求方式（*）
            contentType: "application/x-www-form-urlencoded",//post 必须制定类型，否则不能正常传值
            toolbar: '#toolbar',                //工具按钮用哪个容器
            striped: true,                      //是否显示行间隔色
            cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
            pagination: true,                   //是否显示分页（*）
            sortName: "createDate",             //默认排序列
            sortable: true,                     //是否启用排序
            sortOrder: "desc",                   //排序方式
            queryParams: oTableInit.queryParams,//传递参数（*）
            sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
            pageNumber: 1,                       //初始化加载第一页，默认第一页
            pageSize: 10,                       //每页的记录行数（*）
            pageList: [10, 15, 20, 50],        //可供选择的每页的行数（*）
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
                field: 'number',
                title: '数量'
            }, {
                field: 'price',
                title: '单价'
            }, {
                field: 'createDate',
                title: '申请时间',
                //——修改——获取日期列的值进行转换
                formatter: function (value, row, index) {
                    return jsonDateFormat(value)
                }
            }, {
                field: 'status',
                title: '状态',
                formatter: function (data) {
                    var t = "-";
                    if (data == 1) {
                        t = "在途";
                    } else if (data == 2) {
                        t = "驳回";
                    }
                    return t;
                }
            }, {
                field: 'id',
                title: '操作',
                formatter: function (value, row, index) {//value:代表当前单元格中的值，row：代表当前行,index:代表当前行的下标,
                    var temp = ""
                    if (row.status == 1) {//在途，可以撤销
                        temp = "<a href='javascript:void(0)' onclick=deleteOrder('" + value + "')>撤销</a>";
                    } else if (row.status == 2) {//被驳回，修改重新提交
                        temp = "<a href='javascript:void(0)' onclick=deleteOrder('" + value + "')>撤销</a>";
                        temp += "&nbsp;&nbsp; <a href='javascript:void(0)' onclick=modifyOrder('" + value +
                            "','" + row.typeName + "','" + row.number + "','" + row.price + "')>修改</a>  ";
                        temp += "&nbsp;&nbsp; <a href='javascript:void(0)'  data-toggle='popover'" +
                            "data-content='" + row.remark + "' data-placement='left' >查看原因</a>";
                    }
                    return temp;
                }
            }],
            onLoadSuccess: function () {
                //控制弹出框的显示，加载完之后执行
                $('[data-toggle="popover"]').popover()
                    .on("mouseenter", function () {//绑定鼠标事件
                        var _this = this;
                        $(this).popover("show");
                        $(this).siblings(".popover").on("mouseleave", function () {
                            $(_this).popover('hide');
                        });
                    }).on("mouseleave", function () {
                    var _this = this;
                    setTimeout(function () {
                        if (!$(".popover:hover").length) {
                            $(_this).popover("hide")
                        }
                    }, 100);
                });
            }
        });
    };

    //得到查询的参数
    oTableInit.queryParams = function (params) {
        var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
            limit: params.limit,   //页面大小
            offset: params.offset,  //页码
            search: params.search,//还不知道上面用途
            // sort: params.sort,//排序字段
            // order: params.order,//排序类型 desc 还是asc
            typeName: $("#txt_search_name").val(),//搜索内容框的值
            orderType: orderType
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
        $("#btn_save").bind("click", modify);//提交修改
    };

    return oInit;
};

function jsonDateFormat(jsonDate) {
    //json日期格式转换为正常格式
    var jsonDateStr = jsonDate.toString();//此处用到toString（）是为了让传入的值为字符串类型，目的是为了避免传入的数据类型不支持.replace（）方法
    try {
        var k = parseInt(jsonDateStr.replace("/Date(", "").replace(")/", ""), 10);
        if (k < 0)
            return null;

        var date = new Date(parseInt(jsonDateStr.replace("/Date(", "").replace(")/", ""), 10));
        var month = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1;
        var day = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
        var hours = date.getHours() < 10 ? "0" + date.getHours() : date.getHours();
        var minutes = date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes();
        var seconds = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();
        var milliseconds = date.getMilliseconds();
        return date.getFullYear() + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds;
    }
    catch (ex) {
        return "时间格式转换错误";
    }
}

var orderType = 0;
//查询
var query = function () {
    $("#tb_productOrdersOn").bootstrapTable('refresh');
}
//撤销订单
var deleteOrder = function (id) {
    $.ajax({
        url: "/jxcProductOrder/deleteOrder.do",
        type: "post",
        data: {
            orderId: id
        },
        dataType: "json",
        success: function (info) {
            $("#tb_productOrdersOn").bootstrapTable('refresh');
            if (!info.flag) {
                alert(info.errorMsg);
            }
        }
    })
}


var modifyId;
//修改订单
var modifyOrder = function (id, name, number, price) {
    modifyId = id;
    $('#orderModal').modal("show");
    GetAuditor()
    $("#orderModalLabel").html(name);
    $("#inputNumber").val(number);
    $("#inputPrice").val(price);
    //审核员输入框未做重新赋值
}

//加载审核员到审核原输入框
function GetAuditor() {
    $.ajax({
        url: '/Product/GetAuditor.do',
        data: {},
        dataType: 'json',
        type: 'post',
        success: function (auditors) {
            var options = '';
            $(auditors).each(function () {
                var option = '';
                //定义自定义属性auditorId=，便于后面获取，value也是id 注释了
                option += '<option auditorId="' + this.id + '">' + this.name + '</option>'
                options += option;
            })
            $("#selectAuditor").html(options);
        }
    });
}

var modify = function () {

    $.ajax({
        url: "/jxcProductOrder/modifyOrder.do",
        type: "post",
        data: {
            id: modifyId,
            number: $("#inputNumber").val(),
            price: $("#inputPrice").val(),
            auditorId: $("#selectAuditor option:selected").attr("auditorId")//审核员Id---userId  做重新提交的认领人
        },
        dataType: "json",
        success: function (info) {
            $('#orderModal').modal("hide");
            $("#tb_productOrdersOn").bootstrapTable('refresh');
            if (!info.flag) {
                alert(info.errorMsg);
            }
        }
    })
}