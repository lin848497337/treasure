package sample.view;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Predicates;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Test {
    public static void main(String[] args) throws ExecutionException, RetryException {
        RetryerBuilder.newBuilder()
            .retryIfResult(Predicates.isNull())// 1.1当重试的方法返回null时候进行重试
            .retryIfException()
            .withStopStrategy(StopStrategies.stopAfterAttempt(3))// 1.3尝试执行三次（也就是重试2次）
            .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS))//1.4重试间隔
            .build().call(()->{
                System.out.println("retry execute");
                throw new Exception("retry shelf design error");
            });
    }
}
