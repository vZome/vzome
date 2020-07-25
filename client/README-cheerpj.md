# CheerpJ POC

This project is an attempt to provide a browser-native implementation of vZome without abandoning all the legacy Java code, and without requiring a running server as in the client/server approach used until now.  There are two possible approaches for a pure web client: Java-to-Javascript source transpiling, and Java-to-WebAssembly compilation.  This project uses the latter approach, relying on [CheerpJ](https://www.leaningtech.com/pages/cheerpj.html).

I had previously tried TeaVM, and even an earlier student project.  There were simply too many hurdles for those attempts, the performance was not acceptable, and I believe there were still important gaps in functionality.  CheerpJ seems to address all those shortcomings; the technology finally seems mature enough to use.  The tooling is excellent, the documentation is fair to good, and the overall experience has been pretty rewarding in a short amount of time.

## Status

As of now, the Cheerp test page is able to parse a local file upload.

I'm using a file input and `FileReader` to select and "upload" a local
vZome file.  I then pass the file in to Java using `cheerpjAddStringFile`,
then pass the `/str/...` path in for the `ApplicationController.main()` to
load.

The only real error I encountered in the Java code was the use of `Thread
.getContextClassLoader()` in a number of places.  I know I've gone back
and forth on this over the years, but my Googling today seems to clearly
indicate that I should *always* use `getClass().getClassLoader()`.  I've
made those changes; it may cause trouble with Oculus or perhaps the server
for client/server, but if so, I'll deal with it by passing a `ClassLoader`
where necessary.

## Building the Test App

The `cheerpj.bash` script offers a complete, start-from-scratch solution to compiling all the necessary JARs, placing everything under `public` ready to use.  The script is overkill for ongoing development, since it recompiles all JARs from scratch, even the third-party dependencies.

Once you have run `cheerpj.bash `, you can do `npm start`.  In fact, they are independent, and you should run them in separate shells, since the development server will force a page reload whenever the static files under `public` have changed.

If you have to change the Java code, you can perform these steps, analogous to parts of `cheerpj.bash`:
	
	pushd ..
	./gradlew distZip

	cp core/build/libs/core-7.0.jar client/public/
	( cd client/public; /Applications/cheerpj/cheerpjfy.py --deps jackson-core-2.9.5.jar:jackson-annotations-2.9.3.jar:jackson-databind-2.9.5.jar:javax.json-1.0.4.jar:vecmath-1.6.0-final.jar core-7.0.jar )

	cp desktop/build/libs/desktop-7.0.jar client/public/
	( cd client/public; /Applications/cheerpj/cheerpjfy.py --deps jackson-core-2.9.5.jar:jackson-annotations-2.9.3.jar:jackson-databind-2.9.5.jar:javax.json-1.0.4.jar:vecmath-1.6.0-final.jar:core-7.0.jar  desktop-7.0.jar )
	
	popd

Of course, you can optimize and skip one JAR or another, if you know exactly what you changed, but the risk of missing your code change is not worth the time saved.

