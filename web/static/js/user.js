//初始化
$(document).ready(function () {
    //加载数据拼成表格，生成分页工具条
    loadUserPageBean('', null, null);
    $("#btn_query").bind("click",query);
    $("#pz_btn").bind("click",setPageSize);
    $("#btn_add").bind("click", openAdd);
    $("#modal_save").bind("click", add);
    $("#association_btn").bind("click", associationRole);
    $('#myModal').on('hidden.bs.modal', modalHiddenEvent);//模态框隐藏触发
    $('#associateRoleModal').on('hidden.bs.modal', roleModalHiddenEvent);//模态框隐藏触发
    initRoles();
});

//查询功能
var search_name = '';
function query() {
    search_name = $("#search_name").val();
    if(search_name=="undefined"){
        search_name='';
    }
    loadUserPageBean(search_name, null, myPageSize);
}
//设置分页大小功能
var myPageSize = 5;
function setPageSize() {
    myPageSize = $("#pageSize").val();
    if(myPageSize=="undefined"){
        search_name=5;
    }
    loadUserPageBean(search_name, null, myPageSize);
}

//loadUserPageBean()
function loadUserPageBean(name, currentPage, pageIze) {
    $.ajax({
        url: '/user/queryUserPageBean.do',
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
            loadPageBar(pb, name,pageIze);
        }
    });
}
//定义日期转换方法

function formatDate(date) {
    var d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;
    return [year, month, day].join('-');
}

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
        temp = "<a href='javascript:void(0)' onclick=edit('" + this.id + "','" + this.account + "','" + this.name + "','" +this.birthday + "')>编辑</a>  &nbsp;&nbsp;";
        temp += "<a href='javascript:void(0)' onclick=dele('" + this.id + "')>删除</a>   &nbsp;&nbsp;";
        temp += "<a href='javascript:void(0)' onclick=roleJoin('" + this.id + "','" + this.account + "')>关联菜单</a>  ";
        var tr = "";
        tr = "<tr class='" + style + "'>" +
            "<td><input type='checkbox' name='id' ck value='" + this.id + "' /></td>" +
            "<td>" + this.account + "</td>" +
            "<td>" + this.name + "</td>" +
            "<td>" +formatDate(this.birthday)+ "</td>" +
            "<td>" + temp + "</td>" +
            "</tr>"
        trs += tr
    });
    $("#tbody").html(trs)
}

//分页条方法
function loadPageBar(pb, name,pageIze) {
    var lis = ""
    var fristPage = '<li data-option="' + 1 + '"><a href="javascript:void(0)">首页</a></li>';
    //计算上一页的页码
    var beforeNum = pb.currentPage - 1;
    if (beforeNum <= 0) {
        beforeNum = 1;
    }
    var beforePage = '<li data-option="' + beforeNum + '"><a href="javascript:void(0)" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>'
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
        if(pb.currentPage == i) {
            li = '<li class="active" data-option="' + i + '"><a href="javascript:void(0)">' + i + '</a></li>';
        }else {
            li = '<li data-option="' + i + '"><a href="javascript:void(0)">' + i + '</a></li>';
        }
        //拼接字符串
        lis += li;
    }
    var nextNum = pb.currentPage + 1;
    if (nextNum > pb.totalPage) {
        nextNum = pb.totalPage;
    }
    var nextPage = '<li data-option="' + nextNum + '"><a href="javascript:void(0)" aria-label="Next"><span aria-hidden="true">&raquo;</span></a></li>';
    var lastPage = '<li data-option="' + pb.totalPage + '"><a href="javascript:void(0)">末页</a></li>';
    lis += nextPage;
    lis += lastPage;
    $("#pageBar").html(lis);

    //第一页时禁用上一页
    if(pb.currentPage==1){
        //相当于第一个和第二个li添加样式
        $("#pageBar li").slice(0,2).addClass("disabled");
    }
    //最后一页时禁用下一页
    if(pb.currentPage==pb.totalPage){
        // 倒数第二个
        $("#pageBar li").eq(-2).addClass("disabled");
        // 最后一个
        $("#pageBar li:last").addClass("disabled");
    }

    //分页点击事件
    $("#pageBar li").click(function(){
        if($(this).hasClass('disabled')||$(this).hasClass('active')){
            return;
        }
        loadUserPageBean(name, $(this).attr("data-option"),pageIze);
    });
}


//打开新增角色的模态框
function openAdd() {
    $('#myModal').modal('show');
    $('#myModalLabel').html("新增用户");
}

function modalHiddenEvent() {
    $('#modal-form')[0].reset();
    // $('#datetimepicker').datetimepicker('update',"");
}

function add() {
    saveOrUpdate();
}

//编辑角色
function edit(id,account,name,birthday) {
    $("#userId").val(id);
    $("#userAccount").val(account);
    $("#userName").val(name);
    if (birthday == "undefined") birthday = "";
    $("#userBirth").val(birthday);

    $('#myModal').modal('show');
    $('#myModalLabel').html("编辑用户");
}

function saveOrUpdate() {
    var account = $("#userAccount").val();
    var name = $("#userName").val();
    var birthday = $("#userBirthday").val();

    if (account == "" || account == null) {
        return;
    }
    if (name == "" || name == null) {
        return;
    }
    $("#modal_save").attr('disabled', "true");
    $.ajax({
        type: 'post',
        url: '/user/edit.do',
        data: {
            id: $("#userId").val(),
            account: account,
            name: name,
            birthday:birthday
        },
        dataType: "json",
        success: function (data) {
            $('#modal_save').removeAttr("disabled");
            if (data.flag) {
                $('#myModal').modal('toggle');
                //老师用插件的方式将table刷新，
                // $("#tb_role").bootstrapTable('refresh');
                //那我通过调用document.ready里面拼接方法吧
                loadUserPageBean('', null, null);
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
        url: '/user/delete.do',
        dataType: "json",
        success: function (data) {
            if (data.flag) {
                //重新加载页面上的表格
                alert("删除成功")
                loadUserPageBean('', null, null);
            } else {
                alert(data.errorMsg)
            }
        }
    });
}

//将关联菜单的模态框在document.ready(function(){});时之后初始化到模态框的<select>标签下。加载的时全部
function initRoles() {
    $.ajax({
        type: 'post',
        url: '/role/queryAllRole.do',
        dataType: "json",
        success: function (data) {
            var html = "";
            $(data).each(function () {
                html += "<option value='" + this.id + "'>" + this.name + "</option>";
            });
            $('#userRole').html(html);
            $('#userRole').selectpicker('refresh');
        }
    });
}

//通过用户id来找出对应的角色，在上方加载角色到select下的optin
function roleJoin(id,account) {
    $('#userIdRole').val(id)
    $("#associateModalLabel").html(account+"：关联角色")
    $('#associateRoleModal').modal('show');
    $.ajax({
        type: 'post',
        url: '/role/getByUserId.do',
        data:{
            userId:id,
        },
        dataType: "json",
        success: function (data) {
            if (data.flag) {
                var arr = [];
                $(data.data).each(function () {
                    arr.push(this.id);
                });
                //用户对应的角色id通过数字的形式通过selectpicher 设置进去
                $('#userRole').selectpicker('val', arr);
            } else {
                alert(data.errorMsg)
            }
        }
    });
}

function roleModalHiddenEvent() {
    $('#roleForm')[0].reset();
    $('#userRole').selectpicker('deselectAll');
    $("#userRole").selectpicker('refresh');
}

function associationRole() {
    var roleSelect = $('#userRole').val();
    console.log(roleSelect)
    //roleJoin(id)处已经对$('#userIdRole').val();进行了赋值，模态框显示的状态未变化，所以这里可以直接获取它的值
    var userId = $('#userIdRole').val();
    if (roleSelect == null) {
        alert("请至少关联一个角色")
        return;
    }
    $.ajax({
        type: 'post',
        url: '/user/associationRole.do',
        dataType: "json",
        data: {
            userId: userId,
            roleArr: roleSelect
        },
        success: function (data) {
            if (data.flag) {
                //切换模态框显示状态
                $('#associateRoleModal').modal('toggle');
                alert("关联好了!")
            } else {
                alert(data.errorMsg)
            }
        }
    });
}