package ru.mauveferret.Simulators;

import ru.mauveferret.Dependencies.*;

import java.io.*;
import java.util.ArrayList;

public class TRIM extends ParticleInMatterCalculator{

    String dataPath;

    public TRIM(String directoryPath, boolean doVizualization) {
        super(directoryPath, doVizualization);
        dataPath = "";

        projectileIncidentAzimuthAngle = 0;
        projectileIncidentPolarAngle = 0;
        projectileAmount = -1;
    }

    @Override
    public String initializeModelParameters() {
        calculatorType = "TRIM";
        File dataDirectory = new File(directoryPath);
        if (dataDirectory.isDirectory()){
            String tscConfig = "";

            //get info from COLLISON.txt file

            try {
                for (File file:  dataDirectory.listFiles()){
                    if (file.getName().contains("COLLIS")){
                        modelingID = "TRIM"+((int) (Math.random()*1000));
                        tscConfig = file.getAbsolutePath();
                    }
                    if (file.getName().contains("TRIMOUT")){
                        dataPath = file.getAbsolutePath();
                    }
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(tscConfig)));
                String line;
                String someParameter = "";
                int i=0;
                while (reader.ready()){
                    line = reader.readLine();
                    i++;
                    if (line.contains("=")) someParameter = line.substring(line.indexOf("=")+1).trim();
                    if (line.contains(">")) someParameter = line.substring(line.indexOf(">")+1).trim();

                    if (line.contains("Ion Name")) projectileElements= someParameter;
                    if (line.contains(">") ) targetElements += someParameter.substring(0,2).trim()+" ";

                    if (line.contains("Ion Energy")) projectileMaxEnergy = Double.parseDouble(someParameter.substring(0, someParameter.indexOf("k")).trim().replace(",","."));
                    if (i>1000) break;
                }
                reader.close();
            }
            catch (FileNotFoundException ex){
                return "\"COLLISION.txt\" config is not found";
            }
            catch (IOException ex){
                return "File "+tscConfig+" is damaged";
            }
            catch (Exception e){
                e.printStackTrace();
            }

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
    public void postProcessCalculatedFiles(ArrayList<Dependence> distributions) {

        //FIXME initializeArrays of dependencies


        calcTime = System.currentTimeMillis();

        //find all TRIM-related distributions

        float en = 0, cosx, cosy, cosz;
        String sort;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataPath));

            //rubbish lines
            String line = br.readLine();
            while (!line.contains("TRIM Calc.")) line = br.readLine();
            //two rubbish lines must be excluded
            br.readLine();
            br.readLine();
            //now lets sort

            while (br.ready()) {
                line = br.readLine();

                sort = ((line.charAt(0)+"").equals("T")) ? "I" : line.charAt(0)+"";
                //FIXME axis directions
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

                //Here is several spectra calculators

                PolarAngles angles = new PolarAngles(cosx,cosy,cosz);

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

            }
            br.close();
            finishCalcVariables();
            finishTime();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
