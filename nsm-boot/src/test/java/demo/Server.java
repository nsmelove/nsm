package demo;

/**
 * Created by nieshuming on 2018/12/5
 */
public class Server
{
    public static void main(String[] args)
    {
        Ice.Communicator communicator = Ice.Util.initialize(args);
        Ice.ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("SimplePrinterAdapter", "default -p 9999");
        Ice.Object object = new PrinterI();
        adapter.add(object, Ice.Util.stringToIdentity("SimplePrinter"));
        adapter.activate();
        //communicator.waitForShutdown();
        System.out.println("start");
    }
}