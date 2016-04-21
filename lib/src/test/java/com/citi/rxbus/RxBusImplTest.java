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

import com.google.common.collect.ImmutableList;

import org.junit.Before;
import org.junit.Test;

import rx.observers.TestSubscriber;

public final class RxBusImplTest {

    private RxBusImpl target;

    @Before
    public void setUp() {
        target = new RxBusImpl();
    }

    @Test
    public void only_send() {
        target.send(1);
        target.send(2);
        final TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
        target.toObservable(Integer.class).subscribe(testSubscriber);
        target.send(4);
        target.send(5);
        testSubscriber.unsubscribe();
        target.send(6);

        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(ImmutableList.of(4, 5));
    }

    @Test
    public void only_send_mult_subscribers() {
        target.send(1);
        final TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
        target.toObservable(Integer.class).subscribe(testSubscriber);
        target.send(2);
        target.send(3);
        final TestSubscriber<Integer> testSubscriber2 = new TestSubscriber<>();
        target.toObservable(Integer.class).subscribe(testSubscriber2);
        target.send(4);
        target.send(5);
        target.send(6);
        target.send(7);
        target.send(8);
        testSubscriber.unsubscribe();
        target.send(9);
        testSubscriber2.unsubscribe();
        target.send(10);

        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(ImmutableList.of(2, 3, 4, 5, 6, 7, 8));

        testSubscriber2.assertNoErrors();
        testSubscriber2.assertReceivedOnNext(ImmutableList.of(4, 5, 6, 7, 8, 9));
    }

    @Test
    public void only_send_multi_types() {
        target.send(1);
        target.send("no one is listening");
        target.send("no one is listening!");
        target.send(2);
        final TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        target.toObservable(String.class).subscribe(testSubscriber);
        target.send(3);
        target.send("someone is listening - 1");
        target.send("someone is listening - 2");
        final TestSubscriber<Integer> testSubscriber2 = new TestSubscriber<>();
        target.toObservable(Integer.class).subscribe(testSubscriber2);
        target.send(4);
        target.send("someone is listening - 3");
        target.send(5);
        testSubscriber.unsubscribe();
        target.send(6);
        testSubscriber2.unsubscribe();

        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(
                ImmutableList.of("someone is listening - 1", "someone is listening - 2", "someone is listening - 3"));

        testSubscriber2.assertNoErrors();
        testSubscriber2.assertReceivedOnNext(ImmutableList.of(4, 5, 6));
    }

    @Test
    public void only_sticky() {
        target.sendSticky(1);
        target.sendSticky(2);
        final TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
        target.toObservable(Integer.class).subscribe(testSubscriber);
        target.sendSticky(4);
        target.sendSticky(5);
        testSubscriber.unsubscribe();
        target.sendSticky(6);

        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(ImmutableList.of(2, 4, 5));
    }

    @Test
    public void only_sticky_multi_subscribers() {
        target.sendSticky(1);
        final TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
        target.toObservable(Integer.class).subscribe(testSubscriber);
        target.sendSticky(2);
        target.sendSticky(3);
        final TestSubscriber<Integer> testSubscriber2 = new TestSubscriber<>();
        target.toObservable(Integer.class).subscribe(testSubscriber2);
        target.sendSticky(4);
        target.sendSticky(5);
        target.sendSticky(6);
        target.sendSticky(7);
        target.sendSticky(8);
        final TestSubscriber<Integer> testSubscriber3 = new TestSubscriber<>();
        target.toObservable(Integer.class).subscribe(testSubscriber3);
        testSubscriber.unsubscribe();
        target.sendSticky(9);
        testSubscriber2.unsubscribe();
        target.sendSticky(10);
        testSubscriber3.unsubscribe();

        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(ImmutableList.of(1, 2, 3, 4, 5, 6, 7, 8));

        testSubscriber2.assertNoErrors();
        testSubscriber2.assertReceivedOnNext(ImmutableList.of(3, 4, 5, 6, 7, 8, 9));

        testSubscriber3.assertNoErrors();
        testSubscriber3.assertReceivedOnNext(ImmutableList.of(8, 9, 10));
    }

    @Test
    public void only_sticky_multi_types() {
        target.sendSticky(1);
        target.sendSticky("no one is listening");
        target.sendSticky("no one is listening!");
        target.sendSticky(2);
        final TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        target.toObservable(String.class).subscribe(testSubscriber);
        target.sendSticky(3);
        target.sendSticky("someone is listening - 1");
        target.sendSticky("someone is listening - 2");
        final TestSubscriber<Integer> testSubscriber2 = new TestSubscriber<>();
        target.toObservable(Integer.class).subscribe(testSubscriber2);
        target.sendSticky(4);
        target.sendSticky("someone is listening - 3");
        target.sendSticky(5);
        testSubscriber.unsubscribe();
        target.sendSticky(6);
        target.sendSticky("last");
        testSubscriber2.unsubscribe();

        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(
                ImmutableList.of("no one is listening!", "someone is listening - 1", "someone is listening - 2", "someone is listening - 3"));

        testSubscriber2.assertNoErrors();
        testSubscriber2.assertReceivedOnNext(ImmutableList.of(3, 4, 5, 6));
    }

    @Test
    public void both_sticky_and_non_sticky() {
        target.sendSticky(1);
        target.send(42);
        target.sendSticky("no one is listening");
        target.send("ignore me");
        target.sendSticky("no one is listening!");
        target.sendSticky(2);
        target.send(42);
        final TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        target.toObservable(String.class).subscribe(testSubscriber);
        target.sendSticky(3);
        target.send(33);
        target.sendSticky("someone is listening - 1");
        target.send("someone is listening - 1.5");
        target.send("someone is listening - 1.51");
        target.sendSticky("someone is listening - 2");
        final TestSubscriber<Integer> testSubscriber2 = new TestSubscriber<>();
        target.toObservable(Integer.class).subscribe(testSubscriber2);
        target.sendSticky(4);
        target.send(44);
        target.send("someone is listening - 2.5");
        target.send(444);
        target.sendSticky("someone is listening - 3");
        target.sendSticky(5);
        testSubscriber.unsubscribe();
        target.sendSticky(6);
        testSubscriber2.unsubscribe();

        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(
                ImmutableList.of("no one is listening!", "someone is listening - 1", "someone is listening - 1.5", "someone is listening - 1.51",
                        "someone is listening - 2", "someone is listening - 2.5", "someone is listening - 3"));

        testSubscriber2.assertNoErrors();
        testSubscriber2.assertReceivedOnNext(ImmutableList.of(3, 4, 44, 444, 5, 6));
    }
}