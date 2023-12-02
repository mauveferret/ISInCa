package ru.mauveferret.Simulators;

import ru.mauveferret.Dependencies.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class SDTrimSP extends Simulator {

    ArrayList<String> particDataPath;

    public SDTrimSP(String directoryPath, boolean doVizualization, boolean getSummary) {
        super(directoryPath, doVizualization);
        this.getSummary = getSummary;
        particDataPath = new ArrayList<>();
    }

    @Override
    public String initializeModelParameters() {
        calculatorType = "SDTrimSP";
        File dataDirectory = new File(directoryPath);
        if (dataDirectory.isDirectory()){
            String triConfig = "";

            //get info from tri.inp file

            try {
                for (File file:  dataDirectory.listFiles()){
                    if (file.getName().contains("tri.inp")){
                        triConfig = file.getAbsolutePath();
                    }
                    if (file.getName().contains("partic")){
                        particDataPath.add(file.getAbsolutePath());
                    }
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(triConfig)));
                String line;
                String someParameter = "";
                int projAmount = 0;

                // if nr_pproj was written than projectileAmount = nr_pproj* nh else projectileAmount = nh*10
                // because default for nr_pproj is 10
                boolean nr_pprojWasWritten = false;


                try {


                    while (reader.ready()) {
                        line = reader.readLine();

                        if (line.contains("=")) {
                            someParameter = line.substring(line.indexOf("=") + 1).trim();
                        }

                        if (line.contains("symbol")) {
                            projectileElements = someParameter;
                            targetElements = "";

                            //all elements are in projectileElements variable, in form "H","W"
                             projectileElements = projectileElements.replaceAll("\"","");
                             elements = projectileElements.split(",");


                             /*
                             //FIXME What is it for?
                            try {
                                for (int i = 0; i < elements.length; i++)
                                    elements[i] = elements[i].substring(1, elements[i].length() - 1);
                            } catch (Exception e) {
                                //System.out.println("73");
                                System.out.println("[WARNING] SDTrimSP75 : "+e.getMessage());
                            }

                              */

                        }

                        if (line.contains("e0") && !line.contains("case")) {
                            if (someParameter.contains(",")) {
                                projectileMaxEnergy = Double.parseDouble(someParameter.substring(0, someParameter.indexOf(",")).trim());
                            } else projectileMaxEnergy = Double.parseDouble(someParameter.trim());
                        }

                        if (line.contains("alpha0")) {
                            String[] alphas = someParameter.split(",");
                            projectileIncidentPolarAngle = Double.parseDouble(alphas[0]);
                            projectileIncidentAzimuthAngle = 0;
                        }

                        if (line.contains("nh")) {
                            projectileAmount = Integer.parseInt(someParameter);
                        }
                        if (line.contains("nr_pproj")) {
                            nr_pprojWasWritten = true;
                            projectileAmount = projectileAmount * Integer.parseInt(someParameter);
                        }
                        if (line.contains("qu")) {
                            String[] qu = someParameter.split(",");
                            for (int i = 0; i < qu.length; i++) {
                                try {
                                    if (Double.parseDouble(qu[i]) == 0) projAmount++;
                                    else
                                        break;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    break;
                                }
                            }
                        }
                    }
                    reader.close();
                }
                catch (Exception e) {e.printStackTrace();}


                if (!nr_pprojWasWritten) projectileAmount = projectileAmount*10;

                projectileElements = elements[0];
                for (int i=1; i<projAmount; i++) projectileElements+=elements[i];

                //projectileElements.replaceAll("\\W", "");
                for (int i=projAmount; i<elements.length; i++) targetElements+=elements[i];
                //targetElements.replaceAll("\\W","");
                elementsList = new ArrayList<>(Arrays.asList(elements));
                elementsList.add("all");
            }
            catch (FileNotFoundException ex){
                ex.printStackTrace();
                return "\"tri.inp\" config is not found";
            }
            catch (Exception ex){
                ex.printStackTrace();
                return "File "+triConfig+" is damaged";
            }

            //check whether the *.dat file exist

            try {
                for (String someDataFilePath: particDataPath) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(someDataFilePath)));
                    reader.close();
                }
            }
            catch (Exception e){
                return "data file wasn't found";
            }

        }
        else return dataDirectory.getName()+" is not a directory";

        initializeCalcVariables();

        modelingID = "SDTrimSP_"+getDirSubname()+"_"+((int) (Math.random()*10000));

        return "OK";
    }




    @Override
    public void postProcessCalculatedFiles(ArrayList<Dependence> depr) {

        //load elements in dependencies

        int LINE_BYTES_AMOUNT = 274;

        dependencies = depr;
        for (Dependence dep: dependencies) dep.initializeArrays(elementsList);

        calcTime = System.currentTimeMillis();

        //find all TRIM-related dependencies

        try {

            ArrayList<Thread> fileThreads = new ArrayList<>();

            for (String someDataFilePath: particDataPath) {

                Thread newFile = new Thread(()->{

                    try {


                        DataInputStream br = new DataInputStream(new FileInputStream(someDataFilePath));
                        byte[] bufLarge = new byte[STRING_COUNT_PER_CYCLE *LINE_BYTES_AMOUNT];
                        byte[] bufSmall = new byte[LINE_BYTES_AMOUNT];

                        //rubbish lines
                        byte[] bufSmall12 = new byte[486];

                        br.read(bufSmall12);

                        //System.out.println(new String( bufSmall12, StandardCharsets.UTF_8 ));

                       /* byte[] bufSmall1 = new byte[274];
                        br.read(bufSmall1);
                        System.out.println("23fvwe");
                        String p = new String( bufSmall1, StandardCharsets.UTF_8 );
                        System.out.println("$"+p+"$");
                        //System.out.println("*1  897*-"+p+"!1");
                        br.read(bufSmall1);
                        System.out.println("*2"+new String( bufSmall1, StandardCharsets.UTF_8 )+"!2");
                        br.read(bufSmall1);
                        System.out.println("*3"+new String( bufSmall1, StandardCharsets.UTF_8 )+"!3");
                        br.read(bufSmall1);
                        System.out.println("*4"+new String( bufSmall1, StandardCharsets.UTF_8 )+"!4");
                        br.read(bufSmall1);
                        System.out.println("*5"+new String( bufSmall1, StandardCharsets.UTF_8 )+"!5");
                        System.exit(0);


                        */

                        //now lets sort

                        String[] datas;

                        String particlesType = someDataFilePath.substring(someDataFilePath.indexOf(File.separator));
                        particlesType = particlesType.substring(particlesType.indexOf("_"), particlesType.lastIndexOf("."));

                        String  sort = "";
                        if (particlesType.contains("back_p")) sort = "B";
                        else if (particlesType.contains("back_r")) sort = "S";
                        else if (particlesType.contains("stop_p")) sort = "I";
                        else if (particlesType.contains("stop_r")) sort = "D";
                        else if (particlesType.contains("tran")) sort = "T";

                        String sorts = "";
                        for (Dependence someDistr : dependencies) sorts += someDistr.getSort();

                        if (sorts.contains(sort) || getSummary) {

                            while (br.available()>=LINE_BYTES_AMOUNT) {
                                if (br.available()>= STRING_COUNT_PER_CYCLE *LINE_BYTES_AMOUNT){
                                    br.read(bufLarge);
                                    String line = new String( bufLarge, StandardCharsets.UTF_8 );

                                    for (int i = 0; i< STRING_COUNT_PER_CYCLE; i++){
                                        //System.out.println("+"+
                                        // line.substring(i*LINE_BYTES_AMOUNT+1,((i+1)*LINE_BYTES_AMOUNT))+"*");
                                        analyse(line.substring(i*LINE_BYTES_AMOUNT+1,(i+1)*LINE_BYTES_AMOUNT),
                                                dependencies, sort);
                                    }

                                }
                                else {
                                    br.read(bufSmall);
                                    analyse(new String( bufSmall, StandardCharsets.UTF_8 ),dependencies,sort);
                                }

                            }
                            br.close();

                        }
                    }
                    catch (Exception e){
                        System.out.println("for thread "+Thread.currentThread().getName()+" error: "+ e.getMessage());
                        e.printStackTrace();
                    }
                });
                File file = new File(someDataFilePath);
                String threadName = file.getParentFile().getName()+File.separator+file.getName();
                newFile.setName(threadName);
                fileThreads.add(newFile);
            }

            for (Thread thread: fileThreads) thread.start();

            //wait for end of the calculation
            double startTime = System.currentTimeMillis();
            int j=0;
            for (Thread thread: fileThreads){
                j++;
                thread.join();
                System.out.println("   ++++++++++++++++++++++++++++++");
                System.out.println("    [SDTrimSP]["+thread.getName()+"] PROGRESS: "+j*100/fileThreads.size()+"%");
                double newTime = (((double) System.currentTimeMillis())-startTime)/((double) 60000);
                System.out.println("    [SDTrimSP]["+thread.getName()+"] time: "+
                        new BigDecimal(newTime).setScale(4, RoundingMode.UP)+" min");
                System.out.println("   ++++++++++++++++++++++++++++++");
            }

            finishCalcVariables();
            finishTime();


        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void analyse(String line, ArrayList<Dependence> distributions, String sort){


        double en = 0, collisionsAmount = 0, fluence = 0, xEnd = 0, yEnd = 0, zEnd = 0, cosP = 0, cosA = 0, path = 0;
        String element = "";
        if (!line.contains("end")) {


            line = line.replaceAll("\\h+"," ");
            line = line.replaceAll("\\n", "");

           // System.out.println("*"+line+"+");

            String[] datas = line.split(" ");


            try {
                element = elements[Integer.parseInt(datas[1])-1];
                //collisionsAmount = Double.parseDouble(datas[2]);
                //fluence = Double.parseDouble(datas[3]);
                en = Double.parseDouble(datas[4]);
                xEnd = Double.parseDouble(datas[8]);
                yEnd = Double.parseDouble(datas[9]);
                zEnd = Double.parseDouble(datas[10]);
                cosP = Double.parseDouble(datas[14]);
                cosA = Double.parseDouble(datas[15]);
               // path = Double.parseDouble(datas[16]);
            }
            catch (Exception ex) {//ex.printStackTrace();}
            }
            //System.out.println(en +"   "+cosA+"   "+cosP+"   "+element);

            //Here is several spectra calculators

            PolarAngles angles = new PolarAngles(cosP, cosA, xEnd, yEnd);

           //System.out.println(cosP+" "+cosA );

            for (Dependence distr : distributions) {
                switch (distr.getDepName()) {
                    case "energy":
                        ((Energy) distr).check(angles, sort, en, element);
                        break;
                    case "polar":
                        ((Polar) distr).check(angles, sort, element);
                        break;
                    case "anglemap":
                        ((AngleMap) distr).check(angles, sort, element);
                        break;
                    case "gettxt":
                        ((getTXT) distr).check(angles, sort, en);
                        break;
                    case "cartesianmap":
                        ((CartesianMap) distr).check(zEnd, yEnd, sort, element);
                        break;
                    case "angenmap":
                        ((AngEnMap) distr).check(angles, sort, en, element);
                }
            }

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
