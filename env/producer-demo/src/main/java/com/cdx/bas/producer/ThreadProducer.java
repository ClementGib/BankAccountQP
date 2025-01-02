package com.cdx.bas.producer;

import com.cdx.bas.domain.bank.transaction.category.NewDigitalTransaction;
import com.cdx.bas.producer.generator.Generator;
import com.cdx.bas.producer.requestor.TransactionSender;

import static com.arjuna.ats.jbossatx.logging.jbossatxLogger.logger;

public class ThreadProducer extends Thread {
    @Override
    public void run() {
        logger.info("Thread producer thread started");
        NewDigitalTransaction newDigitalTransaction = Generator.generateTransaction();
        TransactionSender.send(newDigitalTransaction);
        logger.info("End of thread");
    }
}
