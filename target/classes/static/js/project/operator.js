$(document).ready(function() {
	
    if ($("#menuText").val() == "") {
        $("#menuBtn1").attr("disabled", true);
        $("#menuBtn1").css("background-color", "#CCC");
    }
    $("#menuText").keydown(function() {
        if (event.keyCode == "13")
            $("#menuBtn2").click();
    });
    /********************************************************************/
    $("#menuBtn1").click(function() {
        window.location.href = "operator";
    });
    $("#menuBtn2").click(function() {
        var name = $("#menuText").val();
        if (name.trim() != "")
            window.location.href = "operator?name=" + name;
    });
    /********************************************************************/
    /** 初始化表格 */
    $("#tab1 tbody tr").each(function(i) {
    	var id = $(this).attr("id");
        var name = $("#menuText").val();
        if (name.trim() != "") {
            var text = $(this).find("td:eq(1)").text();
            var exp = new RegExp(name,"gm");
            var font = "<font color='#f00'>" + name + "</font>";
            var cont = text.replace(exp, font);
            $(this).find("td:eq(1)").html(cont);
        }
        $(this).find("a").attr("target", "_blank");
        $(this).find("td:eq(1) a").attr("href", "/cctv/pipe/showpipe?id=" + id);
        /*********************************************/
        $(this).find(".tablebtn1").click(function() {
            window.open("/cctv/file/download?id=" + id);
        });
        $(this).find(".tablebtn2").click(function() {
            if (confirm("确定要移除该数据吗?")) {
                if (Ajax("remove", {id: id}))
                	showTips("数据删除成功！");
                setTimeout("location.reload()", 2000);
            }
        });
        $(this).click(function() {
            $("#tab1 tbody tr:even").find("td:eq(0)").css("background-color", "#FFFFFF");
            $("#tab1 tbody tr:odd").find("td:eq(0)").css("background-color", "#FAFAFA");
            $(this).find("td:eq(0)").css("background-color", "#FFD58D");
        });
    });
    /********************************************************************/
    /** 上一页 */
    $(".pagebtn:eq(0)").click(function() {
        var name = $("#menuText").val();
        var page = Number($("#tab2 a:eq(0)").text()) - 1;
        window.location.href = "operator?name=" + name + "&page=" + page;
    });
    /** 下一页 */
    $(".pagebtn:eq(1)").click(function() {
        var name = $("#menuText").val();
        var page = Number($("#tab2 a:eq(0)").text()) + 1;
        window.location.href = "operator?name=" + name + "&page=" + page;
    });
    $(".pagebtn:eq(0)").attr("disabled", false);
    $(".pagebtn:eq(1)").attr("disabled", false);
    var page1 = $("#tab2 a:eq(0)").text();
    var page2 = $("#tab2 a:eq(1)").text();
    if (page1 <= 1) {
        $(".pagebtn:eq(0)").attr("disabled", true);
        $(".pagebtn:eq(0)").css("color", "#999");
    }
    if (page1 == page2) {
        $(".pagebtn:eq(1)").attr("disabled", true);
        $(".pagebtn:eq(1)").css("color", "#999");
    }
    /********************************************************************/
    function showTips(text) {
        $("#Tip").text(text);
        $("#Tip").show().delay(1800).hide(200);
    }
    function Ajax(url, data) {
        var result = null;
        $.ajax({
            url:url,
            data:data,
            type:"post",
            async:false,
            datatype:"json",
            success:function(data) {
                result = data;
            }
        });
        return result;
    }
});
