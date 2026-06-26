package com.example.demo.client;

import com.example.demo.dto.external.ExternalTaskResponse;
import java.net.URI;

public record CreatedExternalTask(
        ExternalTaskResponse task,
        URI location
) {
}
