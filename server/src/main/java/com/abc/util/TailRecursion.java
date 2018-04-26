package com.abc.util;

import java.util.stream.Stream;

/**
 * 尾递归函数接口
 * @author : martrix
 */
@FunctionalInterface
public interface TailRecursion<T> {

    /**
     * 用于递归栈帧之间的连接,惰性求值
     * @return 下一个递归栈帧
     */
    TailRecursion<T> apply();

    /**
     * 判断当前递归是否结束
     * @return 默认为false,因为正常的递归过程中都还未结束
     */
    default boolean isFinished(){
        return false;
    }

    /**
     * 获得递归结果,只有在递归结束才能调用,这里默认给出异常,通过工具类的重写来获得值
     * @return 递归最终结果
     */
    default T getResult()  {
        throw new Error("递归还没有结束,调用获得结果异常!");
    }

    /**
     * 及早求值,执行者一系列的递归,因为栈帧只有一个,所以使用findFirst获得最终的栈帧,接着调用getResult方法获得最终递归值
     * @return 及早求值,获得最终递归结果
     */
    default T invoke() {
        return Stream.iterate(this, TailRecursion::apply)
                .filter(TailRecursion::isFinished)
                .findFirst()
                .get()
                .getResult();
    }
}
