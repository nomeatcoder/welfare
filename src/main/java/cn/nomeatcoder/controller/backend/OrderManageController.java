package cn.nomeatcoder.controller.backend;

import cn.nomeatcoder.common.Const;
import cn.nomeatcoder.common.PageInfo;
import cn.nomeatcoder.common.ResponseCode;
import cn.nomeatcoder.common.ServerResponse;
import cn.nomeatcoder.common.domain.User;
import cn.nomeatcoder.common.vo.OrderVo;
import cn.nomeatcoder.service.OrderService;
import cn.nomeatcoder.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("/manage/order")
public class OrderManageController {

    @Resource
    private UserService userService;
    @Resource
    private OrderService orderService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse orderList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                    @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");

        }
        if(userService.checkAdminRole(user).isSuccess()){
            //填充我们增加产品的业务逻辑
            return orderService.manageList(pageNum,pageSize);
        }else{
            return ServerResponse.error("无权限操作");
        }
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse orderDetail(HttpSession session, Long orderNo){

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");

        }
        if(userService.checkAdminRole(user).isSuccess()){
            //填充我们增加产品的业务逻辑

            return orderService.manageDetail(orderNo);
        }else{
            return ServerResponse.error("无权限操作");
        }
    }



    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse orderSearch(HttpSession session, Long orderNo, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                                @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");

        }
        if(userService.checkAdminRole(user).isSuccess()){
            //填充我们增加产品的业务逻辑
            return orderService.manageSearch(orderNo,pageNum,pageSize);
        }else{
            return ServerResponse.error("无权限操作");
        }
    }



    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse orderSendGoods(HttpSession session, Long orderNo){

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");

        }
        if(userService.checkAdminRole(user).isSuccess()){
            //填充我们增加产品的业务逻辑
            return orderService.manageSendGoods(orderNo);
        }else{
            return ServerResponse.error("无权限操作");
        }
    }

}
