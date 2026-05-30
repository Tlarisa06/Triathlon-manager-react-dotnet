package ro.mpp.triathlon.repository;

import ro.mpp.triathlon.model.Entity;

import java.util.List;

public interface IRepository<ID, T extends Entity> {
    void add(T element);
    void remove(ID id);
    void update(T element);
    T findById(ID id);
    List<T> getAll();
    void clear();
}