package Class5Call;

public class A {
    public String message(String name) {
        return "Hello, " + name + "! I from Java";
    }

    public static void main(String[] args) {
        B b = new B();
        String msg = b.message("Java");
        System.out.println(msg);
    }
}