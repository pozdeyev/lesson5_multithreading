/**
 * Java. Level 2. Lesson 5.
 * @author Dmitry Pozdeyev
 * @version 07.02.2019
 */

/*
Урок 5. Многопоточность
1. Необходимо написать два метода, которые делают следующее:
1) Создают одномерный длинный массив, например:
static final int size = 10000000;
static final int h = size / 2;
float[] arr = new float[size];
2) Заполняют этот массив единицами;
3) Засекают время выполнения: long a = System.currentTimeMillis();
4) Проходят по всему массиву и для каждой ячейки считают новое значение по формуле:
arr[i] = (float)(arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
5) Проверяется время окончания метода System.currentTimeMillis();
6) В консоль выводится время работы: System.out.println(System.currentTimeMillis() - a);

Отличие первого метода от второго:
Первый просто бежит по массиву и вычисляет значения.
Второй разбивает массив на два массива, в двух потоках высчитывает новые значения и
потом склеивает эти массивы обратно в один.

Пример деления одного массива на два:
System.arraycopy(arr, 0, a1, 0, h);
System.arraycopy(arr, h, a2, 0, h);

Пример обратной склейки:
System.arraycopy(a1, 0, arr, 0, h);
System.arraycopy(a2, 0, arr, h, h);

Примечание:
System.arraycopy() копирует данные из одного массива в другой:
System.arraycopy(массив-источник, откуда начинаем брать данные из массива-источника,
массив-назначение, откуда начинаем записывать данные в массив-назначение, сколько ячеек копируем)
По замерам времени:

Для первого метода надо считать время только на цикл расчета:
for (int i = 0; i < size; i++) {
arr[i] = (float)(arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
}

Для второго метода замеряете время разбивки массива на 2, просчета каждого из двух массивов и склейки.
*/

public class Lesson5_multithreading {

    private static final int SIZE = 10000500; //Объявляем размер массива
    private static final int HALF = SIZE / 2; //Половина массива
    private static float[] array = new float[SIZE]; ///Объявляем массив

    public static void main(String[] args) {

        //Заполняем массив array единицами
        fillArrayOne (array);

        //Вычисляем время окончания метода в один поток, который просто вычисляет значения по формуле
        long firstTime = firstSingleThreadMethod(array);

        /*
        Вычисляем время метода, который
        разбивает массив на два массива, в двух потоках высчитывает новые значения и
        потом склеивает эти массивы обратно в один
        */
        long secondTime = secondBiThreadMethod(array);

        System.out.printf("Вычисление в два потока выполняются быстрее, чем в один поток на: %d мсек",
                firstTime - secondTime);

    }

    /**
     * Метод заполняющий массив единицами
     * @param arr
     */

    private static void fillArrayOne(float arr[]) {
        for (int i = 0; i < arr.length; i++)
            arr[i] = 1;
    }



    /**
     * Метод, который заполняет массив по формуле и вычисляет время выполнения
     * @param arr
     * @return long
     */

    private static long firstSingleThreadMethod (float[] arr) {

        long firstTime;
        long startPosition = System.currentTimeMillis(); //точка отсчета

        for (int i = 0; i < arr.length; i++)
            arr[i] = (float) (arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));

        firstTime = System.currentTimeMillis() - startPosition; //расчет времени выполнения

        System.out.printf("Время выполнения метода в один поток: %d%n", firstTime);
        return firstTime;
    }

    /**
     * Метод, который  который разбивает массив на два массива, в двух потоках высчитывает новые значения и
     * потом склеивает эти массивы обратно в один. Возвращает время выполнения
     * @param arr
     * @return long
     */

    private static long secondBiThreadMethod (float[] arr) {

        float[] a1 = new float[HALF]; //Объявляем два массива размером HALF
        float[] a2 = new float[HALF];

        long secondTime;
        long startPosition = System.currentTimeMillis(); //точка отсчета

        //Разбиваем массив arr на два и копируем половины в a1 и a2
        System.arraycopy(arr, 0, a1, 0, HALF);
        System.arraycopy(arr, HALF, a2, 0, HALF);

        //Создаем потоки
        NewThread th1 = new NewThread("array 1", a1);
        NewThread th2 = new NewThread("array 2", a2);

        //Запускаем поток 1 и заполняем его по формуле см. метод calculation
        th1.start();
        //Запускаем поток 2
        th2.start();

        try {
            th1.join(); //главный поток ждет полного выполнения потока, для корректного возврата значения массива
            th2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        a1 = th1.getArray(); //возвращаем значения первой половины массивы
        a2 = th2.getArray(); //возвращаем значения второй половины массива




        //Склеиваем массив
        System.arraycopy(a1, 0, arr, 0, HALF);
        System.arraycopy(a2, 0, arr, a1.length, a2.length);

        secondTime = System.currentTimeMillis() - startPosition; //расчет времени выполнения

        System.out.printf("Время выполнения метода в два потока: %d%n", secondTime);
        return secondTime;

    }



}
