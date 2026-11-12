package com.rafael.todo.module.auth.controller.mapper;

import com.rafael.todo.module.auth.controller.dto.LoginDto;
import com.rafael.todo.module.auth.controller.dto.ResponseAuthDto;
import com.rafael.todo.module.auth.controller.dto.ResponseRefreshTokenDto;
import com.rafael.todo.module.auth.model.RefreshTokenModel;
import com.rafael.todo.module.auth.service.dto.AuthTokensDtoService;
import com.rafael.todo.module.auth.service.dto.LoginDtoService;
import com.rafael.todo.module.auth.service.dto.RefreshTokenRequestDtoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public final class AuthMapperController {

    private AuthMapperController() {
    }

    public static LoginDtoService toServiceDto(LoginDto dto) {
        return new LoginDtoService(dto.email(), dto.password());
    }

    public static RefreshTokenRequestDtoService toServiceDto(String refreshToken) {
        return new RefreshTokenRequestDtoService(refreshToken);
    }

    public static ResponseAuthDto toResponseDto(AuthTokensDtoService dto) {
        return new ResponseAuthDto(dto.accessToken());
    }

    public static ResponseRefreshTokenDto toResponseDto(RefreshTokenModel model) {
        return new ResponseRefreshTokenDto(
                model.getIdValue(),
                model.getUserIdValue(),
                model.getExpiresAtValue(),
                model.isRevokedValue(),
                model.getCreatedAt()
        );
    }

    public static Page<ResponseRefreshTokenDto> toPage(List<RefreshTokenModel> models, int page, int size) {
        var pageable = PageRequest.of(page, size);
        var pages = new PageImpl<>(models, pageable, models.size());
        return pages.map(AuthMapperController::toResponseDto);
    }
}

