package com.example.nevera.service;

import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OcrService {

    private final ImageAnnotatorClient visionClient;

    public List<String> extractTextFromImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.EMPTY_IMAGE_FILE);
        }

        try {
            ByteString imgBytes = ByteString.copyFrom(file.getBytes());
            Image img = Image.newBuilder().setContent(imgBytes).build();

            Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();

            BatchAnnotateImagesResponse response = visionClient.batchAnnotateImages(List.of(request));
            List<AnnotateImageResponse> responses = response.getResponsesList();

            List<String> detectedTexts = new ArrayList<>();

            for (AnnotateImageResponse res : responses) {

                if (res.hasError()) {
                    log.error("Google Vision API Error: {}", res.getError().getMessage());
                    throw new BusinessException(ErrorCode.GOOGLE_VISION_API_ERROR);
                }

                if (!res.getTextAnnotationsList().isEmpty()) {
                    String fullText = res.getTextAnnotationsList().get(0).getDescription();
                    detectedTexts.addAll(Arrays.asList(fullText.split("\n")));
                }
            }

            return detectedTexts;

        } catch (IOException e) {

            log.error("OCR Image Processing IO Error", e);
            throw new BusinessException(ErrorCode.OCR_PROCESS_ERROR);
        } catch (Exception e) {

            log.error("Unexpected OCR Error", e);
            throw e;
        }
    }
}