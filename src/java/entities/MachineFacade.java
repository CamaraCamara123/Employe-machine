/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author Lenovo
 */
@Stateless
public class MachineFacade extends AbstractFacade<Machine> {
    @PersistenceContext(unitName = "Employe_machinePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MachineFacade() {
        super(Machine.class);
    }
    
    public List<Machine> findByDate(Date date) {
        Query query = em.createQuery("SELECT m FROM Machine m WHERE m.dateAchat = :date");
        query.setParameter("date", date);
        return query.getResultList();
    }
    
    public List<Machine> findByEmployee(Employe employee) {
        TypedQuery<Machine> query = em.createQuery("SELECT m FROM Machine m WHERE m.employe = :employee", Machine.class);
        query.setParameter("employee", employee);
        return query.getResultList();
    }
    
}
