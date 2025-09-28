package com.enterprise.suporte.mapper;

import com.enterprise.suporte.dto.customer.CustomerRequestDTO;
import com.enterprise.suporte.dto.customer.CustomerResponseDTO;
import com.enterprise.suporte.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toCustomer(CustomerRequestDTO customerDTO) {
        return Customer.builder()
                .name(customerDTO.name())
                .email(customerDTO.email())
                .password(customerDTO.password())
                .phone(customerDTO.phone())
                .build();
    }

    public CustomerResponseDTO toCustomerResponseDTO(Customer customer) {
        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .createdAt(customer.getCreatedAt())
                .isActive(customer.getIsActive())
                .tickets(customer.getTickets().stream().map(ticket -> CustomerResponseDTO.TicketDTO.builder()
                        .id(ticket.getId())
                        .title(ticket.getTitle())
                        .status(ticket.getStatus())
                        .build()).toList())
                .build();
    }
}
