package Class4Implement;

public class D implements C {
    @Override
    public void func() {
        System.out.println("I am Java class D, implements kotlin interface C");
    }

    public static void main(String[] args) {
        C c = new D();
        c.func();
    }
}