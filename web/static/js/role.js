$(document).ready(function () {
    //加载所有校色并分页
    loadRolePage('', null, null);
    //指定click方法
    $("#btn_add").bind("click", openAdd);
    //指定编辑新增模态框提交的方法
    $("#modal_save").bind("click", add);
    //指定关联菜单功能的模态框save按钮对应的方法
    $("#associateMenu").bind("click", associateMenu);

    $('#myModal').on('hidden.bs.modal', modalHiddenEvent);//模态框隐藏触发
    $('#associateMenuModal').on('hidden.bs.modal', menuModalHiddenEvent);//模态框隐藏触发

    //加载关联信息到加载ztree模态款框里
    $.fn.zTree.init($("#treeDemo"), setting);
    zTree = $.fn.zTree.getZTreeObj("treeDemo");
});
//查询功能
var search_name = '';
$("#btn_query").click(function () {
    search_name = $("#search_name").val();
    loadRolePage(search_name, null, myPageSize);
});
//设置分页大小功能
var myPageSize = 0;
$("#pz_btn").click(function () {
    myPageSize = $("#pageSize").val();
    loadRolePage(search_name, null, myPageSize);
});

function loadRolePage(name, currentPage, pageIze) {
    $.ajax({
        url: '/role/queryRolePageBean.do',
        type:'post',
        data: {
            name: name,
            currentPage: currentPage,
            pageSize: pageIze
        },
        dataType: 'json',
        success: function (pb) {
            $("#totalPage").html(pb.totalPage);
            $("#totalCount").html(pb.totalCount);
            //调用表格拼接方法
            loadToTable(pb.list);
            //调用分页方法
            loadPageBar(pb, name);
        }
    })
}

//loadTable
//遍历数据list数据
function loadToTable(list) {
    var trs = "";
    $(list).each(function (index, el) {
        var style = ""
        if (index % 5 == 0) {
            style = "active"
        }
        if (index % 5 == 1) {
            style = "success"
        }
        if (index % 5 == 2) {
            style = "info"
        }
        if (index % 5 == 3) {
            style = "warning"
        }
        if (index % 5 == 4) {
            style = "danger"
        }

        var temp = ""
        temp = "<a href='javascript:void(0)' onclick=edit('" + this.id + "','" + this.name + "','" + this.flag + "')>编辑</a>  &nbsp;&nbsp;";
        temp += "<a href='javascript:void(0)' onclick=dele('" + this.id + "')>删除</a>   &nbsp;&nbsp;";
        temp += "<a href='javascript:void(0)' onclick=menuJoin('" + this.id + "','" + this.name + "')>关联菜单</a>  ";

        var tr = "";
        tr = "<tr class='" + style + "'>" +
            "<td><input type='checkbox' name='id' ck value='" + this.id + "' /></td>" +
            "<td>" + this.name + "</td>" +
            "<td>" + this.flag + "</td>" +
            "<td>" + temp + "</td>" +
            "</tr>"
        trs += tr
    });
    $("#tbody").html(trs)
}

//分页条方法
function loadPageBar(pb, name) {
    var lis = ""
    var fristPage = '<li><a href="javascript:loadRolePage(\'' + name + '\',' + 1 + ',' + pb.pageSize + ')">首页</a></li>';
    //计算上一页的页码
    var beforeNum = pb.currentPage - 1;
    if (beforeNum <= 0) {
        beforeNum = 1;
    }
    var beforePage = '<li><a href="javascript:loadRolePage(\'' + name + '\',' + beforeNum + ',' + pb.pageSize + ')" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>'
    lis += fristPage;
    lis += beforePage;
    //1.2 展示分页页码
    /*
        1.一共展示10个页码，能够达到前5后4的效果
        2.如果前边不够5个，后边补齐10个
        3.如果后边不足4个，前边补齐10个
    */
    // 定义开始位置begin,结束位置 end
    var begin; // 开始位置
    var end; //  结束位置
    //1.要显示10个页码
    if (pb.totalPage < 10) {
        //总页码不够10页
        begin = 1;
        end = pb.totalPage;
    } else {
        //总页码超过10页
        begin = pb.currentPage - 5;
        end = pb.currentPage + 4;
        //2.如果前边不够5个，后边补齐10个
        if (begin < 1) {
            begin = 1;
            end = begin + 9;
        }
        //3.如果后边不足4个，前边补齐10个
        if (end > pb.totalPage) {
            end = pb.totalPage;
            begin = end - 9;
        }
    }
    for (var i = begin; i <= end; i++) {
        var li = '';
        //判断当前页码是否等于i
        if (pb.currentPage == i) {
            li = '<li class="active"><a href="javascript:loadRolePage(\'' + name + '\',' + i + ',' + pb.pageSize + ')">' + i + '</a></li>';
        } else {
            //创建页码的li
            li = '<li><a href="javascript:loadRolePage(\'' + name + '\',' + i + ',' + pb.pageSize + ')">' + i + '</a></li>';
        }
        //拼接字符串
        lis += li;
    }
    var nextNum = pb.currentPage + 1;
    if (nextNum > pb.totalPage) {
        nextNum = pb.totalPage;
    }
    var nextPage = '<li><a href="javascript:loadRolePage(\'' + name + '\',' + nextNum + ',' + pb.pageSize + ')" aria-label="Next"><span aria-hidden="true">&raquo;</span></a></li>';
    var lastPage = '<li><a href="javascript:loadRolePage(\'' + name + '\',' + pb.totalPage + ',' + pb.pageSize + ')">末页</a></li>';
    lis += nextPage;
    lis += lastPage;
    $("#pageBar").html(lis);
}

//添加功能
//打开新增角色的模态框
function openAdd() {
    $('#myModal').modal('show');
    $('#myModalLabel').html("新增角色");
}

/**
 * 模态框隐藏后 清空form表单数据
 */
function modalHiddenEvent() {
    $('#modal-form')[0].reset();
}

//新增角色(老师)
function add() {
    saveOrUpdate();
}

//编辑角色(老师)
function edit(id, name, flag) {
    $("#roleId").val(id);
    $("#roleName").val(name);
    $("#roleFlag").val(flag);
    $('#myModal').modal('show');
    $('#myModalLabel').html("编辑角色");
}

//更新编辑（老师）
function saveOrUpdate() {
    var name = $("#roleName").val();
    var flag = $("#roleFlag").val();
    if (name == "" || name == null) {
        return;
    }
    if (flag == "" || flag == null) {
        return;
    }
    $("#m_btn").attr('disabled', "true");
    $.ajax({
        type: 'post',
        url: '/role/edit.do',
        data: {
            id: $("#roleId").val(),
            name: name,
            flag: flag,
        },
        dataType: "json",
        success: function (data) {
            $('#m_btn').removeAttr("disabled");
            if (data.flag) {
                $('#myModal').modal('toggle');
                //老师用插件的方式将table刷新，
                // $("#tb_role").bootstrapTable('refresh');
                //那我通过调用document.ready里面拼接方法吧
                loadRolePage('', null, null);
            } else {
                alert(data.errorMsg);
            }
        }
    });
}

//删除（老师）
function dele(id) {
    if (!confirm('确定删除吗 ？')) {
        return;
    }
    $.ajax({
        type: 'post',
        data: {id: id},
        url: '/role/delete.do',
        dataType: "json",
        success: function (data) {
            if (data.flag) {
                //重新加载页面上的表格
                loadRolePage('', null, null);
            } else {
                alert(data.errorMsg)
            }
        }
    });
}

////////////菜单相关//////////////////////////////
var roleId = "";

function menuJoin(id, roleName) {
    roleId = id;
    $('#associateModalLabel').html(roleName + "-关联菜单");
    //打开菜单模态框
    $('#associateMenuModal').modal('show');
    //查询该角色的所有菜单，并设置树为选中状态
    //更好的做法：在查询角色的时候，就将角色的菜单关联出来，此处就可以不用去查询数据库
    $.ajax({
        type: 'post',
        url: '/menu/findByRoleId.do',
        data: {roleId: id},
        dataType: "json",
        success: function (data) {
            if (data.flag) {
                $(data.data).each(function () {
                    zTree.checkNode(zTree.getNodeByParam("id", this.id, null), true, false);
                });
            } else {
                alert(data.errorMsg);
            }
        }
    });
}

//关联角色更新数据库  获得这个角色的id，传递到后台删除
//
function associateMenu() {
    var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
    var nodes = treeObj.getCheckedNodes(true);
    var ids = "null,";//传到后台遍历时同构equals("null") continue;跳过，为了避免真null的出现吧也许
    $.each(nodes, function () {
        ids += this.id + ","
    });
    console.log(ids)
    $.ajax({
        url: '/role/roleAssociateMenu.do',
        dataType: 'json',
        data: {
            roleId: roleId,
            menuIds: ids
        },
        success: function (result) {
            $("#associateMenuModal").modal("toggle");
            if (result.flag) {
                alert("关联好了！");
            } else {
                alert(result.errorMsg);
            }
        }
    });
}


//关闭模态框，取消所有的选中项
function menuModalHiddenEvent() {
    zTree.checkAllNodes(false);
    roleId = "";
}

//下方为模态框显示显示ztree
var setting = {
    //加载菜单ztree到模态框里面
    async: {
        enable: true,
        url: "/menu/allMenu.do",
        dataType: "json"
        //autoParam: ["id", "name"]
    },
    check: {
        enable: true
    },
    data: {
        simpleData: {
            enable: true,
            idKey: "id",
            pIdKey: "pid"
        },
        key: {
            name: "text",
            // children: "menus"
        }
    },
    callback: {
        onAsyncSuccess: zTreeOnAsyncSuccess,
        onClick: zTreeOnClick
    }
};

function zTreeOnClick(event, treeId, treeNode) {
    //选中的时候，关联check方法
    zTree.checkNode(treeNode, !treeNode.checked, true);
}

function zTreeOnAsyncSuccess(event, treeId, msg) {
    //异步加载完后展开到第二级节点
    /*
      event js event 对象 标准的 js event 对象
      treeId String 对应 zTree 的 treeId，便于用户操控
      treeNode JSON 进行异步加载的父节点 JSON 数据对象 针对根进行异步加载时，treeNode = null
      msg String / Object 异步获取的节点数据字符串，主要便于用户调试使用。
      实际数据类型会受 setting.async.dataType 的设置影响，请参考 JQuery API 文档。
    */
    var nodes = zTree.getNodes();
    for (var i = 0; i < nodes.length; i++) { //设置节点展开
        zTree.expandNode(nodes[i], true, false, true);
    }
}

function filter(treeId, parentNode, childNodes) {
    //将子节点的名字按正则要求去除无用字符，我的name是text
    if (!childNodes)
        return null;
    for (var i = 0, l = childNodes.length; i < l; i++) {
        childNodes[i].text = childNodes[i].text.replace(/\.n/g, '.');
    }
}






