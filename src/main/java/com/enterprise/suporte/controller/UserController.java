package com.enterprise.suporte.controller;

import com.enterprise.suporte.dto.user.UserResponseDTO;
import com.enterprise.suporte.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Retorna todos os usuários, em páginas com 10 objetos ordenados por id." +
                    "Para chamar este endpoint é necessário possuir permissão de 'ADMIN' ou 'SUPERVISOR'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão"),
                    @ApiResponse(responseCode = "204", description = "Nenhum registro a exibir")
            }
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(@PageableDefault(page = 1, size = 10, sort = "id") Pageable pageable) {
        var users = userService.getAllUsers(pageable);
        return users.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Retorna um usuário com o id informado. Para chamar este endpoint é necessário possuir " +
                    "permissão de 'ADMIN', 'SUPERVISOR' ou 'ATENDENTE'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(
            summary = "Exclui o usuário com o id informado. Para chamar este endpoint" +
                    " é necessário possuir a permissão 'ADMIN'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Usuário excluído com sucesso: " + id);
    }
}
