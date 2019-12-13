package eu.fasten.analyzer.dummyanalyzer;

//import eu.fasten.analyzer.javacgopal.OPALPlugin;
import eu.fasten.core.plugins.FastenPlugin;
import eu.fasten.core.plugins.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import eu.fasten.server.KafkaConsumerCon;
import org.pf4j.Extension;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.time.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Currently, this is the Analyzer plugins. Its functionality is described:
 * 1- It gets Maven coordinates from the producer, which is the Python crawler.
 * 2- It generates a call graph for the given Maven coordinates. Note that the Kafka consumer and
 *  its properties are declared in this class.
 */

@Extension
public class DummyAnalyzer implements FastenPlugin, KafkaConsumer<String> {

    //private String serverAddress;
    private org.apache.kafka.clients.consumer.KafkaConsumer<String, String> kafkaConsumerCon;
    //private static final String topic = "maven.packages";
    //private final String groupId = "some_app";
    private final Logger logger = LoggerFactory.getLogger(DummyAnalyzer.class.getName());

    public DummyAnalyzer(KafkaConsumerCon kafkaConsumerCon) {
        this.kafkaConsumerCon = kafkaConsumerCon.getConsumer();
    }

//    private Properties consumerProps(String serverProperties) {
//        String deserializer = StringDeserializer.class.getName();
//        Properties properties = new Properties();
//
//        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, serverProperties);
//        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, this.groupId);
//        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, deserializer);
//        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
//        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//
//        return properties;
//    }

    @Override
    public List<String> consumerTopic() {
        return new ArrayList<>(Collections.singletonList("maven.packages"));
    }

    @Override
    public void consume(String topic, ConsumerRecords<String, String> records) {
//        // Generates call graphs from given Maven records.
//        OPALPlugin opal = new OPALPlugin();
//        opal.consume(records);
        for(var record: records) {
            System.out.println("Key: " + record.key());
            System.out.println("Value: " + record.value());
        }
    }

    private class ConsumerRunnable implements Runnable {
        private CountDownLatch mLatch;

        ConsumerRunnable(CountDownLatch latch){
            mLatch = latch;
        }

        @Override
        public void run(){
            try{

                do{
                    ConsumerRecords<String, String> records = kafkaConsumerCon.poll(Duration.ofMillis(100));
                    consume(consumerTopic().get(0), records);

                    //logger.debug("******************* Generated call graph: " + opal.getRevisionCallGraphs().get(0).toJSON().toString() + " ***********************************");
                }while (true);
            } catch (WakeupException e){
                logger.info("Received shutdown signal!");
            } finally {
                kafkaConsumerCon.close();
                mLatch.countDown();
            }
        }

        void shutdown() {
            kafkaConsumerCon.wakeup();
        }
    }

    @Override
    public String name() {
        return "DummyAnalyzer";
    }

    @Override
    public String description() {
        return "DummyAnalyzer";
    }

    @Override
    public void start() {
        //Properties props = consumerProps(this.serverAddress);
        //this.MVCConsumer = new KafkaConsumer<String, String>(props);

        logger.info("Consumer initialized");

        CountDownLatch latch = new CountDownLatch(1);

        ConsumerRunnable consumerRunnable = new ConsumerRunnable(latch);
        Thread thread = new Thread(consumerRunnable);
        thread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.debug("Caught shutdown hook");
            consumerRunnable.shutdown();
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            logger.debug("MVC Consumer has exited");
        }));

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }

    public static void main(String[] args) {

        KafkaConsumerCon kafkaConsumerCon = new KafkaConsumerCon("localhost:9092", "maven.packages",
                "some_app");

        DummyAnalyzer analyzer = new DummyAnalyzer(kafkaConsumerCon);
        analyzer.start();
    }

}
