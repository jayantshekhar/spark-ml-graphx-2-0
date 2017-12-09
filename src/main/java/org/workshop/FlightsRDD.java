package org.workshop;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

public class FlightsRDD {

    public static void main(String[] args) throws  org.apache.spark.sql.AnalysisException {

        Logger.getLogger("org").setLevel(Level.OFF);
        Logger.getLogger("akka").setLevel(Level.OFF);

        SparkSession spark = SparkSession.builder().master("local").appName("FlightData").config("spark.some.config.option", "some-value")
                .getOrCreate();

        rdd1(spark);
    }

    public static void rdd1(SparkSession spark) {
        JavaRDD<Flight> flights = createFlightsRDD(spark, "data/flights_data_noheader.csv");

        JavaRDD<Airport> airports = createAirportsRDD(spark, "data/airport_codes.csv");

        // count
        long numCARRIER = flights.map(new Function<Flight, String>() {
            @Override
            public String call(Flight r) {
                return r.CARRIER;
            }
        }).distinct().count();

        System.out.println("Number of CARRIERS : " + numCARRIER);

        JavaPairRDD<String, Integer> ones = flights.mapToPair(
                new PairFunction<Flight, String, Integer>() {
                    @Override
                    public Tuple2<String, Integer> call(Flight s) {
                        return new Tuple2<>(s.CARRIER, 1);
                    }
                });


        // count
        long numAirports = airports.map(new Function<Airport, String>() {
            @Override
            public String call(Airport r) {
                return r.IATA;
            }
        }).distinct().count();

        System.out.println("Number of airports : " + numAirports);


    }

    public static JavaRDD<Flight> createFlightsRDD(SparkSession sparkSession, String inputFile) {

        // create RDD
        JavaRDD<String> lines = sparkSession.sparkContext().textFile(inputFile, 1).toJavaRDD();;

        JavaRDD<Flight> flights = lines.map(new Function<String, Flight>() {
            @Override
            public Flight call(String s) throws Exception {
                String[] arr = s.split(",");

                // user::movie::rating
                return new Flight(arr);
            }
        });

        return flights;
    }

    public static JavaRDD<Airport> createAirportsRDD(SparkSession sparkSession, String inputFile) {

        // create RDD
        JavaRDD<String> lines = sparkSession.sparkContext().textFile(inputFile, 1).toJavaRDD();;

        JavaRDD<Airport> airports = lines.map(new Function<String, Airport>() {
            @Override
            public Airport call(String s) throws Exception {
                String[] arr = s.split(",");

                // user::movie::rating
                return new Airport(arr);
            }
        });

        return airports;
    }


}