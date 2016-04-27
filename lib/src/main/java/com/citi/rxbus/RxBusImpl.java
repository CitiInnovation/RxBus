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
import rx.observables.ConnectableObservable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public final class RxBusImpl implements RxBus {

    private final Subject<Object, Object> mBus = new SerializedSubject<>(PublishSubject.create());
    private final Subject<Object, Object>
            mStickyBusInput =
            new SerializedSubject<>(PublishSubject.create());

    private final ConnectableObservable<Pair<Class, Observable<Object>>> mStickyBusOutput =
            mStickyBusInput.groupBy(Object::getClass)
                    .map(groupedByClassObservable -> {
                        final ConnectableObservable<Object> replay = groupedByClassObservable.replay(1);
                        replay.subscribe(); // see: github.com/ReactiveX/RxJava/issues/3219
                        replay.connect();
                        return Pair.create((Class) groupedByClassObservable.getKey(),
                                           (Observable<Object>) replay);
                    }).replay();

    public RxBusImpl() {
        mStickyBusOutput.connect();
    }

    @Override
    public void send(final Object o) {
        mBus.onNext(o);
    }

    @Override
    public void sendSticky(final Object o) {
        mStickyBusInput.onNext(o);
    }

    @Override
    public <T> Observable<T> toObservable(final Class<T> clazz) {
        return mBus
                .mergeWith(mStickyBusOutput
                        .filter(classObservablePair -> classObservablePair.first.equals(clazz))
                        .flatMap(classObservablePair -> classObservablePair.second))
                .ofType(clazz);
    }
}
