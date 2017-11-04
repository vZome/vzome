# Running in Eclipse

With Eclipse Oxygen (4.7.1a), it is no longer necessary to generate the Eclipse project files, since the Gradle plugin is built-in.  The core and desktop projects imported without a hitch, building on the first try.

However, trying to run the desktop app launch configuration threw an exception:

	java.lang.NoClassDefFoundError: apple/awt/CGraphicsDevice
	
A quick Google search for that string revealed this [Jogamp forum thread](http://forum.jogamp.org/Crash-with-Java-7-and-Macos-10-9-1-td4031722.html]), which proved to be correct.
After removing the Java3d and JAI libraries from `/System/Library/Java/Extensions`, it worked fine.

My guess is that the Eclipse installer re-introduced these extensions, since I have set them aside many times.



