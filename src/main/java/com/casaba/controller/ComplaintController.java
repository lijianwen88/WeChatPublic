package com.casaba.controller;

import com.casaba.entity.Elevator;
import com.casaba.entity.User;
import com.casaba.entity.WeChatUser;
import com.casaba.service.IComplaintService;
import com.casaba.service.IElevatorService;
import com.casaba.service.IUserService;
import com.casaba.util.CommonUtil;
import com.casaba.util.WeChatUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by Ulric on 2018/7/22
 */

@Controller
@RequestMapping("/complaint")
public class ComplaintController {

    private static final Log LOGGER = LogFactory.getLog(ComplaintController.class);

    @Resource
    private IElevatorService elevatorService;

    @Resource
    private IComplaintService complaintService;

    @Resource
    private IUserService userService;


    /**
     * 从投诉电梯信息填写页跳转到投诉页面
     *
     * @author Ulric
     * @date 2018/7/23
     */
    @RequestMapping("/toComplaint_fillIn")
    public ModelAndView toComplaint_fillIn(HttpServletRequest request,
                                           String certificate, String addressOfUse) {
        LOGGER.info("=====接收到的参数：\n\t#certificate: " + certificate
                + "\n\t#addressOfUse: " + addressOfUse);

        ModelAndView mv = new ModelAndView();

        Map paramMap = new HashMap();

        HttpSession session = request.getSession();

        // 查找出该电梯的设备地址
//        Elevator elevator = elevatorService.queryByCertificate(certificate);
        List<Elevator> elevatorList = elevatorService.queryElevator(certificate, addressOfUse);

//        if (elevator == null) {
        if (null == elevatorList || elevatorList.isEmpty()) {
            mv.setViewName("error");
            mv.addObject("msg", "您查找的数据不存在");
            return mv;
        }/* else if(elevatorList.size() == 1){
            paramMap.put("elevator",elevatorList.get(0));
        }*/else {
            paramMap.put("elevatorList", elevatorList);
        }

//        session.setAttribute("toJsp", "elevator_info");
        session.setAttribute("paramMap", paramMap);
//        mv.setViewName("redirect:/wechat/wclogin");
        mv.setViewName("elevator_info");

        return mv;
    }

    /**
     * 从电梯信息列表页跳到投诉页面
     *
     * @author Ulric
     * @date 2018/7/23
     */
    @RequestMapping(value = "/toComplaint_eleInfo")
    public ModelAndView toComplaint_eleInfo(String certificateOfUse, String deviceAddress,
                                            HttpServletRequest request) {
        Elevator elevator = new Elevator();
        elevator.setCertificateOfUse(certificateOfUse);
        elevator.setDeviceAddress(deviceAddress);

        Map paramMap = new HashMap();

        paramMap.put("elevator", elevator);

        HttpSession session = request.getSession();
        session.setAttribute("toJsp", "complaint");
        session.setAttribute("paramMap", paramMap);

        ModelAndView mv = new ModelAndView();

        // 先进行微信登录
        mv.setViewName("redirect:/wechat/wclogin");

        return mv;
    }

    /**
     * 执行投诉操作
     *
     * @author Ulric
     * @date 2018/7/23
     */
    @RequestMapping("/doComplaint")
    public ModelAndView doComplaint(HttpServletRequest request, String certificate, String sketch, String username, String contactNum, String
            details, String imgUrl) {
        LOGGER.info("=====接收到的参数：\n\t#certificate：" + certificate +
                "\n\t#sketch：" + sketch +
                "\n\t#username：" + username +
                "\n\t#contactNum：" + contactNum +
                "\n\t#details：" + details +
                "\n\t#imgUrl：" + imgUrl);

        HttpSession session = request.getSession();

        ModelAndView mv = new ModelAndView();
        mv.addObject("username", username);
        mv.addObject("certificate", certificate);

        // 将用户（投诉人）、投诉单、电梯信息保存到数据库
        try {
            boolean isSuccess =
                    complaintService.saveComplaintSheet(certificate, username, contactNum, sketch, details, imgUrl);
            if (!isSuccess) {
                throw new RuntimeException("保存数据失败");
            }

            // 如果传进来微信用户对象，则说明没绑定电梯用户，在这里进行绑定
            HashMap paramMap = (HashMap) session.getAttribute("paramMap");
            WeChatUser wcUser = null;
            if (paramMap != null && !paramMap.isEmpty()) {
                wcUser = (WeChatUser) paramMap.get("wcUser");
            }
            if (wcUser != null) {
                User user = userService.findByUsername(username);
                if (user != null) {
                    user.setWcOpenId(wcUser.getOpenId());
                    userService.updateUserByName(user);
                }
            }
            mv.setViewName("complaint_success");
        } catch (Exception e) {
            e.printStackTrace();
            mv.setViewName("complaint_fail");
        } finally {
            return mv;
        }
    }

    /**
     * 上传图片
     *
     * @author Ulric
     * @date 2018/7/30
     */
    @RequestMapping("/saveImage")
    @ResponseBody
    public Map uploadImage(HttpServletRequest request, String mediaId) {
        LOGGER.info("=====接收到的参数：\n\t#mediaId：" + mediaId);

        Map<String, Object> resultMap = new HashMap<>();

        String imgName = "";
        // 根据 mediaId 获取图片的字节输入流
        InputStream inStrm = getMedia(mediaId);
        byte[] bytes = new byte[1024];
        int len = 0;
        FileOutputStream fileOutStrm = null;

        // 服务器保存图片的路径
        String webRootPath = request.getSession().getServletContext().getRealPath("/");
        if (webRootPath.endsWith("/") || webRootPath.endsWith("\\")) { // 如果获取的项目根目录最后有斜杠，就去掉
            String temp = webRootPath.substring(0, webRootPath.length() - 1);
            webRootPath = temp;
        }
//        String imgRootPath = webRootPath + "/img_upload";
        String imgRootPath = new File(webRootPath).getParent() + "/ele_img_upload"; // 在项目的上一级创建图片文件夹，这样迭代项目的时候就不会把图片也删除
        File imgDir = new File(imgRootPath);
        if (!imgDir.exists()) {
            imgDir.mkdirs();
        }
        imgName = System.currentTimeMillis() + CommonUtil.genRandom(4) + ".png";
        try {
            File imgFile = new File(imgRootPath + "/" + imgName);
            if (!imgFile.exists()) {
                imgFile.createNewFile();
            }
            fileOutStrm = new FileOutputStream(imgRootPath + "/" + imgName);
            while ((len = inStrm.read(bytes)) != -1) {
                fileOutStrm.write(bytes, 0, len);
            }
            resultMap.put("success", true);
            resultMap.put("imgUrl", imgRootPath + "/" + imgName);
        } catch (IOException e) {
            resultMap.put("success", false);
            resultMap.put("msg", e.getMessage());
            e.printStackTrace();
        } finally {
            if (inStrm != null) {
                try {
                    inStrm.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutStrm != null) {
                try {
                    fileOutStrm.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return resultMap;
        }

//        String imageBase64Str = getImageBase64Str(localData);
//
//        // 对字节数组字符串进行 base64 解码，并生成图片
//        if (null == imageBase64Str) { return null; }
//
//        BASE64Decoder base64Decoder = new BASE64Decoder();
//
//        try {
//            byte[] bytes = base64Decoder.decodeBuffer(imageBase64Str);
//            for (int i = 0; i < bytes.length; i++) {
//                if (bytes[i] < 0) { // 调整异常数据
//                    bytes[i] += 256;
//                }
//            }
//
//            // 生成图片
//            String webRootPath = request.getSession().getServletContext().getRealPath("/"); // 项目根目录
//            LOGGER.info("=====项目根目录：" + webRootPath);
//            if (webRootPath.endsWith("/") || webRootPath.endsWith("\\")) {
//                // 如果最后有 “/” 或者 “\”，则去掉
//                String temp = webRootPath.substring(0, webRootPath.length() - 1);
//                webRootPath = temp;
//            }
//            String imgDirPath = webRootPath + "/img_upload";
//            String imgFilePath = imgDirPath + "/" + System.currentTimeMillis() + ".jpg";
//
//            OutputStream outStrm = new FileOutputStream(imgFilePath);
//            outStrm.write(bytes);
//            outStrm.flush();
//            outStrm.close();
//
//            resultMap.put("success", true);
//        } catch (IOException e) {
//            e.printStackTrace();
//            resultMap.put("success", false);
//        } finally {
//            return resultMap;
//        }

    }

    /**================================================**/

    /**
     * 根据 mediaId 获取临时素材的输入字节流
     *
     * @author Ulric
     * @date 2018/7/31
     */
    private InputStream getMedia(String mediaId) {
        String url = "https://api.weixin.qq.com/cgi-bin/media/get";
        String access_token = WeChatUtil.getAccessToken();
        String params = "access_token=" + access_token + "&media_id=" + mediaId;
        InputStream inStrm = null;
        try {
            String urlNameString = url + "?" + params;
            URL urlGet = new URL(urlNameString);
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("GET"); // 必须是get方式请求
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            http.connect();
            // 获取文件转化为byte流
            inStrm = http.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inStrm;
    }

    /**
     * 将图片转化成 base64 字符串
     *
     * @param imgPath 图片的路径（可以是网络路径）
     * @author Ulric
     * @date 2018/7/30
     */
//    private String getImageBase64Str(String imgPath) {
//        LOGGER.info("=====接收到的参数：\n\t#imgPath：" + imgPath);
//
//        InputStream inStrm = null;
//        byte[] bytes = null;
//
//        // 读取图片字节数组
//        try {
//            inStrm = new FileInputStream(imgPath);
//            // available()：获取从此输入流中可以读取（或跳过）的剩余字节数的估计值
//            bytes = new byte[inStrm.available()];
//            inStrm.read(bytes); // read()：从该输入流读取最多 bytes.length 个字节的数据为字节数组。
//            inStrm.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // 对字节数组进行 base64 编码（会生成编码后的字节字符串）并返回
//        BASE64Encoder base64Encoder = new BASE64Encoder();
//        return base64Encoder.encode(bytes);
//    }

    /**
     * 将 base64 字符串转化成图片
     *
     * @author Ulric
     * @date 2018/7/30
     */
//    private boolean genImage(String base64String) {
//        // 对字节数组字符串进行 base64 解码，并生成图片
//        if (null == base64String) { return false; }
//
//        BASE64Decoder base64Decoder = new BASE64Decoder();
//
//        try {
//            byte[] bytes = base64Decoder.decodeBuffer(base64String);
//            for (int i = 0; i < bytes.length; i++) {
//                if (bytes[i] < 0) { // 调整异常数据
//                    bytes[i] += 256;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return false;
//    }
}
