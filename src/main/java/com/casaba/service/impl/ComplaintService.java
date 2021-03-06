package com.casaba.service.impl;

import com.casaba.entity.Complaint;
import com.casaba.entity.Elevator;
import com.casaba.entity.User;
import com.casaba.mapper.ComplaintMapper;
import com.casaba.mapper.UserMapper;
import com.casaba.service.IComplaintService;
import com.casaba.service.IElevatorService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.logging.Logger;

/**
 * created by Ulric on 2018/7/24
 */

@Service
public class ComplaintService implements IComplaintService {

    private static final Log LOGGER = LogFactory.getLog(ComplaintService.class);

    @Resource
    private IElevatorService iElevatorService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ComplaintMapper complaintMapper;

    /**
     * 把投诉单的内容存入数据库
     *
     * @param certificate 电梯使用证编号
     * @param username    用户名（投诉人）
     * @param contactNum  联系方式
     * @param sketch      电梯异常简述
     * @param details     投诉详情
     * @author Ulric
     * @date 2018/7/24
     */
    @Override
    @Transactional
    public boolean saveComplaintSheet(String certificate, String username, String contactNum, String sketch, String
            details, String imgUrl) throws Exception {
        boolean isSuccess = false;

        // 根据使用证编号查找出电梯
        Elevator elevator = iElevatorService.queryByCertificate(certificate);

        // 创建投诉单对象，并赋值简述和详情属性，以及将电梯的主键赋值到投诉单的电梯外键
        Complaint complaint = new Complaint();
        complaint.setSketch(sketch);
        complaint.setImgUrl(imgUrl);
        complaint.setDetails(details);
        complaint.setElevatorId(elevator.getId());

        User user = userMapper.selectByUsername(username);
        // 该用户不存在，则需要存入该用户，然后获取新插入的主键ID
        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setContactNum(contactNum);

            isSuccess = userMapper.insertUser(user);
            // 添加用户失败，则回滚，取消全部操作
            if (!isSuccess) {
                throw new RuntimeException("添加用户失败");
            }
            long maxId = userMapper.selectMaxId();
            user.setId(maxId);
        }
        // 给投诉单设置用户ID外键
        complaint.setUserId(user.getId());

        // 把投诉单对象存入数据库
        isSuccess = complaintMapper.insertComplaint(complaint);

        if (!isSuccess) {
            throw new RuntimeException("保存投诉单失败");
        }

        return isSuccess;
    }

    /**
     * 根据用户查找投诉单
     *
     * @param user
     * @author casaba-u
     * @date 2018/8/3
     */
    @Override
    public List<Complaint> findComplaintsByUser(User user) {
        LOGGER.info("=====接收到的参数：\n\t#User: " + user);
//        System.out.println("=====ComplaintService--findComplaintsByUser()接收到的参数：\n\t#User: " + user);

        List<Complaint> complaintList = complaintMapper.selectComplaintsByUser(user);

        LOGGER.info("=====查询结果：" + complaintList);

        return complaintList;
    }
}
