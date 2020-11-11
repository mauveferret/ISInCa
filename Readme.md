# ISInCa - Ion Surface Interaction Calculator
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]

ISInCA - is a small JAVA tool dedicated to postprocess *[`SCATTER`](https://www.sciencedirect.com/science/article/pii/S0042207X00001366)*, *[`TRIM`](http://www.srim.org/)*, *[`SDTrimSP`](http://www2.ipp.mpg.de/~stel/SDTrimSP.html)* calculations. It is planned to gradually widen the list of the supported codes (like TRYDIN, MARLOWE and so on).
[![N|Solid](https://i.ibb.co/0cQrTDm/Croco-Logo.png)](https://dobrynja.livejournal.com/38140.html). 
# Basic outputs

Listed codes are created for investigation of particles with surface analysis. Usually you can set some beam parameters (like mass/angle/energy distribution, doze) and the target composition (and a relief for some exotic codes). Usually such codes generate single or several files, containing tables with scattered, sputtered, transmitted, displaced particles with data on their position, motion direction, sort, energy, pathlength etc. ISInCa code can transform this huge (up to hundreds of GB) data files to familiar and easy for analysis distributions. In this way, it can calculate:

 - Energy distributions dN/dE(E) for any solid angles with any energy step **(backscattered primary particles, sputtered and transmitted particles)**
 - Angle distributions dN/dTheta(Theta) **(backscattered primary particles, sputtered and transmitted particles)**
 - Depth distributions for primary particles **NOT FINISHED YET**
 - Polar Maps for ane solid angles
 - Cartesian Maps 
 - Integral coefficients:
   - Scattered coefficient (amount of scattered divided by amount of incident)
   - Sputtered coefficient (amount of sputtered divided by amount of incident)
   - Implanted coefficient 
   - Transmitted coefficient
   - Energy recoil coefficient
   - Displaced coefficient
   - ...
   - 
All data is available for different combination of particles (scattered, sputtered, implanted, transmitted  and displaced).
Since version *[`2020.4.0`](https://github.com/mauveferret/ISInCa/commit/d3d1506027f252289089755e8020599890d4b4ca)* ISInCa can calculate data separatly for every particle from incident beam or a target. Also an opportunity for combining results from several calculations has appeared.

# Installation
ISInCa is fully JAVA program so it can be launched in any OS on every processor architecture!  
Executable files for the latest version are located in *[`/out`](https://github.com/mauveferret/ISInCa/tree/master/out)* directory:
- For "easy mode" in Windows OS you can download an executable file *[`ISInCa.exe`](https://github.com/mauveferret/ISInCa/blob/master/out/ISInCa.exe)* (for GUI mode only with limiting calculation capabilities!)
- For all OS you can take *[`isinca.jar`](https://github.com/mauveferret/ISInCa/blob/master/out/ISInCa.jar)*, which provides full functionality of ISInCa
- For console mode you will also need a template for config file: *[`isinca.xml`](https://github.com/mauveferret/ISInCa/blob/master/out/isinca.xml)*

In any case you need to have a **Java Virtual Machine** (version **11** or newer) installed on your computer. To check whether the JVM was already installed, open the terminal (in Windows OS press "Win"+"R", then type "cmd" in an appeared window, then press enter) and type `java -version`, then press "enter". If the output looks like 
> Java is not recognized as an internal or external command

or the version is less then **11**, you need to download JVM. For example, from the [Oracle](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) (the company, that supports Java). Installation of the JDK (which includes JVM + libraries) seems not to be tricky (look for *[`tutorial`](https://docs.oracle.com/javase/9/install/installation-jdk-and-jre-microsoft-windows-platforms.htm#JSJIG-GUID-2B9D2A17-176B-4BC8-AE2D-FD884161C958)*). ISInCa executables don't need special installation, so you can run ISInCa right after downloading and installing JVM

# Launch

For your convinience **CONSOLE** and **GUI** modes were made in ISInCA. GUI is based on the ***[`JavaFX`](https://openjfx.io/)*** package, which is included in the executable (as from the 11-th version of Java it was removed from the JDK)

## GUI

GUI is the default mode so there is no need in any launch arguments, so just run it... Main window of the GUI mode looks like:
[![N|Solid](https://i.ibb.co/ckSBLs9/10-10-2020-180648.png)](https://github.com/mauveferret)
The interface seems to be user-friendly. In case of troubles check `help` tab. 
The main feature of the GUI mode os the posibility to visualize postprocessed data. For this ISInCa was equipped with **JFreeChart** library. It allows to create, modify and safe plots. Look at the example for a polar distribution:

[![N|Solid](https://i.ibb.co/Ykvbk2N/10-10-2020-180630.png)](https://github.com/mauveferret)

## Console
Console mode provides more functionality for postprocessing of calculated files, but needs little bit more computer skills from the user.Console mode is launched by the command  ` java -jar ISInCa.jar -argument `,  where `java` is the call to the JVM, `-jar` is command to execute file *[`ISInCa.jar`](https://github.com/mauveferret/ISInCa/blob/master/out/ISInCa.jar)*, and ` -argument ` is some command line argument. There are several possible arguments:

argument                  | alias             | brief description
:------------------------:|:-----------------:|:------
`-help`                   | `-h`              | prints arguments list with description (this table)
`-version`                | `-v`              | prints JVM version and ISInCa version 
`-config` *some_path*     | `-c` *some_path*  | launch posprocessing of calcs according to second argument - path to config file
`-generate`               | `-g`              | generates config file for launching SCATTER/SDTrimSP calculations (**NOT READY YET**)
`-gui`                    | ---               | launch graphical  mode (also can be launched without any arguments)

In this way, to launch posprocessing of same calculation you should type in the terminal:

` java -jar isinca.jar -c configs/isinca.xml`

where  `configs/isinca.xml` is the path to the config file named *[`isinca.xml`](https://github.com/mauveferret/ISInCa/blob/master/out/isinca.xml)* (by and large, the config file name may be any) with instructions for ISInCa. You can specify the path to config either relatively to executable (without slash in the beginning, like in example) or absolutely. 

### Configuration file 

An template for ISInCa posprocessing can be found *[`here`](https://github.com/mauveferret/ISInCa/blob/master/out/isinca.xml)*. The format of the config is **XML**, which is quite common in use. The first line is a declaration that this is an **XML** and sets the xml version and encoding:
```xml
<?xml version="1.0" encoding="UTF-8"?>
```
Then you have the root tag `<ISInCa>` which shouldn't be changed. You have several tags groups inside of ISInCa. The first one - `<pref>` sets some flags:

flag             |   values           | brief description
:---------------:|:------------------:|:----------
`<getTXT>`       | *true, false*      | sets whether to transform calculation source files in **.txt* format with **UTF-8** encoding
`<getSummary>`   | *true, false*      |sets whether to process **ALL** calculation source files or only those, which are used to generate distributions
`<visualize>`    | *true, false*      |sets whether to show and save plots
`<combine>`     | *true, false*      |sets whether to combine files from one `<calc>` section (look for "Combine mode" further)

The flags need some clarification. Monte-Carlo Codes like Scatter generate outputs which can't be opened with a plain text editors. So if you want to posptprocess this files by yourself, you need to transform the outputs to some readable format. For this `<getTXT>` was made. It doesn't provide any calculation, just transform ouputs of the codes to **txt* file with common **UTF-8** encoding. Then, some codes like SDTrimSP generate several outputs files, which corresponds to different particle sort. In this way, if you only want to calculate energy spectrum of scattered, you don't have to process files with sputtered or displaced particles. So `<getSummary>` helps you to economy time for posprocessing, which can be very usefull for case of huge outputs. But you should understand that in this case no calculation of some constants would be provided, because not all of particles were postprocessed. For our example, you would't get sputter and displace coefficient. So if integral coefficients are important for you - leave the `<getSummary>` to be **true**, else - make it **false**.
```xml
<pref>
    <getTXT>false</getTXT>
    <getSummary>true</getSummary>
    <visualize>false</visualize>
    <combine>true</combine>
    </pref>
```
After `<pref>` section you might have several `<calc>` sections, which corresponds to series of posprocessings. In each of `<calc>` you can specify several **dirs** with calculation outputs inside `<dir>` tag. The path can be set either relatively to *isinca.xml* or absolutely. All this calculations would be posprocessed by ISInCa with the same preferences listed further inside current `<calc>` tag. 
```xml
<calc id="0">
    <dir>calculations/D,T_FeCrNiTi10keV0deg4750k</dir>
    <dir>calculations/D,T_FeCrNiTi20keV0deg500k</dir>
    <dir>calculations/add/D_FeCrNiTi8keV0deg2375k</dir>
    <dir>/home/user/calculations/H,D_FeCrNiTi20keV45deg2,5M</dir>
    ...
</calc>
```
Preferences are mainly related to distributions, integral coefficients are calculated independantly. You can specify needed dependencies by their tags: `<N_E>` - for energy sitribution, `<N_Theta>` - for polat distribution, `<polar_Map>` - for particles motion direction plotting in polar coordinates and etc. (**additional dependencies would be added soon 11.11.20**). There are several tags inside each of dependencies, which allow some customization: you can specify sorts of particles (**B** - for backscattered, **S** - for sputtered, **I** - for implanted, **D** - for displaced, **T** - for passed through the target or any of their combinations). It should be noted, that not all codes support all of this types,  so,  for example, you wouldn't get implanted coefficient for SCATTER calculation as scatter has no flag for implanted particles. Then, you may specify specific angles (by tags `<phi>` and `<theta>`) and distributions steps (by tags `<deltaPhi>`, `<deltaTheta>` and `<delta>` - for energy distribution and for steps in polar map).
```xml
<calc id="0">
    <dir>...</dir>
    <N_E>
        <sort>B</sort>
        <phi>0</phi>
        <deltaPhi>3</deltaPhi>
        <deltaTheta>3</deltaTheta>
    </N_E>
    <N_Theta>
        <sort>S</sort>
        <phi>0</phi>
        <deltaPhi>3</deltaPhi>
        <deltaTheta>3</deltaTheta>
    </N_Theta>
    <polar_Map>
        <sort>BS</sort>
        <delta>3</delta>
    </polar_Map>
</calc>
```
In such manner you can specify parameters for all possible dependencies. All calculations in `<dir>` tags would be postprocessed with this parameters. If you want to change some parameters of some distributions for some specific calculations, you don't hav to make separate config. You can create several `<calc>` groups with different parameters and dirs. You should take into account, that it is not necessarily to specify all parameters for all dependencies. Firstly, ISInCa has default values for all distributions. Secondly, if you'v changed some parameter for some dependency in one `<calc>`, then you can skip this parameter in the next `<calc>` and the parameter's value would be the same as for the previous one. In the same way, if you'll create a `<calc>`:
```xml
<calc>
    <dir>calcs/Cs_Ge</dir>
</calc>
```
it would be processed with dependencies, specified in the previous `<calc>`. One may ask what is th difference between specifying several `<dir>` tags in one calc and several `<calc><dir>...</dir></calc>`. the first case is used also for so called **Combine mode**.

### Combine mode
Imagine a situation: you want to estimate the sputtering of steel by some multicomponent beam/plasma. In this way, you might have different energy/angle distributions for different masses. Or imagine, that you use code, which doesn't support beam aenergy/angle sitributions at all. In such cases you have no choice but to do several calculations with various parameters. But you need to combine them in "one": several sputter distributions for, e.g., one-component beam/plasma transform to sputter distribution for multi-component. So, **Combine mode**  transforms corresponding dependencies for calculations in every `<dir>` into one dependency: all energy distributions - in one energy distributions, all maps - in one map. Output directory for "multi-component" dependencies will be so in which the first `<dir>` lies.

## Generate mode

**not finished yet**

## Direcories structure

**Not finished, to lazy. will end later...** 
