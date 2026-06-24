package com.re.artworkgallery_security.controller;

import com.re.artworkgallery_security.dto.AuthRequest;
import com.re.artworkgallery_security.dto.AuthResponse;
import com.re.artworkgallery_security.dto.RefreshRequest;
import com.re.artworkgallery_security.model.Account;
import com.re.artworkgallery_security.model.TokenSession;
import com.re.artworkgallery_security.repository.AccountRepository;
import com.re.artworkgallery_security.repository.TokenSessionRepository;
import com.re.artworkgallery_security.security.JwtUtils;
import com.re.artworkgallery_security.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/gallery/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TokenSessionRepository tokenSessionRepository;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Save TokenSession
        TokenSession tokenSession = new TokenSession();
        tokenSession.setAccountId(userDetails.getId());
        tokenSession.setRefreshTokenValue(refreshToken);
        tokenSession.setIsExpired(false);
        tokenSession.setIsRevoked(false);
        tokenSessionRepository.save(tokenSession);

        return ResponseEntity.ok(new AuthResponse(jwt, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshtoken(@RequestBody RefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        Optional<TokenSession> tokenSessionOpt = tokenSessionRepository.findByRefreshTokenValue(requestRefreshToken);

        if (tokenSessionOpt.isPresent()) {
            TokenSession tokenSession = tokenSessionOpt.get();
            if (!tokenSession.getIsRevoked() && !tokenSession.getIsExpired()) {
                if (jwtUtils.validateJwtToken(requestRefreshToken)) {
                    Account account = accountRepository.findById(tokenSession.getAccountId()).orElseThrow();
                    
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            UserDetailsImpl.build(account), null, UserDetailsImpl.build(account).getAuthorities());
                    
                    String newJwt = jwtUtils.generateJwtToken(authentication);
                    return ResponseEntity.ok(new AuthResponse(newJwt, requestRefreshToken));
                }
            }
        }
        return ResponseEntity.status(403).body("Invalid refresh token");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long accountId = userDetails.getId();
            
            // Xây dựng API POST /api/gallery/auth/logout để đánh dấu is_revoked = true cho token hiện tại bằng cách dùng Stream duyệt qua các phiên làm việc của user trong DB
            List<TokenSession> sessions = tokenSessionRepository.findByAccountId(accountId);
            sessions.stream()
                    .filter(session -> !session.getIsRevoked())
                    .forEach(session -> session.setIsRevoked(true));
            tokenSessionRepository.saveAll(sessions);
            
            return ResponseEntity.ok("Log out successful");
        }
        return ResponseEntity.status(401).body("Not authenticated");
    }
}
