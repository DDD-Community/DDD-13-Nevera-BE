package com.example.nevera.repository;

import com.example.nevera.dto.inventory.OcrRefineResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OcrJobStore {

    public enum JobStatus {
        READY, PROCESSING, DONE
    }

    private final Map<String, JobStatus> jobs = new ConcurrentHashMap<>();
    private final Map<String, List<OcrRefineResponse>> results = new ConcurrentHashMap<>();

    public String createJob() {
        String jobId = UUID.randomUUID().toString();
        jobs.put(jobId, JobStatus.READY);
        return jobId;
    }

    public void updateStatus(String jobId, JobStatus status) {
        jobs.put(jobId, status);
    }

    public boolean exists(String jobId) {
        return jobs.containsKey(jobId);
    }

    public void saveResult(String jobId, List<OcrRefineResponse> result) {
        results.put(jobId, result);
    }

    public List<OcrRefineResponse> getResult(String jobId) {
        return results.get(jobId);
    }

    public void remove(String jobId) {
        jobs.remove(jobId);
        results.remove(jobId);
    }
}