package com.enterprise.suporte.service;

import com.enterprise.suporte.dto.user.UserRequestDTO;
import com.enterprise.suporte.dto.user.UserResponseDTO;
import com.enterprise.suporte.enuns.UserProfile;
import com.enterprise.suporte.exception.BusinessException;
import com.enterprise.suporte.exception.ResourceNotFoundException;
import com.enterprise.suporte.mapper.UserMapper;
import com.enterprise.suporte.model.Authority;
import com.enterprise.suporte.model.User;
import com.enterprise.suporte.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private Authority adminAuthority;
    private Authority clienteAuthority;
    private User user;
    private UserRequestDTO userRequestDTO;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setup() {
        adminAuthority = Authority.builder()
                .id(1L)
                .name("ADMIN")
                .build();

        clienteAuthority = Authority.builder()
                .id(2L)
                .name("CLIENTE")
                .build();

        user = User.builder()
                .id(1L)
                .username("dev@teste.com")
                .password("dev")
                .isActive(true)
                .profile(UserProfile.ADMIN)
                .authorities(List.of(adminAuthority))
                .build();

        userRequestDTO = UserRequestDTO.builder()
                .username("dev2@teste.com")
                .password("dev")
                .profile(UserProfile.CLIENTE)
                .build();

        userResponseDTO = UserResponseDTO.builder()
                .id(1L)
                .username("dev@teste.com")
                .isActive(true)
                .userProfile(UserProfile.ADMIN)
                .build();
    }

    @Test
    void getAllUsers_WhenCalled_ShouldReturnPageOfUsers() {
        var pageable = PageRequest.of(1, 10);
        var listOfUsers = List.of(user);
        var pageUsers = new PageImpl<>(listOfUsers, pageable, listOfUsers.size());

        when(userRepository.findAll(pageable)).thenReturn(pageUsers);
        when(userMapper.toUserResponseDTO(user)).thenReturn(userResponseDTO);

        var result = userService.getAllUsers(pageable);

        assertThat(result)
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void getUserById_WhenCalled_ShouldReturnUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDTO(user)).thenReturn(userResponseDTO);

        var result = userService.getUserById(user.getId());

        assertThat(result)
                .isNotNull()
                .extracting(UserResponseDTO::username, UserResponseDTO::userProfile)
                .containsExactly("dev@teste.com", UserProfile.ADMIN);
    }

    @Test
    void getUserById_WhenIdDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(user.getId()));
    }

    @Test
    void deleteUser_WhenCalled_ShouldDeleteSuccessfully() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        user.setIsActive(false);

        userService.deleteUser(user.getId());
        verify(userRepository).findById(user.getId());
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_WhenUserIsActive_ShouldThrowException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(BusinessException.class, () -> userService.deleteUser(user.getId()));
    }

    @Test
    void deleteUser_WhenIdDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(user.getId()));
    }
}
