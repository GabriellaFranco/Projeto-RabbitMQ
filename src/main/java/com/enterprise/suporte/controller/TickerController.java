package com.enterprise.suporte.controller;

import com.enterprise.suporte.dto.ticket.AssignTicketPriority;
import com.enterprise.suporte.dto.ticket.TicketRequestDTO;
import com.enterprise.suporte.dto.ticket.TicketResponseDTO;
import com.enterprise.suporte.dto.ticket.UpdateTicketStatusDTO;
import com.enterprise.suporte.service.TicketService;
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
@RequestMapping("/tickets")
public class TickerController {

    private final TicketService ticketService;

    @Operation(
            summary = "Retorna todos os tickets, em páginas com 10 objetos ordenados por id." +
                    "Para chamar este endpoint é necessário possuir permissão de 'ADMIN' ou 'SUPERVISOR'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão"),
                    @ApiResponse(responseCode = "204", description = "Nenhum registro a exibir")
            }
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Page<TicketResponseDTO>> getAllTickets(@PageableDefault(page = 1, value = 10, sort = "id") Pageable pageable) {
        var tickets = ticketService.getAllTickets(pageable);
        return tickets.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(tickets);
    }

    @Operation(
            summary = "Retorna todos os tickets de um cliente, em páginas com 10 objetos ordenados por id." +
                    "Para chamar este endpoint é necessário possuir permissão de 'ADMIN' ou 'SUPERVISOR'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão"),
                    @ApiResponse(responseCode = "204", description = "Nenhum registro a exibir")
            }
    )
    @GetMapping("/customer/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Page<TicketResponseDTO>> getAllTicketsFromCustomer(@PageableDefault(page = 1, size = 10, sort = "id")
                                                                                 @PathVariable Long id, Pageable pageable) {

        var tickets = ticketService.getAllTicketsFromCustomer(id, pageable);
        return tickets.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(tickets);
    }

    @Operation(
            summary = "Retorna todos os tickets de um atendente, em páginas com 10 objetos ordenados por id." +
                    "Para chamar este endpoint é necessário possuir permissão de 'ADMIN' ou 'SUPERVISOR'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão"),
                    @ApiResponse(responseCode = "204", description = "Nenhum registro a exibir")
            }
    )
    @GetMapping("/support-agent/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Page<TicketResponseDTO>> getAllTicketsFromSupportAgent(@PageableDefault(page = 1, size = 10, sort = "id")
                                                                             @PathVariable Long id, Pageable pageable) {

        var tickets = ticketService.getAllTicketsFromAgent(id, pageable);
        return tickets.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(tickets);
    }

    @Operation(
            summary = "Retorna um usuário com o id informado. Para chamar este endpoint é necessário possuir " +
                    "permissão de 'ADMIN', 'SUPERVISOR' ou 'ATENDENTE'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão"),
                    @ApiResponse(responseCode = "404", description = "Ticket não encontrado")
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE')")
    public ResponseEntity<TicketResponseDTO> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @Operation(
            summary = "Cria um novo ticket. Para chamar este endpoint é necessário possuir " +
                    "permissão de 'ADMIN', 'CLIENTE' ou 'ATENDENTE'.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão"),
                    @ApiResponse(responseCode = "400", description = "Informações inválidas")
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE', 'ATENDENTE')")
    public ResponseEntity<TicketResponseDTO> createTicket(@RequestBody TicketRequestDTO ticketDTO) {
        var ticket = ticketService.createTicket(ticketDTO);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(ticket.id()).toUri();
        return ResponseEntity.created(uri).body(ticket);
    }

    @Operation(
            summary = "Atualiza o status de um ticket. Para chamar este endpoint é necessário possuir " +
                    "permissão de 'SUPERVISOR' ou 'ATENDENTE'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão"),
                    @ApiResponse(responseCode = "404", description = "Ticket não encontrado")
            }
    )
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ATENDENTE', 'SUPERVISOR')")
    public ResponseEntity<String> updateTicketStatus(@PathVariable Long id, @RequestBody UpdateTicketStatusDTO updateDTO) {
        ticketService.updateTicketStatus(updateDTO, id);
        return ResponseEntity.ok("Status atualizado com sucesso para: " + updateDTO.newStatus());
    }

    @Operation(
            summary = "Atualiza a prioridade de um ticket. Para chamar este endpoint é necessário possuir " +
                    "permissão de 'SUPERVISOR' ou 'ATENDENTE'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão"),
                    @ApiResponse(responseCode = "404", description = "Ticket não encontrado")
            }
    )
    @PatchMapping("/{id}/priority")
    @PreAuthorize("hasAnyRole('ATENDENTE', 'SUPERVISOR')")
    public ResponseEntity<String> assignTicketPriority(@PathVariable Long id, @RequestBody AssignTicketPriority priority) {
        ticketService.assignTicketPriority(priority, id);
        return ResponseEntity.ok("Prioridade do ticket atualizado para: " + priority.priority());
    }

    @Operation(
            summary = "Exclui o cliente com o id informado. Para chamar este endpoint" +
                    " é necessário possuir a permissão 'ADMIN'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão"),
                    @ApiResponse(responseCode = "404", description = "Ticket não encontrado")
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.ok("Ticket excluído com sucesso: " + id);
    }

}
