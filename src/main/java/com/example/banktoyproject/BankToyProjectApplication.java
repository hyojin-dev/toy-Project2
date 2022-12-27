package com.example.banktoyproject;

import com.example.banktoyproject.elasticsearch.analysis.HanguelJamoMorphTokenizer;
import com.example.banktoyproject.elasticsearch.model.Subway;
import com.example.banktoyproject.elasticsearch.model.SubwayDocument;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@SpringBootApplication
@EnableJpaAuditing
@EnableBatchProcessing // Spring Batch
public class BankToyProjectApplication {
    public static List<Subway> subways;
    public static List<SubwayDocument> subDocs;
    public static HanguelJamoMorphTokenizer morphTokenizer;

    public static void main(String[] args) {
        morphTokenizer = HanguelJamoMorphTokenizer.getInstance();
        executeSubwayPipeline();
        System.exit(0);
//        SpringApplication.run(BankToyProjectApplication.class, args);
    }

    public static void executeSubwayPipeline() {
        try {
            readSubwayCsvFile();
            translateSubwayDocuments();
            generateSubwayFlatFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void readSubwayCsvFile() throws Exception {
        String csvFile = "src/main/resources/sources/subway.csv";

        subways = new CsvToBeanBuilder<Subway>(new FileReader(csvFile))
                .withType(Subway.class)
                .withSkipLines(1)
                .build()
                .parse();

        log.info("{}", subways);
    }

    public static void translateSubwayDocuments() {
        subDocs = new ArrayList<>();
        SubwayDocument doc;

        long start = System.currentTimeMillis();
        for (Subway subway : subways) {
            doc = new SubwayDocument();

            doc.setCode(subway.getCode());
            doc.setStation(subway.getStation());
            doc.setLine(subway.getLine());
            doc.setExcode(subway.getExcode());
            doc.setChosung(morphTokenizer.chosungTokenizer(subway.getStation()));
            doc.setJamo(morphTokenizer.jamoTokenizer(subway.getStation()));
            doc.setEngtokor(morphTokenizer.convertKoreanToEnglish(subway.getStation()));

            subDocs.add(doc);
        }
        long end = System.currentTimeMillis();
        log.info("수행시간 : " + (end - start) + " ms");
        log.info("Total Converted Documents : {} ", subDocs.size());
    }

    public static void generateSubwayFlatFile() {
        String flatJsonFile = "build/generated/subway.json";
        String index = "";
        String bulkDocTemplate =
                "{\"index\": { \"_index\": \"subway\", \"_id\": \"_DOCID_\"}}\n" +
                        "{\"id\": \"_CODE_\", \"station\": \"_STATION_\", \"line\": \"_LINE_\"," +
                        " \"excode\": \"_EXCODE_\", \"chosung\": \"_CHOSUNG_\", \"jamo\": \"_JAMO_\"," +
                        " \"engtokor\": \"_ENGTOKOR_\"}\n";
        String bulkDoc = "";

        try {
            FileWriter writer = new FileWriter(flatJsonFile);

            long start = System.currentTimeMillis();
            for (SubwayDocument doc : subDocs) {
                bulkDoc = bulkDocTemplate;
                bulkDoc = bulkDoc.replaceAll("_DOCID_", doc.getCode());
                bulkDoc = bulkDoc.replaceAll("_CODE_", doc.getCode());
                bulkDoc = bulkDoc.replaceAll("_STATION_", doc.getStation());
                bulkDoc = bulkDoc.replaceAll("_LINE_", doc.getLine());
                bulkDoc = bulkDoc.replaceAll("_EXCODE_", doc.getExcode());
                bulkDoc = bulkDoc.replaceAll("_CHOSUNG_", doc.getChosung());
                bulkDoc = bulkDoc.replaceAll("_JAMO_", doc.getJamo());
                bulkDoc = bulkDoc.replaceAll("_ENGTOKOR_", doc.getEngtokor());

                writer.write(bulkDoc);
            }

            writer.close();

            long end = System.currentTimeMillis();
            log.debug("수행시간 = " + (end - start) + "ms");
            log.debug("Total Generated Documents : {} " + subDocs.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
