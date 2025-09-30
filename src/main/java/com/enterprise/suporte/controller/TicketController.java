package com.enterprise.suporte.controller;

import com.enterprise.suporte.dto.ticket.TicketRequestDTO;
import com.enterprise.suporte.dto.ticket.TicketResponseDTO;
import com.enterprise.suporte.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponseDTO> createTicket(@RequestBody TicketRequestDTO ticketDTO) {
        var ticket = ticketService.createTicket(ticketDTO);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(ticket.id()).toUri();
        return ResponseEntity.created(uri).body(ticket);
    }
}
