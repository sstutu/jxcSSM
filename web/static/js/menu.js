var rMenu;//树的右键菜单
var zTree;//接收ztree对象
var gNode;//接收选中节点对象
var setting = {
    async: {
        enable: true,
        url: "/menu/allMenu.do",
        dataType:"json"
        //autoParam: ["id", "name"]
    },
    data: {
        simpleData: {
            enable: true,
            idKey: "id",
            pIdKey: "pid",
        },
        key: {
            name: "text",
            // children: "menus"
        }
    },
    view: {
        dblClickExpand: false
    },
    check: {
        // enable: true
    },
    callback: {
        onRightClick: OnRightClick,
        onAsyncSuccess: zTreeOnAsyncSuccess
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

function filter(treeId, parentNode, childNodes) {
    if (!childNodes)
        return null;
    for (var i = 0, l = childNodes.length; i < l; i++) {
        childNodes[i].name = childNodes[i].name.replace(/\.n/g, '.');
    }
    return childNodes;
}
$(document).ready(function(){
    $.fn.zTree.init($("#treeDemo"), setting);
    zTree = $.fn.zTree.getZTreeObj("treeDemo");
    rMenu = $("#rMenu");
    $('#myModal').on('hidden.bs.modal', modalHiddenEvent);//模态框隐藏触发
    $("#m_btn").bind("click",saveOrUpdateNode);//指定模态框saveButtion对应的方法
});
function OnRightClick(event, treeId, treeNode) {//右击方法，在开头的setting里已经做了指定
    if (!treeNode && event.target.tagName.toLowerCase() != "button" && $(event.target).parents("a").length == 0) {
        zTree.cancelSelectedNode();
        gNode=null;//不是在节点上点击时将gNode置空，让其后不能点出值来
        showRMenu("root", event.clientX, event.clientY);
    } else if (treeNode && !treeNode.noR) {
        zTree.selectNode(treeNode);
        gNode=treeNode;
        showRMenu("node", event.clientX, event.clientY);
    }
}
function showRMenu(type, x, y) {
    $("#rMenu ul").show();
    if (type=="root") {
        //在非节点的ztree ul区域点击隐藏删除和修改，两个菜单项，保留增加节点
        $("#m_del").hide();
        $("#m_modify").hide();
    } else {
        //在节点点上的点击，完整显示菜单
//                $("#m_add").show();//老师的做法是这句不写，因添加这个dom目前就显示状态
        $("#m_modify").show();
        $("#m_del").show();
    }
    rMenu.css({"top":y+"px", "left":x+"px", "visibility":"visible"});
    $("body").bind("mousedown", onBodyMouseDown);
}
function hideRMenu() {
    if (rMenu) rMenu.css({"visibility": "hidden"});
    $("body").unbind("mousedown", onBodyMouseDown);
}
function onBodyMouseDown(event){
    if (!(event.target.id == "rMenu" || $(event.target).parents("#rMenu").length>0)) {
        rMenu.css({"visibility" : "hidden"});
    }
}
var rMenuBtnType=0;//此参数用于标记是修改还是添加下级0,无状态，1表示添加下级2表示修改节点
function addTreeNode() {//function增加节点的(添加下级和添加跟节点)
    hideRMenu();//已经点击了右键，所以对菜单进行隐藏
    hideRMenu();
    rMenuBtnType=1;//添加下级
    //修改模态框标题 id为myModalLabel
    if(gNode && gNode!=null) {
        $("#myModalLabel").html(gNode.text + "#添加下级");//直接点出它的属性，记住要点出的是menu Bean里面的字段，如果是添加根节点那么，这个gNode上面已经置空为null，点出来也会是null
    }else{
        $('#myModalLabel').html("添加下级");
    }
    $('#myModal').modal('show');//触发模态框显示
}
function modifyTreeNode() {//function修改节点函数
    hideRMenu();
    rMenuBtnType=2;//修改
    $('#myModal').modal('show');
    if(gNode && gNode!=null){
        findMenuById(gNode.id)//调用方法去数据库查出菜单中的每个字段的值 赋值到模态框的输入框框内便于修改。
    }
}
function findMenuById(id) {//调用方法去数据库查出菜单中的每个字段的值 赋值到模态框的输入框框内便于修改。
    $.ajax({
        type: 'post',
        url: '/menu/findByMenuId.do',
        data:{id:id},
        dataType:"json",
        success: function (data) {
//                    if(data.code==1){
//                        var node = data.data;
            var node = data
            $('#myModalLabel').html(gNode.text+"-编辑");
            $("#menuName").val(gNode.text);
            $("#menuUrl").val(node.url);
            $("#menuIcon").val(node.icon);
            $("#menuOrder").val(node.order);
            /*}else{
                alert(data.msg)
            }*/
        }
    });
}

//定义模态框提交按钮对应的方法
function saveOrUpdateNode() {
    var pid = "", id = "", name = "", canParent = 0;//这个变量用于判定是不是父节点，1表示是父节点
    if (rMenuBtnType == 1) {//添加下级//ajax传递的id的值是""空字符串
        if (gNode != null) {//判断是不是添加根节点：不为空为[添加下级]，为空为添加跟节点
            pid = gNode.id;//到此说明为添加下级，那么把pid设置为上面这个父级的id，也相当于gNode为空时未对menu的任何字段赋值
        }
    } else if (rMenuBtnType == 2) {//修改
        pid = gNode.pid;//这里的gNode已经在OnRightClick做了赋值，1.在节点上右击gNode复制为空，2.不在节点上点击赋值为节点对象
        id = gNode.id;
        if (gNode.isParent) {
            canParent = 1;//表示该节点是父节点，即：有子节点
        }
    } else {//如果是是直接添加父级是怎么操作或者其他不做menu字段的赋值
        return;
    }
    name = $("#menuName").val();
    var menuUrl = $("#menuUrl").val();
    var icon = $("#menuIcon").val();
    var order = $("#menuOrder").val();
    if (name == "" || name == null) {//这里用于处理字段不输内容的处理，后期有时间可以考虑用正则处理
        return;
    }
    if (order == "" || order == null) {
        order = 1;
    }
    $("#m_btn").attr('disabled', "true");
    //添加下级的时候id是""，修改的时候id是gNode.id，这是我的判断,如果id和canParent
    $.ajax({
        type: 'post',
        url: '/menu/insertOrUpdate.do',
        data: {
            id: id,
            pid: pid,
            text: name,
            url: menuUrl,
            icon: icon,
            order: order
        },
        dataType: "json",
        success: function (data) {
            console.log(data)
            $('#m_btn').removeAttr("disabled");
            //if (data.code == 1) {
            //    alert(data)
            $('#myModal').modal('toggle');//这段代码  modal("toggle") 这里机放一个 toggle就是切换的意思   如果这个弹层是显示的就关闭，如果这个弹层是关闭的就显示，所以在页面上点击 点击我按钮，弹层就显示出来，点击弹层是的 保存按钮就能关闭弹层。
            //重新初始化树，为了排序生效
            $.fn.zTree.init($("#treeDemo"), setting);
        }
    });
}
//模态框隐藏后 清空form表单数据
function modalHiddenEvent(){//点击模态框savebutton按钮时模态框隐藏，函数在$(document).ready(function(){}已经做了指定
    gNode=null;
    $('#errorMsg').html("");
    $('#menuForm')[0].reset();
    rMenuBtnType==0;
}
function removeTreeNode() {//function删除节点
    hideRMenu();
    if(gNode.children && gNode.children.length>0){
        alert("该菜单有下级，无法删除");
        return ;
    }
    var msg = "真的要删吗。\n\n请确认！";
    if (confirm(msg)==true){
        $.ajax({
            url:"/menu/delete.do",
            type:"post",
            // dataType:"json",
            data:{id:gNode.id},
            //移除页面上的节点
            success:function (data) {
                if(data.flag){
                zTree.removeNode(gNode)
                }else{
                    alert(data.errorMsg)
                }
            }
        });

    }

}

