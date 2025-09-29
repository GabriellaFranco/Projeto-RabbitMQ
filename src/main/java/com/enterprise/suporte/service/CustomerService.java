package com.enterprise.suporte.service;

import com.enterprise.suporte.dto.customer.CustomerRequestDTO;
import com.enterprise.suporte.dto.customer.CustomerResponseDTO;
import com.enterprise.suporte.enuns.UserProfile;
import com.enterprise.suporte.exception.BusinessException;
import com.enterprise.suporte.exception.ResourceNotFoundException;
import com.enterprise.suporte.mapper.CustomerMapper;
import com.enterprise.suporte.model.Customer;
import com.enterprise.suporte.model.User;
import com.enterprise.suporte.repository.AuthorityRepository;
import com.enterprise.suporte.repository.CustomerRepository;
import com.enterprise.suporte.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public Page<CustomerResponseDTO> getAllCustomers(Pageable pageable) {
        var customers = customerRepository.findAll(pageable);
        return customers.map(customerMapper::toCustomerResponseDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE')")
    public CustomerResponseDTO getCustomerById(Long id) {
        return customerRepository.findById(id).map(customerMapper::toCustomerResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + id));
    }

    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerDTO) {
        validateUniqueEmail(customerDTO.email());
        var customer = customerMapper.toCustomer(customerDTO);

        customer.setIsActive(true);
        customer.setPassword(passwordEncoder.encode(customerDTO.password()));
        customer.setCreatedAt(LocalDate.now());

        var savedCustomer = customerRepository.save(customer);
        createUserBasedOnCustomer(savedCustomer);

        return customerMapper.toCustomerResponseDTO(savedCustomer);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCustomer(Long id) {
        var customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + id));
        customerRepository.delete(customer);
    }

    private void validateUniqueEmail(String email) {
        var user = userRepository.findByUsername(email);
        if (user.isPresent()) {
            throw new BusinessException("Email já cadastrado: " + email);
        }
    }

    private void createUserBasedOnCustomer(Customer customer) {
        var authority = authorityRepository.findByName("CLIENTE")
                .orElseThrow(() -> new ResourceNotFoundException("Autoridade não encontrada"));

        var userCreated = User.builder()
                .username(customer.getEmail())
                .password(customer.getPassword())
                .profile(UserProfile.CLIENTE)
                .isActive(true)
                .customer(customer)
                .createdAt(customer.getCreatedAt())
                .authorities(List.of(authority))
                .build();

        userRepository.save(userCreated);
    }

}
