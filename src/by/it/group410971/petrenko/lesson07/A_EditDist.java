package by.it.group410971.petrenko.lesson07;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

/*
Задача на программирование: расстояние Левенштейна
    https://ru.wikipedia.org/wiki/Расстояние_Левенштейна
    http://planetcalc.ru/1721/

Дано:
    Две данных непустые строки длины не более 100, содержащие строчные буквы латинского алфавита.

Необходимо:
    Решить задачу МЕТОДАМИ ДИНАМИЧЕСКОГО ПРОГРАММИРОВАНИЯ
    Рекурсивно вычислить расстояние редактирования двух данных непустых строк

    Sample Input 1:
    ab
    ab
    Sample Output 1:
    0

    Sample Input 2:
    short
    ports
    Sample Output 2:
    3

    Sample Input 3:
    distance
    editing
    Sample Output 3:
    5

*/

public class A_EditDist {


    int getDistanceEdinting(String one, String two) {
        //!!!!!!!!!!!!!!!!!!!!!!!!!     НАЧАЛО ЗАДАЧИ     !!!!!!!!!!!!!!!!!!!!!!!!!
        return calculateDistance(one, two, one.length(), two.length());
    }

    private int calculateDistance(String s1, String s2, int m, int n) {
        //выполнение задания: базовые случаи рекурсии
        if (m == 0) return n; // если первая строка пустая, нужно вставить все символы второй
        if (n == 0) return m; // если вторая строка пустая, нужно удалить все символы первой

        //выполнение задания: если последние символы совпадают, переходим к следующим
        if (s1.charAt(m-1) == s2.charAt(n-1)) {
            return calculateDistance(s1, s2, m-1, n-1);
        }

        //выполнение задания: рекурсивно вычисляем минимальную стоимость операций
        return 1 + Math.min(
                Math.min(
                        calculateDistance(s1, s2, m, n-1),   // вставка
                        calculateDistance(s1, s2, m-1, n)),   // удаление
                calculateDistance(s1, s2, m-1, n-1)       // замена
        );

        //!!!!!!!!!!!!!!!!!!!!!!!!!     КОНЕЦ ЗАДАЧИ     !!!!!!!!!!!!!!!!!!!!!!!!!
    }


    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = A_EditDist.class.getResourceAsStream("dataABC.txt");
        A_EditDist instance = new A_EditDist();
        Scanner scanner = new Scanner(stream);
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine()));
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine()));
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine()));
    }
}
