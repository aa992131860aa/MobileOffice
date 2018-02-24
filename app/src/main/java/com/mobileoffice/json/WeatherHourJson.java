package com.mobileoffice.json;

import java.util.List;

/**
 * Created by 99213 on 2017/6/6.
 */

public class WeatherHourJson {

    /**
     * showapi_res_code : 0
     * showapi_res_error :
     * showapi_res_body : {"ret_code":0,"area":"杭州","areaid":"101210101","hourList":[{"weather_code":"02","time":"201706060900","wind_direction":"南","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"26"},{"weather_code":"03","time":"201706061000","wind_direction":"西南","wind_power":"0-3级 微风  <5.4m/s","weather":"阵雨","temperature":"27"},{"weather_code":"03","time":"201706061100","wind_direction":"西南偏西","wind_power":"0-3级 微风  <5.4m/s","weather":"阵雨","temperature":"27"},{"weather_code":"03","time":"201706061200","wind_direction":"西","wind_power":"0-3级 微风  <5.4m/s","weather":"阵雨","temperature":"27"},{"weather_code":"03","time":"201706061300","wind_direction":"西","wind_power":"0-3级 微风  <5.4m/s","weather":"阵雨","temperature":"27"},{"weather_code":"03","time":"201706061400","wind_direction":"西","wind_power":"0-3级 微风  <5.4m/s","weather":"阵雨","temperature":"22"},{"weather_code":"03","time":"201706061500","wind_direction":"西","wind_power":"0-3级 微风  <5.4m/s","weather":"阵雨","temperature":"23"},{"weather_code":"03","time":"201706061600","wind_direction":"西","wind_power":"0-3级 微风  <5.4m/s","weather":"阵雨","temperature":"27"},{"weather_code":"03","time":"201706061700","wind_direction":"西","wind_power":"0-3级 微风  <5.4m/s","weather":"阵雨","temperature":"23"},{"weather_code":"03","time":"201706061800","wind_direction":"西","wind_power":"0-3级 微风  <5.4m/s","weather":"阵雨","temperature":"22"},{"weather_code":"02","time":"201706061900","wind_direction":"东","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"25"},{"weather_code":"02","time":"201706062000","wind_direction":"东","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"24"},{"weather_code":"02","time":"201706062100","wind_direction":"东","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"22"},{"weather_code":"02","time":"201706062200","wind_direction":"东","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"21"},{"weather_code":"02","time":"201706062300","wind_direction":"东北偏东","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"23"},{"weather_code":"02","time":"201706070000","wind_direction":"东北偏东","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"22"},{"weather_code":"02","time":"201706070100","wind_direction":"东北偏东","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"20"},{"weather_code":"02","time":"201706070200","wind_direction":"东北偏东","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"21"},{"weather_code":"02","time":"201706070300","wind_direction":"东北","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"21"},{"weather_code":"02","time":"201706070400","wind_direction":"东北偏东","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"21"},{"weather_code":"02","time":"201706070500","wind_direction":"东北","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"20"},{"weather_code":"02","time":"201706070600","wind_direction":"东北","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"22"},{"weather_code":"02","time":"201706070700","wind_direction":"西北偏北","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"24"},{"weather_code":"02","time":"201706070800","wind_direction":"西北偏北","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"24"}]}
     */

    private int showapi_res_code;
    private String showapi_res_error;
    private ShowapiResBodyBean showapi_res_body;

    public int getShowapi_res_code() {
        return showapi_res_code;
    }

    public void setShowapi_res_code(int showapi_res_code) {
        this.showapi_res_code = showapi_res_code;
    }

    public String getShowapi_res_error() {
        return showapi_res_error;
    }

    public void setShowapi_res_error(String showapi_res_error) {
        this.showapi_res_error = showapi_res_error;
    }

    public ShowapiResBodyBean getShowapi_res_body() {
        return showapi_res_body;
    }

    public void setShowapi_res_body(ShowapiResBodyBean showapi_res_body) {
        this.showapi_res_body = showapi_res_body;
    }

    public static class ShowapiResBodyBean {
        /**
         * ret_code : 0
         * area : 杭州
         * areaid : 101210101
         * hourList : [{"weather_code":"02","time":"201706060900","wind_direction":"南","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"26"},{"weather_code":"03","time":"201706061000","wind_direction":"西南","wind_power":"0-3级 微风  <5.4m/s","weather":"阵雨","temperature":"27"},{"weather_code":"03","time":"201706061100","wind_direction":"西南偏西","wind_power":"0-3级 微风  <5.4m/s","weather":"阵雨","temperature":"27"},{"weather_code":"03","time":"201706061200","wind_direction":"西","wind_power":"0-3级 微风  <5.4m/s","weather":"阵雨","temperature":"27"},{"weather_code":"03","time":"201706061300","wind_direction":"西","wind_power":"0-3级 微风  <5.4m/s","weather":"阵雨","temperature":"27"},{"weather_code":"03","time":"201706061400","wind_direction":"西","wind_power":"0-3级 微风  <5.4m/s","weather":"阵雨","temperature":"22"},{"weather_code":"03","time":"201706061500","wind_direction":"西","wind_power":"0-3级 微风  <5.4m/s","weather":"阵雨","temperature":"23"},{"weather_code":"03","time":"201706061600","wind_direction":"西","wind_power":"0-3级 微风  <5.4m/s","weather":"阵雨","temperature":"27"},{"weather_code":"03","time":"201706061700","wind_direction":"西","wind_power":"0-3级 微风  <5.4m/s","weather":"阵雨","temperature":"23"},{"weather_code":"03","time":"201706061800","wind_direction":"西","wind_power":"0-3级 微风  <5.4m/s","weather":"阵雨","temperature":"22"},{"weather_code":"02","time":"201706061900","wind_direction":"东","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"25"},{"weather_code":"02","time":"201706062000","wind_direction":"东","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"24"},{"weather_code":"02","time":"201706062100","wind_direction":"东","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"22"},{"weather_code":"02","time":"201706062200","wind_direction":"东","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"21"},{"weather_code":"02","time":"201706062300","wind_direction":"东北偏东","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"23"},{"weather_code":"02","time":"201706070000","wind_direction":"东北偏东","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"22"},{"weather_code":"02","time":"201706070100","wind_direction":"东北偏东","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"20"},{"weather_code":"02","time":"201706070200","wind_direction":"东北偏东","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"21"},{"weather_code":"02","time":"201706070300","wind_direction":"东北","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"21"},{"weather_code":"02","time":"201706070400","wind_direction":"东北偏东","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"21"},{"weather_code":"02","time":"201706070500","wind_direction":"东北","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"20"},{"weather_code":"02","time":"201706070600","wind_direction":"东北","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"22"},{"weather_code":"02","time":"201706070700","wind_direction":"西北偏北","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"24"},{"weather_code":"02","time":"201706070800","wind_direction":"西北偏北","wind_power":"0-3级 微风  <5.4m/s","weather":"阴","temperature":"24"}]
         */

        private int ret_code;
        private String area;
        private String areaid;
        private List<HourListBean> hourList;

        public int getRet_code() {
            return ret_code;
        }

        public void setRet_code(int ret_code) {
            this.ret_code = ret_code;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getAreaid() {
            return areaid;
        }

        public void setAreaid(String areaid) {
            this.areaid = areaid;
        }

        public List<HourListBean> getHourList() {
            return hourList;
        }

        public void setHourList(List<HourListBean> hourList) {
            this.hourList = hourList;
        }

        public static class HourListBean {
            /**
             * weather_code : 02
             * time : 201706060900
             * wind_direction : 南
             * wind_power : 0-3级 微风  <5.4m/s
             * weather : 阴
             * temperature : 26
             */

            private String weather_code;
            private String time;
            private String wind_direction;
            private String wind_power;
            private String weather;
            private String temperature;

            public String getWeather_code() {
                return weather_code;
            }

            public void setWeather_code(String weather_code) {
                this.weather_code = weather_code;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getWind_direction() {
                return wind_direction;
            }

            public void setWind_direction(String wind_direction) {
                this.wind_direction = wind_direction;
            }

            public String getWind_power() {
                return wind_power;
            }

            public void setWind_power(String wind_power) {
                this.wind_power = wind_power;
            }

            public String getWeather() {
                return weather;
            }

            public void setWeather(String weather) {
                this.weather = weather;
            }

            public String getTemperature() {
                return temperature;
            }

            public void setTemperature(String temperature) {
                this.temperature = temperature;
            }
        }
    }
}
