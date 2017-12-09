package org.workshop;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Row;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

public class FlightsStreaming implements Serializable{

    public static void main(String[] args) throws InterruptedException, IOException {
        Logger.getLogger("org.apache.spark").setLevel(Level.WARNING);
        //JavaStreamingContext jssc = new JavaStreamingContext("spark://192.168.1.19:7077", "JavaWordCount",new Duration(1000));
        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("JavaWordCount");
        JavaStreamingContext jssc = new JavaStreamingContext(conf,new Duration(1000));
       /* SparkSession spark = SparkSession
        		  .builder()
        		  .appName("JavaStructuredNetworkWordCount")
        		  .getOrCreate();
        Dataset<Row> csvDF = spark
        		  .readStream()
        		  .format("com.databricks.spark.csv")
        		  .option("sep", ",")
        		  .load("data/");*/

        JavaDStream<String> flightsData = jssc.textFileStream("data/").cache();

        /*JavaDStream<String> words = flightsData.flatMap(
        		  new FlatMapFunction<String, String>() {
        			@Override
        		    public Iterator<String> call(String s) {
        		      return Arrays.asList(s.split("\n")).iterator();
        		    }
        });*/

        flightsData.foreachRDD(new VoidFunction<JavaRDD<String>>() {

            public void call(JavaRDD<String> rdd) throws Exception {
                List<String> output = rdd.collect();
                System.out.println("Data Collected from files " + output);
                return;
            }

        });

        flightsData.print();
        jssc.start();
        jssc.awaitTermination();
        //Files.move(Paths.get("flights_data_noheader.csv"), Paths.get("data/temp_flight_data1.txt"));
    }


}