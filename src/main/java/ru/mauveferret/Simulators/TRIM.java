package ru.mauveferret.Simulators;

import ru.mauveferret.Dependencies.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class TRIM extends Simulator {


    public TRIM(String directoryPath, boolean doVizualization) {
        super(directoryPath, doVizualization);
    }

    @Override
    public String initializeModelParameters() {
        calculatorType = "TRIM";
        elementsList = new ArrayList<>();
        elementsList.add("all");
        particDataPathsList = new ArrayList<>();

        File dataDirectory = new File(directoryPath);
        if (dataDirectory.isDirectory()){
            String tscConfig = "";

            //get info from TRIM.IN file

            try {
                for (File file:  dataDirectory.listFiles()){
                    if (file.getName().contains("TRIM.IN")){
                        modelingID = "TRIM"+((int) (Math.random()*1000));
                        tscConfig = file.getAbsolutePath();
                    }
                    if (file.getName().contains("BACKSCAT")){
                        particDataPathsList.add(file.getAbsolutePath());
                    }

                    if (file.getName().contains("SPUTTER")){
                        particDataPathsList.add(file.getAbsolutePath());
                    }

                    if (file.getName().contains("TRANSMIT")){
                        particDataPathsList.add(file.getAbsolutePath());
                    }
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(tscConfig)));
                String line;
                targetElements = "";
                boolean nextlineIsIon = false;

                while (reader.ready()){
                    line = reader.readLine();


                    if (nextlineIsIon){
                        line = line.replaceAll("\\h+"," ");
                        line = line.replaceAll("\\n", "").trim();
                        String[] ionParams = line.split(" ");

                        try {
                            projectileAmount = Integer.parseInt(ionParams[4]);
                            projectileIncidentPolarAngle = Double.parseDouble(ionParams[3]);
                            projectileMaxEnergy = Double.parseDouble(ionParams[2])*1000;
                            projectileElements = "Z1:"+ionParams[0];
                            elementsList.add(projectileElements);

                        }
                        catch (Exception e){
                            System.out.println("[ERROR 867] "+e.getMessage());
                            System.out.println("[ERROR 867] TRIM.IN parsing error");
                        }
                        nextlineIsIon=false;
                    }

                    if (line.contains("Atom") && line.contains("="))
                    {
                        line = line.replaceAll("\\h+","");
                        line = line.replaceAll("\\n", "").trim();
                        String[] targetParams = line.split("=");
                        targetElements+=targetParams[1]+" ";
                        elementsList.add(targetParams[1]);
                    }

                    if (line.contains("Ion") && line.contains("Energy")) nextlineIsIon=true;
              }
                reader.close();
                targetElements = targetElements.trim();
                elements = targetElements.split(" ");

            }
            catch (FileNotFoundException ex){
                return "\"TRIM.IN\" config is not found";
            }
            catch (IOException ex){
                System.out.println("[ERROR784]"+ex.getMessage());
                return "File "+tscConfig+" is damaged";
            }
            catch (Exception e){
                System.out.println("[ERROR 785] "+e.getMessage());
            }

            //check whether the *.dat file exist
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(particDataPathsList.get(0))));
                reader.close();
            }
            catch (Exception e){
                System.out.println("[ERROR 786] "+e.getMessage());
                return "data file wasn't found";
            }

        }
        else return dataDirectory.getName()+" is not a directory";

        initializeCalcVariables();
        return "OK";
    }

    @Override
    public void postProcessCalculatedFiles(ArrayList<Dependence> depr) {

        calcTime = System.currentTimeMillis();

        ArrayList<Thread> fileThreads = new ArrayList<>();

        //TODO would not be better to do it in the simulator?
        dependencies = depr;
        for (Dependence dep: dependencies) dep.initializeArrays(elementsList);


        for (String someDataFilePath: particDataPathsList) {

            Thread newFile = new Thread(()->{

                try {
                    String sort="";

                    String particlesType = someDataFilePath.substring(someDataFilePath.lastIndexOf(File.separator)+1);

                    if (particlesType.contains("BACKSCAT")) sort = "B";
                    else if (particlesType.contains("SPUTTER")) sort = "S";
                    else if (particlesType.contains("TRANSMIT")) sort = "T";
                    String sorts = "";
                    for (Dependence someDistr : dependencies) sorts += someDistr.getSort();


                    if (sorts.contains(sort)) {

                        BufferedReader br = new BufferedReader(new FileReader(someDataFilePath));

                        //rubbish lines
                        String line = br.readLine();
                        while (!line.contains("TRIM Calc.")) line = br.readLine();
                        //two rubbish lines must be excluded
                        br.readLine();
                        br.readLine();
                        //now lets sort

                        while (br.ready()) analyse(br.readLine(), dependencies, sort);
                        br.close();
                        }
                }
                catch (Exception e){
                    System.out.println("for thread "+Thread.currentThread().getName()+" error: "+ e.getMessage());
                    System.out.println("[ERROR 666] "+e.getMessage());
                }
            });
            File file = new File(someDataFilePath);
            String threadName = file.getParentFile().getName()+File.separator+file.getName();
            newFile.setName(threadName);
            fileThreads.add(newFile);
        }


        for (Thread thread: fileThreads) thread.start();

        finishCalcVariables();
        finishTime();
    }

    private void analyse(String line, ArrayList<Dependence> distributions, String sort){

        //find all TRIM-related distributions
        float en = 0, cosx, cosy, cosz;
        double  collisionsAmount = 0, fluence = 0, xEnd = 0, yEnd = 0, zEnd = 0, cosP = 0, cosA = 0, path = 0;
        String element = "";
        if (!line.contains("end")) {



            line = line.substring(line.indexOf(","));
            en = Float.parseFloat(line.substring(0, line.indexOf(" ")).replace(",", "0."));
            //find "cosz" column
            if (line.endsWith(" ")) line = line.substring(0, line.length() - 1);
            cosy = Float.parseFloat(line.substring(line.lastIndexOf(" ") + 1).replace(",", "0."));
            //find "cosy" column
            line = line.substring(0, line.lastIndexOf(" "));
            if (line.endsWith(" ")) line = line.substring(0, line.length() - 1);
            cosx = Float.parseFloat(line.substring(line.lastIndexOf(" ") + 1).replace(",", "0."));
            //find "cosx" column
            line = line.substring(0, line.lastIndexOf(" "));
            if (line.endsWith(" ")) line = line.substring(0, line.length() - 1);
            cosz = -1 * Float.parseFloat(line.substring(line.lastIndexOf(" ") + 1).replace(",", "0."));

            //отвергаем "оборванную" строку
            if (cosx > 1 || cosy > 1 || cosz > 1) {
                cosx = 0;
                cosy = 0;
                cosz = 0;  //одна битая точка никому не повредит 2018
            }

            PolarAngles angles = new PolarAngles(cosx,cosy,cosz);

            //FIXME all coefficients goes to zero target element
            element=(sort.equals("B"))?projectileElements:elementsList.get(0);

            //System.out.println(cosP+" "+cosA );

            for (Dependence distr: distributions){
                switch (distr.getDepName())
                {
                    case "energy": ((Energy) distr).check(angles,sort,en, projectileElements);
                        break;
                    case "polar": ((Polar) distr).check(angles,sort, projectileElements);
                        break;
                    case "anglemap": ((AngleMap) distr).check(angles,sort, projectileElements);
                        break;
                    case "gettxt": ((getTXT) distr).check(angles,sort,en);
                }
            }

            //calculate some scattering constants
            if (!sort.contains("S") && !sort.contains("D")) particleCount++;

            if (sort.equals("B")) {
                scattered.put("all", scattered.get("all") + 1);
                energyRecoil.put("all", energyRecoil.get("all") + en);
            }
            else if (sort.equals("S")) sputtered.put("all", sputtered.get("all") + 1);
            else if (sort.equals("I")) implanted.put("all", implanted.get("all") + 1);


            //calculate some scattering constants
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
        }
    }


}
