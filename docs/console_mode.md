# Console mode in ISInCa


This mode provides more functionality for postprocessing of calculated files, 
but needs little bit more computer skills from the user. 
Console mode is launched by the command  
>java -jar ISInCa.jar -argument

where 

`java` is the call to the JVM, 

`-jar` is command to execute file *[`ISInCa.jar`](https://github.com/mauveferret/ISInCa/blob/master/out/ISInCa.jar)*,

` -argument ` is some command line argument. There are several possible arguments:

argument                  | alias             | brief description
:------------------------:|:-----------------:|:------
`-help`                   | `-h`              | prints arguments list with description (this table)
`-version`                | `-v`              | prints JVM version and ISInCa version 
`-config` *some_path*     | `-c` *some_path*  | launch posprocessing of calcs according to second argument - path to config file
`-generate`               | `-g`              | generates config file for launching SCATTER/SDTrimSP calculations (**NOT READY YET**)
`-gui`                    | ---               | launch graphical  mode (also can be launched without any arguments)

In this way, to launch posprocessing of same calculation you should type in the terminal:

` java -jar isinca.jar -c configs/isinca.xml`

where  `configs/isinca.xml` is the path to the config file named *[`isinca.xml`](https://github.com/mauveferret/ISInCa/blob/master/out/isinca.xml)* 
(the config file name may be any) with instructions for ISInCa. 
You can specify the path to config either relatively to executable (without slash in the beginning, like in example) or absolutely.

### Configuration file

An template for ISInCa posprocessing can be found *[`here`](https://github.com/mauveferret/ISInCa/blob/master/out/isinca.xml)*. The format of the config is **XML**, which is quite common in use. The first line is a declaration that this is an **XML** and sets the xml version and encoding:
```xml
<?xml version="1.0" encoding="UTF-8"?>
```
Then you have the root tag `<ISInCa>` which shouldn't be changed. You have several tags groups inside of ISInCa. The first one - `<pref>` sets some flags:

flag             |   values             | brief description
:---------------:|:--------------------:|:----------
`<getTXT>`       | *true, false*        | sets whether to transform calculation source files in **.txt* format with **UTF-8** encoding
`<getSummary>`   | *true, false*        |sets whether to process **ALL** calculation source files or only those, which are used to generate distributions
`<visualize>`    | *true, false*        |sets whether to show and save plots
`<combine>`      | *true, false*        |sets whether to combine files from one `<calc>` section (look for "Combine mode" further)
`<dirSubname>`   | *angle, energy, ...* | adds an incident angle/energy... of calcs. to combine's mode dir
`<combSum>`      | *W,Au,Fe,Cl,U,...*   | list of elements for **combSum** mode: summarizes combines
The flags need some clarification. Monte-Carlo Codes like Scatter generate outputs which can't be opened with a plain
text editors. So if you want to post-process this files by yourself, 
you need to transform the outputs to some readable format. For this `<getTXT>` was made.
It doesn't provide any calculation, just transform outputs of the codes to **txt* file with common **UTF-8** encoding.
This is useful for `SCATTER` code, which saves data in not readable binary format.
Then, some codes like SDTrimSP generate several outputs files, which corresponds to different particle sort. 
In this way, if you only want to calculate energy spectrum of scattered, you don't have to process files with
sputtered or displaced particles. So `<getSummary>` helps you to economy time for posprocessing, 
which can be very usefull for case of huge outputs. 
But you should understand that in this case no calculation of some constants would be provided,
because not all particles would be postprocessed. For our example, you would not get sputter and displace coefficient. 
So if integral coefficients are important for you - leave the `<getSummary>` to be **true**, 
else - make it **false**.

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
Preferences are mainly related to distributions, integral coefficients are calculated independently. You can specify needed dependencies by their tags: `<N_E>` - for energy sitribution, `<N_Beta>` - for polat distribution, `<polar_Map>` - for particles motion direction plotting in polar coordinates and etc. (**additional dependencies would be added soon 11.11.20**). There are several tags inside each of dependencies, which allow some customization: you can specify sorts of particles (**B** - for backscattered, **S** - for sputtered, **I** - for implanted, **D** - for displaced, **T** - for passed through the target or any of their combinations). It should be noted, that not all codes support all of this types,  so,  for example, you wouldn't get implanted coefficient for SCATTER calculation as scatter has no flag for implanted particles. Then, you may specify specific angles (by tags `<phi>` and `<beta>`) and distributions steps (by tags `<deltaPhi>`, `<deltaBeta>` and `<delta>` - for energy distribution and for steps in polar map).
```xml
<calc id="0">
    <dir>...</dir>
    <N_E>
        <sort>B</sort>
        <phi>0</phi>
        <deltaPhi>3</deltaPhi>
        <deltaBeta>3</deltaBeta>
    </N_E>
    <N_Beta>
        <sort>S</sort>
        <phi>0</phi>
        <deltaPhi>3</deltaPhi>
        <deltaBeta>3</deltaBeta>
    </N_Beta>
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
