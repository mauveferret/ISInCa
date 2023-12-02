package ru.mauveferret.Simulators;

import ru.mauveferret.Dependencies.Dependence;
import ru.mauveferret.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public abstract class ParticleInMatterCalculator{

    //how mich lines per FileRead can be processed
    final int STRING_COUNT_PER_CYCLE = 1000;
    public int LINE_LENGTH = 90;

    public final String directoryPath;

    //flags
    public boolean doVizualization, getSummary;
    String dirSubname = "";

    //like SC100432, H_W and etc.
    public String modelingID;

    //like SCATTER, TRIM, SDTrimSP
    public String calculatorType;
    ArrayList<Dependence> dependencies;
    public double calcTime;

    //primary beam

    public double projectileMaxEnergy;
    public double projectileIncidentPolarAngle;
    public double projectileIncidentAzimuthAngle;

    //specially for header in logs, for Combiner calculator mostly, where you need to print several energies and angles
    String sProjectileMaxEnergy = "";
    String sProjectileIncidentPolarAngle = "";
    String sProjectileIncidentAzimuthAngle = "";

    public int projectileAmount;
    public String projectileElements = "";
    public String[] elements; //without "all"
    public ArrayList<String> elementsList; //with "all"

    // target

    public String targetElements;

    //some scattering variables
    public HashMap<String, Double>  scattered, sputtered, implanted, transmitted, displaced, energyRecoil;
    public double particleCount;

    public ParticleInMatterCalculator(String directoryPath, boolean doVizualization) {
        this.doVizualization = doVizualization;

        targetElements = "no elements";
        projectileElements = "no elements";
        modelingID = "no ID";
        calculatorType = "";
        projectileIncidentAzimuthAngle = 0;
        projectileIncidentPolarAngle = -1;
        projectileAmount = -1;

        this.directoryPath = directoryPath;
        try{
            new File(directoryPath+File.separator+"ISInCa").mkdir();
        }catch (Exception ignored){}
    }

    public abstract String initializeModelParameters();

    public void setDirSubname(String dirSubname){
        this.dirSubname = dirSubname;
    }

    public String getDirSubname(){
        switch (dirSubname){
            case "angle": return projectileIncidentPolarAngle+"";
            case "energy": return projectileMaxEnergy+"";
        }
        return "";
    }


    public void initializeCalcVariables(){
        particleCount = 0;
        scattered = new HashMap<>();
        sputtered = new HashMap<>();
        implanted = new HashMap<>();
        transmitted = new HashMap<>();
        displaced = new HashMap<>();
        energyRecoil = new HashMap<>();

        for (String element: elementsList) {
            scattered.put(element,0.0);
            sputtered.put(element, 0.0);
            implanted.put(element, 0.0);
            transmitted.put(element, 0.0);
            displaced.put(element, 0.0);
            energyRecoil.put(element,0.0);
        }
    }

    public void finishCalcVariables(){
        for (String element: elementsList) {
            scattered.put(element, scattered.get(element) / projectileAmount);
            sputtered.put(element, sputtered.get(element) / projectileAmount);
            implanted.put(element, implanted.get(element) / projectileAmount);
            transmitted.put(element, transmitted.get(element) / projectileAmount);
            displaced.put(element, displaced.get(element) / projectileAmount);
            energyRecoil.put(element,energyRecoil.get(element) / (projectileMaxEnergy * projectileAmount));
        }
    }

    public void finishTime(){
        calcTime =System.currentTimeMillis()- calcTime;
        calcTime = calcTime /((double) 60000);
    }

    public abstract void postProcessCalculatedFiles(ArrayList<Dependence> distributions);

    public void printAndVisualizeData(ArrayList<Dependence> distributions){
        for (Dependence distr: distributions){
            if (!distr.logDependencies())  System.out.println("ERROR during logging "+distr.getDepName());
            if (!logAdditionalData()) System.out.println("ERROR during logging summary");
            if (doVizualization) {
              if (!distr.visualize())  System.out.println("ERROR during plotting "+distr.getDepName());
          }

        }
    }

    private boolean logAdditionalData(){

        String pathToLog = directoryPath + File.separator+ "ISInCa"+File.separator+
                modelingID.toUpperCase()+File.separator+modelingID+"_summary.txt";
        try {
            FileOutputStream summary = new FileOutputStream(pathToLog);
            summary.write((createHeader()+"\n").getBytes());
            summary.write(("Monte-Carlo model: "+calculatorType+"\n").getBytes());
            summary.write(("modeling ID: "+modelingID+"\n").getBytes());
            summary.write(("ISInCa version: "+ Main.getVersion()+"\n").getBytes());
            summary.write(("ISInCa calculation time, min: "+new BigDecimal(calcTime).setScale(4, RoundingMode.UP)+"\n").getBytes());
            summary.write(("Projectile particles counted: "+particleCount+"\n\n").getBytes());
            summary.write(("*".repeat(LINE_LENGTH)+"\n").getBytes());

            summary.write((cl("element")+cl("backscattered")+cl("sputtered")+cl("implanted")+
                            cl("transmitted")+cl("displaced")+cl("energy recoil")+"\n").getBytes());
            for (String element: elementsList) {
                //"%-10s %-10s %-10s\n", "osne", "two", "thredsfe"

                summary.write((cl(element)+
                        cl(new BigDecimal(scattered.get(element)).setScale(4, RoundingMode.UP)+"")+
                        cl(new BigDecimal(sputtered.get(element)).setScale(4, RoundingMode.UP)+"")+
                        cl(new BigDecimal(implanted.get(element)).setScale(4, RoundingMode.UP)+"")+
                        cl(new BigDecimal(transmitted.get(element)).setScale(4, RoundingMode.UP)+"")+
                        cl(new BigDecimal(displaced.get(element)).setScale(4, RoundingMode.UP)+"")+
                        cl(new BigDecimal(energyRecoil.get(element)).setScale(4, RoundingMode.UP)+"")+"\n"
                ).getBytes());
            }

            summary.close();
        }
        catch (Exception e){
            e.printStackTrace();
            return  false;
        }
        return true;
    }

    int LENGTH = 15;
    public String cl(String line){
        line = line.trim();
        if (line.length()<LENGTH) return line+" ".repeat(LENGTH-line.length());
        else {
            LENGTH = line.length()+1;
            return cl(line);
        }
    }

    public String createHeader(){
        String headerComment;
        String  name = " ISInCa - Ion Surface Interaction Calculator "+ Main.getVersion()+" ";
        name = "*".repeat((LINE_LENGTH -name.length())/2)+name+"*".repeat((LINE_LENGTH -name.length())/2)+"\n";

        //headerComment = "---------------"+" PARTICLE IN MATTER ANALYZER 2020 "+"---------------"+"\n";
        String author = " by mauveferret@gmail.com from \"Plasma physics\" dep., MEPhI ";

        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        String calc = "Calculated with "+calculatorType+". ISInCa postprocessing id "+modelingID;
        String  calc2 = "Postprocessing started at "+formatter.format(date)+ ". Main input parameters:";

        if (sProjectileMaxEnergy.equals("") && sProjectileIncidentPolarAngle.equals("")){
            sProjectileMaxEnergy = projectileMaxEnergy+"";
            sProjectileIncidentPolarAngle = projectileIncidentPolarAngle+"";
            sProjectileIncidentAzimuthAngle = projectileIncidentAzimuthAngle+"";
        }

        String beam =projectileElements+" beam with E0 = "+sProjectileMaxEnergy+" eV ";
        if (projectileIncidentPolarAngle<0) beam+="with some polar angles distribution";
        else beam+="at polar angle "+sProjectileIncidentPolarAngle+" degrees from normal";
        String beam2 = "azimuth angle "+sProjectileIncidentAzimuthAngle+" degrees with doze "+projectileAmount+" particles";
        String target = "target of "+targetElements;
        headerComment=name+createLine(author)+"*".repeat(LINE_LENGTH)+"\n"+createLine(calc)+createLine(calc2);
        headerComment+=createLine(beam)+createLine(beam2)+createLine(target)+"*".repeat(LINE_LENGTH)+"\n";
        return headerComment;
    }

    public String createLine(String line){
        //in order not to get "Illegal argument exception"
        if (LINE_LENGTH-line.length()<0) LINE_LENGTH=line.length()+2;
        int spaces = (LINE_LENGTH -line.length())/2;
        return "*"+" ".repeat(spaces-1)+line+" ".repeat(spaces-1)+((((LINE_LENGTH -line.length())%2)==0) ? "" : " ")+"*\n";
    }

}
