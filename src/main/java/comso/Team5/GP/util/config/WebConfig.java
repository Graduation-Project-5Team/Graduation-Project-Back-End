package comso.Team5.GP.util.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // application.yml에서 이미지를 저장할 디렉터리 파일 경로
    @Value("${file.upload-dir}")
    private String uploadDir;

    // CORS 전역 설정
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 모든 경로에 CORS 설정
        registry.addMapping("/**")
                // 프론트와 연결중인 도메인 설정
                .allowedOrigins("http://localhost:5173")
                // 허용할 메서드 설정
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                // 허용할 헤더 설정
                .allowedHeaders("*")
                // 허용할 인증 설정(쿠키 .. 등)
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // "/uploads/**" 형태의 URL 요청이 오면
        registry.addResourceHandler("/uploads/**")
                //file:/프로젝트 파일(현재는 프로젝트 파일로 지정되어있음)/images
                .addResourceLocations("file:///" + uploadDir);
    }
}