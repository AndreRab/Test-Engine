package uj.wmii.pwj.anns;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MyTestEngine {

    private final String className;

    public static void main(String[] args) {
        System.out.println(ASCIIIBoard.testEngineAscii);
        String className = findClassName(args);
        System.out.printf("Testing class: %s\n\n", className);
        MyTestEngine engine = new MyTestEngine(className);
        engine.runTests();
    }

    public MyTestEngine(String className) {
        this.className = className;
    }

    public static String findClassName(String[] args){
        if (args.length < 1) {
            Scanner sc = new Scanner(System.in);
            System.out.println("Specify test class name: ");
            String className = sc.nextLine();
            sc.close();
            return className;
        }
        else{
            return args[0].trim();
        }
    }

    public void runTests() {
        final Object unit = getObject(className);
        List<Method> testMethods = getTestMethods(unit);
        int successCount = 0;
        int failCount = 0;
        int errorCount = 0;
        for (Method m: testMethods) {
            String[] paramsFromAnnotation = m.getAnnotation(MyTest.class).params();
            String[] realResult = m.getAnnotation(MyTest.class).result();
            if(paramsFromAnnotation.length == 0){
                TestResult result = launchSingleMethod(m, unit, paramsFromAnnotation, realResult[0]);
                if (result == TestResult.PASS) successCount++;
                else if (result == TestResult.FAIL) failCount++;
                else errorCount++;
                System.out.println("\n");
            }
            for (int i = 0; i < paramsFromAnnotation.length; i++) {
                TestResult result = launchSingleMethod(m, unit, paramsFromAnnotation[i].split(","), realResult[i]);
                if (result == TestResult.PASS) successCount++;
                else if (result == TestResult.FAIL) failCount++;
                else errorCount++;
                System.out.println("\n");
            }
        }
        System.out.printf("Engine launched %d tests.\n", successCount + failCount + errorCount);
        System.out.printf(ASCIIIBoard.GREEN + "%d of them passed," +  ASCIIIBoard.RED + " %d failed, " + ASCIIIBoard.YELLOW + "%d errors.\n\n" + ASCIIIBoard.RESET, successCount, failCount, errorCount);
    }

    private TestResult launchSingleMethod(Method m, Object unit, String[] paramsFromAnnotation, String realResult) {
        Object result = null;
        try {
            Class<?>[] paramTypes = m.getParameterTypes();
            if (paramsFromAnnotation.length == 0) {
                result = m.invoke(unit);
            } else{
                Object[] paramsObject = new Object[paramsFromAnnotation.length];
                for (int i = 0; i < paramsFromAnnotation.length; i++) {
                    paramsObject[i] = paramTypes[i].getConstructor(String.class).newInstance(paramsFromAnnotation[i]);
                }
                result = m.invoke(unit, paramsObject);
            }
            System.out.print("Tested method '"  + m.getName() + "' with params {");
            for (int i = 0; i < paramsFromAnnotation.length; i++){
                System.out.print(paramsFromAnnotation[i]);
                if(i != paramsFromAnnotation.length - 1) System.out.print(", ");
                else System.out.print("}: ");
            }
            if(realResult.equals(result.toString())){
                System.out.println(ASCIIIBoard.GREEN + "test successful." + ASCIIIBoard.RESET);
                return TestResult.PASS;
            }
            else{
                System.out.println(ASCIIIBoard.RED + "test failed." + ASCIIIBoard.RESET);
                System.out.println("Expected value: " + realResult + ". But get: " + result.toString());
                return TestResult.FAIL;
            }
        } catch (ReflectiveOperationException e) {
            System.out.println("Tested method '" + m.getName() + "': " + ASCIIIBoard.YELLOW + "test with error." + ASCIIIBoard.RESET);
            e.printStackTrace();
            return TestResult.ERROR;
        }
    }

    private static List<Method> getTestMethods(Object unit) {
        Method[] methods = unit.getClass().getDeclaredMethods();
        return Arrays.stream(methods).filter(
                m -> m.getAnnotation(MyTest.class) != null).collect(Collectors.toList());
    }

    private static Object getObject(String className) {
        try {
            Class<?> unitClass = Class.forName(className);
            return unitClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return new Object();
        }
    }
}
