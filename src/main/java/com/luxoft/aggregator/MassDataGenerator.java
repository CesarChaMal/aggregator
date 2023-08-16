package com.luxoft.aggregator;

import com.google.common.collect.Iterators;
import rx.Observable;
import rx.*;
import rx.observables.StringObservable;
import java.io.*;
import java.util.*;

/**
 * Tests whether mass data does not kill JVM during calculation.
 * @author Dmitry Dobrynin
 */
public class MassDataGenerator {
    public static Observable<String> createFromFile() throws FileNotFoundException {
        List<String> strings = StringObservable.split(
                StringObservable.from(
//                        new InputStreamReader(new FileInputStream("src/main/resources/instrument_test_input_5MB_1.txt"))),
                        new InputStreamReader(new FileInputStream("src/main/resources/instrument_test_input_1024MB_1.txt"))),
                "(\r)?\n").toList().toBlocking().single();

        Iterator<String> infinite = Iterators.cycle(strings);

        return Observable.create(new Observable.OnSubscribe<String>() {
            public void call(Subscriber<? super String> subscriber) {
                for (int i = 0; i < 1000000000; i++) {
                    try {
                        if (infinite.hasNext())
                            subscriber.onNext(infinite.next());
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                }
            }
        });
    }

    public static void main(String[] args) throws IOException {
        Aggregator aggregator = new Aggregator(createFromFile().publish());
        new StreamDriver(aggregator).run();
    }
}
