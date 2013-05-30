package com.affy;

import com.affy.generated.Person;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.gora.util.GoraException;
import org.apache.hadoop.conf.Configuration;

public class Create_Save_Read_Person_Driver {

    public static void main(String[] args) throws GoraException {
        DataStore<String, Person> datastore = DataStoreFactory.getDataStore(String.class, Person.class, new Configuration());

        Create_Save_Read_Person object = new Create_Save_Read_Person();
        object.setDatastore(datastore);
        object.process();
    }
}
