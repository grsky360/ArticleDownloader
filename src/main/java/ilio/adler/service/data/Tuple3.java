package ilio.adler.service.data;

import lombok.RequiredArgsConstructor;

/**
 * @author zqy
 * @version 1.0
 * @date 2020/1/19 18:54
 */
@RequiredArgsConstructor
public class Tuple3<T1, T2, T3> {
    public final T1 _1;
    public final T2 _2;
    public final T3 _3;

    public static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 t1, T2 t2, T3 t3) {
        return new Tuple3<>(t1, t2, t3);
    }
}
