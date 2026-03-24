package com.example.nevera.controller;

import com.example.nevera.entity.TestEntity;
import com.example.nevera.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/dbtest")
    public List<TestEntity> getAllPosts() {
        return testService.findAllPosts();
    }
}
