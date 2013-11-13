package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import data.TrainingData;

public class TrainingDataTest {

    @Test
    public void testLoadTrainingData() {
        System.out.println("***testLoadTrainingData***");
        
        int maxXValue = 10;
        try {
            TrainingData.generateInitialTrainingData(maxXValue);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Could not generate intitial training data");
        }
        
        ArrayList<TrainingData> trainingData = null;
        try {
            trainingData = TrainingData.getTrainingData();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Could not load intitial training data");
        }
        
        for (TrainingData td : trainingData) {
            System.out.println(td.inputData() + "," + td.outputData());
        }
    }
}
