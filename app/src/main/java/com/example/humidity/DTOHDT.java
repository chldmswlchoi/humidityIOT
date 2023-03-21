package com.example.humidity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DTOHDT {


        @Expose
        @SerializedName("status")
        private String status;

        @Expose
        @SerializedName("data")
        private List<DHTDATA> data;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<DHTDATA> getData() {
            return data;
        }

        public void setData(List<DHTDATA> data) {
            this.data = data;
        }


        @Override
        public String toString() {
            return "DHT{" +
                    "status='" + status + '\'' +
                    ", data=" + data +
                    '}';
        }

        public class DHTDATA {

            public String getDht_num() {
                return dht_num;
            }

            public void setDht_num(String dht_num) {
                this.dht_num = dht_num;
            }

            public Integer getHumidity() {
                return humidity;
            }

            public void setHumidity(Integer humidity) {
                this.humidity = humidity;
            }

            public Integer getTemperature() {
                return temperature;
            }

            public void setTemperature(Integer temperature) {
                this.temperature = temperature;
            }

            public String getDetail_date() {
                return detail_date;
            }

            public void setDetail_date(String detail_date) {
                this.detail_date = detail_date;
            }

            public Integer getRelative_humidity() {
                return relative_humidity;
            }

            public void setRelative_humidity(Integer relative_humidity) {
                this.relative_humidity = relative_humidity;
            }

            @SerializedName("dht_num")
            @Expose
            private String dht_num;

            @SerializedName("humidity")
            @Expose
            private Integer humidity;

            @SerializedName("temperature")
            @Expose
            private Integer temperature;

            @SerializedName("detail_date")
            @Expose
            private String detail_date;

            @SerializedName("relative_humidity")
            @Expose
            private Integer relative_humidity;


            @Override
            public String toString() {
                return "Review{" +
                        "dht_num='" + dht_num + '\'' +
                        ", humidity='" + humidity + '\'' +
                        ", temperature=" + temperature +
                        ", detail_date='" + detail_date + '\'' +
                        ", relative_humidity='" + relative_humidity + '\'' +
                        '}';
            }
        }
    }


