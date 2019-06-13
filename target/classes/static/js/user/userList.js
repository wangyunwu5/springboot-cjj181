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
        window.location.href = "getUserList";
    });
    $("#menuBtn2").click(function() {
        var name = $("#menuText").val();
        var urlstr = "";
        if($("#companyid").val() !=0 ){
        	urlstr = "&companyid=" + $("#companyid").val();
        }
        if (name.trim() != "")
            window.location.href = "getUserList?keyword=" + name +""+ urlstr;
    });

    
    $("#edit_bnt").on('click',function(){
     	var id = $(this).attr("data-value");
    	showBox('/cctv/user/userManage/editUser?action=edit&id='+id,'编辑用户');
    });
    
    $("#companyid").change(function(){
        var name = $("#menuText").val();
        var urlstr = "";
        if($("#companyid").val() !=0 ){
        	urlstr = "&companyid=" + $("#companyid").val();
        }
  
        window.location.href = "getUserList?keyword=" + name +""+ urlstr;
    });
    
    /** 上一页 */
    $(".pagebtn:eq(0)").click(function() {
        var name = $("#menuText").val();
        var page = Number($("#tab2 a:eq(0)").text()) - 1;
        var urlstr = "";
        if($("#companyid").val() !=0 ){
        	urlstr = "&companyid=" + $("#companyid").val();
        }
        window.location.href = "getUserList?keyword=" + name + "&pageIndex=" + page+""+ urlstr;
    });
    /** 下一页 */
    $(".pagebtn:eq(1)").click(function() {
        var name = $("#menuText").val();
        var page = Number($("#tab2 a:eq(0)").text()) + 1;
        var urlstr = "";
        if($("#companyid").val() !=0 ){
        	urlstr = "&companyid=" + $("#companyid").val();
        }
        window.location.href = "getUserList?keyword=" + name + "&pageIndex=" + page + "" + urlstr;
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
		  area: ['900px', '550px'],
		  fixed: false, //不固定
		  maxmin: true,
		  title:title,
		  content: url
	});
}