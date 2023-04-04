package me.vinfer.simpleprocess.demo;

import me.vinfer.simpleprocess.core.common.Component;

/**
 * @author vinfer
 * @date 2023-04-03 11:59
 */
public class Demo {


    static class SomeComponent implements Component {

    }

    public static void main(String[] args) {
        System.out.println(new SomeComponent().name());
    }

}
