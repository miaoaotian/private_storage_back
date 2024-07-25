package com.self_back.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.self_back.utils.Constant;
import com.self_back.utils.ProcessUtils;
import com.self_back.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
public class ChatAiController {

    @PostMapping("/api/ask")
    public Result<?> talkWithGpt(@RequestBody Map<String, String> input) {
        String baseUrl = Constant.API_URL;
        String apiKey = Constant.API_KEY;
        String model = "gpt-4o-mini";
        String userMessage = input.get("input");
        String temperature = "0.7";
        String sessionId = "abcdef";
        String sessionLimit = "2";
        String cmd = String.format(
                "curl %s -H \"Content-Type: application/json\" -H \"Authorization: Bearer %s\" -d \"{\\\"model\\\":\\\"%s\\\", \\\"messages\\\":[{\\\"role\\\":\\\"user\\\", \\\"content\\\":\\\"%s\\\"}], \\\"temperature\\\":%s, \\\"session_id\\\":\\\"%s\\\", \\\"session_limit\\\":%s}\"",
                baseUrl, apiKey, model, userMessage, temperature, sessionId, sessionLimit
        );
        String result = ProcessUtils.executeCommand(cmd,false);

        JSONObject jsonObject = JSON.parseObject(extractJson(result));
        log.info("result:{}", jsonObject);
        String content = jsonObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        return Result.success(content);
    }
    // 提取JSON字符串
    public static String extractJson(String text) {
        int braceCount = 0;
        int startIndex = -1;
        int endIndex = -1;

        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '{') {
                if (braceCount == 0) {
                    startIndex = i;
                }
                braceCount++;
            } else if (text.charAt(i) == '}') {
                braceCount--;
                if (braceCount == 0 && startIndex != -1) {
                    endIndex = i + 1;
                    break;
                }
            }
        }

        if (startIndex != -1 && endIndex != -1) {
            return text.substring(startIndex, endIndex);
        }
        return null; // 如果没有找到合法的JSON，返回null
    }

}
