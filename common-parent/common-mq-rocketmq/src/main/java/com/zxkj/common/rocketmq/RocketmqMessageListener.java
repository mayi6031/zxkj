package com.zxkj.common.rocketmq;

import com.zxkj.common.rocketmq.support.RocketmqTopicTagEnum;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RocketmqMessageListener {
    RocketmqTopicTagEnum value();

}