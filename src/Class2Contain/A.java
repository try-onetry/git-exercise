package Class2Contain;

public class A {
    int numa = 1;
    String stra = "I am class A";
    static B depend =  new B();

    public Integer getNuma() {
        return numa;
    }

    public String getStra() {
        return stra;
    }

    public static void main(String[] args) {
        System.out.println(depend.getStrb());
    }
}