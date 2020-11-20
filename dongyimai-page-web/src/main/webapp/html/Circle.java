package day07;
//注意:制作API的类必须由public修饰

/**
 * 圆的工具类
 * @author jim
 * @version v1.0
 */
public class Circle{

    final static double PI = 3.14;

    /**
     * 获取圆的面积
     * @param r 半径
     * @return 面积
     */
    public static double getArea(double r){
        return PI*r*r;
    }
}