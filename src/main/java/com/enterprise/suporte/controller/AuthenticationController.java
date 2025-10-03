package com.enterprise.suporte.controller;


import com.enterprise.suporte.configuration.jwt.JWTUtil;
import com.enterprise.suporte.dto.authentication.LoginRequestDTO;
import com.enterprise.suporte.dto.authentication.LoginResponseDTO;
import com.enterprise.suporte.dto.authentication.UpdateEmailDTO;
import com.enterprise.suporte.dto.authentication.UpdatePasswordDTO;
import com.enterprise.suporte.dto.user.UserRequestDTO;
import com.enterprise.suporte.dto.user.UserResponseDTO;
import com.enterprise.suporte.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final AuthenticationService authenticationService;
    private final JWTUtil jwtUtil;


    @Operation(
            summary = "Altera a senha do usuário autenticado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos ou senha atual incorreta"),
                    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
            }
    )
    @PatchMapping("/update-password")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody UpdatePasswordDTO updateDTO) {
        authenticationService.updatePassword(updateDTO);
        return ResponseEntity.ok("Senha alterada com sucesso");
    }

    @Operation(
            summary = "Altera a senha do usuário autenticado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
            }
    )
    @PatchMapping("/update-email")
    public ResponseEntity<String> updateEmail(UpdateEmailDTO updateDTO) {
        authenticationService.updateEmail(updateDTO);
        return ResponseEntity.ok("Email alterado com sucesso");
    }

    @Operation(
            summary = "Cria um novo usuário.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Sucesso"),
                    @ApiResponse(responseCode = "400", description = "Informações inválidas")
            }
    )
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRequestDTO userDTO) {
        var user = authenticationService.registerUser(userDTO);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.id()).toUri();
        return ResponseEntity.created(uri).body(user);
    }

    @Operation(
            summary = "Autentica o usuário e retorna um token JWT.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(), loginRequest.password()
                )
        );

        var authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority).toList();

        var token = jwtUtil.generateToken(authentication.getName(), authorities);

        return ResponseEntity.ok(new LoginResponseDTO("Login successful", token));
    }
}
