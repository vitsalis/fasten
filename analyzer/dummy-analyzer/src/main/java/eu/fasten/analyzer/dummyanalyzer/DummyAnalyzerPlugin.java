package eu.fasten.analyzer.dummyanalyzer;

import eu.fasten.core.plugins.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DummyAnalyzerPlugin extends Plugin {

    public DummyAnalyzerPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Extension
    public static class DummyAnalyzer implements KafkaConsumer<String> {

        private String consumeTopic = "fasten.mvn.pkg";
        private final Logger logger = LoggerFactory.getLogger(DummyAnalyzer.class.getName());


        @Override
        public List<String> consumerTopics() {
            return new ArrayList<>(Collections.singletonList(consumeTopic));
        }

        @Override
        public void setTopic(String topicName) {
            this.consumeTopic = topicName;
        }

        @Override
        public void consume(String topic, ConsumerRecord<String, String> record) {
            logger.debug("Key: " + record.key() + " Value:" + record.value());
        }

        @Override
        public boolean recordProcessSuccessful() {
            return true;
        }

        @Override
        public String name() {
            return "Dummy plugin";
        }

        @Override
        public String description() {
            return "Dummy plug-in. Connects to Kafka queue and prints all its contents to the debug log";
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
        }

        @Override
        public void setPluginError(Throwable throwable) {

        }

        @Override
        public String getPluginError() {
            return "";
        }

        @Override
        public void freeResource() {

        }
    }
}
