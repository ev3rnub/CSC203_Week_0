package org.verboseStory.model;

public class Weather {
    private boolean clouds;
    private boolean rain;
    private boolean thunder;
    private boolean snow;
    private boolean fog;
    private boolean wind;
    private int temperature;
    private int cloudPoints;
    private int rainPoints;
    private int thunderPoints;
    private int snowPoints;
    private int fogPoints;
    private int windPoints;

    public Weather() {
        this.clouds = false;
        this.rain = false;
        this.thunder = false;
        this.snow = false;
        this.fog = false;
        this.wind = false;
        this.temperature = 75;
        this.cloudPoints = 0;
        this.rainPoints = 0;
        this.thunderPoints = 0;
        this.snowPoints = 0;
        this.fogPoints = 0;
        this.windPoints = 0;
    }

    public boolean isCloudy() {
        return clouds;
    }

    public void setCloudy(boolean clouds) {
        this.clouds = clouds;
    }

    public boolean isRain() {
        return rain;
    }
    public void setRain(boolean rain) {
        this.rain = rain;
    }

    public boolean isThunder() {
        return thunder;
    }

    public void setThunder(boolean thunder) {
        this.thunder = thunder;
    }

    public boolean isSnow() {
        return snow;
    }
    public void setSnow(boolean snow) {
        this.snow = snow;
    }

    public boolean isFog() {
        return fog;
    }

    public void setFog(boolean fog) {
        this.fog = fog;
    }

    public boolean isWind() {
        return wind;
    }

    public void setWind(boolean wind) {
        this.wind = wind;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getCloudPoints() {
        return cloudPoints;
    }

    public void setCloudPoints(int cloudPoints) {
        this.cloudPoints = cloudPoints;
    }

    public int getRainPoints() {
        return rainPoints;
    }

    public void setRainPoints(int rainPoints) {
        this.rainPoints = rainPoints;
    }

    public int getThunderPoints() {
        return thunderPoints;
    }

    public void setThunderPoints(int thunderPoints) {
        this.thunderPoints = thunderPoints;
    }

    public int getSnowPoints() {
        return snowPoints;
    }

    public void setSnowPoints(int snowPoints) {
        this.snowPoints = snowPoints;
    }

    public int getFogPoints() {
        return fogPoints;
    }

    public void setFogPoints(int fogPoints) {
        this.fogPoints = fogPoints;
    }

    public int getWindPoints() {
        return windPoints;
    }

    public void setWindPoints(int windPoints) {
        this.windPoints = windPoints;
    }
}
