## FTC11587 Engineering Notebook
This code branch contains the electronic documents required to produce the team Engineering Notebook.  The notebook will use the GNU [groff] (https://www.gnu.org/software/groff/) software and supporting libraries to generate the final product.

### Introduction to groff
GNU troff, or groff, is an open-source software tool designed to quickly produce well-formatted, indexed documentation.  It allows the user to focus on content over formatting, leading to rapid, yet efficient development of complex techincal documentation.  groff relies on *macros* to generate output in an appropriate/desired format.  This project will use the groff -ms macro set.

Using additional GNU tools, groff can easily handle complex content such as tables, images, and even engineering equations with ease, making groff a popular choice for documentation of graduate theses, dissertations, engineering manuals and the like.

### Methodology
Using GitHub, updates to the Engineering Notebook can be tracked in real time, with 100% accountability for commits.  The Team Project Manager will have primary responsibility for maintaining the Engineering Notebook, and others may commit changes as directed by the Project Manager.

### Directory and File Structure
The Engineering Notebook branch will utilize the following directory structure to make builds a simple process:
- `/main`:  Contains the .ms file which holds the text content of the Engineering Notebook
- `/main/images`:  Contains any image content used in the Engineering Notebook.  Files placed in this directory should have descriptive file names to identify their content.
- `/main/output`:  This directory should be used to store the output of a groff run.  The preferred output for the Notebook is .pdf format.


