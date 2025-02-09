package Class1Import;
public class A {
    static int numa = 1;
    static String stra = "I am class A";

    public static Integer getNuma() {
        return numa;
    }

    public static String getStra() {
        return stra;
    }

    public static void main(String[] args) {
        B b = new B();
        System.out.println(b.getStrb());
    }
}
