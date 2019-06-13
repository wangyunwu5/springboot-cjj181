$(document).ready(function() {
    $("#logo img").attr("title", "用户登录");
    /********************************************************************/
    $("#tab1 input[type=text]:eq(0)").attr("placeholder", "用户昵称，2-10位");
    $("#tab1 input[type=text]:eq(1)").attr("placeholder", "6-16位，由数字和字母组成");
    $("#tab1 input[type=password]:eq(0)").attr("placeholder", "6-16位，由数字和字母组成");
    $("#tab1 input[type=password]:eq(1)").attr("placeholder", "6-16位，由数字和字母组成");
    $("#tab1 input[type=text]:eq(2)").attr("placeholder", "请输入公司名称");
    /********************************************************************/
    $(".combtn").click(function() {
        if (!checkNickName())
            return false;
        if (!checkUserName() || !checkPassWord())
            return false;
        if (!checkCompany())
            return false;
        if (!check_email())
            return false;
        if (!checkCode())
            return false;
        $("#form1").submit();
    });

    /** 检测昵称 */
    function checkNickName() {
        var nickname = $("#tab1 input[type=text]:eq(0)").val();
        if (nickname.length < 2 || nickname.length > 10) {
            $("#tab1 input[type=text]:eq(0)").css("border-color", "#f00");
            $("#tips").text("*用户名称格式不正确！");
            return false;
        }
        return true;
    }
    /** 检测账号 */
    function checkUserName() {
        var name = $("#tab1 input[type=text]:eq(1)").val();
        if (name.length < 6 || name.length > 16) {
            $("#tab1 input[type=text]:eq(1)").css("border-color", "#f00");
            $("#tips").text("*登录账号格式不正确！");
            return false;
        }
        return true;
    }

    /** 检测密码 */
    function checkPassWord() {
        var password1 = $("#tab1 input[type=password]:eq(0)").val();
        var password2 = $("#tab1 input[type=password]:eq(1)").val();
        if (password1.length < 6 || password1.length > 16) {
            $("#tab1 input[type=password]:eq(0)").css("border-color", "#f00");
            $("#tips").text("*登录密码格式不正确！");
            return false;
        }
        if (password1 != password2) {
            $("#tab1 input[type=password]:eq(0)").css("border-color", "#f00");
            $("#tab1 input[type=password]:eq(1)").css("border-color", "#f00");
            $("#tips").text("*两次密码不一致,请重新输入！");
            return false;
        }
        return true;
    }

    
    /** 检查公司 */
    function checkCompany() {
        if ($("#tab1 input[type=text]:eq(2)").val() == "") {
            $("#tab1 input[type=text]:eq(2)").css("border-color", "#f00");
            $("#tips").text("*请输入公司名称！");
            return false;
        }
        return true;
    }
    

    /** 检查邮箱 */
    function check_email() {
    	var reg = /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/;
    	var email =$("#tab1 input[type=text]:eq(3)").val();
        if (email == "" || !reg.test(email)) {
            $("#tab1 input[type=text]:eq(3)").css("border-color", "#f00");
            $("#tips").text("*请输入正确的邮箱！");
            return false;
        }
        return true;
    }
    
    /** 检验码 */
    function checkCode() {
    	var code = $("#tab1 input[type=text]:eq(4)").val();
        if (code == "" || code.length != 6) {
            $("#tab1 input[type=text]:eq(4)").css("border-color", "#f00");
            $("#tips").text("*请输入正确的邮箱校验码！");
            return false;
        }
        return true;
    }
    
    
    var time = 0;
    $("#getBtn").click(function() {
        if (!check_email())
        	return false;
        var mail = $("#tab1 input[type=text]:eq(3)").val();
        var data = Ajax("/cctv/comm/sendEMailCode", {"email": mail});
        if (data.code != 200 ){
        	$("#tab1 input[type=text]:eq(4)").css("border-color", "#f00");
        	$("#tips").text(data.errorMsg);
        	return false;
        }
        time = 60;
        code = data;
        $(this).attr("disabled", true);
        $("#getBtn").css("color", "#CCCCCC");
        changeTime();
    });

    function changeTime() {
        if (time-- == 0) {
            $("#getBtn").attr("value", "获取验证码");
            $("#getBtn").attr("disabled", false);
            $("#getBtn").css("color", "#28B779");
        } else {
            $("#getBtn").attr("value", time + " S后重新获取");
            setTimeout(changeTime, 1000);
        }
    }
    
    
    /** 输入框输入改变事件 */
    $("#tab1 input[type=text]").bind("click", function() {
        $(this).css("border-color", "#999");
        $("#tips").text("");
    });
    $("#tab1 input[type=password]").bind("click", function() {
        $("input[type=password]").css("border-color", "#999");
        $("#tips").text("");
    });
    
    /** 执行AJAX操作 */
    function Ajax(url, data) {
        var result = null;
        $.ajax({
            url:url,
            data:data,
            type:"post",
            async:false,
            dataType:"json",
            success:function(data) {
                result = data;
            }
        });
        return result;
    }
    
});
