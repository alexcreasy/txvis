package org.jboss.narayana.txvis.dataaccess;

import javax.persistence.*;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 01/05/2013
 * Time: 11:07
 */
public class ResourceDAOJPAImpl implements ResourceDAO {

    private final EntityManagerFactory entityManagerFactory =
            Persistence.createEntityManagerFactory("org.jboss.narayana.txvis");

    private final EntityManager em = entityManagerFactory.createEntityManager();

    @Override
    public Resource get(String resourceID) throws IllegalArgumentException, NullPointerException {
        if (resourceID.trim().isEmpty())
            throw new IllegalArgumentException("empty resourceID");

        Resource r = getByResourceID(resourceID);
        if (r == null) {
            em.getTransaction().begin();
            em.persist(r = new Resource(resourceID));
            em.getTransaction().commit();
        }
        return r;
    }

    private Resource getByResourceID(String resourceID) {
        final String resourceQuery = "FROM "
                + Resource.class.getSimpleName()
                + " e WHERE e.resourceID=:resourceID";
        Query q = em.createQuery(resourceQuery);
        q.setParameter("resourceID", resourceID);

        Resource result = null;
        try {
            result = (Resource) q.getSingleResult();
        }
        catch (EntityNotFoundException enfe) {
            throw new RuntimeException("Entity not found", enfe);
        }
        catch (NonUniqueResultException nure) {
            throw new IllegalStateException("Multiple Resources with equal resourceID exist ", nure);
        }
        catch (NoResultException nre) {}

        return result;
    }

    @Override
    public void deconstruct() {
        em.close();
    }
}
