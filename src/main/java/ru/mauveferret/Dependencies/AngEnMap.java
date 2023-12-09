package ru.mauveferret.Dependencies;

import ru.mauveferret.Simulators.Simulator;
import ru.mauveferret.Charts.EnAngColorMap;

import java.awt.*;
import java.io.FileOutputStream;
import java.util.ArrayList;

import static ru.mauveferret.Console.isConsole;

public class AngEnMap extends Dependence {

    public final double E0;
    private final double dE;
    private final double dBeta;

    public AngEnMap(double dBeta, double dE, String sort, Simulator calculator) {


        //TODO Add the phi and deltaPhi to calculate for specific azimuth

        super(calculator, sort);
        this.dE = dE;
        this.E0 = calculator.projectileMaxEnergy;

        this.dBeta = dBeta;

        depType = "map";
        mapArrayYsize = (int) Math.ceil(90/dBeta)+1;

        mapArrayXsize = (int) Math.ceil(E0/dE)+1;
        endOfPath="_dE "+dE+"_dBeta"+dBeta+".txt";
    }

    @Override
    public void initializeArrays(ArrayList<String> elements) {
        headerComment = simulator.createHeader();
        String addheaderComment = " dE "+dE+" eV dBeta "+dBeta+" degrees ";
        headerComment += simulator.createLine(addheaderComment)+"*".repeat(simulator.LINE_LENGTH)+"\n";
        //headerComment= "Angle dN/dOmega "+"\n"+"degrees  particles \n\n"+headerComment+"\n";
        super.initializeArrays(elements);
    }

    public  void check (PolarAngles angles, String someSort, double E, String element)
    {
        //only for backscattered and sputtered!
        if (sort.contains(someSort))
        {
            mapArray.get(element)[(int) Math.round(E / dE)][(int) Math.round( angles.getPolar()/ dBeta)]++;
            mapArray.get("all")[(int) Math.round(E / dE)][(int) Math.round( angles.getPolar()/ dBeta)]++;
        }
    }

    @Override
    public boolean logDependencies() {

        //FIXME ITS A TRAP!!!
        for (String element: elements) {



            // from probability distr. to angle

            for (int i = 0; i <= (int) Math.round(E0 / dE); i++) {
                for (int j = 0; j <= (int) Math.round(90 / dBeta); j++) {
                    if (j==0){
                        mapArray.get(element)[i][j] = mapArray.get(element)[i][j]
                                /(simulator.projectileAmount*dE*dBeta*Math.sin(Math.toRadians(Math.abs(dBeta/2))));
                    }
                    else {
                        mapArray.get(element)[i][j] = mapArray.get(element)[i][j]
                                /(simulator.projectileAmount*dE*dBeta*Math.sin(Math.toRadians(Math.abs(j*dBeta))));
                        //System.out.println(j+" "+calculator.projectileAmount+" "+dE+" "+dBeta+" "+Math.sin(Math.toRadians(Math.abs(j*dBeta))));
                    }
                }
            }

            try {
                FileOutputStream surfaceWriter = new FileOutputStream(pathsToLog.get(element));
                String stroka = "E";
                surfaceWriter.write(headerComments.get(element).getBytes());
                for (int i = 0; i <= (int) Math.round(90 / dBeta); i++) {
                    stroka = stroka + columnSeparatorInLog + (int) (i * dBeta);
                }
                stroka = stroka + "\n";
                surfaceWriter.write(stroka.getBytes());

                for (int i = 0; i <= (int) Math.round(E0 / dE); i++) {
                    stroka = String.format("%.2f",i * dE) + columnSeparatorInLog;
                    for (int j = 0; j <= (int) Math.round(90 / dBeta); j++) {
                        stroka = stroka + String.format("%12.4e",mapArray.get(element)[i][j])  + columnSeparatorInLog;
                    }
                    stroka = stroka + "\n";
                    surfaceWriter.write(stroka.getBytes());
                }
                surfaceWriter.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }
        }

        //FIXME !!!!!! only for Mamedov's calcs
        if (!doVisualisation) {
            if (isConsole) doVisualisation = true;
            visualize();
        }

        return  true;
    }

    @Override
    public boolean visualize() {
        if (!sort.equals("") && doVisualisation) EventQueue.invokeLater(() ->
                new EnAngColorMap(simulator.projectileElements+" with average energy of "+ simulator.projectileMaxEnergy/1000
                        +" keV hits target of "+
                        simulator.targetElements.replaceAll(" ","")+" at average polar angle of "+
                        simulator.projectileIncidentPolarAngle +" degrees ",  mapArray.get("all"), E0, dE, dBeta,pathsToLog.get("all")));
        return  true;
    }


}

