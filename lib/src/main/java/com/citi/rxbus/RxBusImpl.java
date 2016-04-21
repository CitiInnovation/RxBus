package com.citi.rxbus;

import rx.Observable;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.observables.GroupedObservable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public final class RxBusImpl implements RxBus {

    private final Subject<Object, Object> mBus = new SerializedSubject<>(PublishSubject.create());
    private final Subject<Object, Object>
            mStickyBusInput =
            new SerializedSubject<>(PublishSubject.create());

    private final ConnectableObservable<Pair<Class, Observable<Object>>> mStickyBusOutput =
            mStickyBusInput.groupBy(
                    new Func1<Object, Object>() {
                        @Override
                        public Object call(final Object o) {
                            return o.getClass();
                        }
                    }).map(new Func1<GroupedObservable<Object, Object>, Pair<Class, Observable<Object>>>() {
                @Override
                public Pair<Class, Observable<Object>> call(
                        GroupedObservable<Object, Object> objectObjectGroupedObservable) {

                    final ConnectableObservable<Object> replay = objectObjectGroupedObservable.replay(1);
                    replay.connect();
                    return Pair
                            .create((Class) objectObjectGroupedObservable.getKey(), (Observable<Object>) replay);
                }
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
                        .filter(new Func1<Pair<Class, Observable<Object>>, Boolean>() {
                            @Override
                            public Boolean call(Pair<Class, Observable<Object>> classObservablePair) {
                                return classObservablePair.first.equals(clazz);
                            }
                        }).flatMap(new Func1<Pair<Class, Observable<Object>>, Observable<?>>() {
                            @Override
                            public Observable<?> call(Pair<Class, Observable<Object>> classObservablePair) {
                                return classObservablePair.second;
                            }
                        })).ofType(clazz);
    }
}
