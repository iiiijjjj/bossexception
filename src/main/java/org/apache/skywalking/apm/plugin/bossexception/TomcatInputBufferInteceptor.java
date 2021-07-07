package org.apache.skywalking.apm.plugin.bossexception;

import com.star.boss.skywalking.LogResAndReqConfig;
import com.star.boss.skywalking.domain.BossTag;
import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.context.tag.Tags;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;

import java.lang.reflect.Method;

/**
 *
 * 非Get请求的拦截器
 *
 * @author YULY
 * @version 1.0
 * @date 2021/6/10 0010 上午 10:49
 */

public class TomcatInputBufferInteceptor implements InstanceMethodsAroundInterceptor {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TomcatInputBufferInteceptor.class);

    @Override
    public void beforeMethod(EnhancedInstance enhancedInstance, Method method, Object[] objects, Class<?>[] classes, MethodInterceptResult methodInterceptResult) throws Throwable {

    }

    @Override
    public Object afterMethod(EnhancedInstance enhancedInstance, Method method, Object[] objects, Class<?>[] classes, Object o) throws Throwable {
        //开启日志打印
        if(LogResAndReqConfig.getInstance().isEnable()) {
            //是需要拦截的方法
            if (!ContextManager.getCorrelationContext().get(BossTag.log).isEmpty()) {
                int finalLen = (int) o;
                byte[] buffer = (byte[]) objects[0];
                //内容段较小的请求才会上报请求
                if(finalLen<buffer.length) {
                    String text = new String(buffer,0,finalLen);
                    log.info("request: {}",text);
                    AbstractSpan span = ContextManager.activeSpan();
                    span.tag(Tags.ofKey("request"),text);
                }
            }
        }
        return o;
    }

    @Override
    public void handleMethodException(EnhancedInstance enhancedInstance, Method method, Object[] objects, Class<?>[] classes, Throwable throwable) {

    }
}