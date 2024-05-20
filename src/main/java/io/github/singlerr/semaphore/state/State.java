/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.state;

import io.netty.buffer.ByteBuf;

public interface State {

    void serialize(ByteBuf buffer);

    void deserialize(ByteBuf buffer);
}
