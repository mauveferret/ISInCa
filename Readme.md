# ISInCa - Ion Surface Interaction Calculator

ISInCA - is **Java** tool dedicated to postprocess outputs of
*[`SCATTER`](https://www.sciencedirect.com/science/article/pii/S0042207X00001366)*, *[`TRIM`](http://www.srim.org/)*, *[`SDTrimSP`](https://pure.mpg.de/rest/items/item_3026474_2/component/file_3026477/content)* Monte-Carlo particle-surface interaction codes. 
It is planned to gradually expand the list of supported codes (like TRYDIN, MARLOWE and so on).

![header](https://github.com/mauveferret/ISInCa/blob/master/docs/pics/header.png?raw=true)
 
# Key features

 The codes listed above are intended for the simulation of  particle beams interaction with solids. 
 In such codes you can set some beam parameters (like mass/angle/energy distributions, doze) and 
 multiple target parameters. Such codes usually can generate single or several files, containing tables 
 with backscattered (B), sputtered (S), transmitted (T), implanted (I) and displaced (D) particles with 
 data on their position, motion direction, sort, energy, pathlength etc. **ISInCa** allows to transform 
 this huge (up to hundreds of GB) data files to easy for interpretation data. it can generate:

### **Graphs:**
 - Energy distributions N(E) for any solid angle and any energy step 
**(for backscattered and transmitted primary particles and sputtered particles of target)**
 - Angle distributions dN/dβ(β) **(backscattered and transmitted primary particles and sputtered particles of target)**
 - ~~Depth distributions for primary implanted and target displaced particles~~ `under development`
 
### **Maps:**
 - Polar Map N(β, φ) for backscattered and transmitted primary particles and sputtered particles of target
 - EneAng Maps N(E, β) for backscattered and transmitted primary particles and sputtered particles of target
 - Cartesian Maps N(Z, Y), N(Z, X)

### **Integral coefficients:**
   - Scattered coefficient (number of scattered divided by number of incident)
   - Sputtered coefficient (number of sputtered divided by number of incident)
   - Implanted coefficient (number of implanted divided by number of incident)
   - Transmitted coefficient (number of transmitted divided by number of incident)
c

All distributions can be both visualised with the embedded `JFreeChart` plotter and saved locally as text file. All data is available for different combinations of particles (scattered - B, sputtered - S, implanted - I ,
transmitted - T  and displaced - D). 
You should take into account, that the number of available types of particles
depends on the specific code. As example, `SDTrimSP` supports all types, `Scatter` only B,S and I.
Since version *[`2020.4.0`](https://github.com/mauveferret/ISInCa/commit/d3d1506027f252289089755e8020599890d4b4ca)* ISInCa can calculate data separately for every chemical element from incident beam or a target. 
An opportunity for combining results from several calculations was also added.

You can read more about features in the corresponding docs:

 - **[More on the available distributions](https://github.com/mauveferret/ISInCa/tree/master/docs/distributions.md)**
 -  **[On the energy analyser distortions](https://github.com/mauveferret/ISInCa/tree/master/docs/spectra_distortions.md)**
 -  **[Postprocessing multiple simulations at once](https://github.com/mauveferret/ISInCa/tree/master/docs/console_mode.md)**
 -  **[Combining multiple simulation's results](https://github.com/mauveferret/ISInCa/tree/master/docs/simulations_combination.md)**

# Installation
ISInCa is fully `JAVA`  program, so it can be launched in any OS on every processor architecture with a single executable.  
Executable file for the latest version is located in **[/out](https://github.com/mauveferret/ISInCa/tree/master/out)** directory:
- For "easy mode" in Windows OS you can download an executable file **[ISInCa.exe](https://github.com/mauveferret/ISInCa/blob/master/out/ISInCa.exe)** (works only in GUI mode  with limited capabilities)
- For all OS you can use **[isinca.jar](https://github.com/mauveferret/ISInCa/blob/master/out/ISInCa.jar)**, which provides full functionality of ISInCa
- For console mode you might also need a template for config file: **[isinca.xml](https://github.com/mauveferret/ISInCa/blob/master/out/isinca.xml)**, which allows to postprocess multiple simulations simultaneously.

In any case you need to have a **Java Virtual Machine** (version **11** or newer) installed on your computer to run `JAVA` programs.
To check whether the JVM was already installed, open the terminal (in Windows OS press "Win"+"R", then type "cmd" in an appeared window, then press enter) and type `java -version`, then press "enter". If the output looks like 
> Java is not recognized as an internal or external command

or the version is less than **11**, you need to download/upgrade JVM. For Windows OS you can  download it for free  from the [Oracle](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) (the company, that supports Java). 
Installation of the JDK (which includes JVM + libraries) seems not to be tricky (see the *[`tutorial`](https://docs.oracle.com/javase/9/install/installation-jdk-and-jre-microsoft-windows-platforms.htm#JSJIG-GUID-2B9D2A17-176B-4BC8-AE2D-FD884161C958)*). 
In Linux, you can get it just by typing `sudo apt install default-jdk` in the shell. 
ISInCa executable does not need special installation, so you can run ISInCa right after downloading and installing JVM.

# Launch

For your convenience **CONSOLE** and **GUI** modes were made in ISInCA. GUI is based on the ***[`JavaFX`](https://openjfx.io/)*** package, which is included in the executable (as from the 11-th version of Java it was removed from the JDK)

## GUI

GUI is the default mode so there is no need in any launch arguments, so just run it... Main window of the GUI mode looks like:

![gui](https://github.com/mauveferret/ISInCa/blob/master/docs/pics/gui_main.png?raw=true)

The interface seems to be user-friendly and contains tooltips with recommendations for the most controlling elements.

## Calculation procedure

1. Press "Choose directory button". Select the directory with the files, generated by your Monte-Carlo program. In case of `Scatter` it is most likely `/out`  folder inside SCATTER folder. It must contain 
`SC*****.dat` file. In case of`SDTrimSP` the folder must contain `tri.inp` and `partic_***.dat` files. 
2. ISInCa  will identify the code type automatically in `code type` window in a bottom left section as well as the initial energy and the number of particles.
3. Choose data, that you wish to get in bottom center section. As can be seen, you can choose several types of particles (B,S,I,T,D) 
for several types of charts (energy spectrum, polar chart, polar map,  map with E and β axes and 2 cartesian maps).
4. Note that not all types of particles are available for some codes and charts. Also you can choose whether to visualise data after calculation. Anyway, the calculated charts will be saved in `/ISInCa` directory inside the directory you've chosen previously. In case of `SCATTER` you can also convert binary `SC*****.dat` file to user-frienldy `*.txt` file with UTF-8 encoding readable by any text editor.
5. You can specify parameters of charts in corresponding tabs. AS example, for *dN/dE(E)* you can choose the upper energy limit, amount of dots on chart (deltaE, eV), and the solid registration angle.
6. Finally, press `start calculation` button and just wait  the charts to appear.

In case of troubles check `Help` tab. You also can find there the clarification on the designation of angles.
The main feature of the GUI mode os the possibility to visualize postprocessed data. 
For this ISInCa was equipped with **JFreeChart** library. 
It allows to create, modify and safe plots. Look at the examples for energy and polar distributions:
![Energy chart](https://github.com/mauveferret/ISInCa/blob/master/docs/pics/energy_spectrum3.png?raw=true)

![Polar chart](https://github.com/mauveferret/ISInCa/blob/master/docs/pics/polar_spectrum.png?raw=true)


## Structure of directories

After initiation of the ISInCa calculation it will analyse all the data in the simulation directory.
After the post-pprocessing the calculated data will be saved in 

>./ISInCa/"SIM_TYPE"_"CALC_ID"/

where "SIM_TYPE" - is the name of the simulation code, "CALC_ID" - unique calc ID, given for each ISInCa launch
for the specific folder. The `"SIM_TYPE"_"CALC_ID"` also would be the start for all internal file names.

So if you launched ISInCa for the same directory several times, the outputs won't be mixed up, as they would
be located in directories with different CALC_ID and different prefixes of files.
Inside the calc directory you can find `"SIM_TYPE"_"CALC_ID"_summary.txt` file with  integral coefficients
and several directories for different distributions:

`./ISInCa/"SIM_TYPE"_"CALC_ID"/ENERGY/` for energy spectra

`./ISInCa/"SIM_TYPE"_"CALC_ID"/POLAR/` for polar distributions

`./ISInCa/"SIM_TYPE"_"CALC_ID"/ANGLEMAP/` for Polar Map N(β, φ)

`./ISInCa/"SIM_TYPE"_"CALC_ID"/ENEANGMAP/` for EneAng Maps N(E, β)

Every distribution's directory contain distributions for different sorts of particles (B, S, T, I, D),
depending on what was chosen in ISInCa during the configuration.
You can also find there the sum of all chosen sorts ("all"). For some a `.png` figure are also available.

The file names start with the calculation ID in the form "SIM_TYPE"_"CALC_ID" and contain
some parameters (as sort of particles, registration angle, step etc.).
All calculated data files are in `.txt` format. Each file contain a header with detailed information.

Fun fact for attentive readers: The name of this tool is actually a reference to the artificial 
intelligence from the russian literature project "Ethnogenesis".
