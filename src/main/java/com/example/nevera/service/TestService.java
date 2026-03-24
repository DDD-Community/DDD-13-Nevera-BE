package com.example.nevera.service;

import com.example.nevera.entity.TestEntity;
import com.example.nevera.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용으로 성능 최적화
public class TestService {

    private final TestRepository testRepository;

    public List<TestEntity> findAllPosts() {
        return testRepository.findAll();
    }
}
