package com.salon.util;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class OcrUtil {
    public String extractText(MultipartFile file){
        try(ImageAnnotatorClient vision = ImageAnnotatorClient.create()){
            ByteString imgBytes = ByteString.copyFrom(file.getBytes());

            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();

            AnnotateImageRequest request =
                    AnnotateImageRequest.newBuilder()
                            .addFeatures(feat)
                            .setImage(img)
                            .build();

            List<AnnotateImageRequest> requests = new ArrayList<>();
            requests.add(request);

            AnnotateImageResponse response = vision.batchAnnotateImages(requests).getResponses(0);
            if(response.hasError()){
                log.error("OCR 오류: {}", response.getError().getMessage());
                return null;
            }

            String fullText = response.getFullTextAnnotation().getText();
            log.info("OCR 추출 결과: \n{}", fullText);

            return fullText;
        } catch (Exception e){
            log.error("OCR 처리 실패", e);
            return null;
        }
    }
}
