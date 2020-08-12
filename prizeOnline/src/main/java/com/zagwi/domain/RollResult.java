package com.zagwi.domain;

import java.io.Serializable;
import java.util.List;

public class RollResult implements Serializable {
    private List<String> allNumbers;
    private List<History> randomResult;
    private int version;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<String> getAllNumbers() {
        return allNumbers;
    }

    public void setAllNumbers(List<String> allNumbers) {
        this.allNumbers = allNumbers;
    }

    public List<History> getRandomResult() {
        return randomResult;
    }

    public void setRandomResult(List<History> randomResult) {
        this.randomResult = randomResult;
    }

    @Override
    public String toString() {
        return "RollResult{" +
                "allNumbers=" + allNumbers +
                ", randomResult=" + randomResult +
                ", version=" + version +
                '}';
    }
}
