package ru.savimar.mqwildfly.Repository;

import ru.savimar.mqwildfly.Entity.Person;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class PersonRepository {

    @PersistenceContext(unitName = "myUnit")
    EntityManager entityManager;

    public void save(Person user){
        entityManager.persist(user);
    }

    public List<Person> findAll(){
        return entityManager.createQuery("select u from Person u")
                .getResultList();
    }

}
