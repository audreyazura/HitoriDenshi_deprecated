# HitoriDenshi

This software aim at simulating single electron in a solar cell absorber with a specific electric field. It uses a SCAPS-1D EB file as input to get the base electric field, to which can be added effective electric fields to represent specific band engineering. These features can be:
- band grading (default at the moment)
- quantum dots (to be added)
- more to come

As of now, only the CIGS is implemented and the material choice at the top has no consequence on the simulation.

# Input files

As of now, the input files have to be named with the norm `E[V]V_N[notch]nm.eb` with "V" the applied bias voltage in Volt and "notch" the notch position in nanometers. Future version may allow more liberties on the naming of the files.

The different configuration fields can be filled automatically by using a properties file. The available fields are:
```properties
abscissa_unit=[nm,μm]
material=[effect not yet implemented, put CIGS by default]
bias_voltages=[may be a list separated by semicolumn, in V]
notch_positions=[may be a list separated by semicolumn, in the same unit as the abscissa]
generation_positions=[may be a list separated by semicolumn, in the same unit as the abscissa]
zero_position=[front,back]
sample_width=[in the same unit as the abscissa]
bufferwindow_width=[in the same unit as the abscissa]
front_bandgap=[in eV]
minimum_bandgap=[in eV]
back_bandgap=[in eV]
simulated_particle=[electron,hole]
effective_mass=[in electron mass]
lifetime=[in ns]
number_of_simulated_particles=
input_folder=
output_folder=
```

An example is given in the file ConfigurationFiles/default.conf

# Depedency

In order to build this software, you will need my [CommonUtils library package](https://github.com/audreyazura/CommonUtils).
