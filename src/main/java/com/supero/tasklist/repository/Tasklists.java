package com.supero.tasklist.repository;


import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.supero.tasklist.filter.TasklistFilter;
import com.supero.tasklist.model.Tasklist;

public class Tasklists implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;
	
	public List<Tasklist> filtrados(TasklistFilter filtro) {
		Session session = manager.unwrap(Session.class);
		Criteria criteria = session.createCriteria(Tasklist.class);	
		
		if ((filtro.getTitulo() != null)&&(filtro.getTitulo() != "")) { 
			criteria.add(Restrictions.like("titulo","%"+filtro.getTitulo()+"%"));
		}
		
		if ((filtro.getDescricao() != null)&&(filtro.getDescricao() != "")) { 
			criteria.add(Restrictions.like("descricao","%"+filtro.getDescricao()+"%"));
		}
		
		if ((filtro.getStatus() != null)&&(filtro.getStatus() != "")) { 
			criteria.add(Restrictions.like("status","%"+filtro.getStatus()+"%"));
		}

		return criteria.addOrder(Order.asc("titulo")).list();
		
	}
	
	public Tasklist porId(Long idtasklist) {
		return this.manager.find(Tasklist.class, idtasklist);
	}

	public List<Tasklist> listaTasklists() {
		return this.manager.createQuery("from Tasklist", Tasklist.class)
				.getResultList();
	}
	
	
	@com.supero.tasklist.util.jpa.Transactional
	public Tasklist salvar(Tasklist tasklist){		
		tasklist = manager.merge(tasklist);		
		return tasklist;
	}
	
	@com.supero.tasklist.util.jpa.Transactional
	public void excluir(Tasklist tasklist) {		
		try {			
			tasklist = porId(tasklist.getIdtasklist());
			manager.remove(tasklist);
			manager.flush();
			
		} catch (PersistenceException e) {
			throw new PersistenceException("Tarefa Não pode ser Excluída!");
		}
	}
	
	public Tasklist porDescricao(String descricao) {
		Tasklist tasklist = null;
		
		try {
			tasklist = this.manager.createQuery("SELECT x from Tasklist x where lower(x.descricao) = :descricao", Tasklist.class)
				.setParameter("descricao", descricao.toLowerCase()).getSingleResult();
		} catch (NoResultException e) {
			// nenhum usuário encontrado com o e-mail informado
		}
		
		return tasklist;
	}
	
	public Tasklist porTitulo(String titulo) {
		Tasklist tasklist = null;
		
		try {
			tasklist = this.manager.createQuery("SELECT x from Tasklist x where lower(x.titulo) = :titulo", Tasklist.class)
				.setParameter("titulo", titulo.toLowerCase()).getSingleResult();
		} catch (NoResultException e) {
			// nenhum usuário encontrado com o e-mail informado
		}
		
		return tasklist;
	}
	
}
