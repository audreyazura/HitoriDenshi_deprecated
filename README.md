# HitoriDenshi

This software aim at simulating single electron in a solar cell absorber with a specific electric field. It uses a SCAPS-1D EB file as input to get the base electric field, to which can be added effective electric fields to represent specific band engineering. These features can be:
- band grading (default at the moment)
- quantum dots (to be added)
- more to come

As of now, only the CIGS is implemented and the material choice at the top has no consequence on the simulation.

# Input files

As of now, the input files have to be named with the norm
* E[V]V_N[notch]nm.be
with "V" the applied bias voltage in Volt and "notch" the notch position in nanometers. Future version may allow more liberties on the naming of the files.

The different configuration fields can be filled automatically by using a properties file. Such properties file have to contain the following fields:
* abscissa\ unit=[nm,μm]
* material=[effect not yet implemented, put CIGS by default]
* bias\ voltages=[may be a list separated by semicolumn, in V]
* notch\ positions=[may be a list separated by semicolumn, in the same unit as the abscissa]
* generation\ positions=[may be a list separated by semicolumn, in the same unit as the abscissa]
* zero position=[front,back]
* sample\ width=[in the same unit as the abscissa]
* buffer+window\ width=[in the same unit as the abscissa]
* front\ bandgap=[in eV]
* minimum\ bandgap=[in eV]
* back\ bandgap=[in eV]
* simulated\ particle=[electron,hole]
* effective\ mass=[in electron mass]
* lifetime=[in ns]
* numbre\ of\ simulated\ particles=
* input\ folder=
* output\ folder=
An example is given in the file ConfigurationFiles/default.conf

# Depedency

In order to build this software, you will need my [CommonUtils library package](https://github.com/audreyazura/CommonUtils).
