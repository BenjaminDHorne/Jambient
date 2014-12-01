Jambient
========

A Java Library for Programming with Mobile Ambients

Distributed systems are essential for today’s computing infrastructures. Due to this, distribution and mobility are prevalent necessities for modern applications; yet, many computer science students are not exposed to programming with distribution or mobility until late in their undergraduate degree, if at all, creating a lack of needed skills in computer science graduates. This lack of skill is partially due to computer science curriculum placing related courses as electives or not being able to offer them at all. Fortunately, curriculum is beginning to make a shift towards the inclusion of distributed computing in the requirements. Despite the movement towards updated curriculum, distributed programming skills are still lacking simply due to the difficulty of teaching distributed programming. Many languages have thread support, but typically have no native support for distribution or code mobility. Thus, these languages can be useful for teaching some parallel processing, but not distributing code to different machines over the internet. Therefore, we need practical methods to teach distribution theories early on in computer science students careers. One hypothesis for accomplishing this task is to make theory based distribution and mobility available in a language sophomore undergraduate students are already comfortable with. This work builds a Java library based on the Ambient Calculus to do just that.

Jambient is a Java library built to program using the Ambient Calculus. The goal of this library is to follow the Ambient Calculus semantics and syntax as closely as possible, while maintaining the Java behaviors that students are familiar with. Ideally, the Ambient Calculus would be taught to students prior to using Jambient, and then Jambient would be used to solidify the theories taught and provide mobile and distribution implementation skills. 

Jambient Beta Version is built with the base Ambient Calculus structure and with the following mobile ambient features:
•	Subjective and objective migration capabilities
•	Objective move securities specified in Cardelli and Gordon's work
•	Code mobility across a network
•	Simple Blocking for incorrect moves
•	Lock example keywords specified in Cardelli and Gordon's work

Users can begin using Jambient to implement ambient calculus models very simply. Once the library is imported, users can create new ambient objects and processes. Ambient objects are fairly straightforward, they are simply ambients with all of the capabilities from the ambient calculus with some additional code mobility methods and construction methods. The methods available to users include:
•	make - properly constructs the ambients references to other ambients. This method must be called for every new ambient object created.
•	in - moves ambient into parameterized ambient
•	out - moves ambient out of parameterized ambient
•	open - opens up ambient just as the ambient calculus open capability
•	migrate_runall - takes string IP address, moves ambient and all enclosed objects to remote machine, there it will run all processes enclosed in the ambient. The remote machine must run Mobility-RPC's standalone or  embedded server.
•	migrate_runone - takes string IP address and string name of a process object, moves ambient and all enclosed objects to remote machine, there it will run one specified process enclosed in the ambient. The remote machine must run Mobility-RPC's standalone or  embedded server.
•	setObjectiveMoveSecurity - sets if processes can enter or exit an ambient
•	acquire - keyword for lock example from Cardelli and Gordon's work
•	release - keyword for lock example from Cardelli and Gordon's work
•	isasibling - checks if ambient is a sibling of parameterized ambient
•	isachild - checks if ambient is a child of parameterized ambient


The Jambient process library must be extended in order to create proper processes. When extended, users will overwrite the runProcess method; this  method will have the code for the process. The process class has the follow method available to users:
•	make - properly constructs the process references to ambients. This method must be called for every new ambient object created.
•	mvin - moves process into parameterized ambient, if allowed
•	mvout - moves process out of parameterized ambient, if allowed
•	runProcess - abstract method to be overwritten
 Figure 3: Extending Jambient Process Class
 Figure 4: Instance of extended process
 
Jambient Beta Version does not currently have true concurrency built in; however, a user using the library can program their own threads using the Java threads library. There are several reasons for true concurrency to not be implemented in this version of Jambient. First, the combination of threads and mobility provided some unexpected challenges. The mobility is dependent on another Java library called Mobility-RPC. When initially building Jambient there were issues with serializing an object that had threads encapsulated inside; however, after talking with the authors of that library these issues are now fixed and ready to be implemented inside Jambient. The problem was found in how Jambient was using the library, and not the library itself. Second, with the concurrent mobility issue fixed and ready to be implemented, there are significant challenges in creating a concurrent responsive user program. In other words, it is difficult to have threading inside our library that can utilize the code written by the user without the user doing all of the threading implementation. Ideally, we want the user to not deal with the threading directly to focus on the ambient calculus. Due to this challenge, we currently do not have features like concurrent blocking, in which if an ambient attempts to move into another ambient that is not it's sibling, it will wait until that sibling is rather than simple notify the user that ambient has been blocked from moving. Another feature that still needs to be implemented is local communication restrictions. In the Ambient Calculus, communication between processes can only happen at the local ambient level. Currently, this restriction is left up to the user, which is not ideal. 
