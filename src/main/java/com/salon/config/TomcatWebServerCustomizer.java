package com.salon.config;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 이 클래스가 스프링 설정 파일임을 명시합니다.
public class TomcatWebServerCustomizer {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> customizeTomcat() {
        return factory -> {
            factory.addConnectorCustomizers(connector -> {
                // 이 부분이 핵심입니다: Tomcat의 HTTP 커넥터에 maxRequestParts를 설정합니다.
                // maxRequestParts는 Multipart 요청에서 허용되는 개별 파트(필드, 파일)의 최대 개수입니다.
                // 기본값은 10000이지만, 파일 업로드 시에만 영향을 받는 maxPartCount가 문제일 수 있습니다.
                // 50은 예시이며, 실제 필요한 파트 개수보다 충분히 크게 설정해야 합니다.
                connector.setProperty("maxRequestParts", "500"); // <-- 이 값을 50으로 설정 (혹은 더 크게)
            });
        };
    }
}