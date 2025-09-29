package com.enterprise.suporte.service;

import com.enterprise.suporte.dto.ticket.TicketRequestDTO;
import com.enterprise.suporte.dto.ticket.TicketResponseDTO;
import com.enterprise.suporte.dto.ticket.UpdateTicketStatusDTO;
import com.enterprise.suporte.enuns.TicketPriority;
import com.enterprise.suporte.enuns.TicketStatus;
import com.enterprise.suporte.enuns.UserProfile;
import com.enterprise.suporte.exception.ResourceNotFoundException;
import com.enterprise.suporte.mapper.TicketMapper;
import com.enterprise.suporte.model.Customer;
import com.enterprise.suporte.model.SupportAgent;
import com.enterprise.suporte.model.Ticket;
import com.enterprise.suporte.model.User;
import com.enterprise.suporte.repository.CustomerRepository;
import com.enterprise.suporte.repository.SupportAgentRepository;
import com.enterprise.suporte.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private SupportAgentRepository supportAgentRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TicketMapper ticketMapper;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private TicketService ticketService;

    private Ticket ticket;
    private TicketRequestDTO ticketRequestDTO;
    private TicketResponseDTO ticketResponseDTO;
    private User user;
    private Customer customer;
    private SupportAgent supportAgent;

    @BeforeEach
    void setup() {
         user = User.builder()
                 .id(1L)
                 .username("dev@teste.com")
                 .password("dev")
                 .isActive(true)
                 .profile(UserProfile.ADMIN)
                 .build();

         customer = Customer.builder()
                 .id(1L)
                 .name("Gabriella")
                 .email("dev@teste.com")
                 .password("dev")
                 .isActive(true)
                 .user(user)
                 .build();

         supportAgent = SupportAgent.builder()
                 .id(1L)
                 .name("Gabriella")
                 .email("dev@teste.com")
                 .password("dev")
                 .isActive(true)
                 .user(user)
                 .build();

         ticket = Ticket.builder()
                 .id(1L)
                 .title("Ticket novo")
                 .description("Descrição do ticket")
                 .status(TicketStatus.ABERTO)
                 .priority(TicketPriority.BAIXA)
                 .customer(customer)
                 .agentResponsible(supportAgent)
                 .build();

         ticketRequestDTO = TicketRequestDTO.builder()
                 .title("Ticket novo")
                 .description("Descrição do ticket")
                 .build();

         ticketResponseDTO = TicketResponseDTO.builder()
                 .id(1L)
                 .title("Ticket novo")
                 .description("Descrição do ticket")
                 .status(TicketStatus.ABERTO)
                 .priority(TicketPriority.BAIXA)
                 .agentResponsible(new TicketResponseDTO.AgentDTO(1L, "Liana", "dev@teste.com"))
                 .build();
    }

    @Test
    void getAllTickets_WhenCalled_ShouldReturnPageOfTickets() {
        var pageable = PageRequest.of(1, 10);
        var ticketList = List.of(ticket);
        var ticketPage = new PageImpl<>(ticketList, pageable, ticketList.size());

        when(ticketRepository.findAll(pageable)).thenReturn(ticketPage);
        when(ticketMapper.toTicketResponseDTO(ticket)).thenReturn(ticketResponseDTO);

        var result = ticketService.getAllTickets(pageable);

        assertThat(result)
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void getTicketById_WhenCalled_ShouldReturnTicket() {
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));
        when(ticketMapper.toTicketResponseDTO(ticket)).thenReturn(ticketResponseDTO);

        var result = ticketService.getTicketById(user.getId());

        assertThat(result)
                .isNotNull()
                .extracting(TicketResponseDTO::status, TicketResponseDTO::id)
                .containsExactly(TicketStatus.ABERTO, 1L);
    }

    @Test
    void getTicketById_WhenIdDoesNotExist_ShouldThrowException() {
        when(ticketRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> ticketService.getTicketById(user.getId()));
    }

    @Test
    void createTicket_WhenCalled_ShouldCreateSuccessfully() {
        when(authenticationService.getLoggedUser()).thenReturn(user);
        when(customerRepository.findCustomerByUser(user.getId())).thenReturn(customer);
        when(supportAgentRepository.findAll()).thenReturn(List.of(supportAgent));
        when(ticketMapper.toTicket(ticketRequestDTO, customer, supportAgent)).thenReturn(ticket);
        when(ticketRepository.save(ticket)).thenReturn(ticket);
        when(ticketMapper.toTicketResponseDTO(ticket)).thenReturn(ticketResponseDTO);

        var result = ticketService.createTicket(ticketRequestDTO);

        assertThat(result)
                .isNotNull()
                .extracting(TicketResponseDTO::id, TicketResponseDTO::status, TicketResponseDTO::title)
                .containsExactly(1L, TicketStatus.ABERTO, "Ticket novo");
    }

    @Test
    void deleteTicket_WhenCalled_ShouldDeleteSuccessfully() {
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));
        ticketService.deleteTicket(ticket.getId());
        verify(ticketRepository).findById(ticket.getId());
        verify(ticketRepository).delete(ticket);
    }

    @Test
    void deleteTicket_WhenIdDoesNotExist_ShouldThrowException() {
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> ticketService.deleteTicket(ticket.getId()));

    }


}
