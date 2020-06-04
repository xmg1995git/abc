import java.io.FileNotFoundException;

/**
 * @author xiaoMaGe
 * @date 2020-05-24 - 14:31
 */
public class Test {

    public int getNum(int i){

        if(i == 1){
            return 1;
        }else{
            return i+getNum(--i);
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {

        System.out.println(new Test().getNum(5));


        System.out.println("-------------------------");

        Class<?> book = Class.forName(
                "com.test.Book",
                false,
                Thread.currentThread().getContextClassLoader());


        System.out.println("--------------");
       if(1>0){
           return;
       }
        Class<?> book1 = Class.forName(
                "com.test.Book",
                false,
                ClassLoader.getSystemClassLoader());

        System.out.println("--------------");

        System.out.println(book1.getName());
    }
}
