package com.enterprise.suporte.service;

import com.enterprise.suporte.dto.supportagent.SupportAgentRequestDTO;
import com.enterprise.suporte.dto.supportagent.SupportAgentResponseDTO;
import com.enterprise.suporte.dto.supportagent.UpdateAgentStatusDTO;
import com.enterprise.suporte.enuns.AgentStatus;
import com.enterprise.suporte.enuns.UserProfile;
import com.enterprise.suporte.exception.BusinessException;
import com.enterprise.suporte.exception.OperationNotAllowedException;
import com.enterprise.suporte.exception.ResourceNotFoundException;
import com.enterprise.suporte.mapper.SupportAgentMapper;
import com.enterprise.suporte.model.SupportAgent;
import com.enterprise.suporte.model.User;
import com.enterprise.suporte.repository.AuthorityRepository;
import com.enterprise.suporte.repository.SupportAgentRepository;
import com.enterprise.suporte.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SupportAgentService {

    private final SupportAgentRepository supportAgentRepository;
    private final SupportAgentMapper supportAgentMapper;
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public List<SupportAgentResponseDTO> getAllSupportAgents() {
        return supportAgentRepository.findAll().stream().map(supportAgentMapper::toSupportAgentResponseDTO).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE')")
    public SupportAgentResponseDTO getSupportAgentById(Long id) {
        return supportAgentRepository.findById(id).map(supportAgentMapper::toSupportAgentResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Atendente não encontrado: " + id));
    }

    @Transactional
    public SupportAgentResponseDTO createSupportAgent(SupportAgentRequestDTO supportAgentDTO) {
        validateUniqueEmail(supportAgentDTO.email());
        var supportAgent = supportAgentMapper.toSupportAgent(supportAgentDTO);

        supportAgent.setCreatedAt(LocalDate.now());
        supportAgent.setStatus(AgentStatus.OFFLINE);
        supportAgent.setPassword(passwordEncoder.encode(supportAgentDTO.password()));
        supportAgent.setIsActive(true);

        var savedSupportAgent = supportAgentRepository.save(supportAgent);
        createUserBasedOnSupportAgent(savedSupportAgent);
        return supportAgentMapper.toSupportAgentResponseDTO(savedSupportAgent);
    }

    @Transactional
    @PreAuthorize("hasRole('ATENDENTE')")
    public void updateSupportAgentStatus(UpdateAgentStatusDTO updateDTO) {
        var loggedUser = authenticationService.getLoggedUser();
        var supportAgent = loggedUser.getSupportAgent();
        if (supportAgent != null) {
            validateUpdateAgentStatus(supportAgent, updateDTO);
            supportAgent.setStatus(updateDTO.newStatus());
            supportAgentRepository.save(supportAgent);
        }
        else {
            throw new OperationNotAllowedException("Usuário logado não é um atendente");
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteSupportAgent(Long id) {
        var supportAgent = supportAgentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atendente não encontrado: " + id));

        validateDeleteSupportAgent(supportAgent.getIsActive());
        supportAgentRepository.delete(supportAgent);
    }

    private void createUserBasedOnSupportAgent(SupportAgent supportAgent) {
        var authority = authorityRepository.findByName("ATENDENTE")
                .orElseThrow(() -> new ResourceNotFoundException("Autoridade não encontrada"));

        var user = User.builder()
                .username(supportAgent.getEmail())
                .password(supportAgent.getPassword())
                .profile(UserProfile.ATENDENTE)
                .authorities(List.of(authority))
                .supportAgent(supportAgent)
                .isActive(true)
                .createdAt(supportAgent.getCreatedAt())
                .build();

        userRepository.save(user);
    }

    private void validateUniqueEmail(String email) {
        var user = userRepository.findByUsername(email);
        if (user.isPresent()) {
            throw new BusinessException("Email já cadastrado: " + email);
        }

    }

    private void validateUpdateAgentStatus(SupportAgent supportAgent, UpdateAgentStatusDTO updateDTO) {
        if (!supportAgent.getIsActive()) {
            throw new BusinessException("Não é possível alterar o status de um usuário desativado");
        }

        if (supportAgent.getStatus().equals(updateDTO.newStatus())) {
            throw new BusinessException("Status do atendente já é " + updateDTO.newStatus());
        }
    }

    private void validateDeleteSupportAgent(Boolean isActive) {
        if (isActive) {
            throw new BusinessException("Não é possível excluir um atendente ativo");
        }
    }
}
