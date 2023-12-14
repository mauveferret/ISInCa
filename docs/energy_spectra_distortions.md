## On the spectra broadening due to the hardware function of the electrostatic analysers

As every scientists knows, depending on the settings of the measuring equipment, 
you can obtain a significantly different signal from the same object under study.
The same goes for computer simulation. 

Let's imagine outputs of the particle-in-matter simulation 
codes as an object under study, and the ISInCa - as the measurement instrument, which allows
to obtain some values, charts and maps, which characterises is some way the outputs of the simulation.
And like any such instrument, ISInCa has a set of parameters that affect the received data.

In this section discussion is provided on the module responsible for 
generating energy spectra. The most common energy analyzers in the area of keV particle's analysis are
electrostatic and magnetic separators with homogeneous field. You can read about it in 
[[1](https://doi.org/10.1016/S1076-5670(09)01606-1)], [[2](https://arc.aiaa.org/doi/10.2514/1.B35413)].
As the most  spectrometers, these analyzers influence the signal. Thus, even if the particle beam is completely
mono-energetic, its spectra would not look like a delta-function, it would have some "energy width" and some shape.
Such spectrum is called a hardware function of the analyzer, and it is unique for every device. 

It is a good  practice to consider the influence of the energy analyzer on the spectrum, in other words, 
to restore "the true" energy spectrum with the measured one and the hardware function.
However, it might be tricky for a number of reasons. The description of the way for reconstruction of energy spectra
is given in [[3](https://doi.org/10.1134/S1063785010050196)]. In some ways this procedure can be simplified 
to the normalization of the spectra on the energy in every point [[4](https://doi.org/10.1016/C2009-0-07296-1)].




 Thus, ISInCa module, which is responsible for generating energy spectra can be imagined as virtual 
 energyanalyzer: an array of channels, each of those is associated with some range of measurable energies. 
 Let's think for the simplicity that the channels are equally distributed, in other words, the distance between 
the centers of the energy channels is constant. This is quite typical for real analyzers. 
 Let's refer to it as to Estep [eV].
So, if the maximum energy of the ions of interest is E0, the number of channels would be E0/Estep.
Meanwhile, the energy width of the channels do not have to be equal.  Let's refer to it as to dE [eV].
We can distinguish several modes of such analyzer.

## ISInCa virtual energy analyzer with dE=Estep

![dE=Estep](https://github.com/mauveferret/ISInCa/blob/master/docs/pics/dE=Estep.png?raw=true)


As can be seen frome the figure above, in this mode our virtual energy analyzer
covers the entire measuring range and the sum of the signal of channels will give the total number
of particles, flying into the analyzer.




### How to configure?

This is a basic mode of ISInCa. All you need in GUI is to specify 
 the `Estep`, while `dE` and/or `dE/E` should be zero (default value).
In the same way, in Console mode you can only specify `<delta>` node.

```xml
<calc id="0">
    <dir>...</dir>
    <N_E>
        <sort>B</sort>
        <phi>0</phi>
        <delta>20</delta>
        <deltaPhi>3</deltaPhi>
        <deltaBeta>3</deltaBeta>
    </N_E>
</calc>
```


## ISInCa virtual energy analyzer with dE=const

![dE=const](https://github.com/mauveferret/ISInCa/blob/master/docs/pics/dE=const.png?raw=true)

This situation, when the Estep and dE are not equal is actually a more general case of the previous mode.
If `dE<Estep`,  not all particles, flying into the analyzer, would be registered. This submode can be named
as the most preferrable from the point of view of obtaining the true particle energy distribution in the experiment.

If `dE>Estep`,the sum of particles in channels would be even more, than the total number of particles, fying
into the virtual analyzer. 

### How to configure?

In this case you need to enable flag dE=const in GUI and enter the value of the `dE` in the corresponding textfield, 
which appears on enabling the flag.

In Console mode you can specify it with `<de>` node.

```xml
<calc id="0">
    <dir>...</dir>
    <N_E>
        <sort>B</sort>
        <phi>0</phi>
        <delta>50</delta>
        <de>20</de>
        <deltaPhi>3</deltaPhi>
        <deltaBeta>3</deltaBeta>
    </N_E>
</calc>
```

## ISInCa virtual energy analyzer with dE/E=const

![dE/E=const](https://github.com/mauveferret/ISInCa/blob/master/docs/pics/dEtoE=const.png?raw=true)

This mode represents the situation, when the width of the channels of the energy analyzer increases linearly with the
energy. This case is the most relevant to the experiment, where electrostatic energy analyzers are utilised. Such mode
causes undesirable distortions in the spectrum. Inter alia it may influence the ratio of peak intensities located at 
sufficiently different  energies and reduce the accuracy of LEIS diagnostics. 

### How to configure?

To enable this regime you need to specify the value of the `dE/E`. The typical values for good electrostatic analysers
are in the range from 0.001 to 0.01. Lower values will correspond to spectra of similar form in compare to the mode
`dE=const & dE<Estep`, the higher will lead to the broadening of the peaks on the spectra.
In console mode you should add `<deltaetoe>` node with the value.

```xml
<calc id="0">
    <dir>...</dir>
    <N_E>
        <sort>B</sort>
        <phi>0</phi>
        <delta>50</delta>
        <deltaetoe>20</deltaetoe>
        <deltaPhi>3</deltaPhi>
        <deltaBeta>3</deltaBeta>
    </N_E>
</calc>
```


## What mode should I choose?

The answer depends on your purposes for providing the simulation. In case of exploring the "nature", embedded in the 
simulation, the  modes  `dE=Estep` or `dE=const & dE<Estep` are acceptable. 
If you want to explore your facility with it real energy analyser, you can try to use modes `dE=const & dE>Estep` or
`dE/E=const`



```