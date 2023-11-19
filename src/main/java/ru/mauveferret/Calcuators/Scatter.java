package ru.mauveferret.Calcuators;

import ru.mauveferret.Dependencies.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class Scatter extends ParticleInMatterCalculator {

    String dataPath;

    public Scatter(String directoryPath, boolean doVizualization) {
        super(directoryPath, doVizualization);
        dataPath = "";
    }

    @Override
    public String initializeModelParameters() {
        calculatorType = "SCATTER";
        //Arrays.asList(elements)
        elements = new String[]{"all"};
        elementsList = new ArrayList<>();
        elementsList.add("all");
        File dataDirectory = new File(directoryPath);
        if (dataDirectory.isDirectory()){
            String tscConfig = "";

            //get info from *.tsk file

            try {
                for (File file:  dataDirectory.listFiles()){
                    if (file.getName().matches("SC\\d+.tsk")){
                        modelingID = file.getName().substring(0,file.getName().indexOf("."));
                        tscConfig = file.getAbsolutePath();
                    }
                    if (file.getName().matches("SC\\d+.dat")){
                        modelingID = file.getName().substring(0,file.getName().indexOf("."));
                        dataPath = file.getAbsolutePath();
                    }
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(tscConfig)));
                String line;
                String previousLine = "";
                String someParameter = "";

                //strange names in order to not participate in "if previousLine.contain..." occasionally "
                String lastElementOf1Layer = "qwerty1234567";
                String lastElementOf2Layer = "qwerty12345678";
                String lastElementOf3Layer = "qwerty123456789";


                while (reader.ready()){
                    line = reader.readLine();
                    if (line.contains("=")) someParameter = line.substring(line.indexOf("=")+1).trim();
                    if (line.contains("Atom") && previousLine.contains("Projectile")) {
                        projectileElements= someParameter;
                        elementsList.add(someParameter);
                    }


                    if (line.contains("Component")&&line.contains("L1"))  lastElementOf1Layer = someParameter;
                    if (line.contains("Component")&&line.contains("L2")) lastElementOf2Layer = someParameter;
                    if (line.contains("Component")&&line.contains("L3")) lastElementOf3Layer = someParameter;

                    if (line.contains("Atom") && !previousLine.contains("Projectile")) {
                        if (targetElements.contains("elements")) targetElements = "";
                        targetElements += someParameter+" ";

                        //  for cases with similar elements in different layers
                        if (elementsList.contains(someParameter+"2")) elementsList.add(someParameter+"3");
                        else
                        if (elementsList.contains(someParameter)) elementsList.add(someParameter+"2");
                        else elementsList.add(someParameter);


                        //fill with empty element in order to keep SCATTER elements numeration
                        if (previousLine.contains(lastElementOf1Layer)){
                            int zeroID = elementsList.size()-2;
                            for (int i=zeroID+1;i<=5;i++) {
                                elementsList.add(i+"");
                            }
                        }
                        if (previousLine.contains(lastElementOf2Layer)){
                            int zeroID = elementsList.size()-5-2;
                            for (int i=zeroID+1;i<=5;i++) {
                                elementsList.add(i+"");
                            }
                        }

                        if (previousLine.contains(lastElementOf3Layer)){
                            int zeroID = elementsList.size()-10-2;
                            for (int i=zeroID+1;i<=5;i++) {
                                elementsList.add(i+"");
                            }
                        }

                    }
                    if (line.contains("StartEnergy")) projectileMaxEnergy = Double.parseDouble(someParameter);
                    if (line.contains("StartAngle")) projectileIncidentPolarAngle = Double.parseDouble(someParameter);
                    if (line.contains("StartPhi")) projectileIncidentAzimuthAngle = Double.parseDouble(someParameter);
                    if (line.contains("Number")) projectileAmount = Integer.parseInt(someParameter);
                    previousLine = line;
                }
                reader.close();
                initializeCalcVariables();
            }
            catch (FileNotFoundException ex){
                return "\"SC******.tsk\" config is not found";
            }
            catch (IOException ex){
                return "File "+tscConfig+" is damaged";
            }

            modelingID+="_"+getDirSubname()+"_"+((int) (Math.random()*10000));

            //check whether the *.dat file exist

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath)));
                reader.close();
            }
            catch (Exception e){
                return "data file wasn't found";
            }

        }
        else return dataDirectory.getName()+" is not a directory";

        return "OK";
    }

    @Override
    public void postProcessCalculatedFiles(ArrayList<Dependence> depr) {


        //for (String elements: elementsList) System.out.println(elements+" ");

        dependencies = depr;
        for (Dependence dep: dependencies) dep.initializeArrays(elementsList);

        calcTime = System.currentTimeMillis();

        //find all SCATTER-related distributions

        float  floatSort, en = 0, cosx, cosy, cosz;
        String sort = "U";

        try {
            DataInputStream reader = new DataInputStream(new FileInputStream(dataPath));
            byte[] buf = new byte[STRING_COUNT_PER_CYCLE *18];

            //TODO +17  //update 22.10.15 WTF?
            while (reader.available() >  STRING_COUNT_PER_CYCLE *18) {

                reader.read(buf);
                int shift = 0;

                 // movement through buf for 'stringCountPerCycle' times
                for (int j = 1; j <= STRING_COUNT_PER_CYCLE; j++) {

                    floatSort = buf[shift];
                    //second byte of buf has  unknown purpose

                    en = ByteBuffer.wrap(ArraySubPart(buf, 2 + shift, 5 + shift)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    cosx = ByteBuffer.wrap(ArraySubPart(buf, 6 + shift, 9 + shift)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    cosy = ByteBuffer.wrap(ArraySubPart(buf, 10 + shift, 13 + shift)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    cosz = ByteBuffer.wrap(ArraySubPart(buf, 14 + shift, 17 + shift)).order(ByteOrder.LITTLE_ENDIAN).getFloat();

                    if (floatSort>=0 && cosz>=0 ) sort = "S"; else
                    if (floatSort<0 && cosz>0) sort = "B"; else
                    if (floatSort<0 && cosz<0 && en<100) sort = "I"; else
                    if (floatSort<0 && cosz<0 && en>100) sort = "T"; else
                    if (floatSort>=0 && cosz<=0 ) sort = "D";
                    else sort = "S"; // for particles with "NaN" coordinates.

                    //Here is several spectra calculators

                    PolarAngles angles = new PolarAngles(cosx,cosy,cosz);

                    for (Dependence distr: dependencies){
                        switch (distr.getDepName())
                        {
                            case "energy": ((Energy) distr).check(angles,sort,en,elementsList.get(((int) floatSort)+2));
                            break;
                            case "polar": ((Polar) distr).check(angles,sort, elementsList.get(((int) floatSort)+2));
                            break;
                            case "anglemap": ((AngleMap) distr).check(angles,sort, elementsList.get(((int) floatSort)+2));
                            break;
                            case "gettxt": ((getTXT) distr).check(angles,sort,en);
                            break;
                        }
                    }

                    //calculate some scattering constants

                    String element = elementsList.get(((int) floatSort)+2);
                    //if (element.contains("D")) System.out.println(element+" "+sort+" "+floatSort);
                    if (!sort.contains("S") && !sort.contains("D") && !sort.contains("T")) particleCount++;

                    switch (sort) {
                        case "B":
                            scattered.put(element, scattered.get(element) + 1);
                            energyRecoil.put(element, energyRecoil.get(element) + en);
                            scattered.put("all", scattered.get("all") + 1);
                            energyRecoil.put("all", energyRecoil.get("all") + en);
                            break;
                        case "S":
                            sputtered.put(element, sputtered.get(element) + 1);
                            sputtered.put("all", sputtered.get("all") + 1);
                            break;
                        case "I":
                            implanted.put(element, implanted.get(element) + 1);
                            implanted.put("all", implanted.get("all") + 1);
                            break;
                        case "T":
                            transmitted.put(element, transmitted.get(element) + 1);
                            transmitted.put("all", transmitted.get("all") + 1);
                            break;
                        case "D":
                            displaced.put(element, displaced.get(element) + 1);
                            displaced.put("all", displaced.get("all") + 1);
                            break;
                    }

                    shift += 18;
                }
            }
            reader.close();

            finishCalcVariables();
            finishTime();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private  byte[] ArraySubPart (byte[] array,int a, int b) //part of one array into another
    {
        byte[] subArray = new byte[b-a+1];
        for (int i=a;i<=b;i++)
        {
            subArray[i-a]=array[i];
        }
        return subArray;
    }

}
