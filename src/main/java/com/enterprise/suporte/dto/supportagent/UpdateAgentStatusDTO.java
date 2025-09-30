package com.enterprise.suporte.dto.supportagent;

import com.enterprise.suporte.enuns.AgentStatus;

public record UpdateAgentStatusDTO(
        AgentStatus newStatus
) {
}
