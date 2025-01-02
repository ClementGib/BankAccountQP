package com.cdx.bas.demo;

import com.cdx.bas.producer.RunnableProducer;
import com.cdx.bas.producer.ThreadProducer;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.arjuna.ats.jbossatx.logging.jbossatxLogger.logger;

public class Demo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            logger.info("How many thread do you want to create to generate transactions: ");
            createThread(scanner.nextInt());
        }
    }

    public static void createThread(int threadLimit) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(threadLimit)) {
            for (int i = 0; i < threadLimit; i++) {
                executorService.submit(new ThreadProducer());
                executorService.submit(new RunnableProducer());
            }
            executorService.shutdown();
        }
    }
}
