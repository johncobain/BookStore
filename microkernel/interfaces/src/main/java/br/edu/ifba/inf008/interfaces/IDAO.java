package br.edu.ifba.inf008.interfaces;

import java.util.List;

public interface IDAO<T, ID> {
  void save(T entity);
  T findById(ID id);
  List<T> findAll();
  void update(T entity);
  void delete(T entity);
}
