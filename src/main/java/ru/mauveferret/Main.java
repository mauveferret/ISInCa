package ru.mauveferret;

import ru.mauveferret.Generators.SDTrimSPCombineTaskGenerator;

public class Main  {

    public static void main(String[] args) {



        if (args.length>0)
        {
            try {
                switch (args[0]){
                    case "-config":
                    case "-c": new Console(args);
                    break;
                    case "-version":
                    case "-v": {
                        System.out.println("[ISInCa] Java Virtual Machine current version: "+
                                System.getProperty("java.version"));
                        System.out.println("[ISInCa] ISInCa current version: "+Main.getVersion());
                        System.out.println("[ISInCa] check updates at https://github.com/mauveferret/ISInCa ");
                    }
                    break;
                    case "-gui": GUI.main(args);
                    break;
                    case "-help":
                    case "-h":
                        System.out.println("-g - GUI mode;\n-c - console mode, needs second argument - path to xml " +
                                "config file; \n -v - print version of JVM and this code; \n -h - this message");
                    break;
                    case "-generate":
                    case "-g": new SDTrimSPCombineTaskGenerator();
                }
            }
            catch (Exception e){
                System.out.println("wrong command! "+e.getMessage());
            }
        }
        else  GUI.main(args);
    }

    public static String getVersion(){
        String version = "v2022.1.0";
        return  version;
    }

}
