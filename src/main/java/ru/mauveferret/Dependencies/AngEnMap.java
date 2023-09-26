package ru.mauveferret.Dependencies;

import ru.mauveferret.Calcuators.ParticleInMatterCalculator;
import ru.mauveferret.EnAngColorMap;
import ru.mauveferret.ScatterColorMap;

import java.awt.*;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class AngEnMap extends Dependence {

    public final double E0;
    private final double dE;
    private final double dTheta;

    public AngEnMap(double dTheta, double dE, String sort, ParticleInMatterCalculator calculator) {
        super(calculator, sort);
        this.dE = dE;
        this.E0 = calculator.projectileMaxEnergy;

        this.dTheta = dTheta;

        depType = "map";
        mapArrayYsize = (int) Math.ceil(90/dTheta)+1;
        mapArrayXsize = (int) Math.ceil(E0/dE)+1;
        endOfPath="_dE "+dE+"_dTheta"+dTheta+".txt";
    }

    @Override
    public void initializeArrays(ArrayList<String> elements) {
        headerComment = calculator.createHeader();
        String addheaderComment = " dE "+dE+" eV dTheta "+dTheta+" degrees ";
        headerComment +=calculator.createLine(addheaderComment)+"*".repeat(calculator.LINE_LENGTH)+"\n";
        //headerComment= "Angle dN/dOmega "+"\n"+"degrees  particles \n\n"+headerComment+"\n";
        super.initializeArrays(elements);
    }

    public  void check (PolarAngles angles, String someSort, double E, String element)
    {
        //only for backscattered and sputtered!
        if (sort.contains(someSort))
        {
            mapArray.get(element)[(int) Math.round(E / dE)][(int) Math.round( angles.getPolar()/ dTheta)]++;
            mapArray.get("all")[(int) Math.round(E / dE)][(int) Math.round( angles.getPolar()/ dTheta)]++;
        }
    }

    @Override
    public boolean logDependencies() {

        //FIXME ITS A TRAP!!!
        for (String element: elements) {



            // from probability distr. to angle

            for (int i = 0; i <= (int) Math.round(E0 / dE); i++) {
                for (int j = 1; j <= (int) Math.round(90 / dTheta); j++) {
                    mapArray.get(element)[i][j] = mapArray.get(element)[i][j] / (dE * Math.sin(Math.toRadians(Math.abs(j * dTheta))));
                }
            }

            try {
                FileOutputStream surfaceWriter = new FileOutputStream(pathsToLog.get(element));
                String stroka = "E";
                surfaceWriter.write(headerComments.get(element).getBytes());
                for (int i = 0; i <= (int) Math.round(90 / dTheta); i++) {
                    stroka = stroka + columnSeparatorInLog + (int) (i * dTheta);
                }
                stroka = stroka + "\n";
                surfaceWriter.write(stroka.getBytes());

                for (int i = 0; i <= (int) Math.round(E0 / dE); i++) {
                    stroka = (int) (i * dE) + columnSeparatorInLog;
                    for (int j = 0; j <= (int) Math.round(90 / dTheta); j++) {
                        if (i < (int) Math.round(360 / dTheta)) stroka = stroka + mapArray.get(element)[i][j] + columnSeparatorInLog;
                        else stroka = stroka + mapArray.get(element)[i - 1][j] + columnSeparatorInLog;
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
        return  true;
    }

    @Override
    public boolean visualize() {
        if (!sort.equals("") && doVisualisation) EventQueue.invokeLater(() ->
                new EnAngColorMap(calculator.projectileElements+" with average energy of "+calculator.projectileMaxEnergy/1000
                        +" keV hits target of "+
                        calculator.targetElements.replaceAll(" ","")+" at average polar angle of "+
                        calculator.projectileIncidentPolarAngle +" degrees ",  mapArray.get("all"), E0, dE, dTheta,pathsToLog.get("all")));
        return  true;
    }


}

