package uj.wmii.pwj.anns;

public class MyBeautifulTestSuite {

    @MyTest(params = {"1,2", "2,3"}, result = {"3", "5"})
    public int testWithTwoParametrs(Integer a, Integer b) {
        System.out.println("I'm testing something!");
        return a + b;
    }

    @MyTest(params = {"1.2", "2.3"}, result = {"2.2", "3.3"})
    public float testWithFloat(Float a) {
        return a + 1;
    }

    @MyTest(result = "lol", params = {"lol"})
    public String testWithSingleParam(String param) {
        return param;
    }

    public void notATest() {
        System.out.println("I'm not a test.");
    }

    @MyTest(result = "")
    public void imFailue() {
        System.out.println("I AM EVIL.");
        throw new NullPointerException();
    }

    @MyTest(params = {"true", "false", "false"}, result = {"false", "false", "false" })
    public boolean testWithWrongAnswer(Boolean b) {
        return !b;
    }

}
