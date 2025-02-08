package Class6Create;

public class A {

    public static B createB() {
        return new B();
    }

    public void message() {
        System.out.println("I am class A");
    }

    public static void main(String[] args) {
        B b2 = A.createB();
        b2.message();
    }
}
