package de.byteagenten.ldr2;

import de.byteagenten.ldr2.log.*;

import java.lang.reflect.InvocationTargetException;

/**
 * Hello world!
 *
 */
public class ScopesExample
{
    public static void main( String[] args ) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        Logger.init("myApp", ElasticsearchLogWriter.class);

        SessionContext sessionContext = new SimpleSessionContext();
        Logger.setSessionContext(sessionContext);
        Logger.setRequestContext(new RequestContext(sessionContext.nextRequestNumber(), ""));

        Logger.log(new SessionStarted());
        Logger.log(new RequestStarted());

        Logger.pushScope("username", "knooma2e", true);
        Logger.log(new UserLogin(1, "Matthias", "Knoop"));

        Logger.pushScope("methodA", "MyMethodA");

        Logger.log(MethodEntrance.class);

        Object x = null;
        try {
            x.toString();
        } catch (Exception e) {
            Logger.log(e, LogEventConfig.create().setLevel(LogEvent.Level.DEBUG).setMessage("bad luck"));
        }

        ScopeStack scopeStack = Logger.createChildScopeStack();

        Thread t = new Thread(() -> {

            Logger.setScopeStack(scopeStack);

            Logger.pushScope("methodB", "MyMethodB");

            Logger.log(MethodEntrance.class);

            Logger.log(MethodExit.class);
            Logger.popScope();

        });
        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Logger.log(MethodExit.class);
        Logger.popScope();

        Logger.log(UserLogout.class, LogEventConfig.create().setLevel(LogEvent.Level.DEBUG).setName("user_logout"));
        Logger.popScope();

        Logger.log(MethodExit.class);
    }
}
