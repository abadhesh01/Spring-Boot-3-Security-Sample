package demo.sb3.security.jwt.app.dto;

import java.util.UUID;

public record SecuredUserInfo(UUID id, String username) {
}
