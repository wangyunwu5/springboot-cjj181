$(document).ready(function() {

    if ($("#menuText").val() == "") {
        $("#menuBtn1").css("background-color", "#CCC");
        $("#menuBtn1").attr("disabled", true);
    }
    $("#menuText").keydown(function() {
        if (event.keyCode == "13")
            $("#menuBtn2").click();
    });
    /********************************************************************/
    $("#menuBtn1").click(function() {
        window.location.href = "getComanyList";
    });
    $("#menuBtn2").click(function() {
        var name = $("#menuText").val();
        if (name.trim() != "")
            window.location.href = "getComanyList?companyName=" + name;
    });

    $("#del_bnt").on('click',function(){
    	var id = $(this).attr("data-value");
    	if(id == 0 || id == null){
    		layer.msg("参数错误请刷新页面再试！")
    		return;
    	}
    	
    	
    	layer.confirm('您确定要删除该公司吗？', {
    	  btn: ['确定','取消'] //按钮
    	}, function(){
    	  var data = Ajax('/cctv/company/delCompany',{"id":id});
    	  if(data.code == 200){
    		 layer.msg(data.msg);
    		 setTimeout(function(){
    			  window.location.reload();
    		 },2000);
    		  return;
    	  }
    	  layer.msg(data.msg);
    	  
    	}, function(){
    		layer.msg("您已取消操作！");
    	});
    	
    });
    
    $("#edit_bnt").on('click',function(){
     	var id = $(this).attr("data-value");
    	showBox('/cctv/company/editCompany?action=edit&id='+id,'编辑公司');
    });
    
    /** 上一页 */
    $(".pagebtn:eq(0)").click(function() {
        var name = $("#menuText").val();
        var page = Number($("#tab2 a:eq(0)").text()) - 1;
        window.location.href = "getComanyList?companyName=" + name + "&pageIndex=" + page;
    });
    /** 下一页 */
    $(".pagebtn:eq(1)").click(function() {
        var name = $("#menuText").val();
        var page = Number($("#tab2 a:eq(0)").text()) + 1;
        window.location.href = "getComanyList?companyName=" + name + "&pageIndex=" + page;
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

function showBox(url,title){
	layer.open({
		  type: 2,
		  area: ['680px', '450px'],
		  fixed: false, //不固定
		  maxmin: true,
		  title:title,
		  content: url
	});
}