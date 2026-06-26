package com.example.demo.api;

import com.example.demo.dto.security.DocsResponse;
import com.example.demo.dto.security.ProfileResponse;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ProfileController {

    @GetMapping("/profile")
    public ProfileResponse profile(Authentication authentication) {
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .toList();

        return new ProfileResponse(authentication.getName(), authorities);
    }

    @GetMapping("/docs")
    public DocsResponse docs() {
        return new DocsResponse(
                "Protected documentation endpoint for privileged readers",
                List.of("/swagger-ui.html", "/actuator/health", "/actuator/metrics"));
    }
}
