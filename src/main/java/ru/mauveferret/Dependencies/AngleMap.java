package ru.mauveferret.Dependencies;

import ru.mauveferret.Simulators.Simulator;
import ru.mauveferret.Charts.JFree_AngleMap;
import java.awt.*;
import java.io.FileOutputStream;
import java.util.ArrayList;

import static ru.mauveferret.Console.isConsole;

public class AngleMap extends Dependence {

    private final double dPhi;
    private final double dBeta;

    public AngleMap(double dPhi, double dBeta, String sort, Simulator calculator) {
        super(calculator, sort);
        this.dPhi = dPhi;
        this.dBeta = dBeta;

        depType = "map";
        mapArrayXsize = (int) Math.ceil(360/dPhi)+1;
        mapArrayYsize = (int) Math.ceil(90/dBeta)+1;
        endOfPath="_dphi "+dPhi+"_dBeta"+dBeta+".txt";
    }

    @Override
    public void initializeArrays(ArrayList<String> elements) {
        headerComment = simulator.createHeader();
        String addheaderComment = " dPhi "+dPhi+" degrees dBeta "+dBeta+" degrees ";
        headerComment += simulator.createLine(addheaderComment)+"*".repeat(simulator.LINE_LENGTH)+"\n";
        headerComment= "Angle dN/dOmega "+"\n"+"degrees  particles \n\n"+headerComment+"\n";
        super.initializeArrays(elements);
    }

    public  void check (PolarAngles angles, String someSort, String element)
    {
        //only for backscattered and sputtered!
        if (sort.contains(someSort))
        {
            mapArray.get(element)[(int) (Math.round(angles.getAzimuth() / dPhi))][(int) Math.round( angles.getPolar()/ dBeta)]++;
            mapArray.get("all")[(int) (Math.round(angles.getAzimuth() / dPhi))][(int) Math.round( angles.getPolar()/ dBeta)]++;
        }
    }

    @Override
    public boolean logDependencies() {

        for (String element: elements) {

            // For some reason for zero azimuth angles values are sufficiently underestimated,
            // so we take neighbour row of azimuth angles
            for (int i = 1; i < (int) Math.ceil(90 / dBeta) + 1; i++) {
                mapArray.get(element)[0][i] = mapArray.get(element)[(int) Math.round((360 - dPhi) / dPhi)][i];
            }

            // from probability distr. to angle
            for (int i = 0; i <= (int) Math.round(360 / dPhi); i++) {
                for (int j = 1; j < (int) Math.round(90 / dBeta); j++) {
                    mapArray.get(element)[i][j] = mapArray.get(element)[i][j] / (dPhi *  Math.sin(Math.toRadians(Math.abs(j * dBeta)))
                            * 2 * Math.sin(Math.toRadians(Math.abs(dBeta/2))));
                }
                // Fix for @IANikiti
                mapArray.get(element)[i][(int) Math.round(90 / dBeta)] = dPhi * Math.sin(Math.toRadians(Math.abs(dBeta/2)));
            }

            // for Surface  Normal angle. Fix for @IANikiti
            mapArray.get(element)[0][0] = mapArray.get(element)[0][0] / (dPhi *  (1 - Math.cos(Math.toRadians(Math.abs(dBeta/2)))));


            try {
                FileOutputStream surfaceWriter = new FileOutputStream(pathsToLog.get(element));
                String stroka = "Phi";
                surfaceWriter.write(headerComments.get(element).getBytes());
                for (int i = 0; i <= (int) Math.round(90 / dBeta); i++) {
                    stroka = stroka + columnSeparatorInLog + (int) (i * dBeta);
                }
                stroka = stroka + "\n";
                stroka = stroka.replaceAll(",",".");
                surfaceWriter.write(stroka.getBytes());

                for (int i = 0; i <= (int) Math.round(360 / dPhi); i++) {
                    stroka = (int) (i * dPhi) + columnSeparatorInLog;
                    for (int j = 0; j <= (int) Math.round(90 / dBeta); j++) {
                        if (i < (int) Math.round(360 / dBeta)) stroka = stroka + mapArray.get(element)[i][j] + columnSeparatorInLog;
                        else stroka = stroka + mapArray.get(element)[i - 1][j] + columnSeparatorInLog;
                    }
                    stroka = stroka + "\n";
                    stroka = stroka.replaceAll(",",".");
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
                new JFree_AngleMap("ISInCa",  mapArray.get("all"), dPhi, dBeta, pathsToLog.get("all")));
        return  true;
    }


}

