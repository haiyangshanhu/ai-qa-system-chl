package com.ai.qa.service.infrastructure.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DeepSeek AI客户端
 * 
 * 负责与DeepSeek API进行通信，发送问题并获取AI回答
 * 封装了HTTP请求的细节，提供简单易用的接口
 * 
 */
@Slf4j
@Component
public class DeepSeekClient {

    /**
     * DeepSeek API的基础URL
     */
    @Value("${deepseek.api.base-url:https://api.deepseek.com/v1}")
    private String baseUrl;

    /**
     * DeepSeek API Key
     */
    @Value("${deepseek.api.key:YOUR_DEEPSEEK_API_KEY_HERE}")
    private String apiKey;

    /**
     * 使用的模型名称
     */
    @Value("${deepseek.api.model:deepseek-chat}")
    private String model;

    private final RestTemplate restTemplate;

    public DeepSeekClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        log.info("DeepSeekClient 初始化完成");
    }

    /**
     * 向DeepSeek AI发送问题并获取回答
     */
    public String askQuestion(String question) {
        log.info("开始调用 DeepSeek API，问题长度: {}", question.length());

        if ("YOUR_DEEPSEEK_API_KEY_HERE".equals(apiKey)) {
            log.warn("DeepSeek API Key 未配置，返回模拟回答");
            return generateMockResponse(question);
        }

        try {
            String url = baseUrl + "/chat/completions";

            Map<String, Object> requestBody = buildRequestBody(question);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey); // DeepSeek使用Bearer Token认证

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            long start = System.currentTimeMillis();
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
            long end = System.currentTimeMillis();

            log.info("DeepSeek API 调用成功，耗时: {} ms", end - start);
            return parseResponse(response.getBody());

        } catch (Exception e) {
            log.error("调用 DeepSeek API 失败: {}", e.getMessage(), e);
            log.info("使用模拟回答作为备用方案");
            return generateMockResponse(question);
        }
    }

    /**
     * 构建DeepSeek API请求体
     */
    private Map<String, Object> buildRequestBody(String question) {
        Map<String, Object> requestBody = new HashMap<>();

        // DeepSeek的请求格式与OpenAI兼容
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", question);

        requestBody.put("model", model);
        requestBody.put("messages", List.of(message));
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 1000);
        requestBody.put("stream", false);

        return requestBody;
    }

    /**
     * 解析DeepSeek API响应
     */
    private String parseResponse(Map<String, Object> responseBody) {
        try {
            Object choicesObj = responseBody.get("choices");
            if (!(choicesObj instanceof List) || ((List<?>) choicesObj).isEmpty()) {
                log.warn("DeepSeek API响应中choices格式不正确或为空");
                return "抱歉，我现在无法回答这个问题。";
            }

            Map<?, ?> firstChoice = (Map<?, ?>) ((List<?>) choicesObj).get(0);
            Object messageObj = firstChoice.get("message");
            if (!(messageObj instanceof Map)) {
                log.warn("DeepSeek API响应中message格式不正确");
                return "抱歉，我现在无法回答这个问题。";
            }

            Map<?, ?> message = (Map<?, ?>) messageObj;
            Object contentObj = message.get("content");

            return contentObj != null ? contentObj.toString().trim() : "抱歉，我现在无法回答这个问题。";

        } catch (Exception e) {
            log.error("解析DeepSeek API响应失败: {}", e.getMessage(), e);
            return "抱歉，处理回答时出现了问题。";
        }
    }

    /**
     * 生成模拟回答（当API调用失败或未配置时使用）
     */
    private String generateMockResponse(String question) {
        // 可以复用你原来的模拟回答逻辑
        String lowerQuestion = question.toLowerCase();

        if (lowerQuestion.contains("你好") || lowerQuestion.contains("hello") || lowerQuestion.contains("介绍")) {
            return "你好！我是DeepSeek AI助手，很高兴为您服务！\n\n" +
                    "我可以帮助您：\n" +
                    "• 回答各种问题\n" +
                    "• 提供信息查询\n" +
                    "• 协助解决问题\n" +
                    "• 进行日常对话\n\n" +
                    "请随时告诉我您需要什么帮助！";
        } else if (lowerQuestion.contains("天气")) {
            return "关于天气查询：\n\n" +
                    "抱歉，我目前无法获取实时天气信息。建议您：\n" +
                    "• 查看手机天气应用\n" +
                    "• 访问天气预报网站\n" +
                    "• 询问语音助手\n\n" +
                    "如果您有其他问题，我很乐意帮助您！";
        } else {
            return String.format("感谢您的提问：\"%s\"\n\n" +
                    "我正在努力理解您的问题。作为DeepSeek AI助手，我会尽力为您提供有用的信息和建议。\n\n" +
                    "如果您能提供更多详细信息，我将能够给出更准确的回答。\n\n" +
                    "请问您还有什么其他问题需要我帮助解决吗？",
                    question);
        }
    }
}