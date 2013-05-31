package com.affy;

import org.apache.gora.util.GoraException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Create_Save_Read_Person_Driver {

    public static void main(String[] args) throws GoraException {
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"application.xml"});

        Create_Save_Read_Person object = context.getBean("createSaveReadPerson", Create_Save_Read_Person.class);
        object.process();
    }
}
