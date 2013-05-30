package com.affy;

import com.affy.generated.Person;
import org.apache.avro.util.Utf8;
import org.apache.gora.store.DataStore;
import org.apache.gora.util.GoraException;

public class Create_Save_Read_Person {

    private DataStore<String, Person> datastore = null;

    public void process() throws GoraException {
        try {
            Person person = new Person();
            person.setFirst(new Utf8("David"));
            System.out.println("Person: " + person);
            System.out.println("First: " + person.getFirst());

            if (!datastore.schemaExists()) {
                datastore.createSchema();
            }
            datastore.put("001", person);

            Person p = datastore.get("001");
            System.out.println("p: " + p);
        } finally {
            datastore.close();
        }
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
