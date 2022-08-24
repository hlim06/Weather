package kr.goldenmine;

public class WeatherInfo {
    private int hour;
    private int minute;
    private int temperature;

    public WeatherInfo(int hour, int minute, int temperature) {
        this.hour = hour;
        this.minute = minute;
        this.temperature = temperature;
    }

    public int getHour() {
        return hour;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getMinute() {
        return minute;
    }
}
