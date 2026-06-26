package com.example.demo.dto.gateway;

import java.net.URI;

public record CreatedGatewayTask(
        GatewayTaskResponse task,
        URI externalLocation
) {
}
