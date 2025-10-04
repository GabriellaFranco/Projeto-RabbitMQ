package com.enterprise.suporte.service;

import com.enterprise.suporte.dto.supportagent.SupportAgentRequestDTO;
import com.enterprise.suporte.dto.supportagent.SupportAgentResponseDTO;
import com.enterprise.suporte.dto.supportagent.UpdateAgentStatusDTO;
import com.enterprise.suporte.enuns.AgentStatus;
import com.enterprise.suporte.enuns.UserProfile;
import com.enterprise.suporte.exception.ResourceNotFoundException;
import com.enterprise.suporte.mapper.SupportAgentMapper;
import com.enterprise.suporte.model.Authority;
import com.enterprise.suporte.model.SupportAgent;
import com.enterprise.suporte.model.User;
import com.enterprise.suporte.repository.AuthorityRepository;
import com.enterprise.suporte.repository.SupportAgentRepository;
import com.enterprise.suporte.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SupportAgentServiceTest {

    @Mock
    private SupportAgentRepository supportAgentRepository;

    @Mock
    private SupportAgentMapper supportAgentMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthorityRepository authorityRepository;

    @InjectMocks
    private SupportAgentService supportAgentService;

    private User user;
    private Authority authority;
    private SupportAgent supportAgent;
    private SupportAgentRequestDTO supportAgentRequestDTO;
    private SupportAgentResponseDTO supportAgentResponseDTO;

    @BeforeEach
    void setup() {
        authority = Authority.builder()
                .id(1L)
                .name("ATENDENTE")
                .build();

        user = User.builder()
                .id(1L)
                .username("dev@teste.com")
                .password("dev")
                .profile(UserProfile.ATENDENTE)
                .authorities(List.of(authority))
                .isActive(true)
                .build();

        supportAgent = SupportAgent.builder()
                .id(1L)
                .name("Gabriella")
                .email("dev@teste.com")
                .password("dev")
                .status(AgentStatus.ONLINE)
                .isActive(true)
                .build();

        supportAgentRequestDTO = SupportAgentRequestDTO.builder()
                .name("Gabriella")
                .email("dev@teste.com")
                .build();

        supportAgentResponseDTO = SupportAgentResponseDTO.builder()
                .id(1L)
                .name("Gabriella")
                .email("dev@teste.com")
                .status(AgentStatus.ONLINE)
                .isActive(true)
                .build();
    }

    @Test
    void getAllSupportAgents_WhenCalled_ShouldReturnAListOfAgents() {
        when(supportAgentRepository.findAll()).thenReturn(List.of(supportAgent));
        when(supportAgentMapper.toSupportAgentResponseDTO(supportAgent)).thenReturn(supportAgentResponseDTO);

        var result = supportAgentService.getAllSupportAgents();

        assertThat(result)
                .isNotEmpty()
                .hasSize(1);
    }

    @Test
    void getSupportAgentById_WhenCalled_ShouldReturnASupportAgent() {
        when(supportAgentRepository.findById(supportAgent.getId())).thenReturn(Optional.of(supportAgent));
        when(supportAgentMapper.toSupportAgentResponseDTO(supportAgent)).thenReturn(supportAgentResponseDTO);

        var result = supportAgentService.getSupportAgentById(supportAgent.getId());

        assertThat(result)
                .isNotNull()
                .extracting(SupportAgentResponseDTO::id, SupportAgentResponseDTO::name)
                .containsExactly(1L, "Gabriella");
    }

    @Test
    void getSupportAgentById_WhenIdDoesNotExist_ShouldThrowException() {
        when(supportAgentRepository.findById(supportAgent.getId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> supportAgentService.getSupportAgentById(supportAgent.getId()));
    }

    @Test
    void createSupportAgent_WhenCalled_ShouldCreateSuccessfully() {
        when(supportAgentMapper.toSupportAgent(supportAgentRequestDTO)).thenReturn(supportAgent);
        when(supportAgentRepository.save(supportAgent)).thenReturn(supportAgent);
        when(authorityRepository.findByName("ATENDENTE")).thenReturn(Optional.of(authority));
        when(supportAgentMapper.toSupportAgentResponseDTO(supportAgent)).thenReturn(supportAgentResponseDTO);

        var result = supportAgentService.createSupportAgent(supportAgentRequestDTO);

        assertThat(result)
                .isNotNull()
                .extracting(SupportAgentResponseDTO::id, SupportAgentResponseDTO::name)
                .containsExactly(1L, "Gabriella");
    }

    @Test
    void updateSupportAgentStatus_WhenCalled_ShouldUpdateSuccessfully() {
        when(supportAgentRepository.findById(supportAgent.getId())).thenReturn(Optional.of(supportAgent));
        var statusDTO = new UpdateAgentStatusDTO(AgentStatus.OFFLINE);
        when(supportAgentRepository.save(supportAgent)).thenReturn(supportAgent);

        supportAgentService.updateSupportAgentStatus(statusDTO);

        assertEquals(AgentStatus.OFFLINE, supportAgent.getStatus());
        assertEquals(1L, supportAgent.getId());
    }

    @Test
    void deleteSupportAgent_WhenCalled_ShouldDeleteSuccessfully() {
        when(supportAgentRepository.findById(supportAgent.getId())).thenReturn(Optional.of(supportAgent));
        supportAgent.setIsActive(false);
        supportAgentService.deleteSupportAgent(supportAgent.getId());

        verify(supportAgentRepository).findById(supportAgent.getId());
        verify(supportAgentRepository).delete(supportAgent);
    }

    @Test
    void deleteSupportAgent_WhenIdDoesNotExist_ShouldThrowException() {
        when(supportAgentRepository.findById(supportAgent.getId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> supportAgentService.deleteSupportAgent(supportAgent.getId()));
    }
}
