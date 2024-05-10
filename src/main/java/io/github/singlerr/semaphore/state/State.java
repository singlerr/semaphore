/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.state;

import io.netty.buffer.ByteBuf;

public interface State<T> {

    void serialize(ByteBuf buffer);

    void deserialize(ByteBuf buffer);

    boolean equals(State<T> other);
}
