package com.zxkj.task.support.jobinit;

import com.dangdang.ddframe.job.api.JobType;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.lifecycle.api.JobSettingsAPI;
import com.dangdang.ddframe.job.lite.lifecycle.api.JobStatisticsAPI;
import com.dangdang.ddframe.job.lite.lifecycle.domain.JobBriefInfo;
import com.dangdang.ddframe.job.lite.lifecycle.domain.JobSettings;
import com.dangdang.ddframe.job.lite.lifecycle.internal.settings.JobSettingsAPIImpl;
import com.dangdang.ddframe.job.lite.lifecycle.internal.statistics.JobStatisticsAPIImpl;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.zxkj.task.listener.DistributeOnceElasticJobListener;
import com.zxkj.task.support.ElasticJobProperties;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;

/**
 * 动态任务初始化(支持简单、流式任务)
 *
 * @author
 * @date 2020/9/14 19:22
 */
@Slf4j
public class DynamicJobInitialization extends AbstractJobInitialization {

    private JobStatisticsAPI jobStatisticsAPI;
    private JobSettingsAPI jobSettingsAPI;

    public DynamicJobInitialization(ZookeeperRegistryCenter zookeeperRegistryCenter) {
        this.jobStatisticsAPI = new JobStatisticsAPIImpl(zookeeperRegistryCenter);
        this.jobSettingsAPI = new JobSettingsAPIImpl(zookeeperRegistryCenter);
    }

    public void init() {
        Collection<JobBriefInfo> allJob = jobStatisticsAPI.getAllJobsBriefInfo();
        if (!CollectionUtils.isEmpty(allJob)) {
            allJob.forEach(jobInfo -> {
                if (JobBriefInfo.JobStatus.CRASHED.equals(jobInfo.getStatus()) || JobBriefInfo.JobStatus.OK.equals(jobInfo.getStatus())) {
                    try {
                        Date currentDate = new Date();
                        CronExpression cronExpression = new CronExpression(jobInfo.getCron());
                        Date nextValidTimeAfter = cronExpression.getNextValidTimeAfter(currentDate);
                        // 表达式还生效的任务
                        if (!ObjectUtils.isEmpty(nextValidTimeAfter)) {
                            this.initJobHandler(jobInfo.getJobName());
                        }
                    } catch (ParseException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            });
        }
    }

    /**
     * 初始化任务操作
     *
     * @param jobName 任务名
     */
    private void initJobHandler(String jobName) {
        try {
            JobSettings jobSetting = jobSettingsAPI.getJobSettings(jobName);
            if (!ObjectUtils.isEmpty(jobSetting)) {
                ElasticJobProperties.JobConfiguration configuration = new ElasticJobProperties.JobConfiguration();
                configuration.setDescription(jobSetting.getDescription());
                configuration.setCron(jobSetting.getCron());
                configuration.setJobParameter(jobSetting.getJobParameter());
                configuration.setShardingTotalCount(jobSetting.getShardingTotalCount());
                configuration.setShardingItemParameters(jobSetting.getShardingItemParameters());
                configuration.setJobClass(jobSetting.getJobClass());
                configuration.setListener(getDistributedListener());
                super.initJob(jobName, JobType.valueOf(jobSetting.getJobType()), configuration);
            }
        } catch (Exception e) {
            log.error("初始化任务操作失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 组装分布式监听对象
     *
     * @return
     */
    public ElasticJobProperties.JobConfiguration.Listener getDistributedListener() {
        ElasticJobProperties.JobConfiguration.Listener listener = new ElasticJobProperties.JobConfiguration.Listener();
        listener.setDistributedListenerClass(DistributeOnceElasticJobListener.class.getCanonicalName());
        listener.setStartedTimeoutMilliseconds(100l);
        listener.setCompletedTimeoutMilliseconds(100l);
        return listener;
    }

    public JobTypeConfiguration getJobTypeConfiguration(String jobName, JobType jobType, ElasticJobProperties.JobConfiguration jobConfiguration, JobCoreConfiguration jobCoreConfiguration) {
        switch (jobType) {
            case SIMPLE:
                return new SimpleJobConfiguration(jobCoreConfiguration, jobConfiguration.getJobClass());
            case DATAFLOW:
                return new DataflowJobConfiguration(jobCoreConfiguration, jobConfiguration.getJobClass(), true);
            case SCRIPT:
                return new ScriptJobConfiguration(jobCoreConfiguration, "");
            default:
                return null;
        }
    }

}