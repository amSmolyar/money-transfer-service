package ru.netology.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.netology.demo.controller.Controller;
import ru.netology.demo.requestObjects.Amount;
import ru.netology.demo.requestObjects.TransferParameters;

import java.util.List;

@Configuration
public class MyWebConfig implements WebMvcConfigurer {
/*
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new TransferParamResolver());
    }

    private class TransferParamResolver implements HandlerMethodArgumentResolver {
        @Override
        public boolean supportsParameter(MethodParameter methodParameter) {
            return methodParameter.hasParameterAnnotation(Controller.TransferDeserializer.class);
        }

        @Override
        public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
            String cardFromNumber = nativeWebRequest.getParameter("cardFromNumber");
            String cardFromValidTill = nativeWebRequest.getParameter("cardFromValidTill");
            String cardFromCVV = nativeWebRequest.getParameter("cardFromCVV");
            String cardToNumber = nativeWebRequest.getParameter("cardToNumber");
            int value = Integer.parseInt(nativeWebRequest.getParameter("value"));
            String currency = nativeWebRequest.getParameter("currency");

            Amount amount = new Amount(value, currency);

            return new TransferParameters(cardFromNumber, cardFromValidTill, cardFromCVV, cardToNumber, amount);
        }
    }
*/
}
