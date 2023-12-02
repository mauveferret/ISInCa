package ru.mauveferret.Dependencies;

import ru.mauveferret.Simulators.Simulator;

import java.io.FileOutputStream;

public class getTXT extends Dependence {

    FileOutputStream energyWriter;

    public getTXT(Simulator calculator, String sort) {
        super(calculator, sort);
        sort = "null";
        String pathToLog = calculator.directoryPath+fileSep+"ISInCa"+fileSep+calculator.modelingID+"_"+
                this.getClass().getSimpleName().toUpperCase()+"_DEP_"+sort+".txt";
        try {
          energyWriter = new FileOutputStream(pathToLog);
        }
        catch (Exception e){e.printStackTrace();}
    }


    @Override
    public boolean logDependencies() { return true; }

    @Override
    public boolean visualize() {
        try {
            energyWriter.close();
        }
        catch (Exception e){e.printStackTrace();}
        return true;
    }

    public void check(PolarAngles angles, String someSort, double E) {
        try {
            energyWriter.write((E+" "+angles.getAzimuth()+" "+angles.getPolar()).getBytes());
            System.out.println(E+" "+angles.getAzimuth()+" "+angles.getPolar());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
