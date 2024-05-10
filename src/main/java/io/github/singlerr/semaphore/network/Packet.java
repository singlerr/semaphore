/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

@SuperBuilder
@NoArgsConstructor
public abstract class Packet implements IMessage {}
