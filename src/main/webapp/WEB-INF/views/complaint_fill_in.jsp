<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%-- 配置页面自适应 --%>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>投诉电梯</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/weui/style/weui.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/toastr/toastr.min.css">
    <%--<script href="https://cdn.bootcss.com/jquery/1.12.1/jquery.min.js"></script>--%>
    <script src="<%=request.getContextPath()%>/js/jquery-1.12.1.min.js"></script>
    <script src="<%=request.getContextPath()%>/toastr/toastr.min.js"></script>
    <style>
        form input {
            border: 0;
            background-color: transparent;
            font-size: 16px;
            height: 16px;
            width: 50%;
            outline: 0;
        }
    </style>
</head>
<body>
<%--<div style="width: 100%; height: 50%;">--%>
<div>
    <%-- 电梯的图片 --%>
    <img src="<%=request.getContextPath()%>/img/headImg.png" alt="图片找不到了"
         style="display: block; margin: auto; padding-top: 80px;">
</div>

<%--<div style="width: 100%; height: 50%; position: absolute; bottom: 0;">--%>
<div style="margin-top: 15px;">
    <form action="/complaint/toComplaint_fillIn" method="post">
        <%--<div class="weui-cells weui-cells_form">
            <div class="weui-cell">
                <div class="weui-cell__hd">
                    <label for="certificate" class="weui-label">使用证编号：</label>
                </div>
                <div class="weui-cell__bd weui-cell_primary">
                    <span>梯粤E</span>
                    <input id="certificate" class="weui-input" name="certificate" type="text"
                           placeholder="请输入电梯使用证编号"/>
                </div>
            </div>
            <div class="weui-cell">
                <div class="weui-cell__hd">
                    <label class="weui-label">单位地址：</label>
                </div>
                <div class="weui-cell__hd weui-cell_primary">
                    <input class="weui-input" name="addressOfUse" type="text" placeholder="请输入单位地址关键字"/>
                </div>
            </div>
        </div>--%>
        <div style="padding: 0 10px;">
            <div style="padding: 10px; height: 25px; border-top: solid rgb(192,192,192) 1px; border-bottom: solid rgb(192,192,192) 1px;">
                <div style="height: 100%;">
                    <label for="certificate">使用证编号:</label>
                    <%--<span style="color: rgb(64,64,64); margin-right: 8px;">梯粤E</span>--%>
                    <%--<input id="certificate" name="certificate" type="text" placeholder="请输入电梯使用证编号"/>--%>
                    <input id="certificate" name="certificate" type="text" value="${certificateOfUse}"/>
                </div>
            </div>
            <%--<hr style="border: solid grey 1px;">--%>
            <div style="padding: 10px; border-bottom: solid rgb(192,192,192) 1px;">
                <div>
                    <div style="display: inline-block;">
                        <label for="address">单位地址:</label>
                    </div>
                    <%--<input id="address" name="addressOfUse" type="text" placeholder="请输入单位地址关键字"/>--%>
                    <div style="display: inline-block;">
                        <span>${addressOfUse}</span>
                    </div>
                    <input id="address" name="addressOfUse" type="hidden" value="${addressOfUse}"/>
                </div>
            </div>
        </div>
        <div class="weui-btn-area">
            <a class="weui-btn weui-btn_primary" id="submitBtn" href="javascript:void(0);">提&nbsp;&nbsp;交</a>
        </div>
    </form>
</div>
<%-- 用户填写提示 --%>
<%--<div style="margin: 20px; color: grey; font-size: 14px;">
    <span>
        <i style="color: red;">*</i>
        填写提示:
    </span>
    <p style="font-size: 12px;">
        1. 按照电梯使用证编号查询，请查看电梯上有关信息，“梯粤E”3个字已给出，请填写后续字段<br>
        2. 按照电梯单位地址查询，请填写单位地址的关键字，支持模糊查询，多个关键字请用空格隔开<br>
        3. 以上查询方式2选1
    </p>
</div>--%>

<script>
    $(function () {
        /*$(".submitBtn").each(function (index) {
            $(this).click(function () {
                var stringify = $("form").serializeArray();
                alert(stringify);
            });
        });*/

        $("#submitBtn").click(function () {
            // 让用户只填写其中一种查询方式
            /*var certificate = $("input[name='certificate']").val();
            var address = $("input[name='addressOfUse']").val();

            if (certificate != null && certificate.trim() != "") {  // 填写了使用编号
                if (address != null && address.trim() != "") {      // 填写了单位地址
                    toastr.options = {
                        "timeOut": "2000",
                        "preventDuplicates": true,
                        "preventManyTimes": true,
                        "hideDuration": "1",
                        "positionClass": "toast-top-center"
                    };
                    toastr.info("请选择其中一种查询方式");
                    return;
                }
            }*/

            $("form")[0].submit();
        });
    });
</script>

</body>
</html>
