package org.apache.skywalking.apm.plugin.bossexception;

import com.star.boss.skywalking.LogResAndReqConfig;
import com.star.boss.skywalking.domain.BossTag;
import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.context.CorrelationContext;
import org.apache.skywalking.apm.agent.core.context.tag.Tags;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.skywalking.apm.dependencies.io.netty.handler.codec.http.HttpMethod;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;



public class InvocableHandlerMethodInterceptor implements InstanceMethodsAroundInterceptor {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(InvocableHandlerMethodInterceptor.class);
    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
        //开启日志打印
        if(LogResAndReqConfig.getInstance().isEnable()) {
            CorrelationContext context = ContextManager.getCorrelationContext();
            Optional<String> httpMethod = context.get(BossTag.log);
            //是需要拦截的方法
            if (httpMethod.isPresent() && HttpMethod.GET.name().equals(httpMethod.get())) {
                AbstractSpan span = ContextManager.activeSpan();
                if(allArguments==null||allArguments.length==0){
                    log.info("request: {}","null");
                    span.tag(Tags.ofKey("request"),"null");
                }else {
                    String allArgsStr = Arrays.toString(allArguments);
                    log.info("request: {}",allArgsStr);
                    span.tag(Tags.ofKey("request"), allArgsStr);
                }
            }
        }
    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret) throws Throwable {
        return ret;
    }

    @Override
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Throwable t) {

    }
}
