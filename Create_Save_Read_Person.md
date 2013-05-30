gora-with-accumulo
==================
Apache Gora (http://gora.apache.org/) provides an abstraction layer to work with various 
data storage engines. In this tutorial, we'll see how to use Gora with Apache Accumulo 
as the storage engine.

I like to start projects with the Maven `pom.xml` file. So here is mine. It's important to 
use Accumulo 1.4.3 instead of the newly released 1.5.0 because of an API incompatibility.
Otherwise, the `pom.xml` file is straightforward.

	<project ...>
      <modelVersion>4.0.0</modelVersion>
  
      <groupId>com.affy</groupId>
      <artifactId>pojos-in-accumulo</artifactId>
      <version>0.0.1-SNAPSHOT</version>
      <packaging>jar</packaging>
  
      <name>POJOs in Accumulo</name>
      <url>http://affy.com</url>
      
      <properties>
          <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
          <!-- Dependency Versions -->
          <accumulo.version>1.4.3</accumulo.version>
          <gora.version>0.3</gora.version>
          <slf4j.version>1.7.5</slf4j.version>
          <!-- Maven Plugin Dependencies -->
          <maven-compiler-plugin.version>2.3.2</maven-compiler-plugin.version>
          <maven-jar-plugin.version>2.4</maven-jar-plugin.version>
          <maven-dependency-plugin.version>2.4</maven-dependency-plugin.version>
          <maven-clean-plugin.version>2.4.1</maven-clean-plugin.version>
      </properties>    
        
      <dependencies>
          <dependency>
              <groupId>org.apache.accumulo</groupId>
              <artifactId>accumulo-core</artifactId>
              <version>${accumulo.version}</version>
              <type>jar</type>
          </dependency>
          <dependency>
              <groupId>org.apache.accumulo</groupId>
              <artifactId>accumulo-server</artifactId>
              <version>${accumulo.version}</version>
              <type>jar</type>
          </dependency>
          <dependency>
              <groupId>org.apache.gora</groupId>
              <artifactId>gora-core</artifactId>
              <version>${gora.version}</version>
          </dependency>
          <dependency>
              <groupId>org.apache.gora</groupId>
              <artifactId>gora-accumulo</artifactId>
              <version>${gora.version}</version>
          </dependency>
          <dependency>
              <groupId>org.slf4j</groupId>
              <artifactId>slf4j-api</artifactId>
              <version>${slf4j.version}</version>
          </dependency>
          <dependency>
              <groupId>org.slf4j</groupId>
              <artifactId>slf4j-log4j12</artifactId>
              <version>${slf4j.version}</version>
          </dependency>
          <!--
          TEST
          -->
          <dependency>
              <groupId>junit</groupId>
              <artifactId>junit</artifactId>
              <version>4.8.2</version>
              <scope>test</scope>
          </dependency>
      </dependencies>
      
  </project>

Now create a `src/main/resources/gora.properties` file configuring Gora by 
specifying how to find Accumulo.

    gora.datastore.default=org.apache.gora.accumulo.store.AccumuloStore
    gora.datastore.accumulo.mock=true
    gora.datastore.accumulo.instance=instance
    gora.datastore.accumulo.zookeepers=localhost
    gora.datastore.accumulo.user=root
    gora.datastore.accumulo.password=

There are some important items to note. Firstly, we'll be using the MockInstance of
Accumulo so that you don't actually need to have it installed. Secondly, the password
needs to be blank if you are depending on Accumulo 1.4.3, change the password to 
'''secret''' if using an earlier version.

That's all it takes to configure Gora. Now let's create a json file to define a very 
simple object - a Person with just a first name. Create a json file with the 
following:

    {
        "type": "record",
        "name": "Person",
        "namespace": "com.affy.generated",
        "fields": [
            {"name": "first", "type": "string"}
        ]
    }

This is the simplest object I could think of. Not very useful for real applications, but 
great for a simple proof-of-concept project.

The json file needs to be compiled into a Java file with the Gora compiler. Hopefully, you
have installed Gora and put its ```bin``` directory onto your path. Run the following to
generate the Java code:

    gora goracompiler src/main/avro/person.json src/main/java
    
One last bit of setup is needed. Create a ```src/main/resources/gora-accumulo-mapping.xml``` 
file with the following:

    <gora-orm>
        <class table="people" keyClass="java.lang.String" name="com.affy.generated.Person">
            <field name="first" family="f" qualifier="q" />
        </class>
    </gora-orm>

Finally we get to the fun part. Actually writing a Java program to create, save, and 
read a Person object. The code is straightforward so I won't explain it, just show it. Create 
a src/main/java/com/affy/Create_Save_Read_Person_Driver.java file like this:

    package com.affy;
    
    import com.affy.generated.Person;
    import org.apache.avro.util.Utf8;
    import org.apache.gora.store.DataStore;
    import org.apache.gora.store.DataStoreFactory;
    import org.apache.gora.util.GoraException;
    import org.apache.hadoop.conf.Configuration;
    
    public class Create_Save_Read_Person_Driver {
    
        private void process() throws GoraException {
            Person person = new Person();
            person.setFirst(new Utf8("David"));
            System.out.println("Person written: " + person);
        
            DataStore<String, Person> datastore = DataStoreFactory.getDataStore(String.class, Person.class, new Configuration());
            if (!datastore.schemaExists()) {
                datastore.createSchema();
            }
            datastore.put("001", person);
            
            Person p = datastore.get("001");
            System.out.println("Person read: " + p);
        }
    
        public static void main(String[] args) throws GoraException {
            Create_Save_Read_Person_Driver driver = new Create_Save_Read_Person_Driver();
            driver.process();
        }
    }

This program has this output:

    Person written: com.affy.generated.Person@20c {
      "first":"David"
    }
    Person read: com.affy.generated.Person@20c {
      "first":"David"
    }
    
