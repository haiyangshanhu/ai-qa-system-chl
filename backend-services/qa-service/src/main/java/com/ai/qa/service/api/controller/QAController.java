package com.ai.qa.service.api.controller;

import com.ai.qa.service.api.dto.AskQaRequest;
import com.ai.qa.service.api.dto.QAHistoryDTO;
import com.ai.qa.service.api.dto.SaveHistoryRequest;
import com.ai.qa.service.application.dto.QAHistoryQuery; // 确保这个导入正确
import com.ai.qa.service.application.dto.SaveHistoryCommand;
import com.ai.qa.service.application.service.QAHistoryService;
import com.ai.qa.service.domain.service.QAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * QA问答控制器
 * 提供问答相关的REST API接口
 */
@RestController
@RequestMapping("/api/qa")
@RequiredArgsConstructor
public class QAController {

    private final QAService qaService;
    private final QAHistoryService qaHistoryService;

    /**
     * 处理用户问题
     *
     * @param userId    用户ID
     * @param question  用户问题
     * @param sessionId 会话ID（可选）
     * @return AI生成的回答
     */
    @PostMapping("/ask")
    public ResponseEntity<String> askQuestion(
            @RequestBody AskQaRequest request) {
        try {
            String answer = qaService.processQuestion(request.getUserId(), request.getQuestion(),
                    request.getSessionId());
            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("处理问题时发生错误: " + e.getMessage());
        }
    }

    /**
     * 保存问答历史记录
     *
     * @param request 保存请求
     * @return 保存后的历史记录
     */
    @PostMapping("/save")

    public ResponseEntity<QAHistoryDTO> saveHistory(@RequestBody SaveHistoryRequest request) {
        SaveHistoryCommand command = new SaveHistoryCommand();
        command.setUserId(request.getUserId());
        command.setQuestion(request.getQuestion());
        command.setAnswer(request.getAnswer());
        command.setSessionId(request.getSessionId());
        command.setRagContext(request.getRagContext());

        QAHistoryDTO dto = qaHistoryService.saveHistory(command);
        return ResponseEntity.ok(dto);
    }

    /**
     * 根据ID获取问答记录
     *
     * @param id 记录ID
     * @return 问答记录
     */
    @GetMapping("/history/{id}")
    public ResponseEntity<QAHistoryDTO> getHistoryById(@PathVariable String id) {
        try {
            QAHistoryDTO dto = qaHistoryService.getHistoryById(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 查询用户问答历史
     *
     * @param userId    用户ID
     * @param sessionId 会话ID（可选）
     * @param page      页码（可选，默认1）
     * @param size      每页大小（可选，默认10）
     * @return 问答历史列表
     */
    @GetMapping("/history/user/{userId}")
    public ResponseEntity<List<QAHistoryDTO>> getUserHistory(
            @PathVariable String userId,
            @RequestParam(required = false) String sessionId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        QAHistoryQuery query = new QAHistoryQuery();
        query.setUserId(userId);
        query.setSessionId(sessionId);
        query.setPage(page);
        query.setSize(size);
        query.setDesc(true);

        List<QAHistoryDTO> historyList = qaHistoryService.queryUserHistory(query);
        return ResponseEntity.ok(historyList);
    }

    /**
     * 查询会话问答历史
     *
     * @param sessionId 会话ID
     * @return 会话问答历史
     */
    @GetMapping("/history/session/{sessionId}")
    public ResponseEntity<List<QAHistoryDTO>> getSessionHistory(@PathVariable String sessionId) {
        List<QAHistoryDTO> historyList = qaHistoryService.querySessionHistory(sessionId);
        return ResponseEntity.ok(historyList);
    }

    /**
     * 删除问答记录
     *
     * @param id 记录ID
     * @return 操作结果
     */
    @DeleteMapping("/history/{id}")
    public ResponseEntity<Void> deleteHistory(@PathVariable String id) {
        try {
            qaHistoryService.deleteHistory(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 清空会话历史
     *
     * @param sessionId 会话ID
     * @return 操作结果
     */
    @DeleteMapping("/history/session/{sessionId}")
    public ResponseEntity<Void> clearSessionHistory(@PathVariable String sessionId) {
        try {
            qaHistoryService.clearSessionHistory(sessionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取用户问答统计
     *
     * @param userId 用户ID
     * @return 问答记录数量
     */
    @GetMapping("/stats/user/{userId}")
    public ResponseEntity<Long> getUserHistoryCount(@PathVariable String userId) {
        try {
            long count = qaHistoryService.getUserHistoryCount(userId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Feign客户端测试接口
     *
     * @return 测试结果
     */
    @GetMapping("/test")
    public String testFeign() {
        System.out.println("测试feign");
        // return qaService.processQuestion(1L, "测试问题", "test-session");
        return "OK";
    }
}