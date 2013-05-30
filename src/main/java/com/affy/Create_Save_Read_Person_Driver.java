package com.affy;

import com.affy.generated.Person;
import org.apache.avro.util.Utf8;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.gora.util.GoraException;
import org.apache.hadoop.conf.Configuration;

public class Create_Save_Read_Person_Driver {

    private DataStore<String, Person> datastore = null;

    
    
    private void process() throws GoraException {
        Person person = new Person();
        person.setFirst(new Utf8("David"));
        System.out.println("Person: " + person);
        System.out.println("First: " + person.getFirst());
    
        DataStore<String, Person> datastore = DataStoreFactory.getDataStore(String.class, Person.class, new Configuration());
        if (!datastore.schemaExists()) {
            datastore.createSchema();
        }
        datastore.put("001", person);
        
        Person p = datastore.get("001");
        System.out.println("p: " + p);
        
        datastore.close();
    }

    public static void main(String[] args) throws GoraException {
        System.out.println("Start.");
        Create_Save_Read_Person_Driver driver = new Create_Save_Read_Person_Driver();
        driver.process();
        System.out.println("End.");
    }

    /**
     * @return the datastore
     */
    public DataStore<String, Person> getDatastore() {
        return datastore;
    }

    /**
     * @param datastore the datastore to set
     */
    public void setDatastore(DataStore<String, Person> datastore) {
        this.datastore = datastore;
    }
}
