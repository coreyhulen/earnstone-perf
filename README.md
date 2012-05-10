
# Earnstone Java Performance Counters 

### Description
There doesn't seem to be a whole lot of open source options 
for Java performance counters.  Since we found it frustrating and 
rolled our own we decided to share our work so others could just ditto it. 
The over-arching principal is Simplicity or more importantly KISS. 
We wanted something fast, simple, easy to use, and thread-safe 
(did we mention fast and simple).  Easy access from JMX or the 
JMX-HTML adapter if required.  we opted for a simple under-engineered 
design.

*   **IncrementCounter** - A performance counter that counts. You may 
    use simple atomic operations to increment and decrement the count value.
*   **PercentCounter** - A performance counter that calculates percent. 
    Great for hit cache performance counters.
*   **LastAccessTimeCounter** - A performance counter for displaying last 
     access time.  Great for counters like last access time or up time.
*   **AvgCounter** - A performance counter for averaging over a sample. 
    This is usually a base class for other more detailed counters, 
    but it could be used to average anything.
*   **AvgTimeCounter** - A performance counter for averaging over a time 
    sample.  Great for things like average time in a method call or 
    average time to process items.
*   **PerfAvgCallsPerSec** - A performance counter for calls per second 
    over a time sample. Makes calculating things like transactions per second 
    or transactions per hour a breeze.
*   **CallbackCounter** - A performance counter which calls a supplied 
    update method before returning the value.  Great for integrating existing
    counters into a unified system.
*   **Registry** - A central repository for storing your performance 
    counters.  The registry is optional, but will be needed to access features
    like exposing counters through JMX or via the JMX-HTML adapter.

### Building ePerf
Building ePerf can be a little tricky because of the jmxtools.jar 
dependency from Sun does not exist in a maven repository because of
licensing issues.  Below are steps needed to manually install this jar
into your local repository.

if you look inside the [jmxtools-1.2.1.pom](http://repo2.maven.org/maven2/com/sun/jdmk/jmxtools/1.2.1/jmxtools-1.2.1.pom) 
it contains a link to the jmxtools.jar [download site](http://java.sun.com/products/JavaManagement/download.html). 
You will need to scan through the page and find the hidden download 
link to the file jmx-1_2_1-ri.zip. Extract the contents and cd into the 
lib directory and run the following command:

`mvn install:install-file -Dfile=jmxtools.jar -DgroupId=com.sun.jdmk -DartifactId=jmxtools -Dversion=1.2.1 -Dpackaging=jar`

Your project should be good to go.  Now you can perform local builds 
using the `mvn` commands.


