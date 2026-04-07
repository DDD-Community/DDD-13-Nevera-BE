package com.example.nevera.controller;

import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.example.nevera.entity.TestEntity;
import com.example.nevera.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/dbtest")
    public TestEntity getAllPosts() {
        return testService.findMemberById();
    }

    @GetMapping("/success")
    public Map<String, String> testSuccess() {

        Map<String, String> data = new HashMap<>();
        data.put("message", "성공했어요!");
        return data;
    }

}
