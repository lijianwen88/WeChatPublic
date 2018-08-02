<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <%-- 配置页面自适应 --%>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>查找电梯</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/weui/style/weui.css">
    <script src="https://cdn.bootcss.com/jquery/1.12.1/jquery.min.js"></script>
</head>
<body>
<%--<p>请输入电梯使用证编号：</p>--%>
<form action="/elevator/findByCertificate" method="post">
    <div class="weui-cells">
        <div class="weui-cell">
            <div class="weui-cell__hd">
                <label class="weui-label">电梯使用证编号：</label>
            </div>
            <div class="weui-cell__hd weui-cell_primary">
                <input class="weui-input" name="certificate" type="text" placeholder="请在此输入使用证编号"/>
            </div>
        </div>
    </div>
    <div class="weui-btn-area">
        <a class="weui-btn weui-btn_primary" id="submitBtn" href="javascript:">提交</a>
    </div>
    <%--<input id="textCertificate" type="text" name="certificate">--%>
    <%--<input id="submitBtn" type="submit" value="提交">--%>
</form>

<script>
    $(function () {
        $("#submitBtn").click(function () {
            $("form")[0].submit();
        });
    });
</script>

<%--<script>
    $(function () {
        $("#submitBtn").click(function () {
            // 获取数据，并转成JSON字符串
            var certificate = $("#textCertificate").val();
            var data = JSON.stringify({"certificate": certificate}); // JSON.stringify()方法将对象或数组转换为JSON字符串
        });
        $.ajax({
            url: "/elevator/findByCertificate",
            dataType: "JSON", // 预期服务器返回的数据类型
            type: "POST",     // 请求方式
            contentType: "application/json",  // 发送信息至服务器时的内容编码格式（不配置有什么影响？）
            // 发送到服务器的数据
            data: data,
            success:function (result) {

            }
        });
    });
</script>--%>
</body>
</html>
