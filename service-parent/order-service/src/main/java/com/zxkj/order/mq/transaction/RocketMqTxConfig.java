package com.zxkj.order.mq.transaction;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RocketMQ事务配置
 *
 * @author ：yuhui
 * @date ：Created in 2021/6/28 9:42
 */
//@Configuration
public class RocketMqTxConfig {

    @Value("${spring.rocketmq.consumer.groupName}")
    private String groupName;
    @Value("${spring.rocketmq.consumer.namesrvAddr}")
    private String namesrvAddr;

    @Bean(value = "transactionMQProducer1")
    public TransactionMQProducer getRocketTransactionMQProducer1() {
        TransactionMQProducer producer = new TransactionMQProducer(this.groupName + "_transaction1");
        producer.setNamesrvAddr(this.namesrvAddr);
        TransactionListener transactionListener = new MyTransaction1Listener();
        producer.setTransactionListener(transactionListener);
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        return producer;
    }

    @Bean(value = "transactionMQProducer2")
    public TransactionMQProducer getRocketTransactionMQProducer2() {
        TransactionMQProducer producer = new TransactionMQProducer(this.groupName + "_transaction2");
        producer.setNamesrvAddr(this.namesrvAddr);
        TransactionListener transactionListener = new MyTransaction2Listener();
        producer.setTransactionListener(transactionListener);
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        return producer;
    }
}
