package phb.ebookstore.dev.security.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import phb.ebookstore.dev.repository.TokenRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  private final TokenRepository tokenRepository;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
    	System.out.println("No Authorization Header Bearer found to logout. Please retry!");
      return;
    }
    jwt = authHeader.substring(7);
    var storedToken = tokenRepository.findByToken(jwt)
        .orElse(null);
    if (storedToken != null) {
      storedToken.setExpired(true);
      storedToken.setRevoked(true);
      tokenRepository.save(storedToken);
      SecurityContextHolder.clearContext();
    }
  }
}