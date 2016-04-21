package com.citi.rxbus;

import rx.Observable;

/**
 * A reactive bus for app inter-component communication.
 * The bus is thread-safe.
 */
public interface RxBus {

    /**
     * Publishes a message to the bus.
     * Only active subscribers to the corresponding event type will receive the message.
     *
     * @param o The message to publish
     */
    void send(Object o);

    /**
     * Publishes a message to the bus.
     * Currently active subscribers to the corresponding event type will receive the message but
     * future subscribers to the same event type will receive the last sticky message sent.
     *
     * @param o The message to publish
     */
    void sendSticky(Object o);

    /**
     * Gets a stream of events of the given {@code clazz} type.
     *
     * @param clazz The event type to subscribe to
     * @param <T> The event type to subscribe to
     * @return A stream of events of the given type
     */
    <T> Observable<T> toObservable(Class<T> clazz);
}

