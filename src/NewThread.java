public class NewThread extends Thread {

        private float[] arr;

        //Конструктор
        NewThread(String name, float[] arr) {
            super(name);
            this.arr = arr;
        }

        //Геттер
        float[] getArray() {
            return arr;
        }


        @Override
        public void run() {
            calculation();
        }

        private void calculation() {
            int len = arr.length;

            for (int i = 0; i < len; i++) {
                arr[i] = (float) (arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
            }

        }
    }

