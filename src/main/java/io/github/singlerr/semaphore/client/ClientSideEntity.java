/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client;

import io.github.singlerr.semaphore.interactors.access.database.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public final class ClientSideEntity {

    private Entity entity;
}
