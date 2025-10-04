package com.enterprise.suporte.controller;

import com.enterprise.suporte.dto.tickethistory.TicketHistoryResponseDTO;
import com.enterprise.suporte.service.TicketHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ticket-histories")
public class TicketHistoryController {

    private final TicketHistoryService ticketHistoryService;

    @Operation(
            summary = "Retorna todos os históricos registrados, em páginas com 10 objetos ordenados por id." +
                    "Para chamar este endpoint é necessário possuir permissão de 'ADMIN' ou 'SUPERVISOR'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão"),
                    @ApiResponse(responseCode = "204", description = "Nenhum registro a exibir")
            }
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Page<TicketHistoryResponseDTO>> getAllTicketHistories(@PageableDefault(page = 1, size = 10, sort = "id")
                                                                                    Pageable pageable) {

        var histories = ticketHistoryService.getAllTicketHistories(pageable);
        return histories.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(histories);
    }

    @Operation(
            summary = "Retorna todos os históricos registrados por um atendente." +
                    "Para chamar este endpoint é necessário possuir permissão de 'ADMIN' ou 'SUPERVISOR'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão"),
                    @ApiResponse(responseCode = "404", description = "Atendente não encontrado"),
                    @ApiResponse(responseCode = "204", description = "Nenhum registro a exibir")
            }
    )
    @GetMapping("/support-agent/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<TicketHistoryResponseDTO>> getHistoryByUser(@PathVariable Long id) {
        var histories = ticketHistoryService.getHistoryByUser(id);
        return histories.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(histories);
    }

    @Operation(
            summary = "Retorna todos os históricos registrados em um ticket. " +
                    "Para chamar este endpoint é necessário possuir permissão de 'ADMIN' 'SUPERVISOR'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão"),
                    @ApiResponse(responseCode = "404", description = "Ticket não encontrado"),
                    @ApiResponse(responseCode = "204", description = "Nenhum registro a exibir")
            }
    )
    @GetMapping("/ticket/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE')")
    public ResponseEntity<List<TicketHistoryResponseDTO>> getHistoryFromTicket(@PathVariable Long id) {
        var history = ticketHistoryService.getHistoryFromTicket(id);
        return history.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(history);
    }

}
