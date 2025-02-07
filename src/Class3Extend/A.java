package Class3Extend;

import Class3Extend.B;

public class A extends B {
    void funa() {
        System.out.println("I am class A");
    }

    public static void main(String[] args) {
        A a = new A();
        a.funa();  // I am class A
        a.funb();  // I am class B
    }
}

