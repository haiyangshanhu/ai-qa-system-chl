package com.ai.qa.service.infrastructure.feign;

import java.net.InetSocketAddress;
import java.net.Proxy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class DeepSeekRestTemplateConfig {

    @Bean("deepseekRestTemplate")
    public RestTemplate deepseekRestTemplate() {
        // 创建不配置代理的 RestTemplate
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // factory.setProxy(createProxy());
        factory.setConnectTimeout(30_000); // 30秒连接超时
        factory.setReadTimeout(60_000); // 60秒读取超时

        log.info("DeepSeek 专用 RestTemplate 已创建（无代理，连接超时: 30s，读取超时: 60s）");
        return new RestTemplate(factory);
    }

    private Proxy createProxy() {
        // 创建代理对象
        String proxyHost = "9.36.235.13";
        int proxyPort = 8080;

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));

        return proxy;
    }

    /**
     * 配置系统代理属性（备用方案）
     */
    private void configureSystemProxy() {
        // 启用系统代理
        System.setProperty("java.net.useSystemProxies", "true");

        // 使用您的Mac代理配置
        String proxyHost = "9.36.235.13";
        String proxyPort = "8080";

        // 设置HTTP代理
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);

        // 设置HTTPS代理
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);

        // 设置不使用代理的主机（本地服务）
        System.setProperty("http.nonProxyHosts", "localhost|127.0.0.1|*.local|54.219.180.170");

        log.info("系统代理属性配置完成: {}:{}", proxyHost, proxyPort);
    }
}
