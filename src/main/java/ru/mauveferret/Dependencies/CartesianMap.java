package ru.mauveferret.Dependencies;

import ru.mauveferret.Simulators.Simulator;

import java.io.FileOutputStream;
import java.util.ArrayList;

public class CartesianMap extends Dependence {

    String mapType;
    String typeOfX, typeOfY;
    private final double delta;
    int size = 1000; //Angstrom
    int center;

    //max needs to cut from matrix zeros
    double max;
    //double xMax, yMax;

    public CartesianMap(double delta, String mapType, String sort, Simulator calculator) {
        super(calculator, sort);
        this.mapType = mapType;
        typeOfX = mapType.charAt(0)+"";
        typeOfY = mapType.charAt(1)+"";
        this.delta = delta;
        center = (int) (size/(2*delta));
        max = delta;

        depType = "map";
        endOfPath="MAP OF "+mapType+" for "+sort+" delta "+delta+".txt";
        mapArrayXsize = (int) Math.ceil(size/delta)+10;
        mapArrayYsize = (int) Math.ceil(size/delta)+10;
          }

    public CartesianMap(double delta, int size, String mapType, String sort, Simulator calculator) {
        super(calculator, sort);
        this.mapType = mapType;
        typeOfX = mapType.charAt(0)+"";
        typeOfY = mapType.charAt(1)+"";
        this.delta = delta;
        this.size = size;
        center = (int) (size/(2*delta));
        max = delta;

        depType = "map";
        endOfPath="MAP OF "+mapType+" for "+sort+" delta "+delta+".txt";
        mapArrayXsize = (int) Math.ceil(size/delta)+10;
        mapArrayYsize = (int) Math.ceil(size/delta)+10;
    }

    @Override
    public void initializeArrays(ArrayList<String> elements) {
        headerComment = simulator.createHeader();
        String addheaderComment = " delta "+delta+" Angstrom sort "+sort+" type "+ depName;
        headerComment += simulator.createLine(addheaderComment)+"*".repeat(simulator.LINE_LENGTH)+"\n";
        headerComment= "Angle dN/dOmega "+"\n"+"degrees  particles \n\n"+headerComment+"\n";
        super.initializeArrays(elements);
    }


    //not real X,Y but some two Axis, specified in mapType variable
    public  void check (double X, double Y, String someSort, String element)
    {
        //only for backscattered and sputtered!
        if (sort.contains(someSort)){
            if (X<size*delta/2 & Y<size*delta/2) {
                if (X > 0 && Y > 0)
                    mapArray.get(element)[center + (int) (Math.round(X / delta))][center + (int) (Math.round(Y / delta))]++;
                else if (X > 0 && Y < 0)
                    mapArray.get(element)[center + (int) (Math.round(X / delta))][center - (int) (Math.round(Y / delta))]++;
                else if (X < 0 && Y > 0)
                    mapArray.get(element)[center - (int) (Math.round(X / delta))][center + (int) (Math.round(Y / delta))]++;
                else
                    mapArray.get(element)[center - (int) (Math.round(X / delta))][center - (int) (Math.round(Y / delta))]++;

                X = Math.abs(X);
                Y = Math.abs(Y);
                if (X > max) max = X;
                if (Y > max) max = Y;
            }
            /*if (X>max) {
                max = X;
                xMax = X;
                yMax = Y;
            }
            if (Y>max) {
                max = Y;
                xMax = X;
                yMax = Y;
            }

             */
        }
    }

    @Override
    public boolean logDependencies() {

        for (String element: elements) {

            try {
                FileOutputStream surfaceWriter = new FileOutputStream(pathsToLog.get(element));
                String stroka = typeOfX + ": ";
                surfaceWriter.write(headerComments.get(element).getBytes());

                int leftEdge = center - ((int) (max / delta)) - 2;
                int rightEdge = center + ((int) (max / delta)) + 2;


            /*check whether is possible to mak edge less

            for (int i = rightEdge; i>center; i--){
                double sum = 0;
                for (int j=center; j<rightEdge;j++) sum+=cartesianMap[((int) (xMax/delta))][j];
                if (sum<1000) rightEdge--;
            }

            for (int i = leftEdge; i<center; i++){
                double sum = 0;
                for (int j=center; j>leftEdge;j--) sum+=cartesianMap[((int) (xMax/delta))][j];
                if (sum<1000) leftEdge--;
            }

            System.out.println("after min"+center+" "+leftEdge+" "+rightEdge);

             */


                for (int i = leftEdge; i <= rightEdge; i++) {
                    stroka = stroka + columnSeparatorInLog + (int) ((i - center) * delta);
                }
                stroka = stroka + "\n";
                surfaceWriter.write(stroka.getBytes());

                for (int i = leftEdge; i <= rightEdge; i++) {
                    stroka = (int) ((i - center) * delta) + columnSeparatorInLog;
                    for (int j = leftEdge; j <= rightEdge; j++) {
                        stroka = stroka + mapArray.get(element)[i][j] + columnSeparatorInLog;
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
        return false;
    }
}
