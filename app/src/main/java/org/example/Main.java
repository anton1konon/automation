package org.example;

import org.example.processor.DefaultStringInitializer;
import org.example.processor.RegexValidator;
import org.example.processor.ShowClassInfo;
import org.example.processor.WithDoubleRunner;

public class Main {
    public static void main(String[] args) {

        Person correctPerson = new Person("Petrenko Ivan", "petrenko@mail.com", "0967547123");
        Person incorrectPerson1 = new Person("Ivan", "petrenko@mail.com", "0967547123");
        Person incorrectPerson2 = new Person("Petrenko Ivan", "petrenkomail.com", "0967547123");
        Person incorrectPerson3 = new Person("Petrenko Ivan", "petrenko@mail.com", "096754123");

        System.out.println("correct = "+RegexValidator.isValid(correctPerson));
        System.out.println("correct = "+RegexValidator.isValid(incorrectPerson1));
        System.out.println("correct = "+RegexValidator.isValid(incorrectPerson2));
        System.out.println("correct = "+RegexValidator.isValid(incorrectPerson3));
        System.out.println("-----------------------------------------------");

        DefaultStringInitializer.setDefaults(correctPerson);

        System.out.println(correctPerson);
        System.out.println("-----------------------------------------------");

        MathForDouble math = new MathForDouble(190.5);

        WithDoubleRunner.run(math);
        System.out.println("-----------------------------------------------");

        ShowClassInfo.show(correctPerson);
        System.out.println("-----------------------------------------------");
        ShowClassInfo.show(math);
        System.out.println("-----------------------------------------------");
        PersonProxy personProxy = new PersonProxy(correctPerson);
        personProxy.introduceMyself();



    }
}