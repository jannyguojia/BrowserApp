package com.example.guoweb;

public class History {
        private String url;
        private String time;
        private String title;

        public History(String url, String time) {
            this.url = url;
            this.time = time;
        }
        public History(String url, String time,String title) {
            this.url = url;
            this.time = time;
            this.title = title;
        }
        public String getUrl() {

            return url;
        }

        public String getTime() {

            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public void setUrl(String url) {

            this.url = url;
        }

        public String getTitle() {

            return title;
        }

        public void setTitle(String title) {

            this.title = title;
        }

        @Override
        public String toString() {

            return     "time=" + time + ", url= " + url ;
        }
    }


