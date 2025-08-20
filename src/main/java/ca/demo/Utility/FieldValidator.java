package ca.demo.Utility;

public class FieldValidator {
    public static int validateIntegerField(String text) {
        if (text.matches("\\d+")) {
            int value = Integer.parseInt(text);
            return value > 0 ? value : -1;  // return -1 for invalid values
        }
        return -1;
    }

    public static double validateDoubleField(String text) {
        if (text.matches("\\d+(\\.\\d+)?")) {
            double value = Double.parseDouble(text);
            return value > 0 ? value : -1;  // return -1 for invalid values
        }
        return -1;
    }


}
