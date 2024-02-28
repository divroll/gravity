package com.divroll.datafactory;

import com.divroll.datafactory.repositories.EntityStore;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public abstract class BaseTest {
    protected static EntityStore entityStore;
    protected static DataFactory dataFactory = DataFactory.getInstance();

    @BeforeClass
    public static void beforeAll() throws Exception {
        try {
            entityStore = DataFactoryClient.getInstance(TestEnvironment.getDomain(), TestEnvironment.getPort()).getEntityStore();
            System.setProperty(Constants.JAVA_RMI_TEST_PORT_ENVIRONMENT, TestEnvironment.getPort());
            dataFactory.register();
            await().atMost(5, TimeUnit.SECONDS);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void afterAll() {
        dataFactory.release();
    }

    public static String getEnvironment() {
        return TestEnvironment.getEnvironment();
    }
}
