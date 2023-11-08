package ru.mauveferret.Dependencies;


import ru.mauveferret.Calcuators.ParticleInMatterCalculator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Dependence implements Cloneable{

    final String sort;
    protected final String fileSep = File.separator;
    //FIXME move to calculator -> to xml
    final String columnSeparatorInLog = " ";
     public ParticleInMatterCalculator calculator;
    public String depName;

    //flags
    boolean doVisualisation;

    // for different elements
    //FIXME Why HashMap?
    HashMap<String, String> pathsToLog;
    String headerComment;
    HashMap<String, String> headerComments;
    String endOfPath;


    //array for different dependencies types (distribution, function, map, 2variables function)
    public String depType;
    ArrayList<String> elements;
    public HashMap<String, double[]> distributionArray;
    public int distributionSize;

    public HashMap<String, double[][]> mapArray;
    public int mapArrayXsize;
    public int mapArrayYsize;


    public Dependence(ParticleInMatterCalculator calculator, String sort) {

        this.calculator = calculator;
        this.sort = sort;
        this.doVisualisation = calculator.doVizualization;
        depName = this.getClass().getSimpleName().toLowerCase();
    }

    public void initializeArrays(ArrayList<String> elements){
        this.elements = elements;
        pathsToLog = new HashMap<>();
        headerComments = new HashMap<>();

        //make folder for dep
        try{
            new File(calculator.directoryPath+fileSep+"ISInCa"+fileSep+calculator.modelingID.toUpperCase()).mkdir();
        }catch (Exception ignored){}
        try{
            new File(calculator.directoryPath+fileSep+"ISInCa"+fileSep+calculator.modelingID.toUpperCase()+
                    fileSep+depName.toUpperCase()).mkdir();
        }catch (Exception ignored){}

        String pathToLog = calculator.directoryPath+fileSep+"ISInCa"+fileSep+calculator.modelingID.toUpperCase()+
                fileSep+depName.toUpperCase()+fileSep+
                calculator.modelingID+"_"+depName.toUpperCase()+"_DEP_"+sort+"_";
        switch (depType){
            case "distribution": distributionArray = new HashMap<>();
            break;
            case "map": mapArray = new HashMap<>();
            break;
        }

        for (String element: elements){
            pathsToLog.put(element, pathToLog+element+"_"+ endOfPath);
            String addheaderComment = " calculated  for "+element+" elements ";
            headerComments.put(element, headerComment+calculator.createLine(addheaderComment)+"*".repeat(calculator.LINE_LENGTH)+"\n");
            switch (depType){
                case "distribution": distributionArray.put(element, new double[distributionSize]);
                break;
                case "map": mapArray.put(element, new double[mapArrayXsize][mapArrayYsize]);
                break;
            }
        }

    }

    public Dependence clone() throws CloneNotSupportedException{

        return (Dependence) super.clone();
    }


    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public void check(){};
    //abstract double[] getSpectrum();
    public abstract boolean logDependencies();
    public abstract boolean visualize();

    public String getDepName() {
        return depName;
    }

    public String getSort(){return  sort;}

    //outputs

    /*
        types:
            B - back scattered
            S - sputtered/recoiled
            I - implanted
            T - transmitted
     */

}
