package ilio.adler.service.data;

import lombok.RequiredArgsConstructor;

/**
 * @author zqy
 * @version 1.0
 * @date 2020/1/19 18:53
 */
@RequiredArgsConstructor
public class Tuple2<T1, T2> {
    public final T1 _1;
    public final T2 _2;

    public static <T1, T2> Tuple2<T1, T2> of(T1 t1, T2 t2) {
        return new Tuple2<>(t1, t2);
    }
}
