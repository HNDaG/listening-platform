package com.nikitahohulia.listeningplatform.bpp

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.cglib.proxy.Enhancer
import org.springframework.cglib.proxy.MethodInterceptor
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class LogOnExceptionAnnotationBeanPostProcessor : BeanPostProcessor {

    private val beanMap = mutableMapOf<String, KClass<*>>()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        val beanClass = bean::class
        if (beanClass.java.isAnnotationPresent(LogOnException::class.java)) {
            beanMap[beanName] = beanClass
        }
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        return beanMap[beanName]
            ?.let { originClass -> decorate(originClass, bean) }
            ?: bean
    }

    private fun decorate(originClass: KClass<*>, bean: Any): Any {
        return Enhancer().apply {
            setSuperclass(originClass.java)
            setCallback(buildInterceptor(bean))
        }.create()
    }

    private fun buildInterceptor(
        bean: Any,
    ): MethodInterceptor = MethodInterceptor { _, method, args, proxy ->
        try {
            proxy.invoke(bean, args)
        } catch (ex: RuntimeException) {
            logger.error(
                "[ERROR] {} - {} threw {}; Message: {}",
                bean.javaClass.simpleName,
                method.name,
                ex::class.simpleName,
                ex.message
            )
            throw ex
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(LogOnExceptionAnnotationBeanPostProcessor::class.java)
    }
}
