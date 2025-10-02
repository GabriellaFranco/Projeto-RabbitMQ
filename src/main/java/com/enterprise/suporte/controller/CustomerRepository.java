package com.enterprise.suporte.controller;

import com.enterprise.suporte.dto.customer.CustomerRequestDTO;
import com.enterprise.suporte.dto.customer.CustomerResponseDTO;
import com.enterprise.suporte.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerRepository {

    private final CustomerService customerService;

    @Operation(
            summary = "Retorna todos os clientes, em páginas com 10 objetos ordenados por id." +
                    "Para chamar este endpoint é necessário possuir permissão de 'ADMIN' ou 'SUPERVISOR'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão"),
                    @ApiResponse(responseCode = "204", description = "Nenhum registro a exibir")
            }
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Page<CustomerResponseDTO>> getAllCustomers(@PageableDefault(page = 1, size = 10, sort = "id") Pageable pageable) {
        var customers = customerService.getAllCustomers(pageable);
        return customers.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(customers);
    }

    @Operation(
            summary = "Retorna um cliente com o id informado. Para chamar este endpoint é necessário possuir " +
                    "permissão de 'ADMIN','SUPERVISOR' ou 'ATENDENTE'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão"),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE')")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @Operation(
            summary = "Cria um novo cliente.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Sucesso"),
                    @ApiResponse(responseCode = "400", description = "Informações inválidas")
            }
    )
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@RequestBody CustomerRequestDTO customerDTO) {
        var customer = customerService.createCustomer(customerDTO);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(customer.id()).toUri();
        return ResponseEntity.created(uri).body(customer);
    }

    @Operation(
            summary = "Exclui o cliente com o id informado. Para chamar este endpoint" +
                    " é necessário possuir a permissão 'ADMIN'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão"),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Cliente excluído com sucesso: " + id);
    }
}
