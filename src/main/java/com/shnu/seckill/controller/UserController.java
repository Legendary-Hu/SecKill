//package com.shnu.seckill.controller;
//
//
//import com.shnu.seckill.pojo.User;
//import com.shnu.seckill.rabbitmq.MQSender;
//import com.shnu.seckill.utils.RespBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
///**
// * <p>
// *  前端控制器
// * </p>
// *
// * @author huxiang
// * @since 2022-06-28
// */
//@Controller
//@RequestMapping("/user")
//public class UserController {
//
//        @Autowired
//        private MQSender mqSender;
//        /**
//         * 用户信息（测试）
//         * @param user
//         * @return
//         */
//        @RequestMapping("/info")
//        @ResponseBody
//        public RespBean info(User user){
//            return RespBean.success(user);
//        }
//
//        /**
//         * 测试发送RabbitMQ消息
//         */
//        @RequestMapping("/mq")
//        @ResponseBody
//        public void mq(){
//            mqSender.send("hello");
//        }
//        /**
//         * 测试发送RabbitMQ消息 fanout
//         */
//        @RequestMapping("/mq/fanout")
//        @ResponseBody
//        public void mq01(){
//            mqSender.send("hello");
//        }
//
//        /**
//         * 测试发送RabbitMQ消息 direct
//         */
//        @RequestMapping("/mq/direct01")
//        @ResponseBody
//        public void mq02(){
//                mqSender.send01("hello,Red");
//        }
//        /**
//         * 测试发送RabbitMQ消息 direct
//         */
//        @RequestMapping("/mq/direct02")
//        @ResponseBody
//        public void mq03(){
//                mqSender.send02("hello,Green");
//        }
//        //Topic 模式
//        @RequestMapping("/mq/topic01")
//        @ResponseBody
//        public void mq04(){
//                mqSender.send03("hello Queue1");
//        }
//        @RequestMapping("/mq/topic02")
//        @ResponseBody
//        public void mq05(){
//                mqSender.send04("hello Queue2");
//        }
//
//        //Headers 模式
//        @RequestMapping("/mq/header01")
//        @ResponseBody
//        public void mq06(){
//                mqSender.send05("hello,header");
//        }
//        @RequestMapping("/mq/header02")
//        @ResponseBody
//        public void mq07(){
//                mqSender.send06("hello,header");
//        }
//}
