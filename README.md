![Galleon Pack Template Java CI](https://github.com/wildfly/wildfly-galleon-pack-template/workflows/Galleon%20Pack%20Template%20Java%20CI/badge.svg)

# Wildfly Galleon Feature Pack Template
A template Galleon feature pack to provision a new subsystem into WildFly using Galleon. 
It is runnable as-is, and provides a very basic subsystem which supplies a CDI `@Produces` method. 
Instances from this `@Produces` method are available to your deployments when your subsystem is 
installed in the server.

We will start off by showing you how to run the example, and dig more into the details of how this
feature pack is structured so that you can adapt it to provide your functionality as a Galleon
feature pack.


## Building the Galleon feature pack

To build the Galleon feature pack, simply clone this repository, and on your command line go to the
checkout folder and run
```
mvn install
```
and it will build everything, and run the testsuite. An example patched server
will be created in the `build/target/` directory. We will explore how to use
Galleon CLI to provision a server from the command line later on.

## Running the example application

The example application lives in the 
[`example/`](https://github.com/wildfly/wildfly-galleon-pack-template/tree/master/example)
directory. It is a trivial application exposing a 
[REST endpoint](https://github.com/wildfly/wildfly-galleon-pack-template/blob/master/example/src/main/java/org/wildfly/extension/galleon/pack/template/example/JaxRsResource.java) 
which is injected with an instance of the bean we have a `@Produces` for in the subsystem's dependency 
[`dependency/`](https://github.com/wildfly/wildfly-galleon-pack-template/tree/master/dependency)
folder. 
The `@ExampleQualifier` Qualifier is also defined in the `dependency/` folder. 

Start the server by running
```
./build/target/wildfly-<WildFly Version>-template-<Template Version>/bin/standalone.sh
```
from the `build/` directory.
 
In another terminal window run:
```
mvn package wildfly:deploy -pl example
```
and see the application gets deployed.

Then go to http://localhost:8080/example/greeting and see the hello message. It just says 'Welcome' in a different
language each time you refresh the page.

## Project structure

The [root POM](https://github.com/wildfly/wildfly-galleon-pack-template/blob/master/pom.xml) contains the
WildFly versions we wrote this template Galleon feature pack for at the top of the `<properties>` section. 
However, newer versions of WildFly are generally backwards compatible (unless you really dug into some server
internals) so you can install it into a later version of WildFly. 

It adds an additional layer called `template-layer`, which contains a new
subsystem called `template-subsystem`. This subsystem is implemented in the 
`org.wildfly.extension.template-subsystem` module which in turn has a dependency
on the `org.wildfly.template-dependency` module. The latter module basically
makes a CDI `@Produces` available. The code for these modules are in the
[`subsystem/`](https://github.com/wildfly/wildfly-galleon-pack-template/tree/master/subsystem)
and [`dependency/`](https://github.com/wildfly/wildfly-galleon-pack-template/tree/master/dependency)
Maven sub-modules, and the subsystem's 
[`DependencyProcessor`](https://github.com/wildfly/wildfly-galleon-pack-template/blob/master/subsystem/src/main/java/org/wildfly/extension/galleon/pack/template/subsystem/deployment/DependencyProcessor.java)
makes the `org.wildfly.template-dependency` module available to the deployment. 

The [`galleon-pack/`](https://github.com/wildfly/wildfly-galleon-pack-template/tree/master/galleon-pack)
Maven sub-module defines the Galleon feature pack. 
It defines the JBoss modules for the subsystem and dependency under the [`galleon-pack/src/main/resources/modules/system/layers/base`](https://github.com/wildfly/wildfly-galleon-pack-template/tree/master/galleon-pack/src/main/resources/modules/system/layers/base)
directory. Both of these modules are brought in by the layer implemented by this Galleon Feature Pack.

The layer is defined in [`galleon-pack/src/main/resources/layers/standalone/template-layer/layer-spec.xml`](https://github.com/wildfly/wildfly-galleon-pack-template/blob/master/galleon-pack/src/main/resources/layers/standalone/template-layer/layer-spec.xml).
As we are making a CDI `@Produces` method available we need CDI, so our layer has a dependency on
the `cdi` layer.

Our layer also has a dependency on the `template-subsystem` feature group which is defined in
[`galleon-pack/src/main/resources/feature_groups/template-subsystem.xml`](https://github.com/wildfly/wildfly-galleon-pack-template/blob/master/galleon-pack/src/main/resources/feature_groups/template-subsystem.xml)
which contains our `<feature spec="subsystem.template-subsystem"/>`.
Note that the 'spec' value is of the format:
```
subsystem.<subsystem-name>
```  
In our case the name of the subsystem is `template-subsystem`, so the full name is `subsystem.template-subsystem`. 
When provisioning the server this results in an operation to add the subsystem:
```
/subsystem=template-subsystem:add()
``` 
Our subsystem is empty, as it has no attributes or child resources. 

As an example, __if__ the subsystem had an attribute called
`test` which should be set to `10`, we would write its feature spec as 
```
<feature spec="subsystem.template-subsystem">
    <param name="test" value="10"/>
</feature
```
which would result in the following __not working__ (since our subsystem has no attributes) 
subsystem add operation:
```
/subsystem=template-subsystem:add(test=10)
``` 




[`galleon-pack/wildfly-feature-pack-build.xml`](https://github.com/wildfly/wildfly-galleon-pack-template/blob/master/galleon-pack/wildfly-feature-pack-build.xml)
takes care of adding the subsystem to the configuration under `<extensions>` near the end of the file
where we add a dependency on the `org.wildfly.extension.template-subsystem` module we have defined for our subsystem.
Galleon is smart enough to look at the dependencies of this module to bring in modules it in turn depends on. 
So in this case it will e.g. bring in the `org.wildfly.template-dependency` module (or 'package' in Galleon terminology)
too. 

It also configures the feature packs that we depend upon. Note
that we have a direct dependency on `org.wildfly:wildfly-galleon-pack`. This in turn has dependencies on 
`org.wildfly:wildfly-servlet-galleon-pack` and `org.wildfly.core:wildfly-core-galleon-pack` which is why the first 
is listed under `dependencies` and the others under `transitive`. For each feature pack we can configure further
what we want to include. 

[`galleon-pack/pom.xml`](https://github.com/wildfly/wildfly-galleon-pack-template/blob/master/galleon-pack/pom.xml)
has the `wildfly-galleon-maven-plugin` which creates the Galleon Feature Pack. Note that it uses
the `build-feature-pack` goal which is needed to add a new subsystem along with the mentioned
entry in `wildfly-feature-pack-build.xml`.

__Note regarding non-configurable feature additions:__
_If you just want to make some additional modules 
available in the server you would use the `build-user-feature-pack` instead, and not use a `wildfly-feature-pack-build.xml`. 
[`wildfly-extras/wildfly-datasources-galleon-pack/`](https://github.com/wildfly-extras/wildfly-datasources-galleon-pack)
contains an example of this simpler scenario. To bring in the module containing our functionality in this case you
need to add it to the `packages` section of the `layer-spec.xml` instead. The name of the package in this case
is the name of your module (i.e. `org.wildfly.extension.template-subsystem`). As before the `org.wildfly.template-dependency`
will also be brought in since it is a dependency in the subsystem's `module.xml`._

If adding licenses is important to you, they are set up in the following places:
* [`galleon-pack/src/license/template-feature-pack-licenses.xml`](https://github.com/wildfly/wildfly-galleon-pack-template/blob/master/galleon-pack/src/license/template-feature-pack-licenses.xml) - 
This file lists the dependencies that are installed by the feature pack, and lists the
license for each.
* [`galleon-pack/src/main/resources/content/docs/licenses/`](https://github.com/wildfly/wildfly-galleon-pack-template/tree/master/galleon-pack/src/main/resources/content/docs/licenses) -
For each license we use, we have a .txt file whose name is the license name in lower case.
   
The [`build/pom.xml`](https://github.com/wildfly/wildfly-galleon-pack-template/blob/master/build/pom.xml)
file uses the `provision` goal of the `galleon-maven-plugin` to provision a server. It essentially
lists our feature pack and its direct dependency `org.wildfly:wildfly-galleon-pack`.

In the `config` section of the plugin we select the layers we want to use. In this case
we are selecting enough functionality for our sample, by selecting the following layers
* `jaxrs` - this in turn depends on the `web-server` layer.
* `management` - this installs the management interfaces, so that the plugin triggered by
`mvn wildfly:deploy` can deploy the sample application via the management interfaces.
* `template-layer` - our custom layer which in turn pulls in `cdi`

The [`testsuite/`](https://github.com/wildfly/wildfly-galleon-pack-template/tree/master/testsuite) 
folder is the root of a hierarchy of Maven modules to run the testsuite against a patched
WildFly server. This root module takes some of the `plumbing` from the main
WildFly testsuite.

It's child module [`testsuite/integration/`](https://github.com/wildfly/wildfly-galleon-pack-template/tree/master/testsuite/integration)
sets up some of the common dependencies for all tests, and allows you to have one or more
child modules.

In this case we have one child module: [`testsuite/integration/subsystem/`](https://github.com/wildfly/wildfly-galleon-pack-template/tree/master/testsuite/integration/subsystem).
Its [`pom`](https://github.com/wildfly/wildfly-galleon-pack-template/blob/master/testsuite/integration/subsystem/pom.xml)
uses the the `provision` goal of the `galleon-maven-plugin` to provision a server as above, although in
this case we are not using the `jaxrs` layer since the test does not use JAX-RS. Instead we use the following
layers:
* `web-server` - Installs the undertow subsystem and its dependencies
* `jmx-remoting` - Needed by Arquillian 
* `template-layer` - our custom layer which in turn pulls in `cdi`

### Adding more dependencies
If you want your layer to bring in more content, which is not a module, you would add it under the 
['galleon-pack/src/main/resources/packages'](https://github.com/wildfly/wildfly-galleon-pack-template/tree/master/galleon-pack/src/main/resources/packages)
directory. The name of the directory is what becomes the package name. You then reference the name of
that package from the `<packages>` section of your `layer-spec.xml`.

For modules, it depends on if you are adding a feature or not.

As we have seen a feature in this context is a WildFly Extension which provides a subsystem which in turn has configuration.
To provision these we use the `build-feature-pack` goal of the plugin which looks for the `wildfly-feature-pack-build.xml`
where the extension module is added.  Modules depended on by the extension module 
are pulled in too. If you have some additional modules to add, you can add those in the
`registerAdditionalRuntimePackages()` method of ['TemplateSubsystemDefinition'](https://github.com/wildfly/wildfly-galleon-pack-template/blob/master/subsystem/src/main/java/org/wildfly/extension/galleon/pack/template/subsystem/TemplateSubsystemDefinition.java).

If your addition is not a feature, we use the `build-user-feature-pack` goal of the plugin. This does not use  
`wildfly-feature-pack-build.xml`, and since it does not define a subsystem there is no
`TemplateSubsystemDefinition`. In this case we add the required modules to 
the `<packages>` section of your `layer-spec.xml`.

## Adapting for your feature
To adapt this for your own usage, you should of course rename things for your subsystem.
I have attempted to add TODO comments in the relevant places as hints for what to change. Note
that quite a lot will need changing!

## Installing the feature pack into a WildFly installation with Galleon CLI

The server we have seen from the `build/` folder is handy for running our example, but it is not really
how we install Galleon feature packs in real life.

To do this we first provision  WildFly itself using Galleon CLI. This is an alternative
to the more common way of downloading and extracting the zip from the
[WildFly downloads page](https://www.wildfly.org/downloads).

You first need to [download Galleon](https://github.com/wildfly/galleon/releases)
and unzip it somewhere. In my case I just have it in my `~/Downloads` folder, and I am using 
Galleon 4.2.3.

As we will see below there are two main ways to use Gallon CLI to provision servers. We can either
run commands directly in Galleon CLI directly, or we can provide an XML file which contains all the
information for Galleon CLI to be able to provision a server.

### Using CLI commands directly

This consists of two steps. 
1) Installing the base WildFly server
2) Installing our Galleon feature pack with the extra functionality

#### Install main server
First we run the CLI to install the full WildFly server (the result will be the same as the downloaded
zip):
```
~/Downloads/galleon-4.2.3.Final/bin/galleon.sh install wildfly:current --dir=wildfly
```
The `wildfly:current` above tells Galleon to provision the latest version of WildFly which
at the time of writing is 18.0.1.Final. If you want to install a particular version of 
WildFly, you can append the version, e.g:
* `wildfly:current#18.0.0.Final` - installs WildFly 18.0.0.Final
* `wildfly:current#19.0.0.Beta1-SNAPSHOT` - installs WildFly from locally build maven artifacts 

`--dir` specifies the directory to install the server into. In this case I am using 
a relative directory called `wildfly`.

If you want to trim the base server that we install (similar to what we did in the testsuite and the 
example server build), you can specify which layers to install by passing in the `--layers`
option. To install the same server as we used to run the example above, you can run:
```
~/Downloads/galleon-4.2.3.Final/bin/galleon.sh install wildfly:current --dir=wildfly --layers=jaxrs,management
```
Note that we did not install our `template-layer` because this is unknown in the main
WildFly feature pack. We will add it in the next step.

#### Install our layer
Next we want to install our layer. We do this by running:
```
~/Downloads/galleon-4.2.3.Final/bin/galleon.sh install org.wildfly.extras.galleon-feature-pack-template:template-galleon-pack:1.0.0.Alpha-SNAPSHOT --layers=template-layer --dir=wildfly
``` 
`org.wildfly.extras.galleon-feature-pack-template:template-galleon-pack:1.0.0.Alpha-SNAPSHOT`
is the Maven GAV of the Galleon feature pack (i.e. what we have in 
[`galleon-pack/pom.xml`](https://github.com/wildfly/wildfly-galleon-pack-template/blob/master/galleon-pack/pom.xml)).

If you went with the trimmed server in the previous step, and you look at
`wildfly/standalone/configuration/standalone.xml`, you should see that 
both the `template-subsystem` and the `weld` subsystems were added in this second step.
Weld is our CDI implementation, and as we have seen CDI is a dependency of our layer, so 
Galleon pulls it in too!

Now you can start the server by running
```
./wildfly/bin/standalone.sh
``` 
and in another terminal window you can deploy the example into this server
```
mvn package wildfly:deploy -pl example/
```
and then go to http://localhost:8080/example/greeting as before.

### Provisioning from an XML file

An alternative to having to type all those CLI commands every time you want to provision a server is
to use an XML file as input to the Galleon CLI. There is an example in 
[`provision.xml`](https://github.com/wildfly/wildfly-galleon-pack-template/blob/master/provision.xml)

As you can see, it lists the feature pack(s) we depend on, and our feature pack.
For each of those we specify the GAV, as in the previous section. We can
set what to include from each feature pack (Refer to the 
[Galleon documentation](https://docs.wildfly.org/galleon/) for more in-depth
explanation of what each setting does). And finally, we say that we want the `cloud-profile` 
and `template-layer` layers. `cloud-profile` is just to give you another example server,
we could have used the same layers as in the previous section.

To provision the server, you now simply run the following command:
```
~/Downloads/galleon-4.2.3.Final/bin/galleon.sh provision /path/to/provision.xml --dir=wildfly
``` 

Now you can start the server and run the example as we saw in the previous section.


## How do I know which layers to depend on?

There is a list of all our layers defined by WildFly and WildFly Core in our 
[documentation](https://docs.wildfly.org/18/Admin_Guide.html#wildfly-galleon-layers).

However, if you want to understand better what their dependencies are, you can look at the 
layer-spec.xml for the various layers in the following locations:
* WildFly Core's [Core Feature Pack](https://github.com/wildfly/wildfly-core/tree/10.0.3.Final/core-galleon-pack/src/main/resources/layers/standalone)
* WildFly's [Servlet Feature Pack](https://github.com/wildfly/wildfly/tree/18.0.1.Final/servlet-galleon-pack/src/main/resources/layers/standalone)
* WildFly's [Full Feature Pack](https://github.com/wildfly/wildfly/tree/18.0.1.Final/galleon-pack/src/main/resources/layers/standalone)

Note that the above links takes you to the versions used for WildFly 18.0.1.Final. If you
are interested in another/newer WildFly version, adjust the tag name in the URL.

## Debugging failures
To debug failures in the provisioning of the server of the creation of the Galleon feature pack, it is
good to run `mvn install -X` which will provide more logging.

If the above doesn't shed any light on your problems, it can also be good to look at the 
`galleon-pack/target/layout/org.wildfly.extras.galleon-feature-pack-template/template-galleon-pack/<version>/`
directory to see if everything you expected there.