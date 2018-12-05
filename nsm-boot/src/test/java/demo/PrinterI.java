package demo;


import demo.slice._PrinterDisp;

/**
 * Created by nieshuming on 2018/12/5
 */
public class PrinterI extends _PrinterDisp {

    @Override
    public void printString(String s, Ice.Current current) {
        System.out.println(s);
    }
}
