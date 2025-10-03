package com.enterprise.suporte.controller;

import com.enterprise.suporte.dto.supportagent.SupportAgentRequestDTO;
import com.enterprise.suporte.dto.supportagent.SupportAgentResponseDTO;
import com.enterprise.suporte.dto.supportagent.UpdateAgentStatusDTO;
import com.enterprise.suporte.service.SupportAgentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("support-agents")
public class SupportAgentController {

    private final SupportAgentService supportAgentService;

    @Operation(
            summary = "Retorna todos os atendentes.Para chamar este endpoint é necessário possuir permissão de 'ADMIN'" +
                    " ou 'SUPERVISOR'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão"),
                    @ApiResponse(responseCode = "204", description = "Nenhum registro a exibir")
            }
    )
    @GetMapping
    public ResponseEntity<List<SupportAgentResponseDTO>> getAllSupportAgents() {
        var supportAgents = supportAgentService.getAllSupportAgents();
        return supportAgents.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(supportAgents);
    }

    @Operation(
            summary = "Retorna um atendente com o id informado. Para chamar este endpoint é necessário possuir " +
                    "permissão de 'ADMIN', 'SUPERVISOR' ou 'ATENDENTE'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão"),
                    @ApiResponse(responseCode = "404", description = "Atendente não encontrado")
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE'')")
    public ResponseEntity<SupportAgentResponseDTO> getSupportAgentById(@PathVariable Long id) {
        return ResponseEntity.ok(supportAgentService.getSupportAgentById(id));
    }

    @Operation(
            summary = "Cria um novo atendente.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Sucesso"),
                    @ApiResponse(responseCode = "400", description = "Informações inválidas")
            }
    )
    @PostMapping
    public ResponseEntity<SupportAgentResponseDTO> createSupportAgent(@RequestBody SupportAgentRequestDTO supportAgentDTO) {
        var supportAgent = supportAgentService.createSupportAgent(supportAgentDTO);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(supportAgent.id()).toUri();
        return ResponseEntity.created(uri).body(supportAgent);
    }

    @Operation(
            summary = "Atualiza o status do atendente logado. Para chamar este endpoint é necessário possuir " +
                    "permissão de 'ATENDENTE'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão")
            }
    )
    @PatchMapping("/status")
    @PreAuthorize("hasRole('ATENDENTE')")
    public ResponseEntity<String> updateSupportAgentStatus(@RequestBody UpdateAgentStatusDTO updateDTO) {
        supportAgentService.updateSupportAgentStatus(updateDTO);
        return ResponseEntity.ok("Status de atendente atualizado para: " + updateDTO.newStatus());
    }

    @Operation(
            summary = "Exclui o atendente com o id informado. Para chamar este endpoint" +
                    " é necessário possuir a permissão 'ADMIN'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão"),
                    @ApiResponse(responseCode = "404", description = "Atendente não encontrado")
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteSupportAgent(@PathVariable Long id) {
        supportAgentService.deleteSupportAgent(id);
        return ResponseEntity.ok("Atendente excluído com sucesso: " + id);
    }

}
