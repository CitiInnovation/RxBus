/*
* The MIT License (MIT)

* Copyright (c) 2016 Citigroup

* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:

* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.

* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


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

