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
 * 返回报文的拦截器
 *
 * @author YULY
 * @version 1.0
 * @date 2021/6/10 0010 上午 10:49
 */

public class TomcatOutputBufferInteceptor implements InstanceMethodsAroundInterceptor {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TomcatOutputBufferInteceptor.class);

    @Override
    public void beforeMethod(EnhancedInstance enhancedInstance, Method method, Object[] objects, Class<?>[] classes, MethodInterceptResult methodInterceptResult) throws Throwable {
            //开启日志打印
            if(LogResAndReqConfig.getInstance().isEnable()) {
                //是需要拦截的方法
                if (!ContextManager.getCorrelationContext().get(BossTag.log).isEmpty()) {
                    byte[] buffer = (byte[]) objects[0];
                    int len = (int) objects[2];
                    if(len<LogResAndReqConfig.getInstance().getMaxContentLen()){
                        String text = new String(buffer,0,len);
                        log.info("response: {}",text);
                        AbstractSpan span = ContextManager.activeSpan();
                        span.tag(Tags.ofKey("response"),text);
                    }

                }
            }
    }

    @Override
    public Object afterMethod(EnhancedInstance enhancedInstance, Method method, Object[] objects, Class<?>[] classes, Object o) throws Throwable {

        return o;
    }

    @Override
    public void handleMethodException(EnhancedInstance enhancedInstance, Method method, Object[] objects, Class<?>[] classes, Throwable throwable) {

    }
}