class bugKotlin {
    val javaClass = BugStr()
    val str = javaClass.getStr()
}
fun main(){
    val javaClass = BugStr()
    //调用Java 域
    val a: String = javaClass.a
    println("a:$a")
    //用属性访问Java的getter
    val b = bugKotlin()
    println("b:${b.str}")
    //调用Java函数
    val result: String = javaClass.result()
    println("result:$result")
}
