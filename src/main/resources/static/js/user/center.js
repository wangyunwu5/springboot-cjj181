var time = 0;
$(function(){
    $("#link1").on("click",function(){
  	  layer.open({
  	    type: 1,
  	    area: ['500px', '345px'], //宽高
  	    content: '<table id="pageTab1">'+
  		      '<tbody><tr height="20px">'+
  		        '<td align="right"></td>'+
  		        '<td><a style="color:#f00"></a></td>'+
  		      '</tr>'+
  		      '<tr height="50px">'+
  		        '<td align="right"><font color="#ff0000">*</font>原 密 码：</td>'+
  		        '<td><input class="textbox" id="oldpass" type="password" style="border-color: rgb(204, 204, 204);" placeholder="请输入登录密码"></td>'+
  		      '</tr>'+
  		      '<tr height="50px">'+
  		        '<td align="right"><font color="#ff0000">*</font>新 密 码：</td>'+
  		        '<td><input class="textbox" id="pass" type="password" style="border-color: rgb(204, 204, 204);" placeholder="新密码由6-16位，由数字和字母组成"></td>'+
  		      '</tr>'+
  		      '<tr height="50px">'+
  		        '<td align="right"><font color="#ff0000">*</font>确认密码：</td>'+
  		        '<td><input class="textbox" id="repass" type="password" style="border-color: rgb(204, 204, 204);" placeholder="请再次输入新密码"></td>'+
  		      '</tr>'+
  		      '<tr height="50px">'+
  		        '<td></td>'+
  		        '<td><input type="button" class="btn" value="确定" onclick="repass()"></td>'+
  		      '</tr>'+
  		    '</tbody></table>',
  	  });
    })
      $("#link2").on("click",function(){
    	  layer.open({
    	    type: 1,
    	    area: ['500px', '345px'], //宽高
    	    content: '<table id="pageTab2">'+
    		      '<tbody><tr height="20px">'+
    		        '<td align="right"></td>'+
    		        '<td><a id="mailMsg" style="color:#f00;display:none;"></a></td>'+
    		      '</tr>'+
    		      '<tr height="50px">'+
    		        '<td align="right"><font color="#ff0000">*</font>用户账号：</td>'+
    		        '<td><a style="color:#F29D17;font-weight:700">'+$("#logtd").attr("logname")+'</a></td>'+
    		      '</tr>'+
    		      '<tr height="50px">'+
    		        '<td align="right"><font color="#ff0000">*</font>电子邮箱：</td>'+
    		        '<td><input class="textbox" id = "reEmail" type="text"></td>'+
    		      '</tr>'+
    		      '<tr height="50px">'+
    		        '<td align="right"><font color="#ff0000">*</font>验 证 码：</td>'+
    		        '<td>'+
    		          '<input class="textbox" id="emailCode" type="text" style="width:120px">'+
    		          '<input id="getBtn" onclick="getcode()" type="button" value="获取验证码">'+
    		        '</td>'+
    		      '</tr>'+
    		      '<tr height="50px">'+
    		        '<td></td>'+
    		        '<td><input type="button" class="btn" value="确定" onclick="remail()"></td>'+
    		      '</tr>'+
    		    '</tbody></table>',
    	  });
      })

	  $("#link3").on('click',function(){
		  layer.msg("还在施工哦");
	  });
   

    
})


function getcode(){
	var mail = $("#reEmail").val();
    if (!check_email())
    	return false;
    var data = Ajax("/cctv/comm/sendEMailCode", {"email": mail},function(){
    	layer.msg("发送成功！");
    	
        time = 60;
        code = data;
        $(this).attr("disabled", true);
        $("#getBtn").css("color", "#CCCCCC");
        changeTime();
    });
}

function repass(){
	var oldpass = $("#oldpass"),pass = $("#pass"),repass=$("#repass");
	if(oldpass.val() == ""){
		oldpass.focus();
		layer.msg("请输入正确的登录密码！");
		return false;
	}
	if(pass.val() == ""){
		pass.focus();
		layer.msg("请输入新密码！");
		return false;
	}
	if(repass.val() == ""){
		repass.focus();
		layer.msg("输入确认密码！");
		return false;
	}
	if(pass.val() != repass.val()){
		layer.msg("输入密码不一致，请确认！");
		return;
	}
	var layerload = layer.msg('加载中', {icon: 16,shade: 0.01});
	Ajax('/cctv/user/repass',{"oldpass":oldpass.val(),"pass":pass.val(),"repass":repass.val()},function(data){
		 layer.close(layerload);
		if(data.code == 200)
		{
			layer.confirm('修改成功，请重新登录！', {
				  btn: ['确认','取消'] //按钮
				}, function(){
					window.location.reload();
				}, function(){
					layer.msg("请你登录后再操作！");
					window.location.reload();
			});
			return;
		}
		layer.alert(data.msg, {icon: 2});
		return;
	});
}
function remail()
{
	var mail = $("#reEmail").val();
	var code = $("#emailCode").val();
    if (!check_email())
    	return false;
    if(code == "" ){
    	layer.msg("请输入正确验证码！");
    }
    
	var layerload = layer.msg('加载中', {icon: 16,shade: 0.01});
	Ajax('/cctv/user/reemil',{"email":mail,"code":code},function(data){
		 layer.close(layerload);
		if(data.code == 200)
		{
			layer.confirm('修改成功!！', {
				  btn: ['确认','取消'] //按钮
				}, function(){
					window.location.reload();
				}, function(){
					window.location.reload();
			});
			return;
		}
		layer.alert(data.msg, {icon: 2});
		return;
	});
}

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

/** 检查邮箱 */
function check_email() {
	var reg = /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/;
	var email = $("#reEmail").val();
    if (email == "" || !reg.test(email)) {
    	layer.msg("请输入正确的邮箱")
        return false;
    }
    return true;
}

/** 执行AJAX操作 */
function Ajax(url, data, result) {
    $.ajax({
        url:url,
        data:data,
        type:"post",
        async:false,
        dataType:"json",
        success:function(data) {
            result(data);
        }
    });
}
