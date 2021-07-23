public class testing {
    public static void main(String[] args){
        // int x = calculateCatalanNumber(1);
        // x = calculateCatalanNumber(2);
        // x = calculateCatalanNumber(3);
        // x = calculateCatalanNumber(4);
        // x = calculateCatalanNumber(5);
        // x = calculateCatalanNumber(6);
        int x = calculateCatalanNumber(7);
    }

    public static int calculateCatalanNumber(int n){
        int numerator = 1;
        int denominator1 = 1;
        int denominator2 = 1;

        for (int i=1; i<(n*2); i++){
            numerator += numerator*i;
        }

        for (int i=1; i<(n+1); i++){
            denominator1 += denominator1*i;
        }

        for (int i=1; i<n; i++){
            denominator2 += denominator2*i;
        }
        
  

        System.out.println(numerator/(denominator1*denominator2));



        return numerator/(denominator1*denominator2);
    }
}
