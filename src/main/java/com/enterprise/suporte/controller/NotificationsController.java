package com.enterprise.suporte.controller;

import com.enterprise.suporte.dto.notification.NotificationResponseDTO;
import com.enterprise.suporte.service.NotificationService;
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
@RequestMapping("/notifications")
public class NotificationsController {

    private final NotificationService notificationService;

    @Operation(
            summary = "Retorna todas as notificações.Para chamar este endpoint é necessário possuir permissão de 'ADMIN'" +
                    " ou 'SUPERVISOR'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão"),
                    @ApiResponse(responseCode = "204", description = "Nenhum registro a exibir")
            }
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Page<NotificationResponseDTO>> getAllNotifications(@PageableDefault(page = 1, size = 10, sort = "id")
                                                                                 Pageable pageable) {

        var notifications = notificationService.getAllNotifications(pageable);
        return notifications.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(notifications);
    }

    @Operation(
            summary = "Retorna todas as notificações relacionadas a um ticket." +
                    "Para chamar este endpoint é necessário possuir permissão de 'ADMIN', 'SUPERVISOR' ou 'ATENDENTE'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão"),
                    @ApiResponse(responseCode = "404", description = "Ticket não encontrado"),
                    @ApiResponse(responseCode = "204", description = "Nenhum registro a exibir")
            }
    )
    @GetMapping("/ticket/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE')")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByTicket(@PathVariable Long id) {
        var notifications = notificationService.getNotificationsByTicket(id);
        return notifications.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(notifications);
    }

    @Operation(
            summary = "Retorna todos as notificações enviadas para o destino informado." +
                    "Para chamar este endpoint é necessário possuir permissão de 'ADMIN' ou 'SUPERVISOR'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão"),
                    @ApiResponse(responseCode = "204", description = "Nenhum registro a exibir")
            }
    )
    @GetMapping("/destiny/{destiny}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Page<NotificationResponseDTO>> getNotificationsByDestiny(@PageableDefault(page = 1, size = 10, sort = "id")
                                                                                   @PathVariable String destiny, Pageable pageable) {

        var notifications = notificationService.getNotificationsByDestiny(destiny, pageable);
        return notifications.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(notifications);
    }
}
