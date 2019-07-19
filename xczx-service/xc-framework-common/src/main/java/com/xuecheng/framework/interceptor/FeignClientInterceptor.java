package com.xuecheng.framework.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**Feign 拦截器
 * @author Kiku
 * @date 2019/7/18 15:27
 */
public class FeignClientInterceptor implements RequestInterceptor {


    /**每次feign 远程调用执行此方法
     * @param requestTemplate
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
            //取出当前请求的header
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes!=null){
            HttpServletRequest request = requestAttributes.getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames!=null){
                while (headerNames.hasMoreElements()){
                    String headerName = headerNames.nextElement();
                    String header = request.getHeader(headerName);
                    //将header向下传递
                    requestTemplate.header(headerName,header);
                }
            }
        }

        //将header向下传递
    }
}
