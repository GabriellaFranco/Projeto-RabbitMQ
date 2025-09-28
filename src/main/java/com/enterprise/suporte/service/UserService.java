package com.enterprise.suporte.service;

import com.enterprise.suporte.dto.user.UserResponseDTO;
import com.enterprise.suporte.exception.BusinessException;
import com.enterprise.suporte.exception.ResourceNotFoundException;
import com.enterprise.suporte.mapper.UserMapper;
import com.enterprise.suporte.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        var users = userRepository.findAll(pageable);
        return users.map(userMapper::toUserResponseDTO);
    }

    @PreAuthorize("hasRole('ADMIN', 'ATENDENTE')")
    public UserResponseDTO getUserById(Long id) {
        return userRepository.findById(id).map(userMapper::toUserResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
        validateUserDelete(user.getIsActive());
        userRepository.delete(user);
    }

    private void validateUserDelete(Boolean isActive) {
        if (isActive) {
            throw new BusinessException("Não é possível excluir um usuário ativo");
        }
    }
}
