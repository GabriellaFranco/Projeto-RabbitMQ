package com.enterprise.suporte.service;

import com.enterprise.suporte.dto.customer.CustomerRequestDTO;
import com.enterprise.suporte.dto.customer.CustomerResponseDTO;
import com.enterprise.suporte.enuns.UserProfile;
import com.enterprise.suporte.exception.ResourceNotFoundException;
import com.enterprise.suporte.mapper.CustomerMapper;
import com.enterprise.suporte.model.Authority;
import com.enterprise.suporte.model.Customer;
import com.enterprise.suporte.model.User;
import com.enterprise.suporte.repository.AuthorityRepository;
import com.enterprise.suporte.repository.CustomerRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private AuthorityRepository authorityRepository;

    @InjectMocks
    private CustomerService customerService;

    private User user;
    private Customer customer;
    private Authority authority;
    private CustomerRequestDTO customerRequestDTO;
    private CustomerResponseDTO customerResponseDTO;

    @BeforeEach
    void setup() {
        authority = Authority.builder()
                .id(1L)
                .name("CLIENTE")
                .build();

        user = User.builder()
                .id(1L)
                .username("dev@teste.com")
                .password("dev")
                .profile(UserProfile.CLIENTE)
                .authorities(List.of(authority))
                .isActive(true)
                .build();

        customer = Customer.builder()
                .id(1L)
                .name("Gabriella")
                .email("dev@teste.com")
                .user(user)
                .isActive(true)
                .build();

        customerRequestDTO = CustomerRequestDTO.builder()
                .name("Gabriella")
                .email("dev@teste.com")
                .build();

        customerResponseDTO = CustomerResponseDTO.builder()
                .id(1L)
                .email("dev@teste.com")
                .isActive(true)
                .build();
    }

    @Test
    void getAllCustomers_WhenCalled_ShouldReturnPageOfCustomers() {
        var pageable = PageRequest.of(1, 10);
        var customerList = List.of(customer);
        var customerPage = new PageImpl<>(customerList, pageable, customerList.size());

        when(customerRepository.findAll(pageable)).thenReturn(customerPage);
        when(customerMapper.toCustomerResponseDTO(customer)).thenReturn(customerResponseDTO);

        var result = customerService.getAllCustomers(pageable);

        assertThat(result)
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void getCustomerById_WhenCalled_ShouldReturnCustomer() {
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(customerMapper.toCustomerResponseDTO(customer)).thenReturn(customerResponseDTO);

        var result = customerService.getCustomerById(customer.getId());

        assertThat(result)
                .isNotNull()
                .extracting(CustomerResponseDTO::email, CustomerResponseDTO::id)
                .containsExactly("dev@teste.com", 1L);
    }

    @Test
    void getCustomerById_WhenIdDoesNotExistShouldThrowException() {
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> customerService.getCustomerById(customer.getId()));
    }

    @Test
    void createCustomer_WhenCalled_ShouldCreateAndSaveSuccessfully() {
        when(customerMapper.toCustomer(customerRequestDTO)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(authorityRepository.findByName(authority.getName())).thenReturn(Optional.of(authority));
        when(customerMapper.toCustomerResponseDTO(customer)).thenReturn(customerResponseDTO);

        var result = customerService.createCustomer(customerRequestDTO);

        assertThat(result)
                .isNotNull()
                .extracting(CustomerResponseDTO::email, CustomerResponseDTO::id)
                .containsExactly("dev@teste.com", 1L);
    }

    @Test
    void deleteCustomer_WhenCalled_ShouldDeleteSuccessfully() {
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        customerService.deleteCustomer(customer.getId());
        verify(customerRepository).findById(customer.getId());
        verify(customerRepository).delete(customer);
    }

    @Test
    void deleteCustomer_WhenIdDoesNotExist_ShouldThrowException() {
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> customerService.deleteCustomer(customer.getId()));
    }
}
