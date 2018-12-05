package com.nsm.common.utils;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nieshuming on 2018/9/19
 */
class IdWorker {
    private final static Logger logger = LoggerFactory.getLogger(IdWorker.class);

    private static final long epoch = 1537321574000L;   // 时间起始标记点，作为基准，一般取系统的最近时间
    private static final long workerIdBits = 10L;      // 机器标识位数
    private static final long maxWorkerId = -1L ^ -1L << workerIdBits;// 机器ID最大值: 1023
    private static final long sequenceBits = 10L;      //毫秒内自增位

    private final long workerIdShift = this.sequenceBits;                             // 10
    private final long timestampLeftShift = this.sequenceBits + this.workerIdBits;// 20
    private final long sequenceMask = -1L ^ -1L << this.sequenceBits;
    private final long workerId;
    private long sequence = 0L;  // 0，并发控制
    private long lastTimestamp = -1L;

    private final static class IdWorkerBuilder {
        private final static IdWorker BUILDER = new IdWorker();
    }

    public static IdWorker getInstance(){
        return IdWorkerBuilder.BUILDER;
    }

    private IdWorker(){
        this(getWorkId());
    }

    private IdWorker(long workerId) {
        if (workerId > this.maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", this.maxWorkerId));
        }
        this.workerId = workerId;
    }

    synchronized long nextId() {
        long timestamp = this.timeGen();
        if (this.lastTimestamp == timestamp) { // 如果上一个timestamp与新产生的相等，则sequence加一(0-4095循环); 对新的timestamp，sequence从0开始
            this.sequence = this.sequence + 1 & this.sequenceMask;
            if (this.sequence == 0) {
                timestamp = this.tilNextMillis(this.lastTimestamp);// 重新生成timestamp
            }
        } else {
            this.sequence = 0;
        }

        if (timestamp < this.lastTimestamp) {
            logger.error(String.format("clock moved backwards.Refusing to generate id for %d milliseconds", (this.lastTimestamp - timestamp)));
            throw new RuntimeException(String.format("clock moved backwards.Refusing to generate id for %d milliseconds", (this.lastTimestamp - timestamp)));
        }

        this.lastTimestamp = timestamp;
        return timestamp - this.epoch << this.timestampLeftShift | this.workerId << this.workerIdShift | this.sequence;
    }

    protected static long getWorkId() {
        //当机器数量接近‘maxWorkerId’，此实现需要修改
        return RandomUtils.nextLong(0, maxWorkerId);
    }


    /**
     * 等待下一个毫秒的到来, 保证返回的毫秒数在参数lastTimestamp之后
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    /**
     * 获得系统当前毫秒数
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        for(int i = 0 ; i< 10 ; i++) {
            System.out.println(IdWorker.getInstance().nextId());
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
