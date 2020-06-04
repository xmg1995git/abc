package com.test;

/**
 * @author xiaoMaGe
 * @date 2020-05-24 - 14:15
 */
public class Book {


    static {
        System.out.println("静态代码快、。。。");
    }

    static int a = 10;

    public static void main(String[] args) throws ClassNotFoundException {
//        ClassLoader classLoader = com.test.Book.class.getClassLoader();
        Class<?> book = Class.forName(
                "com.test.Book",
                false,
                Thread.currentThread().getContextClassLoader());
    }
}
