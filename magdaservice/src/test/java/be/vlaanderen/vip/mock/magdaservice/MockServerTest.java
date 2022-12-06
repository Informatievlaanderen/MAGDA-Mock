package be.vlaanderen.vip.mock.magdaservice;

import java.net.Socket;

public abstract class MockServerTest {

    private static boolean checked = false;
    private static boolean mockServerRunning = false;

    public static boolean somebodyListeningOn(String host, int port) {
        boolean ret = false;
        try {
            Socket s = new Socket(host, port);
            ret = true;
            s.close();
        } catch (Exception e) {
            //
        }
        return ret;
    }

    protected static boolean mockServerIsRunning() {
        if (!checked) {
            System.out.println("Checking if MagdaMock Server is running on this machine");
            mockServerRunning = somebodyListeningOn("localhost", 8080);
            if (mockServerRunning) {
                System.out.println("MagdaMock available. All server tests enabled");
            } else {
                System.err.println("MagdaMock unavailable. All server tests disabled");
            }
            checked = true;
        }
        return mockServerRunning;
    }
}
