package com.enterprise.suporte.service;

import com.enterprise.suporte.dto.authentication.UpdateEmailDTO;
import com.enterprise.suporte.dto.authentication.UpdatePasswordDTO;
import com.enterprise.suporte.dto.user.UserRequestDTO;
import com.enterprise.suporte.dto.user.UserResponseDTO;
import com.enterprise.suporte.exception.BusinessException;
import com.enterprise.suporte.exception.OperationNotAllowedException;
import com.enterprise.suporte.mapper.UserMapper;
import com.enterprise.suporte.model.User;
import com.enterprise.suporte.repository.AuthorityRepository;
import com.enterprise.suporte.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public void updatePassword(UpdatePasswordDTO passwordDTO) {
        var loggedUser = getLoggedUser();
        validatePasswordUpdate(passwordDTO);
        loggedUser.setPassword(passwordEncoder.encode(passwordDTO.newPassword()));
        userRepository.save(loggedUser);
    }

    @Transactional
    public void updateEmail(UpdateEmailDTO emailDTO) {
        var loggedUser = getLoggedUser();
        validadeEmailUpdate(emailDTO);
        validateUniqueEmail(emailDTO.newEmail());
        loggedUser.setUsername(emailDTO.newEmail());
        userRepository.save(loggedUser);
    }

    @Transactional
    public UserResponseDTO registerUser(UserRequestDTO userDTO) {
        var user = userMapper.toUser(userDTO);
        validateUniqueEmail(userDTO.username());

        assignRoleByProfile(user);
        user.setIsActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDate.now());

        var savedUser = userRepository.save(user);
        return userMapper.toUserResponseDTO(savedUser);
    }

    protected User getLoggedUser() {
        var loggedUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(loggedUser instanceof UserDetails userDetails)) {
            throw new OperationNotAllowedException("Usuário não autenticado");
        }

        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceAccessException("Usuário não encontrado"));
    }

    private void validatePasswordUpdate(UpdatePasswordDTO updateDTO) {
        var loggedUser = getLoggedUser();
        if (!loggedUser.getIsActive()) {
            throw new DisabledException("Conta desativada. Contate o administrador.");
        }
        if (!passwordEncoder.matches(updateDTO.currentPassword(), loggedUser.getPassword())) {
            throw new BadCredentialsException("Senha atual incorreta");
        }
        if (passwordEncoder.matches(updateDTO.newPassword(), loggedUser.getPassword())) {
            throw new BusinessException("A nova senha deve ser diferente da atual");
        }
    }

    private void validadeEmailUpdate(UpdateEmailDTO updateDTO) {
        var loggedUser = getLoggedUser();
        if (!loggedUser.getIsActive()) {
            throw new DisabledException("Conta desativada. Contate o administrador.");
        }
        if (loggedUser.getUsername().equals(updateDTO.newEmail())) {
            throw new BusinessException("O novo email deve ser diferente do atual");
        }
    }

    private void validateUniqueEmail(String email) {
        var user = userRepository.findByUsername(email);
        if (user.isPresent()) {
            throw new BusinessException("Email já cadastrado: " + email);
        }
    }

    private void assignRoleByProfile(User user) {
        var authorityName = switch (user.getProfile()) {
            case ADMIN -> "ADMIN";
            case ATENDENTE -> "ATENDENTE";
            case CLIENTE -> "CLIENTE";
        };

        var authority = authorityRepository.findByName(authorityName).orElseThrow(
                () -> new ResourceAccessException("Autoridade não encontrada: " + authorityName));

        user.setAuthorities(List.of(authority));
    }
}
