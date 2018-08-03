<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>用户登录</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/weui/style/weui.css">
    <script src="https://cdn.bootcss.com/jquery/1.12.1/jquery.min.js"></script>
</head>
<body>

<%--<div class="weui-cells__title">表单</div>--%>
<form action="/user/login" method="post">
    <div class="weui-cells weui-cells_form">
        <div class="weui-cell">
            <div class="weui-cell__hd"><label for="username" class="weui-label">用户名</label></div>
            <div class="weui-cell__bd">
                <input id="username" class="weui-input" type="text" name="username"
                       placeholder="请输入用户名"/>
            </div>
        </div>
        <div class="weui-cell">
            <div class="weui-cell__hd"><label for="contactNum" class="weui-label">联系电话</label></div>
            <div class="weui-cell__bd">
                <input id="contactNum" class="weui-input" type="tel" name="contactNum"
                       pattern="^[1][3,4,5,7,8][0-9]{9}$" placeholder="请输入手机号码"/>
            </div>
        </div>
        <div class="weui-btn-area">
            <a id="loginBtn" href="javascript:;" class="weui-btn weui-btn_primary">登录</a>
        </div>
    </div>
</form>

<script>
    $(function () {
        $("#loginBtn").click(function () {
            $("form")[0].submit();
        });
    });
</script>

</body>
</html>
